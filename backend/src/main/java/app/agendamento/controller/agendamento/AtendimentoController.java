package app.agendamento.controller.agendamento;

import app.agendamento.model.agendamento.Atendimento;
import app.agendamento.model.pessoa.Pessoa;
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
public class AtendimentoController {

    @Context
    SecurityContext context;
    private Atendimento atendimento = new Atendimento();
    private Responses responses;
    private Usuario usuarioAuth;

    private Pessoa pessoa;

    public Response addAtendimento(@NotNull Atendimento pAtendimento, Long agendamentoId) {

        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        usuarioAuth = Contexto.getContextUser(context);

        loadAtendimentoByPessoaData(pAtendimento);

        if (BasicFunctions.isEmpty(atendimento)) {

            atendimento = new Atendimento();

            loadPessoaByAtendimento(pAtendimento);

            if (!responses.hasMessages()) {

                atendimento = new Atendimento(pAtendimento, agendamentoId, pessoa, context);

                atendimento.persist();

                responses.setStatus(201);
                responses.setData(atendimento);
                responses.getMessages().add("Atendimento cadastrado com sucesso!");

            } else {
                return Response.ok(responses).status(responses.getStatus()).build();
            }
            return Response.ok(responses).status(Response.Status.CREATED).build();
        } else {

            responses.setStatus(400);
            responses.setData(atendimento);
            responses.getMessages().add("Atendimento  já cadastrado!");
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }

    public Response updateAtendimento(@NotNull Atendimento pAtendimento, Long agendamentoId) {

        usuarioAuth = Contexto.getContextUser(context);

        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        try {

            loadAtendimentoById(pAtendimento);

            if (BasicFunctions.isNotEmpty(atendimento)) {

                loadPessoaByAtendimento(pAtendimento);

                atendimento = atendimento.atendimento(atendimento, pAtendimento, agendamentoId, pessoa, context);

                atendimento.persistAndFlush();

                responses.setStatus(200);
                responses.setData(atendimento);
                responses.getMessages().add("Cadastro de Atendimento  atualizado com sucesso!");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            responses.setData(atendimento);
            responses.getMessages().add("Não foi possível atualizar o cadastro do Atendimento.");
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }

    public Response deleteAtendimento(@NotNull List<Long> pListIdAtendimento) {

        List<Atendimento> atendimentos;
        List<Atendimento> atendimentosAux = new ArrayList<>();
        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        usuarioAuth = Contexto.getContextUser(context);
        atendimentos = Atendimento.list("id in ?1 and ativo = true", pListIdAtendimento);
        int count = atendimentos.size();

        try {

            if (atendimentos.isEmpty()) {

                responses.setStatus(400);
                responses.getMessages().add("Atendimentos não localizados ou já excluídos.");
                return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
            }

            atendimentos.forEach((atendimento) -> {

                Atendimento atendimentoDeleted;

                atendimentoDeleted = atendimento.deletarAtendimento(atendimento, context);

                atendimentoDeleted.persist();
                atendimentosAux.add(atendimentoDeleted);
            });

            responses.setStatus(200);
            if (count <= 1) {
                responses.setData(atendimento);
                responses.getMessages().add("Atendimento excluído com sucesso!");
            } else {
                responses.setDatas(Collections.singletonList(atendimentosAux));
                responses.getMessages().add(count + " Atendimentos excluídos com sucesso!");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            if (count <= 1) {
                responses.setData(atendimento);
                responses.getMessages().add("Atendimento não localizado ou já excluído.");
            } else {
                responses.setDatas(Collections.singletonList(atendimentos));
                responses.getMessages().add("Atendimentos não localizados ou já excluídos.");
            }
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }

    public Response reactivateAtendimento(@NotNull List<Long> pListIdAtendimento) {

        List<Atendimento> atendimentos;
        List<Atendimento> atendimentosAux = new ArrayList<>();
        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        Usuario usuarioAuth = Contexto.getContextUser(context);
        atendimentos = Atendimento.list("id in ?1 and ativo = false", pListIdAtendimento);
        int count = atendimentos.size();

        try {

            if (atendimentos.isEmpty()) {

                responses.setStatus(400);
                responses.getMessages().add("Atendimentos não localizados ou já reativados.");
                return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
            }

            atendimentos.forEach((atendimento) -> {

                Atendimento atendimentoReactivated = atendimento.reativarAgendimento(atendimento, context);

                atendimentoReactivated.persist();
                atendimentosAux.add(atendimentoReactivated);
            });

            responses.setStatus(200);
            if (count <= 1) {
                responses.setData(atendimento);
                responses.getMessages().add("Atendimento reativado com sucesso!");
            } else {
                responses.setDatas(Collections.singletonList(atendimentosAux));
                responses.getMessages().add(count + " Atendimentos reativados com sucesso!");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            if (count <= 1) {
                responses.setData(atendimento);
                responses.getMessages().add("Atendimento não localizado ou já reativado.");
            } else {
                responses.setDatas(Collections.singletonList(atendimentos));
                responses.getMessages().add("Atendimentos não localizados ou já reativados.");
            }
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }

    private void loadPessoaByAtendimento(Atendimento pAtendimento) {

        pessoa = new Pessoa();

        if (BasicFunctions.isNotEmpty(pAtendimento.getPessoa()) && BasicFunctions.isValid(pAtendimento.getPessoa().getId())) {
            pessoa = Pessoa.findById(pAtendimento.getPessoa().getId());
        }
    }

    private void loadAtendimentoByPessoaData(Atendimento pAtendimento) {

        atendimento = new Atendimento();

        if (BasicFunctions.isNotEmpty(pAtendimento) && pAtendimento.getPessoa().isValid()
                && BasicFunctions.isValid(pAtendimento.getDataAtendimento())) {
            atendimento = Atendimento
                    .find("pessoaId = ?1 and dataAtendimento = ?2 and ativo = true",
                            pAtendimento.getPessoa().getId(),
                            pAtendimento.getDataAtendimento())
                    .firstResult();
        }
        validarAtendimento(pAtendimento);
    }

    private void loadAtendimentoById(Atendimento pAtendimento) {

        atendimento = new Atendimento();

        if (pAtendimento.isValid()) {
            atendimento = Atendimento.find("id = ?1 and ativo = true", pAtendimento.getId()).firstResult();
        }
        validarAtendimento(pAtendimento);
    }

    private void validarAtendimento(Atendimento pAtendimento) {
        if (BasicFunctions.isEmpty(pAtendimento) || !pAtendimento.isValid() && !pAtendimento.getPessoa().isValid() && BasicFunctions.isEmpty(pAtendimento.getAvaliacao()) && BasicFunctions.isInvalid(pAtendimento.getDataAtendimento()) && BasicFunctions.isEmpty(pAtendimento.getEvolucaoSintomas())) {
            responses.setStatus(400);
            responses.getMessages().add("Informe os dados para atualizar o cadastro do Atendimento.");
        }
    }
}
