package app.core.model.scheduler;

import app.core.utils.BasicFunctions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "thread")
public class Thread extends PanacheEntityBase {

    public static final Long STATUS_PENDENTE = 1L;
    public static final Long STATUS_EM_EXECUCAO = 2L;
    public static final Long STATUS_FINALIZADO = 3L;
    public static final Long STATUS_FALHA = 4L;
    public static final Long STATUS_CANCELADO = 5L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column()
    private Long status;

    @Column()
    private String statusDescricao;

    @Column()
    @JsonIgnore
    private Boolean ativo;

    @Column()
    @JsonIgnore
    private LocalDateTime dataAcao;
    @Column()
    @JsonIgnore
    private LocalDateTime dataHoraInicio;
    @Column()
    @JsonIgnore
    private LocalDateTime dataHoraFim;
    @Column()
    @JsonIgnore
    private LocalDateTime systemDateDeleted;

    public Thread() {
    }

    public String statusDestricao() {
        if (BasicFunctions.isValid(this.status) && this.status.equals(STATUS_PENDENTE)) {
            return "Pendente";
        } else if (BasicFunctions.isValid(this.status) && this.status.equals(STATUS_EM_EXECUCAO)) {
            return "Em execução";
        } else if (BasicFunctions.isValid(this.status) && this.status.equals(STATUS_FINALIZADO)) {
            return "Finalizado";
        } else if (BasicFunctions.isValid(this.status) && this.status.equals(STATUS_FALHA)) {
            return "Falha";
        } else if (BasicFunctions.isValid(this.status) && this.status.equals(STATUS_CANCELADO)) {
            return "Cancelado";
        } else {
            return "Não identificado";
        }
    }

    public Boolean pendente() {
        return BasicFunctions.isValid(this.status) && this.status.equals(STATUS_PENDENTE);
    }

    public Boolean emExecucao() {
        return BasicFunctions.isValid(this.status) && this.status.equals(STATUS_EM_EXECUCAO);
    }

    public Boolean finalizado() {
        return BasicFunctions.isValid(this.status) && this.status.equals(STATUS_FINALIZADO);
    }

    public Boolean falha() {
        return BasicFunctions.isValid(this.status) && this.status.equals(STATUS_FALHA);
    }

    public Boolean cancelado() {
        return BasicFunctions.isValid(this.status) && this.status.equals(STATUS_CANCELADO);
    }

    public Boolean ThreadsPendentes() {

        List<Thread> threads = Thread.listAll();
        return BasicFunctions.isNotEmpty(threads)
                && threads.stream().anyMatch(thread -> !thread.status.equals(Thread.STATUS_FINALIZADO));
    }

    public Boolean ThreadsEmExecucao() {

        List<Thread> threads = Thread.listAll();
        return BasicFunctions.isNotEmpty(threads)
                && threads.stream().anyMatch(thread -> !thread.status.equals(Thread.STATUS_EM_EXECUCAO));
    }

    public Boolean ThreadsFinalizado() {

        List<Thread> threads = Thread.listAll();
        return BasicFunctions.isNotEmpty(threads)
                && threads.stream().anyMatch(thread -> !thread.status.equals(Thread.STATUS_FINALIZADO));
    }

    public Boolean ThreadsFalha() {

        List<Thread> threads = Thread.listAll();
        return BasicFunctions.isNotEmpty(threads)
                && threads.stream().anyMatch(thread -> !thread.status.equals(Thread.STATUS_FALHA));
    }

    public Boolean ThreadsCanceladas() {

        List<Thread> threads = Thread.listAll();
        return BasicFunctions.isNotEmpty(threads)
                && threads.stream().anyMatch(thread -> !thread.status.equals(Thread.STATUS_CANCELADO));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public String getStatusDescricao() {
        return statusDescricao;
    }

    public void setStatusDescricao(String statusDescricao) {
        this.statusDescricao = statusDescricao;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getDataAcao() {
        return dataAcao;
    }

    public void setDataAcao(LocalDateTime dataAcao) {
        this.dataAcao = dataAcao;
    }

    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    public void setDataHoraInicio(LocalDateTime dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

    public LocalDateTime getDataHoraFim() {
        return dataHoraFim;
    }

    public void setDataHoraFim(LocalDateTime dataHoraFim) {
        this.dataHoraFim = dataHoraFim;
    }

    public LocalDateTime getSystemDateDeleted() {
        return systemDateDeleted;
    }

    public void setSystemDateDeleted(LocalDateTime systemDateDeleted) {
        this.systemDateDeleted = systemDateDeleted;
    }
}
