package app.core.resources.scheduler;

import app.core.model.scheduler.Lembrete;
import app.core.thread.LembreteThread;
import app.core.utils.BasicFunctions;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/scheduler")
public class SchedulerResources {

    @Inject
    LembreteThread lembreteThread;

    @GET
    @Path("/enviarLembrete")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @PermitAll
    public Response enviarLembrete(@QueryParam("mensagem") String mensagem,
                                   @QueryParam("whatsppId") Long whatsppId,
                                   @QueryParam("telegramId") Long telegramId) {

        String telegramApiUrl = System.getenv("TELEGRAM_API_URL");

        String whatsappApiUrl = System.getenv("WHATSAPP_API_URL");

        Lembrete lembrete = new Lembrete();
        // TODO LOGICA ENDPOINT

        if (BasicFunctions.isValid(telegramId)) {
            lembreteThread.enviarLembrete(mensagem, telegramApiUrl, telegramId, lembrete);
        }
        if (BasicFunctions.isValid(whatsppId)) {
            lembreteThread.enviarLembrete(mensagem, whatsappApiUrl, whatsppId, lembrete);
        }

        return Response.ok(lembreteThread).status(200).build();
    }


}
