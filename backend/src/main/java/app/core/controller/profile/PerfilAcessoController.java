package app.core.controller.profile;

import app.agendamento.model.pessoa.Usuario;
import app.core.model.DTO.Responses;
import app.core.model.profile.PerfilAcesso;
import app.core.model.profile.Rotina;
import app.core.utils.BasicFunctions;
import app.core.utils.Contexto;
import org.jetbrains.annotations.NotNull;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
@Transactional
public class PerfilAcessoController {

    @Context
    SecurityContext context;
    private PerfilAcesso perfilAcesso = new PerfilAcesso();
    private Responses responses;
    private Usuario usuarioAuth;

    private static List<Rotina> loadRotinasByPerfilAcesso(@NotNull PerfilAcesso pPerfilAcesso) {
        List<Rotina> rotinas = new ArrayList<>();

        List<Long> rotinasId = new ArrayList<>();

        if (pPerfilAcesso.hasRotinas()) {
            pPerfilAcesso.getRotinas().forEach(tipoAgendamento -> rotinasId.add(tipoAgendamento.getId()));
            rotinas = Rotina.list("id in ?1", rotinasId);
        }
        return rotinas;
    }

    public Response addPerfilAcesso(@NotNull PerfilAcesso pPerfilAcesso) {

        responses = new Responses();
        responses.setMessages(new ArrayList<>());
        usuarioAuth = Contexto.getContextUser(context);

        perfilAcesso = new PerfilAcesso();

        List<Rotina> rotinas = loadRotinasByPerfilAcesso(pPerfilAcesso);

        if (BasicFunctions.isNotEmpty(pPerfilAcesso.getNome())) {
            perfilAcesso.setNome(pPerfilAcesso.getNome());
        }
        if (BasicFunctions.isNotEmpty(pPerfilAcesso.getCriar())) {
            perfilAcesso.setCriar(pPerfilAcesso.getCriar());
        }
        if (BasicFunctions.isNotEmpty(pPerfilAcesso.getLer())) {
            perfilAcesso.setLer(pPerfilAcesso.getLer());
        }
        if (BasicFunctions.isNotEmpty(pPerfilAcesso.getAtualizar())) {
            perfilAcesso.setAtualizar(pPerfilAcesso.getAtualizar());
        }
        if (BasicFunctions.isNotEmpty(pPerfilAcesso.getApagar())) {
            perfilAcesso.setApagar(pPerfilAcesso.getApagar());
        }
        if (BasicFunctions.isNotEmpty(pPerfilAcesso.getRotinas())) {
            perfilAcesso.setRotinas(pPerfilAcesso.getRotinas());
        }
        if (BasicFunctions.isNotEmpty(rotinas)) {
            perfilAcesso.setRotinas(new ArrayList<>());
            perfilAcesso.getRotinas().addAll(rotinas);
        }
        perfilAcesso.setUsuario(usuarioAuth);
        perfilAcesso.setUsuarioAcao(usuarioAuth);
        perfilAcesso.setDataAcao(Contexto.dataHoraContexto());
        perfilAcesso.persist();

        responses.setStatus(201);
        responses.setData(perfilAcesso);
        responses.getMessages().add("Perfil de Acesso cadastrado com sucesso!");
        return Response.ok(responses).status(Response.Status.CREATED).build();
    }

    public Response updatePerfilAcesso(@NotNull PerfilAcesso pPerfilAcesso) throws BadRequestException {

        usuarioAuth = Contexto.getContextUser(context);

        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        List<Rotina> rotinas = loadRotinasByPerfilAcesso(pPerfilAcesso);

        try {

            if (BasicFunctions.isNotEmpty(pPerfilAcesso)) {
                perfilAcesso = PerfilAcesso.findById(pPerfilAcesso.getId());
            }

            if (BasicFunctions.isEmpty(perfilAcesso)) {
                responses.getMessages().add("O Perfil de Acesso qual você deseja alterar os dados não foi localizado!");
                return Response.ok(responses).status(responses.getStatus()).build();
            } else {
                if (BasicFunctions.isNotEmpty(pPerfilAcesso.getLer())) {
                    if (!perfilAcesso.getLer().equals(pPerfilAcesso.getLer())) {
                        perfilAcesso.setLer(pPerfilAcesso.getLer());
                    }
                }

                if (BasicFunctions.isNotEmpty(pPerfilAcesso.getNome())) {
                    if (!perfilAcesso.getNome().equals(pPerfilAcesso.getNome())) {
                        perfilAcesso.setNome(pPerfilAcesso.getNome());
                    }
                }

                if (BasicFunctions.isNotEmpty(pPerfilAcesso.getAtualizar())) {
                    if (BasicFunctions.isNotEmpty(perfilAcesso.getAtualizar())
                            && !perfilAcesso.getAtualizar().equals(pPerfilAcesso.getAtualizar())) {
                        perfilAcesso.setAtualizar(pPerfilAcesso.getAtualizar());
                    }
                }
                if (BasicFunctions.isNotEmpty(pPerfilAcesso.getCriar())) {
                    if (!perfilAcesso.getCriar().equals(pPerfilAcesso.getCriar())) {
                        perfilAcesso.setCriar(pPerfilAcesso.getCriar());
                    }
                }
                if (BasicFunctions.isNotEmpty(pPerfilAcesso.getApagar())) {
                    perfilAcesso.setApagar(pPerfilAcesso.getApagar());
                }
                if (pPerfilAcesso.hasRotinas()) {
                    perfilAcesso.setRotinas(pPerfilAcesso.getRotinas());
                }
                if (BasicFunctions.isNotEmpty(rotinas)) {
                    perfilAcesso.setRotinas(new ArrayList<>());
                    perfilAcesso.getRotinas().addAll(rotinas);
                }
                perfilAcesso.setUsuarioAcao(usuarioAuth);
                perfilAcesso.setDataAcao(Contexto.dataHoraContexto());
                perfilAcesso.persist();

                responses.setStatus(200);
                responses.setData(perfilAcesso);
                responses.getMessages().add("Perfil de Acesso atualizado com sucesso!");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            responses.setData(perfilAcesso);
            responses.getMessages().add("Não foi possível atualizar o Perfil de Acesso.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    public Response deletePerfilAcesso(@NotNull List<Long> pListPerfilAcesso) {

        List<PerfilAcesso> perfilAcessos;
        List<PerfilAcesso> perfilAcessosAux = new ArrayList<>();
        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        perfilAcessos = PerfilAcesso.list("id in ?1", pListPerfilAcesso);
        int count = perfilAcessos.size();

        if (perfilAcessos.isEmpty()) {

            responses.setStatus(400);
            responses.getMessages().add("Perfis de Acesso não localizados ou já excuídos.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }

        try {

            perfilAcessos.forEach((perfilAcesso) -> {
                perfilAcessosAux.add(perfilAcesso);
                perfilAcesso.delete();

            });

            responses.setStatus(200);
            if (count <= 1) {
                responses.setData(perfilAcesso);
                responses.getMessages().add("Perfil de Acesso excluído com sucesso!");
            } else {
                responses.setDatas(Collections.singletonList(perfilAcessosAux));
                responses.getMessages().add(count + " Perfis de Acessos excluídos com sucesso!");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            if (count <= 1) {
                responses.setData(perfilAcesso);
                responses.getMessages().add("Perfil de Acesso não localizado ou já excluído.");
            } else {
                responses.setDatas(Collections.singletonList(perfilAcessos));
                responses.getMessages().add("Perfis de Acessos não localizados ou já excluídos.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }
}
