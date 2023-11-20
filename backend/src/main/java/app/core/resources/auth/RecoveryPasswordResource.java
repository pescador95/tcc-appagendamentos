package app.core.resources.auth;

import app.agendamento.model.pessoa.Usuario;
import app.core.controller.auth.RecoveryPasswordController;
import app.core.model.DTO.Responses;
import io.quarkus.elytron.security.common.BcryptUtil;
import org.jetbrains.annotations.NotNull;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("/recoverPassword")

public class RecoveryPasswordResource {
    @Inject
    RecoveryPasswordController controller;

    Responses responses;

    @POST
    @Path("{email}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    public Response sendMail(@PathParam("email") String email) {
        try {
            return controller.sendEmail(email);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            responses.getMessages().add("Não foi possível localizar um cadastro com o email informado.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @PUT
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response update(Usuario pUsuario, @Context @NotNull SecurityContext context) {
        try {

            return controller.updatePassword(pUsuario);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            responses.getMessages().add("Não foi possível atualizar a senha.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }
    @POST
    @Path("/crypt")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"admin"})
    public Response returnCryptPassword(@QueryParam("password") String password) {
        responses = new Responses();
        try {

            String cryptoPassword = BcryptUtil.bcryptHash(password);
            responses.getMessages().add((password));
            responses.getMessages().add(cryptoPassword);
            responses.setStatus(200);
            responses.setOk(true);
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            responses.getMessages().add("Não foi possível validar a senha.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }
}