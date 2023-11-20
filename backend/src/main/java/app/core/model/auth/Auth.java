package app.core.model.auth;

import app.agendamento.model.pessoa.Usuario;
import app.core.utils.Contexto;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.jwt.auth.principal.ParseException;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class Auth {
    private String login;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Role> privilegio;
    private List<String> roles;
    private Boolean admin;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Usuario usuario;
    private String accessToken;
    private LocalDateTime expireDateAccessToken;
    private String refreshToken;
    private LocalDateTime expireDateRefreshToken;

    public Auth(Usuario usuario, String accessToken, String refreshToken, Long ACTOKEN, Long RFTOKEN) throws ParseException {

        List<String> privilegios = new ArrayList<>();

        if (usuario.hasRole()) {
            usuario.getPrivilegio().forEach(c -> privilegios.add(c.getPrivilegio()));
        }

        this.setLogin(usuario.getLogin());
        this.setPassword(BcryptUtil.bcryptHash(usuario.getPassword()));
        this.setPrivilegio(new ArrayList<>());
        this.setRoles(new ArrayList<>());
        this.getPrivilegio().addAll(usuario.getPrivilegio());
        this.getRoles().addAll(privilegios);
        this.setAdmin(Contexto.isUserAdmin(usuario));
        this.setAccessToken(accessToken);
        this.setRefreshToken(refreshToken);
        this.setUsuario(usuario);
        this.setExpireDateAccessToken(LocalDateTime.ofEpochSecond(ACTOKEN, 0, ZoneOffset.of(usuario.getOrganizacaoDefault().getTimeZoneOffset())));
        this.setExpireDateRefreshToken(LocalDateTime.ofEpochSecond(RFTOKEN, 0, ZoneOffset.of(usuario.getOrganizacaoDefault().getTimeZoneOffset())));
    }

    public Auth() {

    }
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Role> getPrivilegio() {
        return privilegio;
    }

    public void setPrivilegio(List<Role> privilegio) {
        this.privilegio = privilegio;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public LocalDateTime getExpireDateAccessToken() {
        return expireDateAccessToken;
    }

    public void setExpireDateAccessToken(LocalDateTime expireDateAccessToken) {
        this.expireDateAccessToken = expireDateAccessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public LocalDateTime getExpireDateRefreshToken() {
        return expireDateRefreshToken;
    }

    public void setExpireDateRefreshToken(LocalDateTime expireDateRefreshToken) {
        this.expireDateRefreshToken = expireDateRefreshToken;
    }
}
