package app.core.utils;

import app.agendamento.model.organizacao.Organizacao;
import app.agendamento.model.pessoa.Usuario;
import io.smallrye.jwt.auth.principal.DefaultJWTCallerPrincipal;
import org.jetbrains.annotations.NotNull;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.SecurityContext;
import java.time.*;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
@Transactional
public class Contexto {

    @Context
    HttpHeaders headers;

    public static LocalDate dataContexto(Organizacao organizacao) {
        organizacao = Organizacao.findById(organizacao.getId());
        return LocalDate.now(ZoneId.of(organizacao.getZoneId()));
    }

    public static LocalTime horarioContexto(Organizacao organizacao) {
        organizacao = Organizacao.findById(organizacao.getId());
        return LocalTime.now(ZoneId.of(organizacao.getZoneId()));
    }

    public static LocalDate dataContexto() {
        return LocalDate.now(ZoneId.of("America/Sao_Paulo"));
    }

    public static LocalTime horarioContexto() {
        return LocalTime.now(ZoneId.of("America/Sao_Paulo"));
    }

    public static LocalDateTime dataHoraContexto(Organizacao organizacao) {
        organizacao = Organizacao.findById(organizacao.getId());
        return LocalDateTime.now(ZoneId.of(organizacao.getZoneId()));
    }

    public static LocalDateTime dataHoraContexto() {
        return LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
    }

    public static String dataHoraContextoToString(Organizacao organizacao) {
        organizacao = Organizacao.findById(organizacao.getId());
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of(organizacao.getZoneId()));
        return zonedDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSS"));
    }

    public static String dataHoraContextoToString() {
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"));
        return zonedDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSS"));
    }


    public static String dataHoraToString(LocalDateTime dataHora, Organizacao organizacao) {
        organizacao = Organizacao.findById(organizacao.getId());
        ZonedDateTime zonedDateTime = ZonedDateTime.of(dataHora, ZoneId.of(organizacao.getZoneId()));
        return zonedDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSS"));
    }

    public static Boolean dataValida(LocalDate data, Organizacao organizacao) {
        return (data.isAfter(LocalDate.of(1899, 12, 31)) && data.isBefore(LocalDate.now(ZoneId.of(organizacao.getZoneId()))) || data.isEqual(LocalDate.now(ZoneId.of(organizacao.getZoneId()))));
    }

    public static Boolean dataValida(LocalDateTime data, Organizacao organizacao) {
        return (data.isAfter(LocalDateTime.of(1899, 12, 31, 23, 59, 59)) && data.isBefore(LocalDateTime.now(ZoneId.of(organizacao.getZoneId()))) || data.isEqual(LocalDateTime.now(ZoneId.of(organizacao.getZoneId()))));
    }

    public static Usuario getContextUser(@Context @NotNull
                                         SecurityContext context) {

        if (BasicFunctions.isNotEmpty(context.getUserPrincipal())) {

            String login = context.getUserPrincipal().getName();
            return Usuario.find("login = ?1 and ativo = true", login.toLowerCase()).firstResult();
        }
        return Usuario.find("bot = true and ativo = true").firstResult();
    }

    public static String getContextUserKey(@Context @NotNull
                                           SecurityContext context) {
        if (BasicFunctions.isNotEmpty(context.getUserPrincipal())) {
            return ((DefaultJWTCallerPrincipal) context.getUserPrincipal()).getRawToken();
        }
        return "";
    }

    public static Boolean isUserAdmin(Usuario pUsuario) {
        Usuario usuario = Usuario.find("login = ?1 and ativo = true", pUsuario.getLogin().toLowerCase()).firstResult();

        if (usuario.hasRole()) {
            return usuario.admin();
        }
        return false;
    }
}
