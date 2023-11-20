package app.agendamento.model.configurador;

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
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "configuradorAgendamento")
@JsonIgnoreProperties({"usuarioAcao", "usuario", "dataAcao"})
@NamedQueries({
        @NamedQuery(name = "qListConfiguradorAgendamentoByOrganizacao", query = "SELECT c FROM ConfiguradorAgendamento c JOIN c.organizacaoConfigurador o WHERE o.id = :organizacaoId AND o.ativo = true"),
        @NamedQuery(name = "qConfiguradorAgendamentoByOrganizacaoProfissional", query = "SELECT c FROM ConfiguradorAgendamento c JOIN c.organizacaoConfigurador o JOIN c.profissionalConfigurador u WHERE o.id = :organizacaoId AND o.ativo = true AND u.id = :profissionalId AND u.ativo = true")
})

public class ConfiguradorAgendamento extends PanacheEntityBase {

    @Column()
    @SequenceGenerator(name = "configuradorAgendamentoIdSequence", sequenceName = "configuradorAgendamento_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "configuradorAgendamentoIdSequence")
    @Id
    private Long id;

    @Column()
    private String nome;
    @ManyToOne
    @JoinColumn(name = "organizacaoId")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Organizacao organizacaoConfigurador;
    @Column()
    private LocalTime horarioInicioManha;
    @Column()
    private LocalTime horarioFimManha;
    @Column()
    private LocalTime horarioInicioTarde;
    @Column()
    private LocalTime horarioFimTarde;
    @Column()
    private LocalTime horarioInicioNoite;
    @Column()
    private LocalTime horarioFimNoite;
    @Column()
    private LocalTime horaMinutoIntervalo;

    @Column()
    private LocalTime horaMinutoTolerancia;

    @Column()
    private Boolean agendaManha;

    @Column()
    private Boolean agendaTarde;

    @Column()
    private Boolean agendaNoite;

    @Column()
    private Boolean atendeSabado;

    @Column()
    private Boolean atendeDomingo;

    @Column()
    private Boolean agendaSabadoManha;

    @Column()
    private Boolean agendaSabadoTarde;

    @Column()
    private Boolean agendaSabadoNoite;

    @Column()
    private Boolean agendaDomingoManha;

    @Column()
    private Boolean agendaDomingoTarde;

    @Column()
    private Boolean agendaDomingoNoite;

    @ManyToOne
    @JoinColumn(name = "profissionalId")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Usuario profissionalConfigurador;

    @Column()
    private Boolean configuradorOrganizacao;

    @ManyToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JoinColumn(name = "usuarioId", insertable = false, updatable = false)
    private Usuario usuario;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "usuarioId", insertable = false, updatable = false)
    private Usuario usuarioAcao;

    @Column()
    @JsonIgnore
    private LocalDateTime dataAcao;

    public ConfiguradorAgendamento() {

    }

    public ConfiguradorAgendamento(ConfiguradorAgendamento pConfiguradorAgendamento, Organizacao pOrganizacao, Usuario pProfissional, SecurityContext context) {

        Usuario usuarioAuth = Contexto.getContextUser(context);

        if (BasicFunctions.isNotEmpty(pOrganizacao) && pOrganizacao.isValid()) {
            this.setOrganizacaoConfigurador(pOrganizacao);
        }
        if (BasicFunctions.isNotEmpty(pProfissional) && pProfissional.isValid()) {
            this.setProfissionalConfigurador(pProfissional);
        }
        if (BasicFunctions.isNotEmpty(pConfiguradorAgendamento.getNome())) {
            this.setNome(pConfiguradorAgendamento.getNome());
        }
        if (BasicFunctions.isValid(pConfiguradorAgendamento.getHorarioInicioManha())) {
            this.setHorarioInicioManha(pConfiguradorAgendamento.getHorarioInicioManha());
        }
        if (BasicFunctions.isValid(pConfiguradorAgendamento.getHorarioFimManha())) {
            this.setHorarioFimManha(pConfiguradorAgendamento.getHorarioFimManha());
        }
        if (BasicFunctions.isValid(pConfiguradorAgendamento.getHorarioInicioTarde())) {
            this.setHorarioInicioTarde(pConfiguradorAgendamento.getHorarioInicioTarde());
        }
        if (BasicFunctions.isValid(pConfiguradorAgendamento.getHorarioFimTarde())) {
            this.setHorarioFimTarde(pConfiguradorAgendamento.getHorarioFimTarde());
        }
        if (BasicFunctions.isValid(pConfiguradorAgendamento.getHorarioInicioNoite())) {
            this.setHorarioInicioNoite(pConfiguradorAgendamento.getHorarioInicioNoite());
        }
        if (BasicFunctions.isValid(pConfiguradorAgendamento.getHorarioFimNoite())) {
            this.setHorarioFimNoite(pConfiguradorAgendamento.getHorarioFimNoite());
        }
        if (BasicFunctions.isValid(pConfiguradorAgendamento.getHoraMinutoIntervalo())) {
            this.setHoraMinutoIntervalo(pConfiguradorAgendamento.getHoraMinutoIntervalo());
        }
        if (BasicFunctions.isValid(pConfiguradorAgendamento.getHoraMinutoTolerancia())) {
            this.setHoraMinutoTolerancia(pConfiguradorAgendamento.getHoraMinutoTolerancia());
        }
        if (BasicFunctions.isNotEmpty(pConfiguradorAgendamento.getAgendaManha())) {
            this.setAgendaManha(pConfiguradorAgendamento.getAgendaManha());
        } else {
            this.setAgendaManha(Boolean.FALSE);
        }
        if (BasicFunctions.isNotEmpty(pConfiguradorAgendamento.getAgendaTarde())) {
            this.setAgendaTarde(pConfiguradorAgendamento.getAgendaTarde());
        } else {
            this.setAgendaManha(Boolean.FALSE);
        }
        if (BasicFunctions.isNotEmpty(pConfiguradorAgendamento.getAgendaNoite())) {
            this.setAgendaNoite(pConfiguradorAgendamento.getAgendaNoite());
        } else {
            this.setAgendaNoite(Boolean.FALSE);
        }
        if (BasicFunctions.isNotEmpty(pConfiguradorAgendamento.getConfiguradorOrganizacao())) {
            this.setConfiguradorOrganizacao(pConfiguradorAgendamento.getConfiguradorOrganizacao());
        } else {
            this.setConfiguradorOrganizacao(Boolean.FALSE);
        }
        if (BasicFunctions.isNotEmpty(pConfiguradorAgendamento.getAgendaSabadoManha())) {
            this.setAgendaSabadoManha(pConfiguradorAgendamento.getAgendaSabadoManha());
        } else {
            this.setAgendaSabadoManha(Boolean.FALSE);
        }
        if (BasicFunctions.isNotEmpty(pConfiguradorAgendamento.getAgendaSabadoTarde())) {
            this.setAgendaSabadoTarde(pConfiguradorAgendamento.getAgendaSabadoTarde());
        } else {
            this.setAgendaSabadoManha(Boolean.FALSE);
        }
        if (BasicFunctions.isNotEmpty(pConfiguradorAgendamento.getAgendaSabadoNoite())) {
            this.setAgendaSabadoNoite(pConfiguradorAgendamento.getAgendaSabadoNoite());
        } else {
            this.setAgendaSabadoNoite(Boolean.FALSE);
        }
        if (BasicFunctions.isNotEmpty(pConfiguradorAgendamento.getAgendaDomingoManha())) {
            this.setAgendaDomingoManha(pConfiguradorAgendamento.getAgendaDomingoManha());
        } else {
            this.setAgendaDomingoManha(Boolean.FALSE);
        }
        if (BasicFunctions.isNotEmpty(pConfiguradorAgendamento.getAgendaDomingoTarde())) {
            this.setAgendaDomingoTarde(pConfiguradorAgendamento.getAgendaDomingoTarde());
        } else {
            this.setAgendaDomingoManha(Boolean.FALSE);
        }
        if (BasicFunctions.isNotEmpty(pConfiguradorAgendamento.getAgendaDomingoNoite())) {
            this.setAgendaDomingoNoite(pConfiguradorAgendamento.getAgendaDomingoNoite());
        } else {
            this.setAgendaDomingoNoite(Boolean.FALSE);
        }

        if (BasicFunctions.isNotEmpty(pConfiguradorAgendamento.getAtendeSabado())) {
            this.setAtendeSabado(pConfiguradorAgendamento.getAtendeSabado());
        } else {
            this.setAtendeSabado(Boolean.FALSE);
        }
        if (BasicFunctions.isNotEmpty(pConfiguradorAgendamento.getAtendeDomingo())) {
            this.setAtendeDomingo(pConfiguradorAgendamento.getAtendeDomingo());
        } else {
            this.setAtendeDomingo(Boolean.FALSE);
        }

        this.setUsuario(usuarioAuth);
        this.setUsuarioAcao(usuarioAuth);
        this.setDataAcao(Contexto.dataHoraContexto(usuarioAuth.getOrganizacaoDefault()));
    }

    public ConfiguradorAgendamento configuradorAgendamento(ConfiguradorAgendamento pConfiguradorAgendamentoOld, ConfiguradorAgendamento pConfiguradorAgendamento, Organizacao pOrganizacao, Usuario pProfissional, SecurityContext context) {

        Usuario usuarioAuth = Contexto.getContextUser(context);

        if (BasicFunctions.isNotEmpty(pOrganizacao) && pOrganizacao.isValid()) {
            pConfiguradorAgendamentoOld.setOrganizacaoConfigurador(pOrganizacao);
        }
        if (BasicFunctions.isNotEmpty(pProfissional) && pOrganizacao.isValid()) {
            pConfiguradorAgendamentoOld.setProfissionalConfigurador(pProfissional);
        }
        if (BasicFunctions.isNotEmpty(pConfiguradorAgendamento.getNome())) {
            pConfiguradorAgendamentoOld.setNome(pConfiguradorAgendamento.getNome());
        }
        if (BasicFunctions.isValid(pConfiguradorAgendamento.getHorarioInicioManha())) {
            pConfiguradorAgendamentoOld.setHorarioInicioManha(pConfiguradorAgendamento.getHorarioInicioManha());
        }
        if (BasicFunctions.isValid(pConfiguradorAgendamento.getHorarioFimManha())) {
            pConfiguradorAgendamentoOld.setHorarioFimManha(pConfiguradorAgendamento.getHorarioFimManha());
        }
        if (BasicFunctions.isValid(pConfiguradorAgendamento.getHorarioInicioTarde())) {
            pConfiguradorAgendamentoOld.setHorarioInicioTarde(pConfiguradorAgendamento.getHorarioInicioTarde());
        }
        if (BasicFunctions.isValid(pConfiguradorAgendamento.getHorarioFimTarde())) {
            pConfiguradorAgendamentoOld.setHorarioFimTarde(pConfiguradorAgendamento.getHorarioFimTarde());
        }
        if (BasicFunctions.isValid(pConfiguradorAgendamento.getHorarioInicioNoite())) {
            pConfiguradorAgendamentoOld.setHorarioInicioNoite(pConfiguradorAgendamento.getHorarioInicioNoite());
        }
        if (BasicFunctions.isValid(pConfiguradorAgendamento.getHorarioFimNoite())) {
            pConfiguradorAgendamentoOld.setHorarioFimNoite(pConfiguradorAgendamento.getHorarioFimNoite());
        }
        if (BasicFunctions.isValid(pConfiguradorAgendamento.getHoraMinutoIntervalo())) {
            pConfiguradorAgendamentoOld.setHoraMinutoIntervalo(pConfiguradorAgendamento.getHoraMinutoIntervalo());
        }
        if (BasicFunctions.isValid(pConfiguradorAgendamento.getHoraMinutoTolerancia())) {
            pConfiguradorAgendamentoOld.setHoraMinutoTolerancia(pConfiguradorAgendamento.getHoraMinutoTolerancia());
        }
        if (BasicFunctions.isNotEmpty(pConfiguradorAgendamento.getAgendaManha())) {
            pConfiguradorAgendamentoOld.setAgendaManha(pConfiguradorAgendamento.getAgendaManha());
        } else {
            pConfiguradorAgendamentoOld.setAgendaManha(Boolean.FALSE);
        }
        if (BasicFunctions.isNotEmpty(pConfiguradorAgendamento.getAgendaTarde())) {
            pConfiguradorAgendamentoOld.setAgendaTarde(pConfiguradorAgendamento.getAgendaTarde());
        } else {
            pConfiguradorAgendamentoOld.setAgendaManha(Boolean.FALSE);
        }
        if (BasicFunctions.isNotEmpty(pConfiguradorAgendamento.getAgendaNoite())) {
            pConfiguradorAgendamentoOld.setAgendaNoite(pConfiguradorAgendamento.getAgendaNoite());
        } else {
            pConfiguradorAgendamentoOld.setAgendaNoite(Boolean.FALSE);
        }
        if (BasicFunctions.isNotEmpty(pConfiguradorAgendamento.getConfiguradorOrganizacao())) {
            pConfiguradorAgendamentoOld.setConfiguradorOrganizacao(pConfiguradorAgendamento.getConfiguradorOrganizacao());
        } else {
            pConfiguradorAgendamentoOld.setConfiguradorOrganizacao(Boolean.FALSE);
        }
        if (BasicFunctions.isNotEmpty(pConfiguradorAgendamento.getAgendaSabadoManha())) {
            pConfiguradorAgendamentoOld.setAgendaSabadoManha(pConfiguradorAgendamento.getAgendaSabadoManha());
        } else {
            pConfiguradorAgendamentoOld.setAgendaSabadoManha(Boolean.FALSE);
        }
        if (BasicFunctions.isNotEmpty(pConfiguradorAgendamento.getAgendaSabadoTarde())) {
            pConfiguradorAgendamentoOld.setAgendaSabadoTarde(pConfiguradorAgendamento.getAgendaSabadoTarde());
        } else {
            pConfiguradorAgendamentoOld.setAgendaSabadoManha(Boolean.FALSE);
        }
        if (BasicFunctions.isNotEmpty(pConfiguradorAgendamento.getAgendaSabadoNoite())) {
            pConfiguradorAgendamentoOld.setAgendaSabadoNoite(pConfiguradorAgendamento.getAgendaSabadoNoite());
        } else {
            pConfiguradorAgendamentoOld.setAgendaSabadoNoite(Boolean.FALSE);
        }
        if (BasicFunctions.isNotEmpty(pConfiguradorAgendamento.getAgendaDomingoManha())) {
            pConfiguradorAgendamentoOld.setAgendaDomingoManha(pConfiguradorAgendamento.getAgendaDomingoManha());
        } else {
            pConfiguradorAgendamentoOld.setAgendaDomingoManha(Boolean.FALSE);
        }
        if (BasicFunctions.isNotEmpty(pConfiguradorAgendamento.getAgendaDomingoTarde())) {
            pConfiguradorAgendamentoOld.setAgendaDomingoTarde(pConfiguradorAgendamento.getAgendaDomingoTarde());
        } else {
            pConfiguradorAgendamentoOld.setAgendaDomingoManha(Boolean.FALSE);
        }
        if (BasicFunctions.isNotEmpty(pConfiguradorAgendamento.getAgendaDomingoNoite())) {
            pConfiguradorAgendamentoOld.setAgendaDomingoNoite(pConfiguradorAgendamento.getAgendaDomingoNoite());
        } else {
            pConfiguradorAgendamentoOld.setAgendaDomingoNoite(Boolean.FALSE);
        }

        if (BasicFunctions.isNotEmpty(pConfiguradorAgendamento.getAtendeSabado())) {
            pConfiguradorAgendamentoOld.setAtendeSabado(pConfiguradorAgendamento.getAtendeSabado());
        } else {
            pConfiguradorAgendamentoOld.setAtendeSabado(Boolean.FALSE);
        }
        if (BasicFunctions.isNotEmpty(pConfiguradorAgendamento.getAtendeDomingo())) {
            pConfiguradorAgendamentoOld.setAtendeDomingo(pConfiguradorAgendamento.getAtendeDomingo());
        } else {
            pConfiguradorAgendamentoOld.setAtendeDomingo(Boolean.FALSE);
        }

        pConfiguradorAgendamentoOld.setUsuario(usuarioAuth);
        pConfiguradorAgendamentoOld.setUsuarioAcao(usuarioAuth);
        pConfiguradorAgendamentoOld.setDataAcao(Contexto.dataHoraContexto(usuarioAuth.getOrganizacaoDefault()));
        return pConfiguradorAgendamentoOld;
    }

    public Boolean isValid() {
        return BasicFunctions.isValid(this.id);
    }

    public Boolean atendeSabado() {
        return BasicFunctions.isNotEmpty(this.atendeSabado) && this.atendeSabado;
    }

    public Boolean atendeDomingo() {
        return BasicFunctions.isNotEmpty(this.atendeDomingo) && this.atendeDomingo;
    }

    public Boolean agendaManha() {
        return BasicFunctions.isNotEmpty(this.agendaManha) && this.agendaManha;
    }

    public Boolean agendaTarde() {
        return BasicFunctions.isNotEmpty(this.agendaTarde) && this.agendaTarde;
    }

    public Boolean agendaNoite() {
        return BasicFunctions.isNotEmpty(this.agendaNoite) && this.agendaNoite;
    }

    public Boolean agendaSabadoManha() {
        return BasicFunctions.isNotEmpty(this.agendaSabadoManha) && this.agendaSabadoManha;
    }

    public Boolean agendaSabadoTarde() {
        return BasicFunctions.isNotEmpty(this.agendaSabadoTarde) && this.agendaSabadoTarde;
    }

    public Boolean agendaSabadoNoite() {
        return BasicFunctions.isNotEmpty(this.agendaSabadoNoite) && this.agendaSabadoNoite;
    }

    public Boolean agendaDomingoManha() {
        return BasicFunctions.isNotEmpty(this.agendaDomingoManha) && this.agendaDomingoManha;
    }

    public Boolean agendaDomingoTarde() {
        return BasicFunctions.isNotEmpty(this.agendaDomingoTarde) && this.agendaDomingoTarde;
    }

    public Boolean agendaDomingoNoite() {
        return BasicFunctions.isNotEmpty(this.agendaDomingoNoite) && this.agendaDomingoNoite;
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

    public Organizacao getOrganizacaoConfigurador() {
        return organizacaoConfigurador;
    }

    public void setOrganizacaoConfigurador(Organizacao organizacaoConfigurador) {
        this.organizacaoConfigurador = organizacaoConfigurador;
    }

    public LocalTime getHorarioInicioManha() {
        return horarioInicioManha;
    }

    public void setHorarioInicioManha(LocalTime horarioInicioManha) {
        this.horarioInicioManha = horarioInicioManha;
    }

    public LocalTime getHorarioFimManha() {
        return horarioFimManha;
    }

    public void setHorarioFimManha(LocalTime horarioFimManha) {
        this.horarioFimManha = horarioFimManha;
    }

    public LocalTime getHorarioInicioTarde() {
        return horarioInicioTarde;
    }

    public void setHorarioInicioTarde(LocalTime horarioInicioTarde) {
        this.horarioInicioTarde = horarioInicioTarde;
    }

    public LocalTime getHorarioFimTarde() {
        return horarioFimTarde;
    }

    public void setHorarioFimTarde(LocalTime horarioFimTarde) {
        this.horarioFimTarde = horarioFimTarde;
    }

    public LocalTime getHorarioInicioNoite() {
        return horarioInicioNoite;
    }

    public void setHorarioInicioNoite(LocalTime horarioInicioNoite) {
        this.horarioInicioNoite = horarioInicioNoite;
    }

    public LocalTime getHorarioFimNoite() {
        return horarioFimNoite;
    }

    public void setHorarioFimNoite(LocalTime horarioFimNoite) {
        this.horarioFimNoite = horarioFimNoite;
    }

    public LocalTime getHoraMinutoIntervalo() {
        return horaMinutoIntervalo;
    }

    public void setHoraMinutoIntervalo(LocalTime horaMinutoIntervalo) {
        this.horaMinutoIntervalo = horaMinutoIntervalo;
    }

    public LocalTime getHoraMinutoTolerancia() {
        return horaMinutoTolerancia;
    }

    public void setHoraMinutoTolerancia(LocalTime horaMinutoTolerancia) {
        this.horaMinutoTolerancia = horaMinutoTolerancia;
    }

    public Boolean getAgendaManha() {
        return agendaManha;
    }

    public void setAgendaManha(Boolean agendaManha) {
        this.agendaManha = agendaManha;
    }

    public Boolean getAgendaTarde() {
        return agendaTarde;
    }

    public void setAgendaTarde(Boolean agendaTarde) {
        this.agendaTarde = agendaTarde;
    }

    public Boolean getAgendaNoite() {
        return agendaNoite;
    }

    public void setAgendaNoite(Boolean agendaNoite) {
        this.agendaNoite = agendaNoite;
    }

    public Boolean getAtendeSabado() {
        return atendeSabado;
    }

    public void setAtendeSabado(Boolean atendeSabado) {
        this.atendeSabado = atendeSabado;
    }

    public Boolean getAtendeDomingo() {
        return atendeDomingo;
    }

    public void setAtendeDomingo(Boolean atendeDomingo) {
        this.atendeDomingo = atendeDomingo;
    }

    public Boolean getAgendaSabadoManha() {
        return agendaSabadoManha;
    }

    public void setAgendaSabadoManha(Boolean agendaSabadoManha) {
        this.agendaSabadoManha = agendaSabadoManha;
    }

    public Boolean getAgendaSabadoTarde() {
        return agendaSabadoTarde;
    }

    public void setAgendaSabadoTarde(Boolean agendaSabadoTarde) {
        this.agendaSabadoTarde = agendaSabadoTarde;
    }

    public Boolean getAgendaSabadoNoite() {
        return agendaSabadoNoite;
    }

    public void setAgendaSabadoNoite(Boolean agendaSabadoNoite) {
        this.agendaSabadoNoite = agendaSabadoNoite;
    }

    public Boolean getAgendaDomingoManha() {
        return agendaDomingoManha;
    }

    public void setAgendaDomingoManha(Boolean agendaDomingoManha) {
        this.agendaDomingoManha = agendaDomingoManha;
    }

    public Boolean getAgendaDomingoTarde() {
        return agendaDomingoTarde;
    }

    public void setAgendaDomingoTarde(Boolean agendaDomingoTarde) {
        this.agendaDomingoTarde = agendaDomingoTarde;
    }

    public Boolean getAgendaDomingoNoite() {
        return agendaDomingoNoite;
    }

    public void setAgendaDomingoNoite(Boolean agendaDomingoNoite) {
        this.agendaDomingoNoite = agendaDomingoNoite;
    }

    public Usuario getProfissionalConfigurador() {
        return profissionalConfigurador;
    }

    public void setProfissionalConfigurador(Usuario profissionalConfigurador) {
        this.profissionalConfigurador = profissionalConfigurador;
    }

    public Boolean getConfiguradorOrganizacao() {
        return configuradorOrganizacao;
    }

    public void setConfiguradorOrganizacao(Boolean configuradorOrganizacao) {
        this.configuradorOrganizacao = configuradorOrganizacao;
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

    public LocalDateTime getDataAcao() {
        return dataAcao;
    }

    public void setDataAcao(LocalDateTime dataAcao) {
        this.dataAcao = dataAcao;
    }
}
