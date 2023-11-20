package app.agendamento.model.pessoa;

import app.core.utils.BasicFunctions;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;

@Entity
@Table(name = "genero")

public class Genero extends PanacheEntityBase {

    @Column()
    @SequenceGenerator(name = "generoIdSequence", sequenceName = "generoAgendamento_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "generoAgendamentoIdSequence")
    @Id
    private Long id;
    @Column()
    private String genero;

    public Genero() {
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

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }
}
