package app.core.event;

import app.agendamento.model.agendamento.Agendamento;

public record AgendamentoDeletedEvent(Agendamento agendamento) {
}
