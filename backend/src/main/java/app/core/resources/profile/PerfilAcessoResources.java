package app.core.resources.profile;

import app.core.controller.profile.PerfilAcessoController;
import app.core.model.DTO.Responses;
import app.core.model.profile.PerfilAcesso;
import app.core.model.profile.Rotina;
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
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static app.core.filters.profile.PerfilAcessoFilters.makePerfilAcessoQueryStringByFilters;

@Path("/perfilAcesso")
public class PerfilAcessoResources {

    @Inject
    PerfilAcessoController controller;
    PerfilAcesso perfilAcesso;
    Responses responses;

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response getById(@PathParam("id") Long pId) {
        perfilAcesso = PerfilAcesso.findById(pId);
        return Response.ok(perfilAcesso).status(200).build();
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})

    public Response count() {
        String query = "id > 0 ";
        Long count = PerfilAcesso.count(query);
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
            @QueryParam("criar") Boolean criar,
            @QueryParam("ler") Boolean ler,
            @QueryParam("atualizar") Boolean atualizar,
            @QueryParam("apagar") Boolean apagar,
            @QueryParam("usuarioId") Long usuarioId,
            @QueryParam("rotinaId") List<Long> rotinaId,
            @QueryParam("sort") @DefaultValue("desc") @NotNull String sortQuery,
            @QueryParam("page") @DefaultValue("0") int pageIndex,
            @QueryParam("size") @DefaultValue("20") int pageSize,
            @QueryParam("strgOrder") @DefaultValue("id") String strgOrder, @Context @NotNull SecurityContext context) {

        String queryString = makePerfilAcessoQueryStringByFilters(id, nome, criar, ler, atualizar, apagar, usuarioId);
        String query = "id > 0" + " " + queryString + " order by " + strgOrder + " " + sortQuery;
        PanacheQuery<PerfilAcesso> perfilAcesso;
        perfilAcesso = PerfilAcesso.find(query);

        List<PerfilAcesso> perfilAcessoFiltrados = perfilAcesso.page(Page.of(pageIndex, pageSize)).list()
                .stream()
                .filter(x -> BasicFunctions.isEmpty(rotinaId) || new HashSet<>(x.getRotinas().stream().map(Rotina::getId)
                        .collect(Collectors.toList()))
                        .containsAll(rotinaId))
                .collect(Collectors.toList());

        return Response.ok(perfilAcessoFiltrados).status(200).build();
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response add(PerfilAcesso pPerfilAcesso, @Context @NotNull SecurityContext context) {
        try {

            return controller.addPerfilAcesso(pPerfilAcesso);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            responses.getMessages().add("Não foi possível cadastrar o Perfil de Acesso.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @PUT
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response update(PerfilAcesso pPerfilAcesso, @Context @NotNull SecurityContext context) {
        try {

            return controller.updatePerfilAcesso(pPerfilAcesso);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            responses.getMessages().add("Não foi possível atualizar o Perfil de Acesso.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @DELETE
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response deleteList(List<Long> pListPerfilAcesso, @Context @NotNull SecurityContext context) {
        try {

            return controller.deletePerfilAcesso(pListPerfilAcesso);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            if (pListPerfilAcesso.size() <= 1) {
                responses.getMessages().add("Não foi possível excluir o Perfil de Acesso.");
            } else {
                responses.getMessages().add("Não foi possível excluir os Perfis de Acessos.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

}
