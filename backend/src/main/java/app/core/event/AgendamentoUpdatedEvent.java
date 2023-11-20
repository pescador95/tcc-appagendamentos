package app.core.event;

import app.agendamento.model.agendamento.Agendamento;

public record AgendamentoUpdatedEvent(Agendamento agendamento) {
}

