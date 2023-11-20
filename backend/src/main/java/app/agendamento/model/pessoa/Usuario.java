package app.agendamento.model.pessoa;

import app.agendamento.model.agendamento.TipoAgendamento;
import app.agendamento.model.configurador.ConfiguradorAgendamentoEspecial;
import app.agendamento.model.organizacao.Organizacao;
import app.core.model.auth.Role;
import app.core.utils.BasicFunctions;
import app.core.utils.Contexto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import io.quarkus.security.jpa.Username;

import javax.persistence.*;
import javax.ws.rs.core.SecurityContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuario", indexes = {
        @Index(name = "iusuarioak1", columnList = "pessoaId, login, organizacaoDefaultId, ativo")
})
@UserDefinition
@JsonIgnoreProperties({"ativo", "dataAcao", "systemDateDeleted", "token"})
public class Usuario extends PanacheEntityBase {

    public static final Long USUARIO = 1L;
    public static final Long BOT = 2L;
    public static final Long ADMINISTRADOR = 3L;
    @Column()
    @SequenceGenerator(name = "usuarioIdSequence", sequenceName = "usuario_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "usuarioIdSequence")
    @Id
    private Long id;
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Password
    private String password;
    @Column(nullable = false)
    @Username
    private String login;
    @ManyToOne

    @JoinColumn(name = "pessoaId")
    private Pessoa pessoa;
    @Column()
    @JsonIgnore
    private Boolean ativo;
    @Column()
    private Boolean alterarSenha;

    @Column()
    private String token;

    @Column()
    private LocalDateTime dataToken;

    @Column()
    private Boolean bot;
    @Column()
    private LocalDateTime dataAcao;
    @Column()
    private LocalDateTime systemDateDeleted;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "usuarioroles", joinColumns = {
            @JoinColumn(name = "usuarioId")}, inverseJoinColumns = {
            @JoinColumn(name = "roleId")})
    @Roles
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Role> privilegio = new ArrayList<>();
    @Column()
    @JsonIgnore
    private String usuario;
    @Column()
    private String nomeProfissional;
    @Column()
    @JsonIgnore
    private String usuarioAcao;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "usuarioorganizacao", joinColumns = {
            @JoinColumn(name = "usuarioId")}, inverseJoinColumns = {
            @JoinColumn(name = "organizacaoId")})
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Organizacao> organizacoes;
    @ManyToOne
    @JoinColumn(name = "organizacaoDefaultId")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Organizacao organizacaoDefault;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "tipoagendamentousuarios", joinColumns = {
            @JoinColumn(name = "profissionalId")}, inverseJoinColumns = {
            @JoinColumn(name = "tipoAgendamentoId")})
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<TipoAgendamento> tiposAgendamentos = new ArrayList<>();
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "configuradoragendamentoespecialusuario", joinColumns = {
            @JoinColumn(name = "profissionalId")}, inverseJoinColumns = {
            @JoinColumn(name = "configuradorAgendamentoEspecialId")})
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<ConfiguradorAgendamentoEspecial> configuradoresEspeciais = new ArrayList<>();

    public Usuario() {

    }

    public Usuario(Usuario pUsuario, List<Role> roles, List<Organizacao> organizacoes, List<TipoAgendamento> tiposAgendamentos, SecurityContext context){

        Usuario usuarioAuth = Contexto.getContextUser(context);

        if (BasicFunctions.isNotEmpty(pUsuario.getLogin())) {
            this.setLogin(pUsuario.getLogin().toLowerCase());
        }
        if (BasicFunctions.isNotEmpty(pUsuario.getPassword())) {
            this.setPassword(BcryptUtil.bcryptHash(pUsuario.getPassword()));
        }
        if (BasicFunctions.isNotEmpty(roles)) {
            this.setPrivilegio(new ArrayList<>());
            this.getPrivilegio().addAll(roles);
        }
        if (BasicFunctions.isNotEmpty(pUsuario.getPessoa())) {
            this.setPessoa(pUsuario.getPessoa());
            this.setNomeProfissional(this.getPessoa().getNome());
        }
        if (BasicFunctions.isNotEmpty(pUsuario.getOrganizacaoDefault())) {
            this.setOrganizacaoDefault(pUsuario.getOrganizacaoDefault());
        }
        if (BasicFunctions.isNotEmpty(organizacoes)) {
            this.setOrganizacoes(new ArrayList<>());
            this.getOrganizacoes().addAll(organizacoes);
        }
        if (BasicFunctions.isNotEmpty(tiposAgendamentos)) {
            this.setTiposAgendamentos(new ArrayList<>());
            this.getTiposAgendamentos().addAll(tiposAgendamentos);
        }
        if (BasicFunctions.isNotEmpty(pUsuario.getBot())) {
            this.setBot(pUsuario.getBot());
        } else {
            this.setBot(Boolean.FALSE);
        }
        Role roleDefault = new Role();

        if (!this.getBot()) {
            roleDefault.setUsuario();
            this.getPrivilegio().add(roleDefault);
        } else {
            roleDefault.setBot();
            this.getPrivilegio().add(roleDefault);
        }
        this.setUsuario(usuarioAuth.getPessoa().getNome());
        this.setUsuarioAcao(usuarioAuth.getPessoa().getNome());
        this.setAtivo(Boolean.TRUE);
        this.setAlterarSenha(Boolean.FALSE);
        this.setDataAcao(Contexto.dataHoraContexto());
    }

    public Usuario usuario(Usuario pUsuarioOld, Usuario pUsuario, List<Role> roles, List<Organizacao> organizacoes, List<TipoAgendamento> tiposAgendamentos, SecurityContext context){

        Usuario usuarioAuth = Contexto.getContextUser(context);

        if (BasicFunctions.isNotEmpty(pUsuario.getLogin())) {
            pUsuarioOld.setLogin(pUsuario.getLogin().toLowerCase());
        }
        if (BasicFunctions.isNotEmpty(pUsuario.getPassword())) {
            pUsuarioOld.setPassword(BcryptUtil.bcryptHash(pUsuario.getPassword()));
        }
        if (BasicFunctions.isNotEmpty(roles)) {
            pUsuarioOld.setPrivilegio(new ArrayList<>());
            pUsuarioOld.getPrivilegio().addAll(roles);
        }
        if (BasicFunctions.isNotEmpty(pUsuario.getPessoa())) {
            pUsuarioOld.setPessoa(pUsuario.getPessoa());
            pUsuarioOld.setNomeProfissional(pUsuario.getPessoa().getNome());
        }
        if (BasicFunctions.isNotEmpty(pUsuario.getOrganizacaoDefault())) {
            pUsuarioOld.setOrganizacaoDefault(pUsuario.getOrganizacaoDefault());
        }
        if (BasicFunctions.isNotEmpty(organizacoes)) {
            pUsuarioOld.setOrganizacoes(new ArrayList<>());
            pUsuarioOld.getOrganizacoes().addAll(organizacoes);
        }
        if (BasicFunctions.isNotEmpty(tiposAgendamentos)) {
            pUsuarioOld.setTiposAgendamentos(new ArrayList<>());
            pUsuarioOld.getTiposAgendamentos().addAll(tiposAgendamentos);
        }
        if (BasicFunctions.isNotEmpty(pUsuario.getBot())) {
            pUsuarioOld.setBot(pUsuario.getBot());
        } else {
            pUsuarioOld.setBot(Boolean.FALSE);
        }
        Role roleDefault = new Role();

        if (!pUsuarioOld.getBot()) {
            roleDefault.setUsuario();
            pUsuarioOld.getPrivilegio().add(roleDefault);
        } else {
            roleDefault.setBot();
            pUsuarioOld.getPrivilegio().add(roleDefault);
        }
        pUsuarioOld.setUsuario(usuarioAuth.getPessoa().getNome());
        pUsuarioOld.setUsuarioAcao(usuarioAuth.getPessoa().getNome());
        pUsuarioOld.setAtivo(Boolean.TRUE);
        pUsuarioOld.setAlterarSenha(Boolean.FALSE);
        pUsuarioOld.setDataAcao(Contexto.dataHoraContexto());
        return pUsuarioOld;
    }

    public Usuario deletarUsuario(Usuario pUsuario, SecurityContext context){
        Usuario usuarioAuth = Contexto.getContextUser(context);
        pUsuario.setAtivo(Boolean.FALSE);
        pUsuario.setSystemDateDeleted(Contexto.dataHoraContexto());
        pUsuario.setUsuarioAcao(usuarioAuth.getPessoa().getNome());
        return pUsuario;
    }

    public Usuario reativarUsuario(Usuario pUsuario, SecurityContext context){
        Usuario usuarioAuth = Contexto.getContextUser(context);
        pUsuario.setAtivo(Boolean.FALSE);
        pUsuario.setSystemDateDeleted(null);
        pUsuario.setUsuarioAcao(usuarioAuth.getPessoa().getNome());
        return pUsuario;
    }

    public Boolean bot() {
        return this.privilegio.stream().anyMatch(role -> role.getId().equals(BOT));
    }

    public Boolean admin() {
        return this.privilegio.stream().anyMatch(role -> role.getId().equals(ADMINISTRADOR));
    }

    public Boolean user() {
        return this.privilegio.stream().anyMatch(role -> role.getId().equals(USUARIO));
    }

    public Boolean hasRole() {
        return BasicFunctions.isValid(this.id) && BasicFunctions.isNotEmpty(this.privilegio);
    }

    public Boolean isValid() {
        return BasicFunctions.isValid(this.id);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Boolean getAlterarSenha() {
        return alterarSenha;
    }

    public void setAlterarSenha(Boolean alterarSenha) {
        this.alterarSenha = alterarSenha;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getDataToken() {
        return dataToken;
    }

    public void setDataToken(LocalDateTime dataToken) {
        this.dataToken = dataToken;
    }

    public Boolean getBot() {
        return bot;
    }

    public void setBot(Boolean bot) {
        this.bot = bot;
    }

    public LocalDateTime getDataAcao() {
        return dataAcao;
    }

    public void setDataAcao(LocalDateTime dataAcao) {
        this.dataAcao = dataAcao;
    }

    public LocalDateTime getSystemDateDeleted() {
        return systemDateDeleted;
    }

    public void setSystemDateDeleted(LocalDateTime systemDateDeleted) {
        this.systemDateDeleted = systemDateDeleted;
    }

    public List<Role> getPrivilegio() {
        return privilegio;
    }

    public void setPrivilegio(List<Role> privilegio) {
        this.privilegio = privilegio;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getNomeProfissional() {
        return nomeProfissional;
    }

    public void setNomeProfissional(String nomeProfissional) {
        this.nomeProfissional = nomeProfissional;
    }

    public String getUsuarioAcao() {
        return usuarioAcao;
    }

    public void setUsuarioAcao(String usuarioAcao) {
        this.usuarioAcao = usuarioAcao;
    }

    public List<Organizacao> getOrganizacoes() {
        return organizacoes;
    }

    public void setOrganizacoes(List<Organizacao> organizacoes) {
        this.organizacoes = organizacoes;
    }

    public Organizacao getOrganizacaoDefault() {
        return organizacaoDefault;
    }

    public void setOrganizacaoDefault(Organizacao organizacaoDefault) {
        this.organizacaoDefault = organizacaoDefault;
    }

    public List<TipoAgendamento> getTiposAgendamentos() {
        return tiposAgendamentos;
    }

    public void setTiposAgendamentos(List<TipoAgendamento> tiposAgendamentos) {
        this.tiposAgendamentos = tiposAgendamentos;
    }

    public List<ConfiguradorAgendamentoEspecial> getConfiguradoresEspeciais() {
        return configuradoresEspeciais;
    }

    public void setConfiguradoresEspeciais(List<ConfiguradorAgendamentoEspecial> configuradoresEspeciais) {
        this.configuradoresEspeciais = configuradoresEspeciais;
    }
}
