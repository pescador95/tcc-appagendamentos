package app.core.listener;

import app.agendamento.model.agendamento.Agendamento;
import app.core.event.AgendamentoCreatedEvent;
import app.core.event.AgendamentoDeletedEvent;
import app.core.event.AgendamentoUpdatedEvent;
import app.core.scheduler.Scheduler;
import app.core.thread.LembreteThread;
import app.core.utils.BasicFunctions;
import app.core.utils.Contexto;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class AgendamentoListener {

    @Inject
    LembreteThread lembreteThread;

    private final Scheduler scheduler = new Scheduler();

    public void onAgendamentoCreated(@NotNull AgendamentoCreatedEvent event) {
        verificarAgendamentoEAdicionarNaFila(event.agendamento());
    }

    public void onAgendamentoUpdated(@NotNull AgendamentoUpdatedEvent event) {
        verificarAgendamentoEAtualizarNaFila(event.agendamento());
    }

    public void onAgendamentoDeleted(@NotNull AgendamentoDeletedEvent event) {
        verificarAgendamentoERemoverDaFila(event.agendamento());
    }

    private void verificarAgendamentoEAdicionarNaFila(Agendamento agendamento) {
        if (adicionarAgendamentoNaFila(agendamento)) {
            scheduler.verifyAndRunSchedulerEvery30seconds();
        }
    }

    private void verificarAgendamentoEAtualizarNaFila(Agendamento agendamento) {
        if (adicionarAgendamentoNaFila(agendamento)) {
            scheduler.verifyAndRunSchedulerEvery30seconds();
        }
    }

    private void verificarAgendamentoERemoverDaFila(Agendamento agendamento) {
        if (removerAgendamentoDaFila(agendamento)) {
            scheduler.verifyAndRunSchedulerEvery30seconds();
        }
    }

    public Boolean removerAgendamentoDaFila(Agendamento agendamento) {
        if (BasicFunctions.isNotEmpty(agendamento)) {
            if (agendamento.ativo() && !agendamento.getStatusAgendamento().agendado() || !agendamento.ativo()
                    || agendamento.getStatusAgendamento().agendado()
                    && agendamento.getDataAgendamento().isEqual(Contexto.dataContexto())
                    && agendamento.getHorarioAgendamento().isBefore(Contexto.horarioContexto())) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    public Boolean adicionarAgendamentoNaFila(Agendamento agendamento) {
        if (BasicFunctions.isNotEmpty(agendamento)) {
            if (agendamento.ativo() && agendamento.getStatusAgendamento().agendado()
                    && agendamento.getDataAgendamento().isAfter(Contexto.dataContexto())
                    || agendamento.getDataAgendamento().isEqual(Contexto.dataContexto()) && !agendamento.getHorarioAgendamento().isBefore(Contexto.horarioContexto(agendamento.getOrganizacaoAgendamento()))) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}
