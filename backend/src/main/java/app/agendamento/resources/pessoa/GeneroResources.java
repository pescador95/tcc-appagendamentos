package app.agendamento.resources.pessoa;

import app.agendamento.controller.pessoa.GeneroController;
import app.agendamento.model.pessoa.Genero;
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

import static app.agendamento.filters.pessoa.GeneroFilters.makeGeneroQueryStringByFilters;

@Path("/genero")
public class GeneroResources {

    @Inject
    GeneroController controller;
    Genero genero;
    Responses responses;
    private String query;

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})

    public Response getById(@PathParam("id") Long pId) {
        genero = Genero.findById(pId);
        return Response.ok(genero).status(200).build();
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response count(@QueryParam("ativo") @DefaultValue("true") Boolean ativo) {
        query = "id > 0";
        long count = Genero.count(query);
        return Response.ok(count).status(200).build();
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response list(
            @QueryParam("id") Long id,
            @QueryParam("genero") String genero,
            @QueryParam("sort") @DefaultValue("desc") @NotNull String sortQuery,
            @QueryParam("page") @DefaultValue("0") int pageIndex,
            @QueryParam("size") @DefaultValue("20") int pageSize,
            @QueryParam("ativo") @DefaultValue("true") Boolean ativo,
            @QueryParam("strgOrder") @DefaultValue("id") String strgOrder) {
        String queryString = makeGeneroQueryStringByFilters(id, genero);
        query = "id > 0" + " " + queryString + " order by " + strgOrder + " " + sortQuery;
        PanacheQuery<Genero> generos;
        generos = Genero.find(query);
        return Response.ok(generos.page(Page.of(pageIndex, pageSize)).list()).status(200).build();
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response add(Genero pGenero, @Context @NotNull SecurityContext context) {
        try {
            return controller.addGenero(pGenero);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            responses.getMessages().add("Não foi possível cadastrar o Gênero.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @PUT
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})

    public Response update(Genero pGenero, @Context @NotNull SecurityContext context) {
        try {
            return controller.updateGenero(pGenero);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            responses.getMessages().add("Não foi possível atualizar o Gênero.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @DELETE
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response deleteList(List<Long> pListIdGenero, @Context @NotNull SecurityContext context) {
        try {
            return controller.deleteGenero(pListIdGenero);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            if (pListIdGenero.size() <= 1) {
                responses.getMessages().add("Não foi possível excluir o Gênero.");
            } else {
                responses.getMessages().add("Não foi possível excluir os Gêneros.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }
}
