package app.agendamento.resources.agendamento;

import app.agendamento.controller.agendamento.TipoAgendamentoController;
import app.agendamento.model.agendamento.TipoAgendamento;
import app.agendamento.model.organizacao.Organizacao;
import app.core.model.DTO.Responses;
import app.core.utils.BasicFunctions;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import org.jetbrains.annotations.NotNull;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static app.agendamento.filters.tipoAgendamento.TipoAgendamentoFilters.makeTipoAgendamentoQueryStringByFilters;

@Path("/tipoAgendamento")
public class TipoAgendamentoResources {

    @Inject
    TipoAgendamentoController controller;
    TipoAgendamento tipoAgendamento;
    Responses responses;
    private String query;

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})

    public Response getById(@PathParam("id") Long pId) {
        tipoAgendamento = TipoAgendamento.findById(pId);
        return Response.ok(tipoAgendamento).status(200).build();
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response count() {
        query = "id > 0";
        long count = TipoAgendamento.count(query);
        return Response.ok(count).status(200).build();
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response list(
            @QueryParam("id") Long id,
            @QueryParam("tipoAgendamento") String tipoAgendamento,
            @QueryParam("organizacaoId") List<Long> organizacaoId,
            @QueryParam("sort") @DefaultValue("desc") @NotNull String sortQuery,
            @QueryParam("page") @DefaultValue("0") int pageIndex,
            @QueryParam("size") @DefaultValue("20") int pageSize,
            @QueryParam("ativo") @DefaultValue("true") Boolean ativo,
            @QueryParam("strgOrder") @DefaultValue("id") String strgOrder) {
        String queryString = makeTipoAgendamentoQueryStringByFilters(id, tipoAgendamento);
        query = "id > 0" + " " + queryString + " order by " + strgOrder + " " + sortQuery;
        PanacheQuery<TipoAgendamento> tipoAgendamentos;
        tipoAgendamentos = TipoAgendamento.find(query);

        List<TipoAgendamento> tiposAgendamentosFiltrados = tipoAgendamentos.page(Page.of(pageIndex, pageSize)).list()
                .stream()
                .filter(x -> BasicFunctions.isValid(organizacaoId)
                        || new HashSet<>(x.getOrganizacoes().stream().map(Organizacao::getId)
                        .collect(Collectors.toList()))
                        .containsAll(organizacaoId))
                .collect(Collectors.toList());

        return Response.ok(tiposAgendamentosFiltrados).status(200).build();
    }

    @GET
    @Path("/bot")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @PermitAll
    public Response listByScheduler(@QueryParam("organizacoes") @NotNull List<Long> organizacoes,
                                    @QueryParam("sort") @DefaultValue("desc") @NotNull String sortQuery,
                                    @QueryParam("page") @DefaultValue("0") int pageIndex,
                                    @QueryParam("size") @DefaultValue("20") int pageSize,
                                    @QueryParam("ativo") @DefaultValue("true") Boolean ativo,
                                    @QueryParam("strgOrder") @DefaultValue("id") String strgOrder) {

        List<TipoAgendamento> tiposAgendamentosFiltrados = controller.tiposAgendamentosByOrganizacaoId(organizacoes);

        return Response.ok(tiposAgendamentosFiltrados).status(200).build();
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response add(TipoAgendamento pTipoAgendamento, @Context @NotNull SecurityContext context) {
        try {
            return controller.addTipoAgendamento(pTipoAgendamento);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            responses.getMessages().add("Não foi possível cadastrar o Tipo de Agendamento.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @PUT
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})

    public Response update(TipoAgendamento pTipoAgendamento, @Context @NotNull SecurityContext context) {
        try {
            return controller.updateTipoAgendamento(pTipoAgendamento);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            responses.getMessages().add("Não foi possível atualizar o Tipo de Agendamento.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @DELETE
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response deleteList(List<Long> pListIdTipoAgendamento, @Context @NotNull SecurityContext context) {
        try {
            return controller.deleteTipoAgendamento(pListIdTipoAgendamento);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            if (pListIdTipoAgendamento.size() <= 1) {
                responses.getMessages().add("Não foi possível excluir o Tipo de Agendamento.");
            } else {
                responses.getMessages().add("Não foi possível excluir os Tipos de Agendamentos.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }
}
