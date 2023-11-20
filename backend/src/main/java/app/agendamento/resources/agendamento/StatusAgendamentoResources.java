package app.agendamento.resources.agendamento;

import app.agendamento.controller.agendamento.StatusAgendamentoController;
import app.agendamento.model.agendamento.StatusAgendamento;
import app.core.model.DTO.Responses;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import org.jetbrains.annotations.NotNull;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

import static app.agendamento.filters.statusAgendamento.StatusFilters.makeStatusAgendamentoQueryStringByFilters;

@Path("/statusAgendamento")
public class StatusAgendamentoResources {

    @Inject
    StatusAgendamentoController controller;
    StatusAgendamento statusAgendamento;
    Responses responses;
    private String query;

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})

    public Response getById(@PathParam("id") Long pId) {
        statusAgendamento = StatusAgendamento.findById(pId);
        return Response.ok(statusAgendamento).status(200).build();
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response count(@QueryParam("ativo") @DefaultValue("true") Boolean ativo) {
        query = "id > 0";
        long count = StatusAgendamento.count(query);
        return Response.ok(count).status(200).build();
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response list(
            @QueryParam("id") Long id,
            @QueryParam("status") String status,
            @QueryParam("sort") @DefaultValue("desc") @NotNull String sortQuery,
            @QueryParam("page") @DefaultValue("0") int pageIndex,
            @QueryParam("size") @DefaultValue("20") int pageSize,
            @QueryParam("ativo") @DefaultValue("true") Boolean ativo,
            @QueryParam("strgOrder") @DefaultValue("id") String strgOrder) {
        String queryString = makeStatusAgendamentoQueryStringByFilters(id, status);
        query = "id > 0" + " " + queryString + " order by " + strgOrder + " " + sortQuery;
        PanacheQuery<StatusAgendamento> statusAgendamento;
        statusAgendamento = StatusAgendamento.find(query);
        return Response.ok(statusAgendamento.page(Page.of(pageIndex, pageSize)).list()).status(200).build();
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response add(StatusAgendamento pStatusAgendamento, @Context @NotNull SecurityContext context) {
        try {
            return controller.addStatusAgendamento(pStatusAgendamento);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            responses.getMessages().add("Não foi possível cadastrar o Status de Agendamento.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @PUT
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})

    public Response update(StatusAgendamento pStatusAgendamento, @Context @NotNull SecurityContext context) {
        try {
            return controller.updateStatusAgendamento(pStatusAgendamento);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            responses.getMessages().add("Não foi possível atualizar o Status de Agendamento.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @DELETE
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response deleteList(List<Long> pListIdStatusAgendamento, @Context @NotNull SecurityContext context) {
        try {
            return controller.deleteStatusAgendamento(pListIdStatusAgendamento);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            if (pListIdStatusAgendamento.size() <= 1) {
                responses.getMessages().add("Não foi possível excluir o Status de Agendamento.");
            } else {
                responses.getMessages().add("Não foi possível excluir os Status de Agendamentos.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }
}
