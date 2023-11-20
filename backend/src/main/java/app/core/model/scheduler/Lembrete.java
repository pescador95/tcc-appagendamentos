package app.core.model.scheduler;

import app.agendamento.model.agendamento.Agendamento;
import app.core.utils.BasicFunctions;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "lembrete")
public class Lembrete extends PanacheEntityBase {
    public static final Long STATUS_NOTIFICACAO_ENVIADO = 1L;
    public static final Long STATUS_NOTIFICACAO_NAO_ENVIADO = 2L;
    public static final Long STATUS_NOTIFICACAO_FALHA_ENVIO = 3L;
    @Column()
    @SequenceGenerator(name = "lembreteIdSequence", sequenceName = "lembrete_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "lembreteIdSequence")
    @Id
    private Long id;
    @ManyToOne
    @JoinColumn(name = "agendamentoId")
    private Agendamento agendamentoLembrete;
    @Column()
    private LocalDate dataLembrete;
    @Column()
    private LocalTime horarioLembrete;
    @Column(columnDefinition = "TEXT")
    private String mensagem;
    @Column()
    private Long statusNotificacao;

    @Column()
    private String statusLembrete;

    @Column()
    private LocalDateTime dataHoraEnvio;

    @Column()
    private LocalDateTime dataAcao;

    @ManyToOne
    @JoinColumn(name = "queueId")
    private Thread thread;


    public Lembrete() {
    }

    public Boolean lembreteEnviado() {
        return BasicFunctions.isValid(this.statusNotificacao) && this.statusNotificacao.equals(STATUS_NOTIFICACAO_ENVIADO);
    }

    public Boolean lembreteNaoEnviado() {
        return BasicFunctions.isValid(this.statusNotificacao) && this.statusNotificacao.equals(STATUS_NOTIFICACAO_NAO_ENVIADO);
    }

    public Boolean lembreteFalhaEnvio() {
        return BasicFunctions.isValid(this.statusNotificacao) && this.statusNotificacao.equals(STATUS_NOTIFICACAO_FALHA_ENVIO);
    }

    public String statusLembrete() {
        if (lembreteEnviado()) {
            return "Enviado";
        } else if (lembreteNaoEnviado()) {
            return "Não enviado";
        } else if (lembreteFalhaEnvio()) {
            return "Falha no envio";
        } else {
            return "Não enviado";
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Agendamento getAgendamentoLembrete() {
        return agendamentoLembrete;
    }

    public void setAgendamentoLembrete(Agendamento agendamentoLembrete) {
        this.agendamentoLembrete = agendamentoLembrete;
    }

    public LocalDate getDataLembrete() {
        return dataLembrete;
    }

    public void setDataLembrete(LocalDate dataLembrete) {
        this.dataLembrete = dataLembrete;
    }

    public LocalTime getHorarioLembrete() {
        return horarioLembrete;
    }

    public void setHorarioLembrete(LocalTime horarioLembrete) {
        this.horarioLembrete = horarioLembrete;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Long getStatusNotificacao() {
        return statusNotificacao;
    }

    public void setStatusNotificacao(Long statusNotificacao) {
        this.statusNotificacao = statusNotificacao;
    }

    public String getStatusLembrete() {
        return statusLembrete;
    }

    public void setStatusLembrete(String statusLembrete) {
        this.statusLembrete = statusLembrete;
    }

    public LocalDateTime getDataHoraEnvio() {
        return dataHoraEnvio;
    }

    public void setDataHoraEnvio(LocalDateTime dataHoraEnvio) {
        this.dataHoraEnvio = dataHoraEnvio;
    }

    public LocalDateTime getDataAcao() {
        return dataAcao;
    }

    public void setDataAcao(LocalDateTime dataAcao) {
        this.dataAcao = dataAcao;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }
}
