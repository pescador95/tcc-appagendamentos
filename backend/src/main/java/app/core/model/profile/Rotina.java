package app.core.model.profile;

import app.agendamento.model.pessoa.Usuario;
import app.core.utils.BasicFunctions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rotina")

public class Rotina extends PanacheEntityBase {

    @Column()
    @SequenceGenerator(name = "rotinaIdSequence", sequenceName = "rotina_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "rotinaIdSequence")
    @Id
    private Long id;

    @Column()
    private String nome;

    @Column()
    private String icon;

    @Column()
    private String path;

    @Column()
    private String titulo;

    @ManyToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JoinColumn(name = "usuarioId", insertable = false, updatable = false)
    private Usuario usuario;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "usuarioId")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Usuario usuarioAcao;

    @Column()
    @JsonIgnore
    private LocalDateTime dataAcao;

    public Rotina() {

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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
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
