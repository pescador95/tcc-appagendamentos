package app.agendamento.resources.pessoa;

import app.agendamento.controller.pessoa.HistoricoPessoaController;
import app.agendamento.model.pessoa.HistoricoPessoa;
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
import java.util.stream.Collectors;

import static app.agendamento.filters.pessoa.HistoricoPessoaFilters.makeHistoricoPessoaQueryStringByFilters;

@Path("/historicoPessoa")
public class HistoricoPessoaResources {

    @Inject
    HistoricoPessoaController controller;
    HistoricoPessoa historicoPessoa;
    Responses responses;

    private String query;

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response getById(@PathParam("id") Long pId) {
        historicoPessoa = HistoricoPessoa.findById(pId);
        return Response.ok(historicoPessoa).status(200).build();
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response count(@QueryParam("ativo") @DefaultValue("true") Boolean ativo) {
        query = "ativo = " + ativo;
        long count = HistoricoPessoa.count(query);
        return Response.ok(count).status(200).build();
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response list(
            @QueryParam("id") Long id,
            @QueryParam("queixaPrincipal") String queixaPrincipal,
            @QueryParam("medicamentos") String medicamentos,
            @QueryParam("diagnosticoClinico") String diagnosticoClinico,
            @QueryParam("comorbidades") String comorbidades,
            @QueryParam("ocupacao") String ocupacao,
            @QueryParam("responsavelContato") String responsavelContato,
            @QueryParam("nomePessoa") String nomePessoa,
            @QueryParam("sort") @DefaultValue("desc") @NotNull String sortQuery,
            @QueryParam("page") @DefaultValue("0") int pageIndex,
            @QueryParam("size") @DefaultValue("20") int pageSize,
            @QueryParam("ativo") @DefaultValue("true") Boolean ativo,
            @QueryParam("strgOrder") @DefaultValue("id") String strgOrder, @Context @NotNull SecurityContext context) {
        String queryString = makeHistoricoPessoaQueryStringByFilters(id, queixaPrincipal, medicamentos,
                diagnosticoClinico, comorbidades, ocupacao, responsavelContato, nomePessoa);
        query = "ativo = " + ativo + " " + queryString + " order by " + strgOrder + " " + sortQuery;
        PanacheQuery<HistoricoPessoa> historicoPessoa;
        historicoPessoa = HistoricoPessoa.find(query);
        List<HistoricoPessoa> historicoPessoaFiltrados = historicoPessoa.page(Page.of(pageIndex, pageSize)).list()
                .stream().filter(c -> (c.getAtivo().equals(ativo)))
                .collect(Collectors.toList());

        return Response.ok(historicoPessoaFiltrados).status(200).build();
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response add(HistoricoPessoa pHistoricoPessoa, @Context @NotNull SecurityContext context) {
        try {

            return controller.addHistoricoPessoa(pHistoricoPessoa);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            responses.getMessages().add("Não foi possível cadastrar o HistoricoPessoa.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @PUT
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response update(HistoricoPessoa pHistoricoPessoa, @Context @NotNull SecurityContext context) {
        try {

            return controller.updateHistoricoPessoa(pHistoricoPessoa);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            responses.getMessages().add("Não foi possível atualizar o HistoricoPessoa.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @DELETE
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response deleteList(List<Long> pListIdHistoricoPessoa, @Context @NotNull SecurityContext context) {
        try {

            return controller.deleteHistoricoPessoa(pListIdHistoricoPessoa);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            if (pListIdHistoricoPessoa.size() <= 1) {
                responses.getMessages().add("Não foi possível excluir o HistoricoPessoa.");
            } else {
                responses.getMessages().add("Não foi possível excluir os Clientes.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @PUT
    @Path("/reactivate")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response reactivateList(List<Long> pListIdCliente, @Context @NotNull SecurityContext context) {
        try {

            return controller.reactivateHistoricoPessoa(pListIdCliente);
        } catch (Exception e) {
            if (pListIdCliente.size() <= 1) {
                responses = new Responses();

                responses.setStatus(400);
                responses.getMessages().add("Não foi possível reativar o HistoricoPessoa.");
            } else {
                responses = new Responses();

                responses.setStatus(400);
                responses.getMessages().add("Não foi possível reativar os Clientes.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }
}
