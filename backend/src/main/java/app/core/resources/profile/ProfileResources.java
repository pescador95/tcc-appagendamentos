package app.core.resources.profile;

import app.core.controller.profile.ProfileController;
import app.core.model.DTO.Responses;
import app.core.model.profile.MultiPartFormData;
import app.core.model.profile.Profile;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import org.jetbrains.annotations.NotNull;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.util.List;

@Path("uploads")
@Produces({MediaType.APPLICATION_JSON, MediaType.MULTIPART_FORM_DATA})
@Consumes({MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_JSON})

@Transactional
public class ProfileResources {

    @Inject
    ProfileController controller;
    Responses responses;

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response count(@QueryParam("ativo") @DefaultValue("true") Boolean ativo) {
        Long count = Profile.count();
        return Response.ok(count).status(responses.getStatus()).build();
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response listUploads(@QueryParam("sort") @DefaultValue("desc") @NotNull String sortQuery,
                                @QueryParam("ativo") @DefaultValue("true") Boolean ativo,
                                @QueryParam("page") @DefaultValue("0") int pageIndex,
                                @QueryParam("size") @DefaultValue("10") int pageSize,
                                @QueryParam("id") @DefaultValue("0") int id,
                                @QueryParam("strgOrder") @DefaultValue("id") String strgOrder, @Context @NotNull SecurityContext context) {
        String queryString = ""; // makeProfileQueryStringByFilters(id, ativo);
        String query = "id > " + "0" + " " + queryString + " " + "order by " + strgOrder + " " + sortQuery;
        PanacheQuery<Profile> profile;
        profile = Profile.find(query);

        return Response.ok(profile.page(Page.of(pageIndex, pageSize)).list()).status(responses.getStatus()).build();

    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response findOne(@PathParam("id") Long id) {

        try {
            Profile profile = controller.findOne(id);
            return Response.ok(profile).build();
        } catch (RuntimeException e) {
            return Response.ok(e.getMessage(), MediaType.TEXT_PLAIN).status(responses.getStatus()).build();
        }
    }

    @POST
    @Path("/")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_JSON})
    public Response sendUpload(MultiPartFormData file, @QueryParam("fileReference") String fileReference,
                               @QueryParam("idHistoricoPessoa") Long idHistoricoPessoa) {
        try {
            return controller.sendUpload(file, fileReference, idHistoricoPessoa);
        } catch (IOException e) {
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @DELETE
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response removeUpload(List<Long> pListIdProfile) {

        try {
            return controller.removeUpload(pListIdProfile);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            if (pListIdProfile.size() <= 1) {
                responses.getMessages().add("Não foi possível excluir o Arquivo.");
            } else {
                responses.getMessages().add("Não foi possível excluir os Arquivos.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }
}