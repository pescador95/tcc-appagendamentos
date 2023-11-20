package app.agendamento.controller.agendamento;

import app.agendamento.model.agendamento.StatusAgendamento;
import app.core.model.DTO.Responses;
import app.core.utils.BasicFunctions;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.jetbrains.annotations.NotNull;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
@Transactional
public class StatusAgendamentoController {

    private StatusAgendamento StatusAgendamento = new StatusAgendamento();

    private Responses responses;

    public Response addStatusAgendamento(@NotNull StatusAgendamento pStatusAgendamento) {

        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        loadStatusById(pStatusAgendamento);

        if (BasicFunctions.isEmpty(StatusAgendamento)) {
            StatusAgendamento = new StatusAgendamento();

            if (BasicFunctions.isNotEmpty(pStatusAgendamento.getStatus())) {
                StatusAgendamento.setStatus(pStatusAgendamento.getStatus());
            } else {
                responses.getMessages().add("Informe o Status do Agendamento a cadastrar.");
            }
            if (!responses.hasMessages()) {
                StatusAgendamento.persist();

                responses.setStatus(201);
                responses.setData(StatusAgendamento);
                responses.getMessages().add("Status do Agendamento cadastrado com sucesso!");

            } else {
                return Response.ok(responses).status(responses.getStatus()).build();
            }
            return Response.ok(responses).status(Response.Status.CREATED).build();
        } else {

            responses.setStatus(400);
            responses.setData(StatusAgendamento);
            responses.getMessages().add("Status do Agendamento já cadastrado!");
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }

    public Response updateStatusAgendamento(@NotNull StatusAgendamento pStatusAgendamento) {

        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        try {

            loadStatusById(pStatusAgendamento);

            if (!pStatusAgendamento.isValid() && BasicFunctions.isEmpty(pStatusAgendamento.getStatus())) {
                throw new BadRequestException("Informe os dados para atualizar o cadastro do Status do Agendamento.");
            } else {
                if (BasicFunctions.isNotEmpty(pStatusAgendamento.getStatus())) {
                    StatusAgendamento.setStatus(pStatusAgendamento.getStatus());
                }
                StatusAgendamento.persistAndFlush();

                responses.setStatus(200);
                responses.setData(StatusAgendamento);
                responses.getMessages().add("Status do Agendamento atualizado com sucesso!");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            responses.setData(StatusAgendamento);
            responses.getMessages().add("Não foi possível atualizar o cadastro de Status do Agendamento.");
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }

    public Response deleteStatusAgendamento(@NotNull List<Long> pListIdStatusAgendamento) {

        List<StatusAgendamento> statusAgendamentos;
        List<StatusAgendamento> statusAgendamentosAux = new ArrayList<>();
        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        statusAgendamentos = PanacheEntityBase.list("id in ?1", pListIdStatusAgendamento);
        int count = statusAgendamentos.size();

        try {

            if (statusAgendamentos.isEmpty()) {

                responses.setStatus(400);
                responses.getMessages().add("Status dos Agendamentos não localizados ou já excluídos.");
                return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
            }

            statusAgendamentos.forEach((StatusAgendamento) -> {
                StatusAgendamento.delete();
                statusAgendamentosAux.add(StatusAgendamento);
            });

            responses.setStatus(200);
            if (count <= 1) {
                responses.setData(StatusAgendamento);
                responses.getMessages().add("Status do Agendamento excluído com sucesso!");
            } else {
                responses.setDatas(Collections.singletonList(statusAgendamentosAux));
                responses.getMessages().add(count + " Status dos Agendamentos excluídos com sucesso!");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            if (count <= 1) {
                responses.setData(StatusAgendamento);
                responses.getMessages().add("Status do Agendamento não localizado ou já excluído.");
            } else {
                responses.setDatas(Collections.singletonList(statusAgendamentos));
                responses.getMessages().add("Status dos Agendamentos não localizados ou já excluídos.");
            }
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }

    private void loadStatusById(StatusAgendamento pStatusAgendamento) {

        StatusAgendamento = new StatusAgendamento();

        if (BasicFunctions.isNotEmpty(pStatusAgendamento.getStatus())) {
            StatusAgendamento = PanacheEntityBase.find("status = ?1 ", pStatusAgendamento.getStatus()).firstResult();
        }
    }
}
