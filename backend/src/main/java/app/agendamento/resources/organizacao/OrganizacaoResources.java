package app.agendamento.resources.organizacao;

import app.agendamento.controller.organizacao.OrganizacaoController;
import app.agendamento.model.organizacao.Organizacao;
import app.core.model.DTO.Responses;
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
import java.util.List;

import static app.agendamento.filters.organizacao.OrganizacaoFilters.makeOrganizacaoQueryStringByFilters;

@Path("/organizacao")
public class OrganizacaoResources {

    @Inject
    OrganizacaoController controller;
    Organizacao organizacao;
    Responses responses;

    private String query;

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario", "bot"})

    public Response getById(@PathParam("id") Long pId) {
        organizacao = Organizacao.findById(pId);
        return Response.ok(organizacao).status(200).build();
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario", "bot"})
    public Response count(@QueryParam("ativo") @DefaultValue("true") Boolean ativo) {

        query = "ativo = " + ativo;
        long count = Organizacao.count(query);
        return Response.ok(count).status(200).build();
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario", "bot"})
    public Response list(
            @QueryParam("nome") String nome,
            @QueryParam("cnpj") String cnpj,
            @QueryParam("telefone") String telefone,
            @QueryParam("celular") String celular,
            @QueryParam("email") String email,
            @QueryParam("enderecoId") Long enderecoId,
            @QueryParam("tipoAgendamento_Id") Long tipoAgendamento_Id,
            @QueryParam("sort") @DefaultValue("desc") @NotNull String sortQuery,
            @QueryParam("page") @DefaultValue("0") int pageIndex,
            @QueryParam("size") @DefaultValue("20") int pageSize,
            @QueryParam("ativo") @DefaultValue("true") Boolean ativo,
            @QueryParam("strgOrder") @DefaultValue("id") String strgOrder) {
        String queryString = makeOrganizacaoQueryStringByFilters(nome, cnpj, telefone, celular, email, enderecoId,
                tipoAgendamento_Id);
        query = "ativo = " + ativo + " " + queryString + " order by " + strgOrder + " " + sortQuery;
        PanacheQuery<Organizacao> organizacao;
        organizacao = Organizacao.find(query);
        return Response.ok(organizacao.page(Page.of(pageIndex, pageSize)).list()).status(200).build();
    }

    @GET
    @Path("/bot/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @PermitAll
    public Response listByBot(@QueryParam("sort") @DefaultValue("desc") @NotNull String sortQuery,
                              @QueryParam("page") @DefaultValue("0") int pageIndex,
                              @QueryParam("size") @DefaultValue("20") int pageSize,
                              @QueryParam("ativo") @DefaultValue("true") Boolean ativo,
                              @QueryParam("strgOrder") @DefaultValue("id") String strgOrder) {
        String queryString = ""; // makeOrganizacaoQueryStringByFilters();
        query = "ativo = " + ativo + " " + queryString + " order by " + strgOrder + " " + sortQuery;
        PanacheQuery<Organizacao> organizacao;
        organizacao = Organizacao.find(query);
        return Response.ok(organizacao.page(Page.of(pageIndex, pageSize)).list()).status(200).build();
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario", "bot"})
    public Response add(Organizacao pOrganizacao, @Context @NotNull SecurityContext context) {
        try {

            return controller.addOrganizacao(pOrganizacao);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            responses.getMessages().add("Não foi possível cadastrar a Organização.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @PUT
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario", "bot"})

    public Response update(Organizacao pOrganizacao, @Context @NotNull SecurityContext context) {
        try {

            return controller.updateOrganizacao(pOrganizacao);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            responses.getMessages().add("Não foi possível atualizar a Organização.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @DELETE
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario", "bot"})
    public Response deleteList(List<Long> pListIdOrganizacao, @Context @NotNull SecurityContext context) {
        try {

            return controller.deleteOrganizacao(pListIdOrganizacao);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            if (pListIdOrganizacao.size() <= 1) {
                responses.getMessages().add("Não foi possível excluir a Organização.");
            } else {
                responses.getMessages().add("Não foi possível excluir as Organizações.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @PUT
    @Path("/reactivate")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario", "bot"})
    public Response reactivateList(List<Long> pListIdOrganizacao, @Context @NotNull SecurityContext context) {
        try {

            return controller.reactivateOrganizacao(pListIdOrganizacao);
        } catch (Exception e) {
            if (pListIdOrganizacao.size() <= 1) {
                responses = new Responses();

                responses.setStatus(400);
                responses.getMessages().add("Não foi possível reativar a Organização.");
            } else {
                responses = new Responses();

                responses.setStatus(400);
                responses.getMessages().add("Não foi possível reativar as Organizações.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }
}
