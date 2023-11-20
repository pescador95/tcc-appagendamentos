package app.core.controller.contrato;

import app.agendamento.model.organizacao.Organizacao;
import app.agendamento.model.pessoa.Usuario;
import app.core.model.DTO.Responses;
import app.core.model.contrato.Contrato;
import app.core.model.contrato.TipoContrato;
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
public class ContratoController {

    @Context
    SecurityContext context;
    private Contrato contrato = new Contrato();
    private Responses responses;
    private Usuario usuarioAuth;

    public static TipoContrato getTipoContratoByUsuarioOrganizacaoDefault(Usuario pUsuario) {

        Organizacao organizacaoDefault = pUsuario.getOrganizacaoDefault();

        TipoContrato tipoContrato = new TipoContrato();

        Contrato contrato = Contrato.find("organizacaoContrato = ?1", organizacaoDefault).firstResult();

        if (BasicFunctions.isNotEmpty(contrato)) {
            tipoContrato = contrato.getTipoContrato();
        }
        return tipoContrato;
    }

    public Response addContrato(@NotNull Contrato pContrato) {

        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        usuarioAuth = Contexto.getContextUser(context);

        Organizacao organizacao = new Organizacao();

        if (BasicFunctions.isNotEmpty(pContrato.getOrganizacaoContrato()) && pContrato.getOrganizacaoContrato().isValid()) {
            contrato = Contrato.find("organizacaoContrato = ?1 and ativo = true", pContrato.getOrganizacaoContrato())
                    .firstResult();

            organizacao = Organizacao.findById(pContrato.getOrganizacaoContrato().getId());
        }

        if (BasicFunctions.isEmpty(contrato.getResponsavelContrato())) {
            contrato = new Contrato();

            if (BasicFunctions.isNotEmpty(pContrato.getResponsavelContrato())) {
                contrato.setResponsavelContrato(pContrato.getResponsavelContrato());
            }
            if (BasicFunctions.isNotEmpty(organizacao)) {
                contrato.setOrganizacaoContrato(organizacao);
            }
            if (BasicFunctions.isValid(pContrato.getNumeroMaximoSessoes())) {
                contrato.setNumeroMaximoSessoes(pContrato.getNumeroMaximoSessoes());
            } else {
                responses.setStatus(400);
                responses.setData(contrato);
                responses.getMessages().add("Informe o número máximo de sessões!");
                return Response.ok(responses).status(responses.getStatus()).build();
            }
            if (BasicFunctions.isNotEmpty(pContrato.getConsideracoes())) {
                contrato.setConsideracoes(pContrato.getConsideracoes());
            }
            if (BasicFunctions.isValid(pContrato.getDataContrato())) {
                contrato.setDataContrato(pContrato.getDataContrato());
            }
            if (BasicFunctions.isNotEmpty(pContrato.getTipoContrato()) && pContrato.getTipoContrato().isValid()) {
                contrato.getTipoContrato().setId(pContrato.getTipoContrato().getId());
            } else {
                contrato.getTipoContrato().setTipoSessaoUnica();
            }
            if (!responses.hasMessages()) {
                contrato.setUsuario(usuarioAuth);
                contrato.setUsuarioAcao(usuarioAuth);
                contrato.setAtivo(Boolean.TRUE);
                contrato.setDataAcao(Contexto.dataHoraContexto());
                contrato.persist();

                responses.setStatus(201);
                responses.setData(contrato);
                responses.getMessages().add("Contrato cadastrado com sucesso!");

            } else {
                return Response.ok(responses).status(responses.getStatus()).build();
            }
            return Response.ok(responses).status(Response.Status.CREATED).build();
        } else {

            responses.setStatus(400);
            responses.setData(contrato);
            responses.getMessages().add("Contrato já cadastrado!");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    public Response updateContrato(@NotNull Contrato pContrato) {

        usuarioAuth = Contexto.getContextUser(context);

        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        try {

            if (pContrato.isValid()) {
                contrato = Contrato.findById(pContrato.getId());
            }
            if (!pContrato.isValid() && BasicFunctions.isEmpty(pContrato.getResponsavelContrato())
                    && BasicFunctions.isEmpty(pContrato.getOrganizacaoContrato())) {
                throw new BadRequestException("Informe os dados para atualizar o cadastro do Contrato.");
            } else {
                if (BasicFunctions.isNotEmpty(pContrato.getResponsavelContrato())) {
                    contrato.setResponsavelContrato(pContrato.getResponsavelContrato());
                }
                if (BasicFunctions.isNotEmpty(pContrato.getOrganizacaoContrato())) {
                    contrato.setOrganizacaoContrato(pContrato.getOrganizacaoContrato());
                }
                if (BasicFunctions.isNotEmpty(pContrato.getNumeroMaximoSessoes())) {
                    contrato.setNumeroMaximoSessoes(pContrato.getNumeroMaximoSessoes());
                }
                if (BasicFunctions.isNotEmpty(pContrato.getConsideracoes())) {
                    contrato.setConsideracoes(pContrato.getConsideracoes());
                }
                if (BasicFunctions.isNotEmpty(pContrato.getDataContrato())) {
                    contrato.setDataContrato(pContrato.getDataContrato());
                }
                if (BasicFunctions.isNotEmpty(pContrato.getTipoContrato()) && pContrato.getTipoContrato().isValid()) {
                    contrato.getTipoContrato().setId(pContrato.getTipoContrato().getId());
                } else {
                    contrato.getTipoContrato().setTipoSessaoUnica();
                }
                contrato.setUsuarioAcao(usuarioAuth);
                contrato.setDataAcao(Contexto.dataHoraContexto());
                contrato.persist();

                responses.setStatus(200);
                responses.setData(contrato);
                responses.getMessages().add("Cadastro de Contrato atualizado com sucesso!");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            responses.setData(contrato);
            responses.getMessages().add("Não foi possível atualizar o cadastro da Contrato.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    public Response deleteContrato(@NotNull List<Long> pListIdContrato) {

        List<Contrato> contratos;
        List<Contrato> contratosAux = new ArrayList<>();
        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        usuarioAuth = Contexto.getContextUser(context);

        contratos = Contrato.list("id in ?1 and ativo = true", pListIdContrato);
        int count = contratos.size();

        try {

            if (contratos.isEmpty()) {

                responses.setStatus(400);
                responses.getMessages().add("Contratos não localizados ou já excluídos.");
                return Response.ok(responses).status(responses.getStatus()).build();
            }

            contratos.forEach((contrato) -> {

                Contrato contratoDeleted = contrato.deletarContrato(contrato, context);

                contratoDeleted.persist();
                contratosAux.add(contratoDeleted);
            });

            responses.setStatus(200);
            if (count <= 1) {
                responses.setData(contrato);
                responses.getMessages().add("Contrato excluído com sucesso!");
            } else {
                responses.setDatas(Collections.singletonList(contratosAux));
                responses.getMessages().add(count + " Contratos excluídos com sucesso!");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            if (count <= 1) {
                responses.setData(contrato);
                responses.getMessages().add("Contrato não localizado ou já excluído.");
            } else {
                responses.setDatas(Collections.singletonList(contratos));
                responses.getMessages().add("Contratos não localizados ou já excluídos.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    public Response reactivateContrato(@NotNull List<Long> pListIdContrato) {

        List<Contrato> contratos;
        List<Contrato> contratosAux = new ArrayList<>();
        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        Usuario usuarioAuth = Contexto.getContextUser(context);
        contratos = Contrato.list("id in ?1 and ativo = false", pListIdContrato);
        int count = contratos.size();

        try {

            if (contratos.isEmpty()) {

                responses.setStatus(400);
                responses.getMessages().add("Contratos não localizados ou já reativados.");
                return Response.ok(responses).status(responses.getStatus()).build();
            }

            contratos.forEach((contrato) -> {

                Contrato contratoReactivated = contrato.reativarContrato(contrato, context);

                contratoReactivated.persist();
                contratosAux.add(contratoReactivated);
            });

            responses.setStatus(200);
            if (count <= 1) {
                responses.setData(contrato);
                responses.getMessages().add("Contrato reativado com sucesso!");
            } else {
                responses.setDatas(Collections.singletonList(contratosAux));
                responses.getMessages().add(count + " Contratos reativados com sucesso!");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            if (count <= 1) {
                responses.setData(contrato);
                responses.getMessages().add("Contrato não localizado ou já reativado.");
            } else {
                responses.setDatas(Collections.singletonList(contratos));
                responses.getMessages().add("Contratos não localizados ou já reativados.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }
}
