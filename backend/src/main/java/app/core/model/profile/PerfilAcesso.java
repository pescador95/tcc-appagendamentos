package app.core.model.profile;

import app.agendamento.model.pessoa.Usuario;
import app.core.utils.BasicFunctions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "perfilacesso")

public class PerfilAcesso extends PanacheEntityBase {

    @Column()
    @SequenceGenerator(name = "perfilacessoIdSequence", sequenceName = "perfilacesso_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "perfilacessoIdSequence")
    @Id
    private Long id;

    @Column()
    private String nome;

    @Column()
    private Boolean criar;

    @Column()
    private Boolean ler;

    @Column()
    private Boolean atualizar;

    @Column()
    private Boolean apagar;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "rotinaperfilacesso", joinColumns = {
            @JoinColumn(name = "perfilacessoId")}, inverseJoinColumns = {
            @JoinColumn(name = "rotinaId")})
    private List<Rotina> rotinas = new ArrayList<>();

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

    public PerfilAcesso() {

    }

    public Boolean isValid() {
        return BasicFunctions.isValid(this.id);
    }

    public Boolean hasRotinas() {
        return BasicFunctions.isValid(this.id) && BasicFunctions.isNotEmpty(this.rotinas);
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

    public Boolean getCriar() {
        return criar;
    }

    public void setCriar(Boolean criar) {
        this.criar = criar;
    }

    public Boolean getLer() {
        return ler;
    }

    public void setLer(Boolean ler) {
        this.ler = ler;
    }

    public Boolean getAtualizar() {
        return atualizar;
    }

    public void setAtualizar(Boolean atualizar) {
        this.atualizar = atualizar;
    }

    public Boolean getApagar() {
        return apagar;
    }

    public void setApagar(Boolean apagar) {
        this.apagar = apagar;
    }

    public List<Rotina> getRotinas() {
        return rotinas;
    }

    public void setRotinas(List<Rotina> rotinas) {
        this.rotinas = rotinas;
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
