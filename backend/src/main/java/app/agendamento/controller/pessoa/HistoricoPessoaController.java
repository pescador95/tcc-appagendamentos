package app.agendamento.controller.pessoa;

import app.agendamento.model.pessoa.HistoricoPessoa;
import app.agendamento.model.pessoa.Pessoa;
import app.agendamento.model.pessoa.Usuario;
import app.core.model.DTO.Responses;
import app.core.utils.BasicFunctions;
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
public class HistoricoPessoaController {

    @Context
    SecurityContext context;
    private HistoricoPessoa historicoPessoa = new HistoricoPessoa();
    private Responses responses;
    private Usuario usuarioAuth;
    private Pessoa pessoa;

    public Response addHistoricoPessoa(@NotNull HistoricoPessoa pHistoricoPessoa) {

        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        loadByHistoricoPessoaByPessoa(pHistoricoPessoa);

        if (BasicFunctions.isEmpty(historicoPessoa) && BasicFunctions.isNotEmpty(pessoa)) {

            historicoPessoa = new HistoricoPessoa();

            loadByHistoricoPessoa(pHistoricoPessoa);

            if (!responses.hasMessages()) {

                historicoPessoa = new HistoricoPessoa(pHistoricoPessoa, pessoa, context);

                historicoPessoa.persist();

                responses.setStatus(201);
                responses.setData(historicoPessoa);
                responses.getMessages().add("HistoricoPessoa cadastrado com sucesso!");

            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } else {

            responses.setStatus(400);
            responses.setData(historicoPessoa);
            responses.getMessages().add("HistoricoPessoa já cadastrada!");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    public Response updateHistoricoPessoa(@NotNull HistoricoPessoa pHistoricoPessoa) {

        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        try {

            loadHistoricoPessoaById(pHistoricoPessoa);

            if (BasicFunctions.isNotEmpty(historicoPessoa)) {
                loadByHistoricoPessoa(pHistoricoPessoa);
            }

            if (!responses.hasMessages()) {

                historicoPessoa = historicoPessoa.historicoPessoa(historicoPessoa, pHistoricoPessoa, pessoa, context);

                historicoPessoa.persist();

                responses.setStatus(200);
                responses.setData(historicoPessoa);
                responses.getMessages().add("Cadastro de HistoricoPessoa atualizado com sucesso!");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            responses.setData(historicoPessoa);
            responses.getMessages().add("Não foi possível atualizar o cadastro HistoricoPessoa.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    public Response deleteHistoricoPessoa(@NotNull List<Long> pListIdHistoricoPessoa) {

        List<HistoricoPessoa> historicoPessoas;
        List<HistoricoPessoa> clientesAuxes = new ArrayList<>();
        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        historicoPessoas = HistoricoPessoa.list("id in ?1 and ativo = true", pListIdHistoricoPessoa);
        int count = historicoPessoas.size();

        try {

            if (historicoPessoas.isEmpty()) {

                responses.setStatus(400);
                responses.getMessages().add("Históricos não localizados ou já excluídos.");
                return Response.ok(responses).status(responses.getStatus()).build();
            }

            historicoPessoas.forEach((historicoPessoa) -> {

                HistoricoPessoa historicoPessoaDeleted = historicoPessoa.deletarHistoricoPessoa(historicoPessoa, context);

                historicoPessoaDeleted.persist();
                clientesAuxes.add(historicoPessoaDeleted);
            });

            responses.setStatus(200);
            if (count <= 1) {
                responses.setData(historicoPessoa);
                responses.getMessages().add("Histórico excluído com sucesso!");
            } else {
                responses.setDatas(Collections.singletonList(clientesAuxes));
                responses.getMessages().add(count + " Históricos excluídos com sucesso!");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            if (count <= 1) {
                responses.setData(historicoPessoa);
                responses.getMessages().add("Histórico não localizada ou já excluído.");
            } else {
                responses.setDatas(Collections.singletonList(historicoPessoas));
                responses.getMessages().add("Históricos não localizadas ou já excluídos.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    public Response reactivateHistoricoPessoa(@NotNull List<Long> pListIdHistoricoPessoa) {

        List<HistoricoPessoa> historicoPessoas;
        List<HistoricoPessoa> historicoPessoasAux = new ArrayList<>();
        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        historicoPessoas = HistoricoPessoa.list("id in ?1 and ativo = false", pListIdHistoricoPessoa);
        int count = historicoPessoas.size();

        if (historicoPessoas.isEmpty()) {

            responses.setStatus(400);
            responses.getMessages().add("Históricos não localizados ou já reativados.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }

        try {
            historicoPessoas.forEach((historicoPessoa) -> {

                HistoricoPessoa historicoPessoaReactivated = historicoPessoa.reativarHistoricoPessoa(historicoPessoa, context);

                historicoPessoaReactivated.persist();
                historicoPessoasAux.add(historicoPessoaReactivated);
            });

            responses.setStatus(200);
            if (count <= 1) {
                responses.setData(historicoPessoa);
                responses.getMessages().add("Histórico reativado com sucesso!");
            } else {
                responses.setDatas(Collections.singletonList(historicoPessoasAux));
                responses.getMessages().add(count + " Históricos reativados com sucesso!");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            if (count <= 1) {
                responses.setData(historicoPessoa);
                responses.getMessages().add("Histórico não localizada ou já reativado.");
            } else {
                responses.setDatas(Collections.singletonList(historicoPessoas));
                responses.getMessages().add("Históricos não localizadas ou já reativados.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    private void loadByHistoricoPessoa(HistoricoPessoa pHistoricoPessoa) {

        pessoa = new Pessoa();

        if (BasicFunctions.isNotEmpty(pHistoricoPessoa) && BasicFunctions.isNotEmpty(pHistoricoPessoa.getPessoa())
                && pHistoricoPessoa.getPessoa().isValid()) {
            pessoa = Pessoa.findById(pHistoricoPessoa.getPessoa().getId());
        }
        validarHistoricoPessoa(pHistoricoPessoa);
    }

    private void loadByHistoricoPessoaByPessoa(HistoricoPessoa pHistoricoPessoa) {

        historicoPessoa = new HistoricoPessoa();

        if (BasicFunctions.isNotEmpty(pHistoricoPessoa)) {
            historicoPessoa = HistoricoPessoa.find("pessoa = ?1 and ativo = true", pHistoricoPessoa.getPessoa()).firstResult();
        }
        validarHistoricoPessoa(pHistoricoPessoa);
    }

    private void loadHistoricoPessoaById(HistoricoPessoa pHistoricoPessoa) {

        historicoPessoa = new HistoricoPessoa();

        if (BasicFunctions.isNotEmpty(pHistoricoPessoa)) {
            historicoPessoa = HistoricoPessoa.find("id = ?1 and ativo = true", pHistoricoPessoa.getId()).firstResult();
        }
        validarHistoricoPessoa(pHistoricoPessoa);
    }

    private void validarHistoricoPessoa(HistoricoPessoa pHistoricoPessoa) {
        if (!pHistoricoPessoa.isValid() && BasicFunctions.isEmpty(pHistoricoPessoa.getPessoa())
                && BasicFunctions.isEmpty(pHistoricoPessoa.getQueixaPrincipal())
                && BasicFunctions.isEmpty(pHistoricoPessoa.getMedicamentos())
                && BasicFunctions.isEmpty(pHistoricoPessoa.getDiagnosticoClinico())
                && BasicFunctions.isEmpty(pHistoricoPessoa.getComorbidades())
                && BasicFunctions.isEmpty(pHistoricoPessoa.getOcupacao())
                && BasicFunctions.isEmpty(pHistoricoPessoa.getResponsavelContato())) {
            responses.setStatus(400);
            responses.getMessages().add("Informe os dados para atualizar o cadastro do HistoricoPessoa.");
        }
    }


}
