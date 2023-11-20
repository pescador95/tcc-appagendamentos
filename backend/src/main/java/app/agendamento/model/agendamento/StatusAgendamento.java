package app.agendamento.model.agendamento;

import app.core.utils.BasicFunctions;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;

@Entity
@Table(name = "statusAgendamento")

public class StatusAgendamento extends PanacheEntityBase {

    public static final long AGENDADO = 1;
    public static final long REMARCADO = 2;
    public static final long CANCELADO = 3;
    public static final long LIVRE = 4;
    public static final long ATENDIDO = 5;

    @Column()
    @SequenceGenerator(name = "statusAgendamentoIdSequence", sequenceName = "statusAgendamento_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "statusAgendamentoIdSequence")
    @Id
    private Long id;
    @Column()
    private String status;

    public StatusAgendamento() {
    }

    public Boolean agendado() {
        return this.id == AGENDADO;
    }


    public Boolean remarcado() {
        return this.id == REMARCADO;
    }

    public Boolean cancelado() {
        return this.id == CANCELADO;
    }

    public Boolean livre() {
        return this.id == LIVRE;
    }

    public Boolean atendido() {
        return this.id == ATENDIDO;
    }

    public Boolean isValid() {
        return BasicFunctions.isValid(this.id);
    }

    public static StatusAgendamento statusLivre() {
        return StatusAgendamento.findById(StatusAgendamento.LIVRE);
    }

    public static StatusAgendamento statusAgendado() {
        return StatusAgendamento.findById(StatusAgendamento.AGENDADO);
    }

    public static StatusAgendamento statusRemarcado() {
        return StatusAgendamento.findById(StatusAgendamento.REMARCADO);
    }

    public static StatusAgendamento statusCancelado() {
        return StatusAgendamento.findById(StatusAgendamento.CANCELADO);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
