package app.core.services.auth;

import app.agendamento.model.pessoa.Usuario;
import app.core.controller.contrato.ContratoController;
import app.core.model.DTO.Responses;
import app.core.model.contrato.TipoContrato;
import app.core.trace.RemoteHostKeyGenerator;
import app.core.utils.BasicFunctions;
import app.core.utils.Contexto;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.redis.client.RedisAPI;
import io.vertx.mutiny.redis.client.Response;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.Collections;

import static app.core.utils.BasicFunctions.log;


@ApplicationScoped
@RegisterForReflection
public class RedisService {

    @Context
    SecurityContext context;

    private final RedisAPI redisAPI;

    public RedisService(RedisAPI redisAPI) {
        this.redisAPI = redisAPI;
    }

    public Uni<Response> get(String key) {
        return redisAPI.get(key);
    }

    public void setex(String key, String seconds, String value, TipoContrato tipoContrato) {

        if (tipoContrato.sessaoCompartilhada()) {
            key = key + value;
        }

        redisAPI.setex(key, seconds, value).await().indefinitely();
    }

    public javax.ws.rs.core.Response del(String userAgent) {

        Responses responses = new Responses();
        responses.setMessages(new ArrayList<>());

        try {

            Usuario usuario = Contexto.getContextUser(context);

            String key = makeSessionKeyPattern(usuario, userAgent);

            redisAPI.del(Collections.singletonList(key)).await().indefinitely();

            countActiveSessionsForUserAndOrganization(usuario, true, userAgent);
            responses.setStatus(200);
            responses.getMessages().add("Sessão do Usuário " + usuario.getLogin() + " finalizada com sucesso!");
            log("Sessão do Usuário " + usuario.getLogin() + " finalizada com sucesso!");
            return javax.ws.rs.core.Response.ok(responses).status(responses.getStatus()).build();

        } catch (Exception e) {
            responses.setStatus(400);
            responses.getMessages().add("Erro ao finalizadar a Sessão do Redis!");
            return javax.ws.rs.core.Response.ok(responses).status(responses.getStatus()).build();

        }

    }

    public void delByKey(String key) {
        redisAPI.del(Collections.singletonList(key)).await().indefinitely();
    }


    public int countActiveSessionsForUserAndOrganization(Usuario usuario, Boolean firstTime, String userAgent) {

        TipoContrato tipoContrato = ContratoController.getTipoContratoByUsuarioOrganizacaoDefault(usuario);

        String key = getSessionDefaultPattern(usuario, userAgent, false);

        Uni<Integer> activeSessionsCount = redisAPI.keys(key)
                .onItem().transform(Response::size);

        Integer count = activeSessionsCount.await().indefinitely();

        logSessoesAtivas(tipoContrato, usuario, firstTime, count, userAgent);

        return count;
    }

    public static void logSessoesAtivas(TipoContrato tipoContrato, Usuario usuario, Boolean firstTime, Integer count, String userAgent) {

        String pattern = getSessionDefaultPattern(usuario, userAgent, true);

        String patternName = "";

        if (firstTime) {
            log("Número de sessões ativas: " + count + " para o usuário: " + usuario.getLogin());
            if (tipoContrato.sessaoCompartilhada()) {
                patternName = "Sessão Compartilhada com o pattern " + pattern;
            }
            if (tipoContrato.sessaoUnica()) {
                patternName = "Sessão Única com o pattern " + pattern;
            }
            log(patternName);
        }

    }

    public static String makeSessionKeyPattern(Usuario usuario, String userAgent) {

        TipoContrato tipoContrato = ContratoController.getTipoContratoByUsuarioOrganizacaoDefault(usuario);

        RemoteHostKeyGenerator remoteHostKeyGenerator = new RemoteHostKeyGenerator();

        String key = remoteHostKeyGenerator.generateKey(userAgent);

        String session;

        String pattern = usuario.getLogin() + "_organizacaoId_" + usuario.getOrganizacaoDefault().getId() + "_userAgent_" + key;

        if (BasicFunctions.isNotEmpty(tipoContrato)) {
            if (tipoContrato.sessaoCompartilhada()) {
                session  = "shared_";
            } else {
                session = "unique_";
            }
            return session + pattern;
        }
        return "";
    }

    public static String getSessionDefaultPattern(Usuario usuario, String userAgent, Boolean withKey) {

        TipoContrato tipoContrato = ContratoController.getTipoContratoByUsuarioOrganizacaoDefault(usuario);

        RemoteHostKeyGenerator remoteHostKeyGenerator = new RemoteHostKeyGenerator();

        String key = remoteHostKeyGenerator.generateKey(userAgent);

        String session;

        String pattern = usuario.getLogin() + "_organizacaoId_" + usuario.getOrganizacaoDefault().getId() + "_userAgent_";

        if(withKey){
           pattern = pattern + key;
        } else {
            pattern = pattern + "*";
        }

        if (BasicFunctions.isNotEmpty(tipoContrato)) {
            if (tipoContrato.sessaoCompartilhada()) {
                session  = "shared_";
            } else {
                session = "unique_";
            }
            return session + pattern;
        }
     return "";
    }

    public void setex(Usuario usuario, String expiration, String accessToken, TipoContrato tipoContrato, String userAgent) {

        setex(makeSessionKeyPattern(usuario, userAgent), expiration, accessToken, tipoContrato);

        countActiveSessionsForUserAndOrganization(usuario, Boolean.TRUE, userAgent);

    }

    public javax.ws.rs.core.Response flushRedis() {
        Responses responses = new Responses();
        responses.setMessages(new ArrayList<>());
        try {
            redisAPI.flushdbAndAwait(Collections.emptyList());
            responses.setStatus(200);
            responses.getMessages().add("Sessões do Redis finalizadas com sucesso!");
            log("Redis database flushed!");
            return javax.ws.rs.core.Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {
            responses.setStatus(400);
            responses.getMessages().add("Erro ao finalizadar as Sessões do Redis!");
            return javax.ws.rs.core.Response.ok(responses).status(responses.getStatus()).build();

        }
    }
}

