package app.core.controller.auth;

import app.agendamento.model.organizacao.Organizacao;
import app.agendamento.model.pessoa.Usuario;
import app.core.controller.contrato.ContratoController;
import app.core.model.DTO.Responses;
import app.core.model.auth.Auth;
import app.core.model.auth.Role;
import app.core.model.contrato.Contrato;
import app.core.model.contrato.TipoContrato;
import app.core.services.auth.RedisService;
import app.core.utils.AuthToken;
import app.core.utils.BasicFunctions;
import app.core.utils.Contexto;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import org.apache.sshd.common.config.keys.loader.openssh.kdf.BCrypt;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;


@ApplicationScoped
public class AuthController {
    @Inject
    AuthToken token;
    @Inject
    RedisService redisClient;
    @Inject
    JWTParser parser;

    List<Role> roles;

    private Responses responses = new Responses();


    public Response login(Auth data, String userAgent) throws ParseException {

        roles = new ArrayList<>();

        Usuario usuario;
        responses.setMessages(new ArrayList<>());

        if (BasicFunctions.isNotEmpty(data) && BasicFunctions.isNotEmpty(data.getLogin())) {

            usuario = Usuario.find("login = ?1", data.getLogin().toLowerCase()).firstResult();

            if (BasicFunctions.isNotEmpty(usuario)) {

                Responses responses = validaSessao(usuario, userAgent);
                if (BasicFunctions.isNotEmpty(responses)) {
                    return Response.ok(responses).status(Response.Status.FORBIDDEN).build();
                }

            } else {

                responses.setStatus(400);
                responses.setData(data);
                responses.getMessages().add("Credenciais incorretas.");
                return Response.ok(responses).status(responses.getStatus()).build();
            }


            if (BasicFunctions.isEmpty(usuario)) {

                responses.setStatus(400);
                responses.setData(data);
                responses.getMessages().add("Credenciais incorretas.");
                return Response.ok(responses).status(responses.getStatus()).build();
            }

            boolean authenticated = BCrypt.checkpw(data.getPassword(), usuario.getPassword());

            if (!authenticated) {

                responses.setStatus(400);
                responses.setData(data);
                responses.getMessages().add("Credenciais incorretas.");
                return Response.ok(responses).status(responses.getStatus()).build();
            }

            String accessToken = token.GenerateAccessToken(usuario, userAgent);
            String refreshToken = token.GenerateRefreshToken(usuario);

            Long ACTOKEN = parser.parse(accessToken).getClaim("exp");
            Long RFTOKEN = parser.parse(refreshToken).getClaim("exp");

            Auth auth = new Auth(usuario, accessToken, refreshToken, ACTOKEN, RFTOKEN);

            responses = new Responses();

            responses.setMessages(new ArrayList<>());
            responses.getMessages().add("Bem-vindo, " + usuario.getLogin() + "!");

            responses.setStatus(200);
            responses.setData(auth);
            return Response.ok(responses).status(responses.getStatus()).build();
        }
        return null;
    }


    public Response refreshToken(Auth data, String userAgent) {
        Usuario usuario;
        LocalDateTime expireDate;
        roles = new ArrayList<>();

        responses.setMessages(new ArrayList<>());

        try {
            String login = parser.parse(data.getRefreshToken()).getClaim("upn");
            long expireDateOldToken = parser.parse(data.getRefreshToken()).getClaim("exp");

            usuario = Usuario.find("login", login).firstResult();

            if (BasicFunctions.isNotEmpty(usuario)) {

                Responses responses = validaSessao(usuario, userAgent);
                if (BasicFunctions.isNotEmpty(responses)) {
                    return Response.ok(responses).status(Response.Status.FORBIDDEN).build();
                }
            }

            expireDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(expireDateOldToken), ZoneId.of(usuario.getOrganizacaoDefault().getZoneId()));

            if (expireDate.isAfter(Contexto.dataHoraContexto()) && BasicFunctions.isNotEmpty(usuario)) {
                String accessToken = token.GenerateAccessToken(usuario, userAgent);
                String refreshToken = token.GenerateRefreshToken(usuario);

                Long ACTOKEN = parser.parse(accessToken).getClaim("exp");
                Long RFTOKEN = parser.parse(refreshToken).getClaim("exp");

                Auth auth = new Auth(usuario, accessToken, refreshToken, ACTOKEN, RFTOKEN);

                responses.setStatus(200);
                responses.getMessages().add("Bem-vindo novamente, " + usuario.getLogin() + "!");
                responses.setData(auth);
            } else {

                responses.setStatus(400);
                responses.setData(data);
                responses.getMessages().add("Credenciais incorretas.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (ParseException error) {
            System.out.print(error.getMessage());
        }
        return Response.ok(responses).status(responses.getStatus()).build();
    }

    private int getMaxSessionsAllowedForOrganization(Organizacao organizacao) {

        Contrato contrato = Contrato.find("organizacaoContrato = ?1", organizacao).firstResult();

        if (BasicFunctions.isNotEmpty(contrato)) {
            return contrato.getNumeroMaximoSessoes();
        }
        return 0;

    }

    public Responses validaSessao(Usuario usuario, String userAgent) {
        int maxSessionsAllowed = getMaxSessionsAllowedForOrganization(usuario.getOrganizacaoDefault());

        TipoContrato tipoContrato = ContratoController.getTipoContratoByUsuarioOrganizacaoDefault(usuario);
        int activeSessions = redisClient.countActiveSessionsForUserAndOrganization(usuario, false, userAgent);
        if (BasicFunctions.isNotEmpty(tipoContrato)) {
            if (tipoContrato.sessaoUnica()) {
                String existingKey = RedisService.makeSessionKeyPattern(usuario, userAgent);
                if (BasicFunctions.isNotEmpty(existingKey)) {
                    redisClient.delByKey(existingKey);
                }
                activeSessions = redisClient.countActiveSessionsForUserAndOrganization(usuario, false, userAgent);
                if (BasicFunctions.isValid(activeSessions) && activeSessions == 1) {
                    Responses responses = new Responses();
                    responses.setStatus(400);
                    responses.setMessages(new ArrayList<>());
                    responses.getMessages().add("O Tipo de Contrato da sua Organização é: Sessão Única e você já possui uma sessão ativa.");
                    return responses;
                }
            } else {
                if (activeSessions >= maxSessionsAllowed) {
                    responses.setStatus(400);
                    responses.getMessages().add("Número máximo de sessões atingido para a organização "
                            + usuario.getOrganizacaoDefault().getNome() + ". Número limite de sessões simultâneas permitidas: " + maxSessionsAllowed);
                    return responses;
                }
            }
        }
        return null;
    }
}