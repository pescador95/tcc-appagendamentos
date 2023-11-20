package app.core.resources.auth;

import app.core.controller.auth.RoleController;
import app.core.model.DTO.Responses;
import app.core.model.auth.Role;
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

import static app.core.filters.auth.RoleFilters.makeRoleQueryStringByFilters;

@Path("/role")
public class RoleResources {

    Role role;
    @Inject
    RoleController controller;
    private Responses responses;

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response getById(@PathParam("id") Long pId) {
        role = Role.findById(pId);
        return Response.ok(role).status(200).build();
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response count(@QueryParam("ativo") @DefaultValue("true") Boolean ativo) {
        long count = Role.count();
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
            @QueryParam("privilegio") String privilegio,
            @QueryParam("admin") Boolean admin,
            @QueryParam("usuarioId") Long usuarioId,
            @QueryParam("sort") @DefaultValue("desc") @NotNull String sortQuery,
            @QueryParam("page") @DefaultValue("0") int pageIndex,
            @QueryParam("size") @DefaultValue("20") int pageSize,
            @QueryParam("ativo") @DefaultValue("true") Boolean ativo,
            @QueryParam("strgOrder") @DefaultValue("id") String strgOrder, @Context @NotNull SecurityContext context) {

        String queryString = makeRoleQueryStringByFilters(id, nome, privilegio, admin, usuarioId);
        String query = "id > 0" + " " + queryString + " order by " + strgOrder + " " + sortQuery;
        PanacheQuery<Role> roles;
        roles = Role.find(query);
        return Response.ok(roles.page(Page.of(pageIndex, pageSize)).list()).status(200).build();
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response add(Role pRole, @Context @NotNull SecurityContext context) {
        try {
            return controller.addRole(pRole);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            responses.getMessages().add("Não foi possível cadastrar a Role.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @PUT
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response update(Role pRole, @Context @NotNull SecurityContext context) {
        try {
            return controller.updateRole(pRole);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            responses.getMessages().add("Não foi possível atualizar a Role.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @DELETE
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response deleteList(List<Long> pListIdRole, @Context @NotNull SecurityContext context) {
        try {

            return controller.deleteRole(pListIdRole);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            if (pListIdRole.size() <= 1) {
                responses.getMessages().add("Não foi possível excluir a Role.");
            } else {
                responses.getMessages().add("Não foi possível excluir as Roles.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }
}
