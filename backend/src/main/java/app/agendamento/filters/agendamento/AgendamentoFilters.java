package app.agendamento.filters.agendamento;

import app.agendamento.model.agendamento.Agendamento;
import app.core.utils.BasicFunctions;

import java.time.LocalDate;
import java.time.LocalTime;

import static app.core.utils.StringBuilder.makeQueryString;

public class AgendamentoFilters {

    public static String makeAgendamentoQueryStringByFilters(LocalDate dataAgendamento, LocalDate dataInicio, LocalDate dataFim, LocalTime horarioAgendamento, LocalTime horarioInicio, LocalTime HorarioFim, Long pessoaId, String nomePessoa, String nomeProfissional, Long idStatus, Long organizacaoId, Long tipoAgendamentoId, Long profissionalId) {

        String queryString = "";

        if (BasicFunctions.isValid(dataAgendamento)) {
            queryString += makeQueryString(dataAgendamento, "dataAgendamento", Agendamento.class);
        }
        if (BasicFunctions.isValid(dataInicio)) {
            queryString += makeQueryString(dataInicio, "dataInicio", Agendamento.class);
        }
        if (BasicFunctions.isValid(dataFim)) {
            queryString += makeQueryString(dataFim, "dataFim", Agendamento.class);
        }
        if (BasicFunctions.isValid(horarioAgendamento)) {
            queryString += makeQueryString(horarioAgendamento, "horarioAgendamento", Agendamento.class);
        }
        if (BasicFunctions.isValid(horarioInicio)) {
            queryString += makeQueryString(horarioInicio, "horarioInicio", Agendamento.class);
        }
        if (BasicFunctions.isValid(HorarioFim)) {
            queryString += makeQueryString(HorarioFim, "HorarioFim", Agendamento.class);
        }
        if (BasicFunctions.isValid(pessoaId)) {
            queryString += makeQueryString(pessoaId, "pessoaId", Agendamento.class);
        }
        if (BasicFunctions.isValid(idStatus)) {
            queryString += makeQueryString(idStatus, "StatusAgendamentoId", Agendamento.class);
        }
        if (BasicFunctions.isValid(organizacaoId)) {
            queryString += makeQueryString(organizacaoId, "organizacaoId", Agendamento.class);
        }
        if (BasicFunctions.isValid(tipoAgendamentoId)) {
            queryString += makeQueryString(tipoAgendamentoId, "tipoAgendamentoId", Agendamento.class);
        }
        if (BasicFunctions.isValid(horarioAgendamento)) {
            queryString += makeQueryString(horarioAgendamento, "horarioAgendamento", Agendamento.class);
        }
        if (BasicFunctions.isValid(profissionalId)) {
            queryString += makeQueryString(profissionalId, "profissionalId", Agendamento.class);
        }
        if (BasicFunctions.isNotEmpty(nomePessoa)) {
            queryString += makeQueryString(nomePessoa, "nomePessoa", Agendamento.class);
        }
        if (BasicFunctions.isNotEmpty(nomeProfissional)) {
            queryString += makeQueryString(nomeProfissional, "nomeProfissional", Agendamento.class);
        }

        return queryString;
    }
}
