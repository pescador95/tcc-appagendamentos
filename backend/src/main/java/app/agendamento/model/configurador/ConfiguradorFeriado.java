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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "configuradorFeriado", indexes = {
        @Index(name = "iconfiguradorFeriadoak1", columnList = "dataFeriado, horaInicioFeriado, horaFimFeriado")
})
@JsonIgnoreProperties({"usuarioAcao", "dataAcao"})

public class ConfiguradorFeriado extends PanacheEntityBase {

    @Column()
    @SequenceGenerator(name = "configuradorFeriadoIdSequence", sequenceName = "configuradorFeriado_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "configuradorFeriadoIdSequence")
    @Id
    private Long id;

    @Column()
    private String nomeFeriado;

    @Column()
    private LocalDate dataFeriado;

    @Column()
    private LocalTime horaInicioFeriado;

    @Column()
    private LocalTime horaFimFeriado;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "configuradorferiadoorganizacao", joinColumns = {
            @JoinColumn(name = "configuradorFeriadoId")}, inverseJoinColumns = {
            @JoinColumn(name = "organizacaoId")})
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Organizacao> organizacoesFeriado;

    @Column()
    private String observacao;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "usuarioId", insertable = false, updatable = false)
    private Usuario usuarioAcao;

    @Column()
    @JsonIgnore
    private LocalDateTime dataAcao;

    public ConfiguradorFeriado() {

    }
    public ConfiguradorFeriado(ConfiguradorFeriado pConfiguradorFeriado, List<Organizacao> organizacoesExcecoes, SecurityContext context){

        Usuario usuarioAuth = Contexto.getContextUser(context);

        if (BasicFunctions.isNotEmpty(pConfiguradorFeriado.getNomeFeriado())) {
            this.setNomeFeriado(pConfiguradorFeriado.getNomeFeriado());
        }
        if (BasicFunctions.isNotEmpty(pConfiguradorFeriado.getDataFeriado())) {
            this.setDataFeriado(pConfiguradorFeriado.getDataFeriado());
        }
        if (BasicFunctions.isNotEmpty(pConfiguradorFeriado.getHoraInicioFeriado())) {
            this.setHoraInicioFeriado(pConfiguradorFeriado.getHoraInicioFeriado());
        }
        if (BasicFunctions.isNotEmpty(pConfiguradorFeriado.getHoraFimFeriado())) {
            this.setHoraFimFeriado(pConfiguradorFeriado.getHoraFimFeriado());
        }
        if (BasicFunctions.isNotEmpty(pConfiguradorFeriado.getObservacao())) {
            this.setObservacao(pConfiguradorFeriado.getObservacao());
        }
        if (BasicFunctions.isNotEmpty(organizacoesExcecoes)) {
            this.setOrganizacoesFeriado(new ArrayList<>());
            this.getOrganizacoesFeriado().addAll(organizacoesExcecoes);
        }
        this.setUsuarioAcao(usuarioAuth);
        this.setDataAcao(Contexto.dataHoraContexto());
    }

    public ConfiguradorFeriado configuradorFeriado(ConfiguradorFeriado pConfiguradorFeriadoOld, ConfiguradorFeriado pConfiguradorFeriado, List<Organizacao> organizacoesExcecoes, SecurityContext context){

        Usuario usuarioAuth = Contexto.getContextUser(context);

        if (BasicFunctions.isNotEmpty(pConfiguradorFeriado.getNomeFeriado())) {
            pConfiguradorFeriadoOld.setNomeFeriado(pConfiguradorFeriado.getNomeFeriado());
        }
        if (BasicFunctions.isNotEmpty(pConfiguradorFeriado.getDataFeriado())) {
            pConfiguradorFeriadoOld.setDataFeriado(pConfiguradorFeriado.getDataFeriado());
        }
        if (BasicFunctions.isNotEmpty(pConfiguradorFeriado.getHoraInicioFeriado())) {
            pConfiguradorFeriadoOld.setHoraInicioFeriado(pConfiguradorFeriado.getHoraInicioFeriado());
        }
        if (BasicFunctions.isNotEmpty(pConfiguradorFeriado.getHoraFimFeriado())) {
            pConfiguradorFeriadoOld.setHoraFimFeriado(pConfiguradorFeriado.getHoraFimFeriado());
        }
        if (BasicFunctions.isNotEmpty(pConfiguradorFeriado.getObservacao())) {
            pConfiguradorFeriadoOld.setObservacao(pConfiguradorFeriado.getObservacao());
        }
        if (BasicFunctions.isNotEmpty(organizacoesExcecoes)) {
            pConfiguradorFeriadoOld.setOrganizacoesFeriado(new ArrayList<>());
            pConfiguradorFeriadoOld.getOrganizacoesFeriado().addAll(organizacoesExcecoes);
        }
        pConfiguradorFeriadoOld.setUsuarioAcao(usuarioAuth);
        pConfiguradorFeriadoOld.setDataAcao(Contexto.dataHoraContexto());
        return pConfiguradorFeriadoOld;
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

    public String getNomeFeriado() {
        return nomeFeriado;
    }

    public void setNomeFeriado(String nomeFeriado) {
        this.nomeFeriado = nomeFeriado;
    }

    public LocalDate getDataFeriado() {
        return dataFeriado;
    }

    public void setDataFeriado(LocalDate dataFeriado) {
        this.dataFeriado = dataFeriado;
    }

    public LocalTime getHoraInicioFeriado() {
        return horaInicioFeriado;
    }

    public void setHoraInicioFeriado(LocalTime horaInicioFeriado) {
        this.horaInicioFeriado = horaInicioFeriado;
    }

    public LocalTime getHoraFimFeriado() {
        return horaFimFeriado;
    }

    public void setHoraFimFeriado(LocalTime horaFimFeriado) {
        this.horaFimFeriado = horaFimFeriado;
    }

    public List<Organizacao> getOrganizacoesFeriado() {
        return organizacoesFeriado;
    }

    public void setOrganizacoesFeriado(List<Organizacao> organizacoesFeriado) {
        this.organizacoesFeriado = organizacoesFeriado;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
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
