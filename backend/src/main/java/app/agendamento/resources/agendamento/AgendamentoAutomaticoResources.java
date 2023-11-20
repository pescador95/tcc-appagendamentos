package app.agendamento.resources.agendamento;

import app.agendamento.DTO.agendamento.AgendamentoDTO;
import app.agendamento.controller.agendamento.AgendamentoAutomaticoController;
import app.agendamento.controller.agendamento.AgendamentoController;
import app.agendamento.model.agendamento.Agendamento;
import app.agendamento.model.pessoa.Usuario;
import app.core.model.DTO.Responses;
import app.core.utils.BasicFunctions;
import app.core.utils.Contexto;
import org.jetbrains.annotations.NotNull;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Path("/agendamento")
public class AgendamentoAutomaticoResources {

    @Inject
    AgendamentoController controller;
    @Inject
    AgendamentoAutomaticoController agendamentoAutomaticoController;

    Responses responses;
    Usuario usuarioAuth;

    @POST
    @Path("/bot/listar")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @PermitAll
    public Response listFreeAppointmentByUsuario(@QueryParam("sort") @DefaultValue("desc") @NotNull String sortQuery,
                                                 @QueryParam("page") @DefaultValue("0") int pageIndex,
                                                 @QueryParam("size") @DefaultValue("20") int pageSize,
                                                 @QueryParam("ativo") @DefaultValue("true") Boolean ativo,
                                                 @QueryParam("reagendar") @DefaultValue("false") Boolean reagendar,
                                                 @QueryParam("strgOrder") @DefaultValue("id") String strgOrder,
                                                 Agendamento pAgendamento,
                                                 @Context @NotNull SecurityContext context) {

        usuarioAuth = Contexto.getContextUser(context);

        return agendamentoAutomaticoController.listAgendamentosLivres(pAgendamento, pAgendamento.comPreferencia(), reagendar);
    }

    @POST
    @Path("/bot/listar/meusAgendamentos")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @PermitAll
    public Response listAppointmentsByUsuario(@QueryParam("sort") @DefaultValue("desc") @NotNull String sortQuery,
                                              @QueryParam("page") @DefaultValue("0") int pageIndex,
                                              @QueryParam("size") @DefaultValue("20") int pageSize,
                                              @QueryParam("ativo") @DefaultValue("true") Boolean ativo,
                                              @QueryParam("reagendar") @DefaultValue("false") Boolean reagendar,
                                              @QueryParam("strgOrder") @DefaultValue("id") String strgOrder, @Context @NotNull SecurityContext context,
                                              Agendamento pAgendamento) {
        List<Agendamento> agendamentos = Agendamento.list("pessoaId = ?1 and dataAgendamento >= ?2",
                pAgendamento.getPessoaAgendamento().getId(), Contexto.dataContexto());
        agendamentos.removeIf(x -> BasicFunctions.isNotEmpty(x.getStatusAgendamento()) && !x.getStatusAgendamento().agendado());
        if (reagendar) {
            agendamentos.removeIf(x -> BasicFunctions.isNotEmpty(x.getAgendamentoOld()));
        }
        List<AgendamentoDTO> agendamentosAux = AgendamentoDTO.makeListAgendamentoDTO(agendamentos);
        return Response.ok(agendamentosAux).status(200).build();
    }

    @POST
    @Path("/bot/marcar")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @PermitAll
    public Response schedule(Agendamento pAgendamento, @Context @NotNull SecurityContext context) {
        try {

            Boolean dataValida = agendamentoAutomaticoController.validarDataAgendamento(pAgendamento, Boolean.FALSE);

            if (validarDataAgendamento(pAgendamento, dataValida))
                return Response.ok(responses).status(responses.getStatus()).build();
            return controller.marcarAgendamento(pAgendamento);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            responses.setOk(Boolean.FALSE);
            responses.setMessages(new ArrayList<>());
            responses.getMessages().add("Não foi possível cadastrar a Agendamento.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    private boolean validarDataAgendamento(Agendamento pAgendamento, Boolean dataValida) {
        if (!dataValida) {
            responses = new Responses();
            responses.setMessages(new ArrayList<>());

            responses.setStatus(400);
            responses.setOk(Boolean.FALSE);
            responses.setData(pAgendamento);
            responses.getMessages().add("Não será possível agendar na data " + pAgendamento.getDataAgendamento());
            return true;
        }
        return false;
    }

    @POST
    @Path("/bot/remarcar")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @PermitAll
    public Response reschedule(List<Agendamento> pListAgendamento, @Context @NotNull SecurityContext context) {
        try {
            Agendamento agendamentoNew = new Agendamento();

            for (Agendamento agendamento : pListAgendamento) {
                if (!agendamento.isValid()) {
                    agendamentoNew = agendamento;
                }
            }
            Boolean dataValida = agendamentoAutomaticoController.validarDataAgendamento(agendamentoNew, Boolean.TRUE);

            if (validarDataAgendamento(agendamentoNew, dataValida))
                return Response.ok(responses).status(responses.getStatus()).build();
            return controller.remarcarAgendamento(pListAgendamento);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            responses.setOk(Boolean.FALSE);
            responses.setMessages(new ArrayList<>());
            responses.getMessages().add("Não foi possível remarcar o Agendamento.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @POST
    @Path("/bot/verificarData")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @PermitAll
    public Response checkDate(Agendamento pAgendamento,
                              @QueryParam("reagendar") @DefaultValue("false") Boolean reagendar,
                              @Context @NotNull SecurityContext context) {
        try {

            Boolean dataValida = agendamentoAutomaticoController.validarDataAgendamento(pAgendamento, reagendar);

            if (validarDataAgendamento(pAgendamento, dataValida))
                return Response.ok(responses).status(responses.getStatus()).build();
            responses = new Responses();

            responses.setStatus(200);
            responses.setOk(Boolean.TRUE);
            responses.setData(pAgendamento);
            responses.setMessages(new ArrayList<>());
            responses.getMessages().add("a data está disponível.");
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            responses.setOk(Boolean.FALSE);
            responses.setMessages(new ArrayList<>());
            responses.getMessages().add("Não foi possível verificar a data do Agendamento.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @GET
    @Path("/teste")
    @PermitAll
    public Response testeReq() {
        Responses responses = new Responses();
        responses.getMessages().add("teste ok com o 400.");
        responses.setStatus(400);
        responses.setOk(false);
        return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
    }

    @GET
    @Path("/teste-ip")
    @PermitAll
    public Response testeHeaderIp(@HeaderParam("X-Forwarded-For") String forwardedFor,
                                  @HeaderParam("X-Real-IP") String ip,
                                  @HeaderParam("X-Original-Forwarded-For") String forv2) {
        //TODO verificar nginx
        var result = new HashMap<String, Object>();
        result.put("ip",forwardedFor);
        result.put("ip2",ip);
        result.put("ip3",forv2);
        return Response.ok(result).build();
    }

}
