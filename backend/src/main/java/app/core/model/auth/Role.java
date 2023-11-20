package app.core.model.auth;

import app.agendamento.model.pessoa.Usuario;
import app.core.utils.BasicFunctions;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.security.jpa.RolesValue;

import javax.persistence.*;

@Table(name = "role")
@Entity

public class Role extends PanacheEntityBase {

    @Column()
    @SequenceGenerator(name = "roleIdSequence", sequenceName = "role_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "roleIdSequence")
    @Id
    private Long id;

    @Column
    @RolesValue
    private String privilegio;

    @Column
    private Boolean admin;

    @ManyToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Usuario usuario;

    public Role() {

    }

    public Boolean hasPrivilegio() {
        return BasicFunctions.isNotEmpty(this.privilegio);
    }

    public Boolean isValid() {
        return BasicFunctions.isValid(this.id);
    }

    public Boolean admin() {
        return BasicFunctions.isNotEmpty(admin) && this.admin;
    }

    public void setUsuario() {
        this.id = Usuario.USUARIO;
    }

    public void setAdmin() {
        this.id = Usuario.ADMINISTRADOR;
        this.admin = true;
    }

    public void setBot() {
        this.id = Usuario.BOT;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrivilegio() {
        return privilegio;
    }

    public void setPrivilegio(String privilegio) {
        this.privilegio = privilegio;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
