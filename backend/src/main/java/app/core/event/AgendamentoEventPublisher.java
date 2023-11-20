package app.core.event;

import app.agendamento.model.agendamento.Agendamento;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

@ApplicationScoped
public class AgendamentoEventPublisher {

    @Inject
    Event<AgendamentoCreatedEvent> agendamentoCreatedEvent;

    @Inject
    Event<AgendamentoUpdatedEvent> agendamentoUpdatedEvent;

    @Inject
    Event<AgendamentoDeletedEvent> agendamentoDeletedEvent;


    public void onCreate(Agendamento agendamento) {
        AgendamentoCreatedEvent evento = new AgendamentoCreatedEvent(agendamento);
        agendamentoCreatedEvent.fire(evento);
    }

    public void onUpdate(Agendamento agendamento) {
        AgendamentoUpdatedEvent evento = new AgendamentoUpdatedEvent(agendamento);
        agendamentoUpdatedEvent.fire(evento);
    }

    public void onDelete(Agendamento agendamento) {
        AgendamentoDeletedEvent evento = new AgendamentoDeletedEvent(agendamento);
        agendamentoDeletedEvent.fire(evento);
    }
}
