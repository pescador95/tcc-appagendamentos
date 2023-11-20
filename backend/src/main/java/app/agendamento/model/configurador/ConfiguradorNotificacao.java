package app.agendamento.model.configurador;

import app.agendamento.model.pessoa.Usuario;
import app.core.utils.BasicFunctions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "configuradorNotificacao")
@JsonIgnoreProperties({"usuarioAcao", "dataAcao"})

public class ConfiguradorNotificacao extends PanacheEntityBase {

    @Column()
    @SequenceGenerator(name = "configuradorNotificacaoIdSequence", sequenceName = "configuradorNotificacao_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "configuradorNotificacaoIdSequence")
    @Id
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String mensagem;

    @Column()
    private Long dataIntervalo;

    @Column()
    private LocalTime horaMinutoIntervalo;

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

    public ConfiguradorNotificacao() {

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

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Long getDataIntervalo() {
        return dataIntervalo;
    }

    public void setDataIntervalo(Long dataIntervalo) {
        this.dataIntervalo = dataIntervalo;
    }

    public LocalTime getHoraMinutoIntervalo() {
        return horaMinutoIntervalo;
    }

    public void setHoraMinutoIntervalo(LocalTime horaMinutoIntervalo) {
        this.horaMinutoIntervalo = horaMinutoIntervalo;
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
