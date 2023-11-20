package app.core.resources.auth;

import app.core.controller.auth.AuthController;
import app.core.model.auth.Auth;
import app.core.services.auth.RedisService;
import app.core.utils.BasicFunctions;
import io.smallrye.jwt.auth.principal.ParseException;
import io.vertx.core.http.HttpServerRequest;
import org.jetbrains.annotations.NotNull;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;

import static app.core.utils.BasicFunctions.log;

@Path("/auth")
@RequestScoped
public class AuthResources {

    @Inject
    AuthController authController;

    @Inject
    RedisService redisService;

    @Inject
    @Context
    HttpHeaders headers;

    @Context
    HttpServerRequest request;

    public AuthResources() {
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @PermitAll
    public Response auth(Auth data) throws ParseException {
        //TODO implementar no nginx
        String clientIp = request.getHeader("X-Forwarded-For");
        String hostname = request.getHeader("Host");
        String userAgent = headers.getHeaderString("User-Agent");
        if(BasicFunctions.isNotEmpty(clientIp) && BasicFunctions.isNotEmpty(hostname)){
            log("clientIp: " + clientIp);
            log("hostname: " + hostname);
        }

        return authController.login(data, userAgent);
    }

    @POST
    @Path("/refresh")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @PermitAll
    public Response refreshToken(Auth data) {
        String userAgent = headers.getHeaderString("User-Agent");
        return authController.refreshToken(data, userAgent);
    }

    @POST
    @Path("/logout")
    @PermitAll
    public Response logout(@Context @NotNull SecurityContext context) {
        String userAgent = headers.getHeaderString("User-Agent");
        return redisService.del(userAgent);
    }

    @POST
    @Path("/flush")
    @PermitAll
    public Response flush() {
        return redisService.flushRedis();
    }
}