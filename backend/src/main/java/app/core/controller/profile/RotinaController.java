package app.core.controller.profile;

import app.agendamento.model.pessoa.Usuario;
import app.core.model.DTO.Responses;
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
import java.util.Objects;

@ApplicationScoped
@Transactional
public class RotinaController {

    @Context
    SecurityContext context;
    private Rotina rotina = new Rotina();
    private Responses responses;
    private Usuario usuarioAuth;

    public Response addRotina(@NotNull Rotina pRotina) {

        responses = new Responses();
        responses.setMessages(new ArrayList<>());
        usuarioAuth = Contexto.getContextUser(context);

        rotina = new Rotina();

        if (BasicFunctions.isNotEmpty(pRotina.getNome())) {
            rotina.setNome(pRotina.getNome());
        }
        if (BasicFunctions.isNotEmpty(pRotina.getIcon())) {
            rotina.setIcon(pRotina.getIcon());
        }
        if (BasicFunctions.isNotEmpty(pRotina.getPath())) {
            rotina.setPath(pRotina.getPath());
        }
        if (BasicFunctions.isNotEmpty(pRotina.getTitulo())) {
            rotina.setTitulo(pRotina.getTitulo());
        }
        rotina.setUsuario(usuarioAuth);
        rotina.setUsuarioAcao(usuarioAuth);
        rotina.setDataAcao(Contexto.dataHoraContexto());
        rotina.persist();

        responses.setStatus(201);
        responses.setData(rotina);
        responses.getMessages().add("Rotina cadastrado com sucesso!");
        return Response.ok(responses).status(Response.Status.CREATED).build();
    }

    public Response updateRotina(@NotNull Rotina pRotina) throws BadRequestException {

        usuarioAuth = Contexto.getContextUser(context);

        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        try {

            if (BasicFunctions.isValid(pRotina.getId())) {
                rotina = Rotina.findById(pRotina.getId());
            }

            if (BasicFunctions.isEmpty(rotina)) {
                responses.getMessages().add("A Rotina qual você deseja alterar os dados não foi localizada!");
                return Response.ok(responses).status(responses.getStatus()).build();
            } else {
                if (BasicFunctions.isNotEmpty(pRotina.getPath())) {
                    if (!rotina.getPath().equals(pRotina.getPath())) {
                        rotina.setPath(pRotina.getPath());
                    }
                }

                if (BasicFunctions.isNotEmpty(pRotina.getNome())) {
                    if (!Objects.equals(rotina.getNome(), pRotina.getNome())) {
                        rotina.setNome(pRotina.getNome());
                    }
                }

                if (BasicFunctions.isNotEmpty(pRotina.getTitulo())) {
                    if (BasicFunctions.isNotEmpty(rotina.getTitulo()) && !rotina.getTitulo().equals(pRotina.getTitulo())) {
                        rotina.setTitulo(pRotina.getTitulo());
                    }
                }
                if (BasicFunctions.isNotEmpty(pRotina.getIcon())) {
                    if (!rotina.getIcon().equals(pRotina.getIcon())) {
                        rotina.setIcon(pRotina.getIcon());
                    }
                }

                rotina.persist();
                rotina.setUsuarioAcao(usuarioAuth);
                rotina.setDataAcao(Contexto.dataHoraContexto());

                responses.setStatus(200);
                responses.setData(rotina);
                responses.getMessages().add("Rotina atualizado com sucesso!");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            responses.setData(rotina);
            responses.getMessages().add("Não foi possível atualizar a Rotina.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    public Response deleteRotina(@NotNull List<Long> pListRotina) {

        List<Rotina> rotinas;
        List<Rotina> rotinasAux = new ArrayList<>();
        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        usuarioAuth = Contexto.getContextUser(context);
        rotinas = Rotina.list("id in ?1", pListRotina);
        int count = rotinas.size();

        if (BasicFunctions.isEmpty(rotinas)) {

            responses.setStatus(400);
            responses.getMessages().add("Rotinas não localizadas ou já excuídas.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }

        try {

            rotinas.forEach((rotina) -> {
                rotinasAux.add(rotina);
                rotina.delete();

            });

            responses.setStatus(200);
            if (count <= 1) {
                responses.setData(rotina);
                responses.getMessages().add("Rotina excluída com sucesso!");
            } else {
                responses.setDatas(Collections.singletonList(rotinasAux));
                responses.getMessages().add(count + " Rotinas excluídas com sucesso!");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            if (count <= 1) {
                responses.setData(rotina);
                responses.getMessages().add("Rotina não localizada ou já excluída.");
            } else {
                responses.setDatas(Collections.singletonList(rotinas));
                responses.getMessages().add("Rotinas não localizadas ou já excluídas.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }
}
