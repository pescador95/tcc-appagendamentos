package app.agendamento.model.agendamento;

import app.agendamento.model.organizacao.Organizacao;
import app.agendamento.model.pessoa.Pessoa;
import app.agendamento.model.pessoa.Usuario;
import app.core.utils.BasicFunctions;
import app.core.utils.Contexto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import javax.ws.rs.core.SecurityContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "agendamento", indexes = {
        @Index(name = "iagendamentoak1", columnList = "dataAgendamento, horarioAgendamento, pessoaId, profissionalId, organizacaoId, StatusAgendamentoId, ativo")
})
@JsonIgnoreProperties({"usuarioAcao", "ativo", "dataAcao", "systemDateDeleted", "agendamentoOld"})
@NamedQueries({
        @NamedQuery(name = "qloadListAgendamentosByUsuarioDataAgenda", query = "SELECT a FROM Agendamento a JOIN FETCH a.organizacaoAgendamento o JOIN FETCH a.profissionalAgendamento u WHERE u.id = :profissionalId AND a.dataAgendamento = :dataAgendamento AND a.ativo = true AND u.ativo = true"),
        @NamedQuery(name = "qloadListAgendamentosByDataAgenda", query = "SELECT a FROM Agendamento a JOIN FETCH a.profissionalAgendamento p WHERE a.dataAgendamento = :dataAgendamento AND a.ativo = true AND p.ativo = true"),
        @NamedQuery(name = "qloadAgendamentoByPessoaDataAgendaHorario", query = "SELECT a FROM Agendamento a JOIN FETCH a.pessoaAgendamento p WHERE p.id = :pessoaId AND a.dataAgendamento = :dataAgendamento AND a.ativo = true AND p.ativo = true AND a.horarioAgendamento = :horarioAgendamento"),
})

public class Agendamento extends PanacheEntityBase {

    @Column()
    @SequenceGenerator(name = "agendamentoIdSequence", sequenceName = "agendamento_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "agendamentoIdSequence")
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tipoAgendamentoId")
    private TipoAgendamento tipoAgendamento;

    @ManyToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JoinColumn(name = "usuarioId")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "pessoaId")
    private Pessoa pessoaAgendamento;

    @ManyToOne
    @JoinColumn(name = "profissionalId")
    private Usuario profissionalAgendamento;

    @ManyToOne
    @JoinColumn(name = "StatusAgendamentoId")
    private StatusAgendamento statusAgendamento;

    @ManyToOne
    @JoinColumn(name = "organizacaoId")
    private Organizacao organizacaoAgendamento;

    @Column()
    private LocalDate dataAgendamento;

    @Column()
    private LocalTime horarioAgendamento;
    @Column()
    private String nomePessoa;

    @Column()
    private String nomeProfissional;

    @Column()
    private Boolean comPreferencia;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "agendamentoOldId")
    private Agendamento agendamentoOld;

    @Column()
    @JsonIgnore
    private Boolean ativo;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "usuarioId", insertable = false, updatable = false)
    private Usuario usuarioAcao;

    @Column()
    @JsonIgnore
    private LocalDateTime dataAcao;

    @Column()
    @JsonIgnore
    private LocalDateTime systemDateDeleted;

    public Agendamento() {

    }

    public Agendamento(@NotNull Agendamento agendamento, Agendamento agendamentoOld, TipoAgendamento tipoAgendamento, Pessoa pessoaAgendamento, Usuario profissionalAgendamento, StatusAgendamento statusAgendamento, Organizacao organizacaoAgendamento, SecurityContext context) {

        Usuario usuarioAuth = Contexto.getContextUser(context);

        this.tipoAgendamento = tipoAgendamento;

        this.organizacaoAgendamento = organizacaoAgendamento;
        this.dataAgendamento = agendamento.getDataAgendamento();
        this.horarioAgendamento = agendamento.getHorarioAgendamento();
        if (BasicFunctions.isNotEmpty(profissionalAgendamento) && profissionalAgendamento.isValid()) {
            this.profissionalAgendamento = profissionalAgendamento;
            this.nomeProfissional = profissionalAgendamento.getPessoa().getNome();
        } else {
            this.profissionalAgendamento = usuarioAuth;
            this.nomeProfissional = usuarioAuth.getPessoa().getNome();
        }
        if (BasicFunctions.isValid(agendamento.getComPreferencia())) {
            this.comPreferencia = agendamento.getComPreferencia();
        } else {
            this.comPreferencia = Boolean.FALSE;
        }
        if (BasicFunctions.isNotEmpty(statusAgendamento) && statusAgendamento.isValid()) {
            this.statusAgendamento = statusAgendamento;
        } else {
            this.statusAgendamento = StatusAgendamento.statusAgendado();
        }
        if (BasicFunctions.isNotEmpty(pessoaAgendamento) && pessoaAgendamento.isValid()) {
            this.pessoaAgendamento = pessoaAgendamento;
            this.nomePessoa = pessoaAgendamento.getNome();
        }
        if (BasicFunctions.isNotEmpty(agendamentoOld) && agendamentoOld.isValid()) {
            this.agendamentoOld = agendamentoOld;
        }
        this.ativo = Boolean.TRUE;
        this.usuario = Contexto.getContextUser(context);
        this.usuarioAcao = Contexto.getContextUser(context);
        this.dataAcao = Contexto.dataHoraContexto(organizacaoAgendamento);
    }

    public Agendamento agendamento(Agendamento agendamentoOld, Agendamento agendamento, TipoAgendamento tipoAgendamento, Pessoa pessoaAgendamento, Usuario profissionalAgendamento, StatusAgendamento statusAgendamento, Organizacao organizacaoAgendamento, SecurityContext context) {

        Usuario usuarioAuth = Contexto.getContextUser(context);

        agendamentoOld.tipoAgendamento = tipoAgendamento;

        agendamentoOld.organizacaoAgendamento = organizacaoAgendamento;
        agendamentoOld.dataAgendamento = agendamento.getDataAgendamento();
        agendamentoOld.horarioAgendamento = agendamento.getHorarioAgendamento();
        if (BasicFunctions.isNotEmpty(profissionalAgendamento) && profissionalAgendamento.isValid()) {
            agendamentoOld.profissionalAgendamento = profissionalAgendamento;
            agendamentoOld.nomeProfissional = profissionalAgendamento.getPessoa().getNome();
        } else {
            agendamentoOld.profissionalAgendamento = usuarioAuth;
            agendamentoOld.nomeProfissional = usuarioAuth.getPessoa().getNome();
        }
        if (BasicFunctions.isValid(agendamento.getComPreferencia())) {
            agendamentoOld.comPreferencia = agendamento.getComPreferencia();
        } else {
            agendamentoOld.comPreferencia = Boolean.FALSE;
        }
        if (BasicFunctions.isNotEmpty(statusAgendamento) && statusAgendamento.isValid()) {
            agendamentoOld.statusAgendamento = statusAgendamento;
        } else {
            agendamentoOld.statusAgendamento = StatusAgendamento.statusAgendado();
        }
        if (BasicFunctions.isNotEmpty(pessoaAgendamento) && pessoaAgendamento.isValid()) {
            agendamentoOld.pessoaAgendamento = pessoaAgendamento;
            agendamentoOld.nomePessoa = pessoaAgendamento.getNome();
        }
        if (BasicFunctions.isNotEmpty(agendamentoOld) && agendamentoOld.isValid()) {
            agendamentoOld.agendamentoOld = agendamentoOld;
        }
        agendamentoOld.ativo = Boolean.TRUE;
        agendamentoOld.usuario = Contexto.getContextUser(context);
        agendamentoOld.usuarioAcao = Contexto.getContextUser(context);
        agendamentoOld.dataAcao = Contexto.dataHoraContexto(organizacaoAgendamento);
        return agendamentoOld;
    }

    public Boolean isValid() {
        return BasicFunctions.isValid(this.id);
    }

    public Boolean comPreferencia() {
        return BasicFunctions.isNotEmpty(this.comPreferencia);
    }

    public Boolean semPreferencia() {
        return !this.comPreferencia();
    }

    public Boolean hasAgendamentoOld() {
        return BasicFunctions.isNotEmpty(this.agendamentoOld) && BasicFunctions.isValid(this.agendamentoOld.id);
    }

    public Agendamento cancelarAgendamento(Agendamento agendamento, SecurityContext context) {
        Usuario usuarioAuth = Contexto.getContextUser(context);

        agendamento.setStatusAgendamento(StatusAgendamento.statusCancelado());
        agendamento.setUsuarioAcao(usuarioAuth);
        agendamento.setAtivo(Boolean.FALSE);
        agendamento.setDataAcao(Contexto.dataHoraContexto(agendamento.getOrganizacaoAgendamento()));
        agendamento.setSystemDateDeleted(Contexto.dataHoraContexto(agendamento.getOrganizacaoAgendamento()));
        this.statusAgendamento = StatusAgendamento.statusCancelado();
        this.usuarioAcao = Contexto.getContextUser(context);
        this.dataAcao = Contexto.dataHoraContexto(organizacaoAgendamento);
        return agendamento;
    }

    public Agendamento marcarComoLivre(Agendamento agendamento, SecurityContext context){
        Usuario usuarioAuth = Contexto.getContextUser(context);

        agendamento.setStatusAgendamento(StatusAgendamento.statusLivre());
        agendamento.setUsuarioAcao(usuarioAuth);
        agendamento.setAtivo(Boolean.TRUE);
        agendamento.setDataAcao(Contexto.dataHoraContexto(agendamento.getOrganizacaoAgendamento()));
        agendamento.setSystemDateDeleted(null);
        return agendamento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoAgendamento getTipoAgendamento() {
        return tipoAgendamento;
    }

    public void setTipoAgendamento(TipoAgendamento tipoAgendamento) {
        this.tipoAgendamento = tipoAgendamento;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Pessoa getPessoaAgendamento() {
        return pessoaAgendamento;
    }

    public void setPessoaAgendamento(Pessoa pessoaAgendamento) {
        this.pessoaAgendamento = pessoaAgendamento;
    }

    public Usuario getProfissionalAgendamento() {
        return profissionalAgendamento;
    }

    public void setProfissionalAgendamento(Usuario profissionalAgendamento) {
        this.profissionalAgendamento = profissionalAgendamento;
    }

    public StatusAgendamento getStatusAgendamento() {
        return statusAgendamento;
    }

    public void setStatusAgendamento(StatusAgendamento statusAgendamento) {
        this.statusAgendamento = statusAgendamento;
    }

    public Organizacao getOrganizacaoAgendamento() {
        return organizacaoAgendamento;
    }

    public void setOrganizacaoAgendamento(Organizacao organizacaoAgendamento) {
        this.organizacaoAgendamento = organizacaoAgendamento;
    }

    public LocalDate getDataAgendamento() {
        return dataAgendamento;
    }

    public void setDataAgendamento(LocalDate dataAgendamento) {
        this.dataAgendamento = dataAgendamento;
    }

    public LocalTime getHorarioAgendamento() {
        return horarioAgendamento;
    }

    public void setHorarioAgendamento(LocalTime horarioAgendamento) {
        this.horarioAgendamento = horarioAgendamento;
    }

    public String getNomePessoa() {
        return nomePessoa;
    }

    public void setNomePessoa(String nomePessoa) {
        this.nomePessoa = nomePessoa;
    }

    public String getNomeProfissional() {
        return nomeProfissional;
    }

    public void setNomeProfissional(String nomeProfissional) {
        this.nomeProfissional = nomeProfissional;
    }

    public Boolean getComPreferencia() {
        return comPreferencia;
    }

    public void setComPreferencia(Boolean comPreferencia) {
        this.comPreferencia = comPreferencia;
    }

    public Agendamento getAgendamentoOld() {
        return agendamentoOld;
    }

    public void setAgendamentoOld(Agendamento agendamentoOld) {
        this.agendamentoOld = agendamentoOld;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Usuario getUsuarioAcao() {
        return usuarioAcao;
    }

    public void setUsuarioAcao(Usuario usuarioAcao) {
        this.usuarioAcao = usuarioAcao;
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

    public Boolean ativo() {
        return BasicFunctions.isValid(this.ativo) && this.ativo;
    }
}
