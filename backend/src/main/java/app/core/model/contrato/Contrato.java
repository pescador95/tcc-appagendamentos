package app.core.model.contrato;

import app.agendamento.model.organizacao.Organizacao;
import app.agendamento.model.pessoa.Usuario;
import app.core.utils.BasicFunctions;
import app.core.utils.Contexto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import javax.ws.rs.core.SecurityContext;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "contrato")
@JsonIgnoreProperties({"usuarioAcao", "ativo", "dataAcao", "systemDateDeleted"})

public class Contrato extends PanacheEntityBase {

    @Column()
    @SequenceGenerator(name = "contratoIdSequence", sequenceName = "contrato_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "contratoIdSequence")
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "organizacaoId")
    private Organizacao organizacaoContrato;

    @ManyToOne
    @JoinColumn(name = "responsavelId")
    private Usuario responsavelContrato;

    @Column()
    private Integer numeroMaximoSessoes;

    @Column()
    private String consideracoes;

    @Column()
    private LocalDate dataContrato;

    @ManyToOne
    @JoinColumn(name = "tipoContratoId")
    private TipoContrato tipoContrato;

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

    public Contrato() {

    }

    public Contrato deletarContrato(Contrato pContrato, SecurityContext context){

        Usuario usuarioAuth = Contexto.getContextUser(context);

        pContrato.setUsuarioAcao(usuarioAuth);
        pContrato.setAtivo(Boolean.FALSE);
        pContrato.setDataAcao(Contexto.dataHoraContexto());
        pContrato.setSystemDateDeleted(Contexto.dataHoraContexto());
        return pContrato;
    }
    public Contrato reativarContrato(Contrato pContrato, SecurityContext context){

        Usuario usuarioAuth = Contexto.getContextUser(context);

        pContrato.setUsuarioAcao(usuarioAuth);
        pContrato.setAtivo(Boolean.TRUE);
        pContrato.setDataAcao(Contexto.dataHoraContexto());
        pContrato.setSystemDateDeleted(null);
        return pContrato;
    }


    public Boolean sessaoUnica() {
        return BasicFunctions.isValid(this.id) && BasicFunctions.isNotEmpty(this.tipoContrato) && this.tipoContrato.getId().equals(TipoContrato.SESSAO_UNICA);
    }

    public Boolean sessaoCompartilhada() {
        return BasicFunctions.isValid(this.id) && BasicFunctions.isNotEmpty(this.tipoContrato) && this.tipoContrato.getId().equals(TipoContrato.SESSAO_COMPARTILHADA);
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

    public Organizacao getOrganizacaoContrato() {
        return organizacaoContrato;
    }

    public void setOrganizacaoContrato(Organizacao organizacaoContrato) {
        this.organizacaoContrato = organizacaoContrato;
    }

    public Usuario getResponsavelContrato() {
        return responsavelContrato;
    }

    public void setResponsavelContrato(Usuario responsavelContrato) {
        this.responsavelContrato = responsavelContrato;
    }

    public Integer getNumeroMaximoSessoes() {
        return numeroMaximoSessoes;
    }

    public void setNumeroMaximoSessoes(Integer numeroMaximoSessoes) {
        this.numeroMaximoSessoes = numeroMaximoSessoes;
    }

    public String getConsideracoes() {
        return consideracoes;
    }

    public void setConsideracoes(String consideracoes) {
        this.consideracoes = consideracoes;
    }

    public LocalDate getDataContrato() {
        return dataContrato;
    }

    public void setDataContrato(LocalDate dataContrato) {
        this.dataContrato = dataContrato;
    }

    public TipoContrato getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(TipoContrato tipoContrato) {
        this.tipoContrato = tipoContrato;
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
}
