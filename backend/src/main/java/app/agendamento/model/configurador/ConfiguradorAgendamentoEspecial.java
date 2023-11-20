package app.agendamento.model.configurador;

import app.agendamento.model.agendamento.TipoAgendamento;
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
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "configuradorAgendamentoEspecial", indexes = {
        @Index(name = "iconfiguradoragendamentoespecialak1", columnList = "dataInicio, dataFim, profissionalId, organizacaoId")
})
@JsonIgnoreProperties({"usuarioAcao", "dataAcao"})

public class ConfiguradorAgendamentoEspecial extends PanacheEntityBase {

    @Column()
    @SequenceGenerator(name = "configuradorAgendamentoEspecialIdSequence", sequenceName = "configuradorAgendamentoEspecial_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "configuradorAgendamentoEspecialIdSequence")
    @Id
    private Long id;

    @Column()
    private String nome;

    @ManyToOne
    @JoinColumn(name = "profissionalId")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Usuario profissionalConfigurador;

    @Column()
    private LocalDate dataInicio;
    @Column()
    private LocalDate dataFim;

    @ManyToOne
    @JoinColumn(name = "organizacaoId")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Organizacao organizacaoConfigurador;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "configuradorespecialtipoagendamento", joinColumns = {
            @JoinColumn(name = "configAgendamentoEspecialId")}, inverseJoinColumns = {
            @JoinColumn(name = "tipoAgendamentoId")})
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<TipoAgendamento> tiposAgendamentos;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "usuarioId", insertable = false, updatable = false)
    private Usuario usuarioAcao;

    @Column()
    @JsonIgnore
    private LocalDateTime dataAcao;

    public ConfiguradorAgendamentoEspecial() {

    }

    public ConfiguradorAgendamentoEspecial(ConfiguradorAgendamentoEspecial pConfiguradorAgendamentoEspecial, List<TipoAgendamento> tiposAgendamentos, Organizacao organizacaoConfigurador, Usuario profissionalConfigurador, SecurityContext context ) {

        Usuario usuarioAuth = Contexto.getContextUser(context);

        if (BasicFunctions.isNotEmpty(tiposAgendamentos)) {
            this.setTiposAgendamentos(new ArrayList<>());
            this.getTiposAgendamentos().addAll(tiposAgendamentos);
        }
        if (BasicFunctions.isNotEmpty(pConfiguradorAgendamentoEspecial.getNome())) {
            this.setNome(pConfiguradorAgendamentoEspecial.getNome());
        }
        if (BasicFunctions.isNotEmpty(pConfiguradorAgendamentoEspecial.getDataInicio())) {
            this.setDataInicio(pConfiguradorAgendamentoEspecial.getDataInicio());
        }
        if (BasicFunctions.isNotEmpty(pConfiguradorAgendamentoEspecial.getDataFim())) {
            this.setDataFim(pConfiguradorAgendamentoEspecial.getDataFim());
        }
        this.organizacaoConfigurador = organizacaoConfigurador;
        this.profissionalConfigurador = profissionalConfigurador;
        this.setUsuarioAcao(usuarioAuth);
        this.setDataAcao(Contexto.dataHoraContexto());
    }

    public ConfiguradorAgendamentoEspecial configuradorAgendamentoEspecial(ConfiguradorAgendamentoEspecial pConfiguradorAgendamentoEspecialOld, ConfiguradorAgendamentoEspecial pConfiguradorAgendamentoEspecial, List<TipoAgendamento> tiposAgendamentos, Organizacao organizacaoConfigurador, Usuario profissionalConfigurador, SecurityContext context ) {

        Usuario usuarioAuth = Contexto.getContextUser(context);

        if (BasicFunctions.isNotEmpty(tiposAgendamentos)) {
            pConfiguradorAgendamentoEspecialOld.setTiposAgendamentos(new ArrayList<>());
            pConfiguradorAgendamentoEspecialOld.getTiposAgendamentos().addAll(tiposAgendamentos);
        }
        if (BasicFunctions.isNotEmpty(pConfiguradorAgendamentoEspecial.getNome())) {
            pConfiguradorAgendamentoEspecialOld.setNome(pConfiguradorAgendamentoEspecial.getNome());
        }
        if (BasicFunctions.isNotEmpty(pConfiguradorAgendamentoEspecial.getDataInicio())) {
            pConfiguradorAgendamentoEspecialOld.setDataInicio(pConfiguradorAgendamentoEspecial.getDataInicio());
        }
        if (BasicFunctions.isNotEmpty(pConfiguradorAgendamentoEspecial.getDataFim())) {
            pConfiguradorAgendamentoEspecialOld.setDataFim(pConfiguradorAgendamentoEspecial.getDataFim());
        }
        pConfiguradorAgendamentoEspecialOld.organizacaoConfigurador = organizacaoConfigurador;
        pConfiguradorAgendamentoEspecialOld.profissionalConfigurador = profissionalConfigurador;
        pConfiguradorAgendamentoEspecialOld.setUsuarioAcao(usuarioAuth);
        pConfiguradorAgendamentoEspecialOld.setDataAcao(Contexto.dataHoraContexto());

        return pConfiguradorAgendamentoEspecialOld;
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Usuario getProfissionalConfigurador() {
        return profissionalConfigurador;
    }

    public void setProfissionalConfigurador(Usuario profissionalConfigurador) {
        this.profissionalConfigurador = profissionalConfigurador;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public Organizacao getOrganizacaoConfigurador() {
        return organizacaoConfigurador;
    }

    public void setOrganizacaoConfigurador(Organizacao organizacaoConfigurador) {
        this.organizacaoConfigurador = organizacaoConfigurador;
    }

    public List<TipoAgendamento> getTiposAgendamentos() {
        return tiposAgendamentos;
    }

    public void setTiposAgendamentos(List<TipoAgendamento> tiposAgendamentos) {
        this.tiposAgendamentos = tiposAgendamentos;
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
