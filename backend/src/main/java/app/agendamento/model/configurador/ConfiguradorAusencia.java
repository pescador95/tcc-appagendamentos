package app.agendamento.model.configurador;

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
@Table(name = "configuradorAusencia", indexes = {
        @Index(name = "iconfiguradorAusenciaak1", columnList = "dataInicioAusencia, dataFimAusencia")
})
@JsonIgnoreProperties({"usuarioAcao", "dataAcao"})

public class ConfiguradorAusencia extends PanacheEntityBase {

    @Column()
    @SequenceGenerator(name = "configuradorAusenciaIdSequence", sequenceName = "configuradorAusencia_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "configuradorAusenciaIdSequence")
    @Id
    private Long id;

    @Column()
    private String nomeAusencia;

    @Column()
    private LocalDate dataInicioAusencia;

    @Column()
    private LocalDate dataFimAusencia;

    @Column()
    private LocalTime horaInicioAusencia;

    @Column()
    private LocalTime horaFimAusencia;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "configuradorausenciausuario", joinColumns = {
            @JoinColumn(name = "configuradorAusenciaId")}, inverseJoinColumns = {
            @JoinColumn(name = "usuarioId")})
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Usuario> profissionaisAusentes;

    @Column()
    private String observacao;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "usuarioId", insertable = false, updatable = false)
    private Usuario usuarioAcao;

    @Column()
    @JsonIgnore
    private LocalDateTime dataAcao;

    public ConfiguradorAusencia() {

    }

    public ConfiguradorAusencia(ConfiguradorAusencia pConfiguradorAusencia, List<Usuario> profissionaisAusentes, SecurityContext context){

        Usuario usuarioAuth = Contexto.getContextUser(context);

        if (BasicFunctions.isNotEmpty(pConfiguradorAusencia.getNomeAusencia())) {
            this.setNomeAusencia(pConfiguradorAusencia.getNomeAusencia());
        }
        if (BasicFunctions.isValid(pConfiguradorAusencia.getDataInicioAusencia())) {
            this.setDataInicioAusencia(pConfiguradorAusencia.getDataInicioAusencia());
        }
        if (BasicFunctions.isValid(pConfiguradorAusencia.getDataFimAusencia())) {
            this.setDataFimAusencia(pConfiguradorAusencia.getDataFimAusencia());
        }
        if (BasicFunctions.isValid(pConfiguradorAusencia.getHoraInicioAusencia())) {
            this.setHoraInicioAusencia(pConfiguradorAusencia.getHoraInicioAusencia());
        }
        if (BasicFunctions.isValid(pConfiguradorAusencia.getHoraFimAusencia())) {
            this.setHoraFimAusencia(pConfiguradorAusencia.getHoraFimAusencia());
        }
        if (BasicFunctions.isNotEmpty(pConfiguradorAusencia.getObservacao())) {
            this.setObservacao(pConfiguradorAusencia.getObservacao());
        }
        if (BasicFunctions.isNotEmpty(profissionaisAusentes)) {
            this.setProfissionaisAusentes(new ArrayList<>());
            this.getProfissionaisAusentes().addAll(profissionaisAusentes);
        }
        if (BasicFunctions.isNotEmpty(pConfiguradorAusencia.getObservacao())){
            this.setObservacao(pConfiguradorAusencia.getObservacao());
        }
        this.setUsuarioAcao(usuarioAuth);
        this.setDataAcao(Contexto.dataHoraContexto());
    }

    public ConfiguradorAusencia configuradorAusencia(ConfiguradorAusencia pConfiguradorAusenciaOld, ConfiguradorAusencia pConfiguradorAusencia, List<Usuario> profissionaisAusentes, SecurityContext context){

        Usuario usuarioAuth = Contexto.getContextUser(context);

        if (BasicFunctions.isNotEmpty(pConfiguradorAusencia.getNomeAusencia())) {
            pConfiguradorAusenciaOld.setNomeAusencia(pConfiguradorAusencia.getNomeAusencia());
        }
        if (BasicFunctions.isValid(pConfiguradorAusencia.getDataInicioAusencia())) {
            pConfiguradorAusenciaOld.setDataInicioAusencia(pConfiguradorAusencia.getDataInicioAusencia());
        }
        if (BasicFunctions.isValid(pConfiguradorAusencia.getDataFimAusencia())) {
            pConfiguradorAusenciaOld.setDataFimAusencia(pConfiguradorAusencia.getDataFimAusencia());
        }
        if (BasicFunctions.isValid(pConfiguradorAusencia.getHoraInicioAusencia())) {
            pConfiguradorAusenciaOld.setHoraInicioAusencia(pConfiguradorAusencia.getHoraInicioAusencia());
        }
        if (BasicFunctions.isValid(pConfiguradorAusencia.getHoraFimAusencia())) {
            pConfiguradorAusenciaOld.setHoraFimAusencia(pConfiguradorAusencia.getHoraFimAusencia());
        }
        if (BasicFunctions.isNotEmpty(pConfiguradorAusencia.getObservacao())) {
            pConfiguradorAusenciaOld.setObservacao(pConfiguradorAusencia.getObservacao());
        }
        if (BasicFunctions.isNotEmpty(profissionaisAusentes)) {
            pConfiguradorAusenciaOld.setProfissionaisAusentes(new ArrayList<>());
            pConfiguradorAusenciaOld.getProfissionaisAusentes().addAll(profissionaisAusentes);
        }
        if (BasicFunctions.isNotEmpty(pConfiguradorAusencia.getObservacao())){
            pConfiguradorAusenciaOld.setObservacao(pConfiguradorAusencia.getObservacao());
        }
        pConfiguradorAusenciaOld.setUsuarioAcao(usuarioAuth);
        pConfiguradorAusenciaOld.setDataAcao(Contexto.dataHoraContexto());

        return pConfiguradorAusenciaOld;
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

    public String getNomeAusencia() {
        return nomeAusencia;
    }

    public void setNomeAusencia(String nomeAusencia) {
        this.nomeAusencia = nomeAusencia;
    }

    public LocalDate getDataInicioAusencia() {
        return dataInicioAusencia;
    }

    public void setDataInicioAusencia(LocalDate dataInicioAusencia) {
        this.dataInicioAusencia = dataInicioAusencia;
    }

    public LocalDate getDataFimAusencia() {
        return dataFimAusencia;
    }

    public void setDataFimAusencia(LocalDate dataFimAusencia) {
        this.dataFimAusencia = dataFimAusencia;
    }

    public LocalTime getHoraInicioAusencia() {
        return horaInicioAusencia;
    }

    public void setHoraInicioAusencia(LocalTime horaInicioAusencia) {
        this.horaInicioAusencia = horaInicioAusencia;
    }

    public LocalTime getHoraFimAusencia() {
        return horaFimAusencia;
    }

    public void setHoraFimAusencia(LocalTime horaFimAusencia) {
        this.horaFimAusencia = horaFimAusencia;
    }

    public List<Usuario> getProfissionaisAusentes() {
        return profissionaisAusentes;
    }

    public void setProfissionaisAusentes(List<Usuario> profissionaisAusentes) {
        this.profissionaisAusentes = profissionaisAusentes;
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
