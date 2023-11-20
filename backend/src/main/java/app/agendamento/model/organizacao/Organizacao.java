package app.agendamento.model.organizacao;

import app.agendamento.model.pessoa.Usuario;
import app.core.model.profile.TimeZone;
import app.core.utils.BasicFunctions;
import app.core.utils.Contexto;
import app.core.utils.StringBuilder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import javax.ws.rs.core.SecurityContext;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Entity
@Table(name = "organizacao")
@JsonIgnoreProperties({"usuarioAcao", "ativo", "dataAcao", "systemDateDeleted"})

public class Organizacao extends PanacheEntityBase {

    @Column()
    @SequenceGenerator(name = "organizacaoIdSequence", sequenceName = "organizacao_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "organizacaoIdSequence")
    @Id
    private Long id;

    @Column()
    private String nome;

    @Column()
    private String cnpj;

    @Column()
    private String telefone;

    @Column()
    private String celular;

    @Column()
    private String email;

    @Column()
    @JsonIgnore
    private Boolean ativo;

    @Column()
    @JsonIgnore
    private LocalDateTime dataAcao;

    @Column()
    @JsonIgnore
    private LocalDateTime systemDateDeleted;

    @ManyToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Usuario usuario;

    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnore
    @JoinColumn(name = "usuarioId")
    private Usuario usuarioAcao;

    @ManyToOne
    @JoinColumn(name = "timeZoneId")
    private TimeZone timeZone;

    public Organizacao() {

    }

    public Organizacao(Organizacao pOrganizacao, SecurityContext context){

        Usuario usuarioAuth = Contexto.getContextUser(context);

        if (BasicFunctions.isNotEmpty(pOrganizacao.getNome())) {
            this.setNome(pOrganizacao.getNome());
        }
        if (BasicFunctions.isNotEmpty(pOrganizacao.getCnpj())) {
            this.setCnpj(pOrganizacao.getCnpj());
        }
        if (BasicFunctions.isNotEmpty(pOrganizacao.getTelefone())) {
            this.setTelefone(pOrganizacao.getTelefone());
        }
        if (BasicFunctions.isNotEmpty(pOrganizacao.getCelular())) {
            this.setCelular(pOrganizacao.getCelular());
        }
        if (BasicFunctions.isNotEmpty(pOrganizacao.getEmail())) {
            this.setEmail(pOrganizacao.getEmail());
        }
        if(BasicFunctions.isNotEmpty(pOrganizacao.getTimeZone()) && BasicFunctions.isValid(pOrganizacao.getTimeZone().getId())){
            this.timeZone = TimeZone.findById(pOrganizacao.getTimeZone().getId());
        }
        this.setUsuario(usuarioAuth);
        this.setUsuarioAcao(usuarioAuth);
        this.setAtivo(Boolean.TRUE);
        this.setDataAcao(Contexto.dataHoraContexto());

    }

    public Organizacao organizacao(Organizacao pOrganizacaoOld, Organizacao pOrganizacao, SecurityContext context){

        Usuario usuarioAuth = Contexto.getContextUser(context);

        if (BasicFunctions.isNotEmpty(pOrganizacao.getNome())) {
            pOrganizacaoOld.setNome(pOrganizacao.getNome());
        }
        if (BasicFunctions.isNotEmpty(pOrganizacao.getCnpj())) {
            pOrganizacaoOld.setCnpj(pOrganizacao.getCnpj());
        }
        if (BasicFunctions.isNotEmpty(pOrganizacao.getTelefone())) {
            pOrganizacaoOld.setTelefone(pOrganizacao.getTelefone());
        }
        if (BasicFunctions.isNotEmpty(pOrganizacao.getCelular())) {
            pOrganizacaoOld.setCelular(pOrganizacao.getCelular());
        }
        if (BasicFunctions.isNotEmpty(pOrganizacao.getEmail())) {
            pOrganizacaoOld.setEmail(pOrganizacao.getEmail());
        }
        if(BasicFunctions.isNotEmpty(pOrganizacao.getTimeZone()) && BasicFunctions.isValid(pOrganizacao.getTimeZone().getId())){
            pOrganizacaoOld.timeZone = TimeZone.findById(pOrganizacao.getTimeZone().getId());
        }
        pOrganizacaoOld.setUsuario(usuarioAuth);
        pOrganizacaoOld.setUsuarioAcao(usuarioAuth);
        pOrganizacaoOld.setAtivo(Boolean.TRUE);
        pOrganizacaoOld.setDataAcao(Contexto.dataHoraContexto());
        return pOrganizacaoOld;
    }

    public Organizacao deletarOrganizacao(Organizacao pOrganizacao, SecurityContext context){

        Usuario usuarioAuth = Contexto.getContextUser(context);

        pOrganizacao.setUsuarioAcao(usuarioAuth);
        pOrganizacao.setAtivo(Boolean.FALSE);
        pOrganizacao.setDataAcao(Contexto.dataHoraContexto());
        pOrganizacao.setSystemDateDeleted(Contexto.dataHoraContexto());
        return pOrganizacao;
    }

    public Organizacao reativarOrganizacao(Organizacao pOrganizacao, SecurityContext context){

        Usuario usuarioAuth = Contexto.getContextUser(context);

        pOrganizacao.setUsuarioAcao(usuarioAuth);
        pOrganizacao.setAtivo(Boolean.TRUE);
        pOrganizacao.setDataAcao(Contexto.dataHoraContexto());
        pOrganizacao.setSystemDateDeleted(null);
        return pOrganizacao;
    }

    public Boolean cnpjJaUtilizado(Organizacao pOrganizacao) {
        List<Organizacao> organizacoesExistentes = Organizacao.list("cnpj = ?1 and ativo = true", pOrganizacao.getCnpj());

        return BasicFunctions.isNotEmpty(organizacoesExistentes) && organizacoesExistentes.stream().anyMatch(organizacao -> !organizacao.getId().equals(pOrganizacao.getId()));
    }

    public Boolean isValid() {
        return BasicFunctions.isValid(this.id);
    }

    public TimeZone getTimeZone() {
        if (BasicFunctions.isNotEmpty(this.timeZone)) {
            return this.timeZone;
        }
        return new TimeZone("America/Sao_Paulo", "GMT-03:00");
    }

    public String getTimeZoneOffset() {
        if (BasicFunctions.isNotEmpty(this.timeZone)) {
            return String.valueOf(this.timeZone.getTimeZoneOffset());
        }
        return "GMT-03:00";
    }

    public String getZoneId() {
        if (BasicFunctions.isNotEmpty(this.timeZone)) {
            return this.timeZone.getTimeZoneId();
        }
        return String.valueOf(ZoneId.of("America/Sao_Paulo"));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = StringBuilder.makeOnlyNumbers(cnpj);
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = StringBuilder.makeOnlyNumbers(telefone);
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = StringBuilder.makeOnlyNumbers(celular);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getUsuarioAcao() {
        return usuarioAcao;
    }

    public void setUsuarioAcao(Usuario usuarioAcao) {
        this.usuarioAcao = usuarioAcao;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }
}
