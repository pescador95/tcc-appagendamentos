package app.agendamento.controller.organizacao;

import app.agendamento.model.organizacao.Organizacao;
import app.agendamento.model.pessoa.Usuario;
import app.core.model.DTO.Responses;
import app.core.utils.BasicFunctions;
import app.core.utils.Contexto;
import org.jetbrains.annotations.NotNull;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
@Transactional
public class OrganizacaoController {

    @Context
    SecurityContext context;
    private Organizacao organizacao = new Organizacao();
    private Responses responses;
    private Usuario usuarioAuth;

    public Response addOrganizacao(@NotNull Organizacao pOrganizacao) {

        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        usuarioAuth = Contexto.getContextUser(context);

        loadOrganizacaoByCnpj(pOrganizacao);

        if (BasicFunctions.isEmpty(organizacao)) {

            if (!responses.hasMessages()) {

                organizacao = new Organizacao(pOrganizacao, context);

                organizacao.persist();

                responses.setStatus(201);
                responses.setData(organizacao);
                responses.getMessages().add("Organização cadastrada com sucesso!");

            } else {
                return Response.ok(responses).status(responses.getStatus()).build();
            }
            return Response.ok(responses).status(Response.Status.CREATED).build();
        } else {

            responses.setStatus(400);
            responses.setData(organizacao);
            responses.getMessages().add("Organização já cadastrada!");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    public Response updateOrganizacao(@NotNull Organizacao pOrganizacao) {

        usuarioAuth = Contexto.getContextUser(context);

        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        try {

            loadOrganizacaoById(pOrganizacao);

            if (BasicFunctions.isNotEmpty(organizacao) && !responses.hasMessages()) {

                organizacao = organizacao.organizacao(organizacao, pOrganizacao, context);

                organizacao.persistAndFlush();

                responses.setStatus(200);
                responses.setData(organizacao);
                responses.getMessages().add("Cadastro de Organização atualizado com sucesso!");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            responses.setData(organizacao);
            responses.getMessages().add("Não foi possível atualizar o cadastro da Organização.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    public Response deleteOrganizacao(@NotNull List<Long> pListIdOrganizacao) {

        List<Organizacao> organizacoes;
        List<Organizacao> organizacoesAux = new ArrayList<>();
        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        organizacoes = Organizacao.list("id in ?1 and ativo = true", pListIdOrganizacao);
        int count = organizacoes.size();

        try {

            if (organizacoes.isEmpty()) {

                responses.setStatus(400);
                responses.getMessages().add("Organizações não localizadas ou já excluídas.");
                return Response.ok(responses).status(responses.getStatus()).build();
            }

            organizacoes.forEach((organizacao) -> {

                Organizacao organizacaoDeleted = organizacao.deletarOrganizacao(organizacao, context);

                organizacaoDeleted.persist();
                organizacoesAux.add(organizacaoDeleted);
            });

            responses.setStatus(200);
            if (count <= 1) {
                responses.setData(organizacao);
                responses.getMessages().add("Organização excluída com sucesso!");
            } else {
                responses.setDatas(Collections.singletonList(organizacoesAux));
                responses.getMessages().add(count + " Organizações excluídas com sucesso!");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            if (count <= 1) {
                responses.setData(organizacao);
                responses.getMessages().add("Organização não localizada ou já excluída.");
            } else {
                responses.setDatas(Collections.singletonList(organizacoes));
                responses.getMessages().add("Organizações não localizadas ou já excluídas.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    public Response reactivateOrganizacao(@NotNull List<Long> pListIdOrganizacao) {

        List<Organizacao> organizacoes;
        List<Organizacao> organizacoesAux = new ArrayList<>();
        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        organizacoes = Organizacao.list("id in ?1 and ativo = false", pListIdOrganizacao);
        int count = organizacoes.size();

        try {

            if (organizacoes.isEmpty()) {

                responses.setStatus(400);
                responses.getMessages().add("Organizações não localizadas ou já reativadas.");
                return Response.ok(responses).status(responses.getStatus()).build();
            }

            organizacoes.forEach((organizacao) -> {

                Organizacao organizacaoReactivated = organizacao.reativarOrganizacao(organizacao, context);

                organizacaoReactivated.persist();
                organizacoesAux.add(organizacaoReactivated);
            });

            responses.setStatus(200);
            if (count <= 1) {
                responses.setData(organizacao);
                responses.getMessages().add("Organização reativada com sucesso!");
            } else {
                responses.setDatas(Collections.singletonList(organizacoesAux));
                responses.getMessages().add(count + " Organizações reativadas com sucesso!");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            if (count <= 1) {
                responses.setData(organizacao);
                responses.getMessages().add("Organização não localizada ou já reativada.");
            } else {
                responses.setDatas(Collections.singletonList(organizacoes));
                responses.getMessages().add("Organizações não localizadas ou já reativadas.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    private void loadOrganizacaoByCnpj(Organizacao pOrganizacao) {

        organizacao = new Organizacao();


        if (BasicFunctions.isNotEmpty(pOrganizacao.getCnpj())) {
            organizacao = Organizacao.find("cnpj = ?1 and ativo = true", pOrganizacao.getCnpj()).firstResult();
        }
        validaOrganizacao(pOrganizacao);
    }

    private void loadOrganizacaoById(Organizacao pOrganizacao) {
        if (pOrganizacao.isValid()) {
            organizacao = Organizacao.findById(pOrganizacao.getId());
        }
        validaOrganizacao(pOrganizacao);
    }

    private void validaOrganizacao(Organizacao pOrganizacao) {

        if (pOrganizacao.cnpjJaUtilizado(pOrganizacao)) {
            responses.setStatus(400);
            responses.getMessages().add("Já existe uma organização cadastrada com o CNPJ informado!");
        }
        if (BasicFunctions.isEmpty(pOrganizacao.getNome())
                && BasicFunctions.isEmpty(pOrganizacao.getCnpj())) {
            responses.setStatus(400);
            responses.getMessages().add("Informe os dados para o cadastro da Organização.");
        }
    }
}
