package app.core.resources.contrato;

import app.core.controller.contrato.ContratoController;
import app.core.model.DTO.Responses;
import app.core.model.contrato.Contrato;
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

import static app.core.filters.contrato.ContratoFilters.makeContratoQueryStringByFilters;

@SuppressWarnings("RestParamTypeInspection")
@Path("/contrato")
public class ContratoResources {

    @Inject
    ContratoController controller;
    Contrato contrato;
    Responses responses;

    private String query;

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario", "bot"})

    public Response getById(@PathParam("id") Long pId) {
        contrato = Contrato.findById(pId);
        return Response.ok(contrato).status(200).build();
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario", "bot"})
    public Response count(@QueryParam("ativo") @DefaultValue("true") Boolean ativo) {

        query = "ativo = " + ativo;
        long count = Contrato.count(query);
        return Response.ok(count).status(200).build();
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario", "bot"})
    public Response list(
            @QueryParam("organizacaoContrato") Long organizacaoContrato,
            @QueryParam("responsavelContrato") Long responsavelContrato,
            @QueryParam("numeroMaximoSessoes") Integer numeroMaximoSessoes,
            @QueryParam("consideracoes") String consideracoes,
            @QueryParam("dataContrato") LocalDate dataContrato,
            @QueryParam("dataInicio") LocalDate dataInicio,
            @QueryParam("dataFim") LocalDate dataFim,
            @QueryParam("sort") @DefaultValue("desc") @NotNull String sortQuery,
            @QueryParam("page") @DefaultValue("0") int pageIndex,
            @QueryParam("size") @DefaultValue("20") int pageSize,
            @QueryParam("ativo") @DefaultValue("true") Boolean ativo,
            @QueryParam("strgOrder") @DefaultValue("id") String strgOrder) {
        String queryString = makeContratoQueryStringByFilters(organizacaoContrato, responsavelContrato,
                numeroMaximoSessoes,
                consideracoes, dataContrato,
                dataInicio,
                dataFim);
        query = "ativo = " + ativo + " " + queryString + " order by " + strgOrder + " " + sortQuery;
        PanacheQuery<Contrato> contrato;
        contrato = Contrato.find(query);
        return Response.ok(contrato.page(Page.of(pageIndex, pageSize)).list()).status(200).build();
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario", "bot"})
    public Response add(Contrato pContrato, @Context @NotNull SecurityContext context) {
        try {

            return controller.addContrato(pContrato);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            responses.getMessages().add("Não foi possível cadastrar o Contrato.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @PUT
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario", "bot"})

    public Response update(Contrato pContrato, @Context @NotNull SecurityContext context) {
        try {

            return controller.updateContrato(pContrato);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            responses.getMessages().add("Não foi possível atualizar o Contrato.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @DELETE
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario", "bot"})
    public Response deleteList(List<Long> pListIdContrato, @Context @NotNull SecurityContext context) {
        try {

            return controller.deleteContrato(pListIdContrato);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            if (pListIdContrato.size() <= 1) {
                responses.getMessages().add("Não foi possível excluir o Contrato.");
            } else {
                responses.getMessages().add("Não foi possível excluir os Contratos.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @PUT
    @Path("/reactivate")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario", "bot"})
    public Response reactivateList(List<Long> pListIdContrato, @Context @NotNull SecurityContext context) {
        try {

            return controller.reactivateContrato(pListIdContrato);
        } catch (Exception e) {
            if (pListIdContrato.size() <= 1) {
                responses = new Responses();

                responses.setStatus(400);
                responses.getMessages().add("Não foi possível reativar o Contrato.");
            } else {
                responses = new Responses();

                responses.setStatus(400);
                responses.getMessages().add("Não foi possível reativar os Contratos.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }
}
