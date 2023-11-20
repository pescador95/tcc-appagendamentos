package app.agendamento.resources.agendamento;

import app.agendamento.controller.agendamento.AtendimentoController;
import app.agendamento.model.agendamento.Atendimento;
import app.core.model.DTO.Responses;
import app.core.utils.BasicFunctions;
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
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static app.agendamento.filters.atendimento.AtendimentoFilters.makeAtendimentoQueryStringByFilters;

@SuppressWarnings("ALL")
@Path("/atendimento")
public class AtendimentoResources {

    @Inject
    AtendimentoController controller;
    Atendimento atendimento;
    Responses responses;

    private String query;

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response getById(@PathParam("id") Long pId) {
        atendimento = Atendimento.findById(pId);
        return Response.ok(atendimento).status(200).build();
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response count(@QueryParam("ativo") @DefaultValue("true") Boolean ativo) {
        query = "ativo = " + ativo;
        long count = Atendimento.count(query);
        return Response.ok(count).status(200).build();
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response list(
            @QueryParam("dataAtendimento") LocalDate dataAtendimento,
            @QueryParam("dataInicio") LocalDate dataInicio,
            @QueryParam("dataFim") LocalDate dataFim,
            @QueryParam("pessoaId") Long pessoaId,
            @QueryParam("usuarioId") String usuarioId,
            @QueryParam("atividade") String atividade,
            @QueryParam("evolucaoSintomas") String evolucaoSintomas,
            @QueryParam("avaliacao") String avaliacao,
            @QueryParam("sort") @DefaultValue("desc") @NotNull String sortQuery,
            @QueryParam("page") @DefaultValue("0") int pageIndex,
            @QueryParam("size") @DefaultValue("20") int pageSize,
            @QueryParam("ativo") @DefaultValue("true") Boolean ativo,
            @QueryParam("strgOrder") @DefaultValue("id") String strgOrder, @Context @NotNull SecurityContext context) {
        String queryString = makeAtendimentoQueryStringByFilters(dataAtendimento, dataInicio, dataFim,
                usuarioId, atividade, evolucaoSintomas, avaliacao);
        query = "ativo = " + ativo + " " + queryString + " order by " + strgOrder + " " + sortQuery;
        PanacheQuery<Atendimento> atendimento;
        atendimento = Atendimento.find(query);

        List<Atendimento> atendimentoFiltrados = atendimento.page(Page.of(pageIndex, pageSize)).list()
                .stream()
                .filter(x -> BasicFunctions.isEmpty(pessoaId) || x.getPessoa().getId().equals(pessoaId))
                .collect(Collectors.toList());

        return Response.ok(atendimentoFiltrados).status(200).build();
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response add(@QueryParam("agendamentoId") Long agendamentoId,
                        Atendimento pAtendimento,
                        @Context @NotNull SecurityContext context) {
        try {

            return controller.addAtendimento(pAtendimento, agendamentoId);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            responses.getMessages().add("Não foi possível cadastrar o Atendimento.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @PUT
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response update(@QueryParam("agendamentoId") Long agendamentoId,
                           Atendimento pAtendimento,
                           @Context @NotNull SecurityContext context) {
        try {

            return controller.updateAtendimento(pAtendimento, agendamentoId);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            responses.getMessages().add("Não foi possível atualizar o Atendimento.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @DELETE
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response deleteList(List<Long> pListIdAtendimento, @Context @NotNull SecurityContext context) {
        try {

            return controller.deleteAtendimento(pListIdAtendimento);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            if (pListIdAtendimento.size() <= 1) {
                responses.getMessages().add("Não foi possível excluir o Atendimento.");
            } else {
                responses.getMessages().add("Não foi possível excluir os Atendimentos.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @PUT
    @Path("/reactivate")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response reactivateList(List<Long> pListIdAtendimento, @Context @NotNull SecurityContext context) {
        try {

            return controller.reactivateAtendimento(pListIdAtendimento);
        } catch (Exception e) {
            if (pListIdAtendimento.size() <= 1) {
                responses = new Responses();

                responses.setStatus(400);
                responses.getMessages().add("Não foi possível reativar o Atendimento.");
            } else {
                responses = new Responses();

                responses.setStatus(400);
                responses.getMessages().add("Não foi possível reativar os Atendimentos.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }
}
