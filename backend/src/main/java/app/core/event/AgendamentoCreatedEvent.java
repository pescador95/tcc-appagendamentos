package app.core.event;

import app.agendamento.model.agendamento.Agendamento;

public record AgendamentoCreatedEvent(Agendamento agendamento) {
}