package app.core.utils;

import app.agendamento.model.pessoa.Usuario;
import app.core.controller.contrato.ContratoController;
import app.core.model.contrato.TipoContrato;
import app.core.services.auth.RedisService;
import io.smallrye.jwt.build.Jwt;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class AuthToken {

    private final Set<String> privilegios = new HashSet<>();
    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;

    @Inject
    RedisService redisClient;

    public String GenerateAccessToken(Usuario pUsuario, String userAgent) {


        Instant currentInstant = Instant.now();
        ZonedDateTime zonedDateTime = currentInstant.atZone(ZoneId.of(pUsuario.getOrganizacaoDefault().getZoneId()));


        Instant accessExpiresAt = zonedDateTime.plus(Duration.ofMinutes(10)).toInstant();

        int accessSeconds = (int) Duration.between(Instant.now(), accessExpiresAt).getSeconds();


        if (pUsuario.hasRole()) {
            pUsuario.getPrivilegio().forEach(c -> privilegios.add(c.getPrivilegio()));
        }
        String accessToken = Jwt.issuer(this.getIssuer())
                .upn(pUsuario.getLogin())
                .groups(privilegios)
                .expiresAt(accessExpiresAt)
                .sign();

        String expiration = Integer.toString(accessSeconds);

        TipoContrato tipoContrato = ContratoController.getTipoContratoByUsuarioOrganizacaoDefault(pUsuario);

        if (BasicFunctions.isNotEmpty(tipoContrato)) {
            String existingKey = RedisService.makeSessionKeyPattern(pUsuario, userAgent);
            if (BasicFunctions.isNotEmpty(existingKey)) {
                redisClient.delByKey(existingKey);
            }
            redisClient.setex(pUsuario, expiration, accessToken, tipoContrato, userAgent);
        }

        return accessToken;
    }

    public String GenerateRefreshToken(Usuario pUsuario) {


        Instant currentInstant = Instant.now();

        ZonedDateTime zonedDateTime = currentInstant.atZone(ZoneId.of(pUsuario.getOrganizacaoDefault().getZoneId()));

        Instant refreshExpiresAt = zonedDateTime.plus(Duration.ofHours(12)).toInstant();

        if (pUsuario.hasRole()) {
            pUsuario.getPrivilegio().forEach(c -> privilegios.add(c.getPrivilegio()));
        }

        return Jwt.issuer(this.getIssuer())
                .upn(pUsuario.getLogin())
                .groups(privilegios)
                .expiresAt(refreshExpiresAt)
                .sign();
    }

    public String getIssuer() {
        return issuer;
    }
}
