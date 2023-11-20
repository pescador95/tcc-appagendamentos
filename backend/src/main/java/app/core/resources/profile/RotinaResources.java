package app.core.resources.profile;

import app.core.controller.profile.RotinaController;
import app.core.model.DTO.Responses;
import app.core.model.profile.Rotina;
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

import static app.core.filters.profile.RotinaFilters.makeRotinaQueryStringByFilters;

@Path("/rotina")
public class RotinaResources {

    @Inject
    RotinaController controller;
    Rotina rotina;
    Responses responses;
    private String query;

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response getById(@PathParam("id") Long pId) {
        rotina = Rotina.findById(pId);
        return Response.ok(rotina).status(200).build();
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})

    public Response count(@QueryParam("ativo") @DefaultValue("true") Boolean ativo,
                          @Context @NotNull SecurityContext context) {
        query = "id > 0 ";
        long count = Rotina.count(query);
        return Response.ok(count).status(200).build();
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response list(
            @QueryParam("id") Long id,
            @QueryParam("nome") String nome,
            @QueryParam("icon") String icon,
            @QueryParam("path") String path,
            @QueryParam("titulo") String titulo,
            @QueryParam("sort") @DefaultValue("desc") @NotNull String sortQuery,
            @QueryParam("page") @DefaultValue("0") int pageIndex,
            @QueryParam("size") @DefaultValue("20") int pageSize,
            @QueryParam("strgOrder") @DefaultValue("id") String strgOrder, @Context @NotNull SecurityContext context) {
        String queryString = makeRotinaQueryStringByFilters(id, nome, icon, path, titulo);
        query = "id > 0 " + queryString + " order by " + strgOrder + " " + sortQuery;
        PanacheQuery<Rotina> rotinas;
        rotinas = Rotina.find(query);

        List<Rotina> rotinaFiltrados = rotinas.page(Page.of(pageIndex, pageSize)).list();

        return Response.ok(rotinaFiltrados).status(200).build();
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response add(Rotina pRotina, @Context @NotNull SecurityContext context) {
        try {

            return controller.addRotina(pRotina);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            responses.getMessages().add("Não foi possível cadastrar a Rotina.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @PUT
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response update(Rotina pRotina, @Context @NotNull SecurityContext context) {
        try {

            return controller.updateRotina(pRotina);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            responses.getMessages().add("Não foi possível atualizar a Rotina.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @DELETE
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response deleteList(List<Long> pListRotina, @Context @NotNull SecurityContext context) {
        try {

            return controller.deleteRotina(pListRotina);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            if (pListRotina.size() <= 1) {
                responses.getMessages().add("Não foi possível excluir a Rotina.");
            } else {
                responses.getMessages().add("Não foi possível excluir as Rotinas.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

}
