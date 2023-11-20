package app.agendamento.filters.configurador;

import app.agendamento.model.configurador.ConfiguradorAgendamentoEspecial;
import app.core.utils.BasicFunctions;

import java.time.LocalDate;

import static app.core.utils.StringBuilder.makeQueryString;

public class ConfiguradorAgendamentoEspecialFilters {

    public static String makeConfiguradorAgendamentoEspecialQueryStringByFilters(String nome, Long profissionalId, LocalDate dataInicio, LocalDate dataFim, Long organizacaoId) {

        String queryString = "";

        if (BasicFunctions.isNotEmpty(nome)) {
            queryString += makeQueryString(nome, "nome", ConfiguradorAgendamentoEspecial.class);
        }
        if (BasicFunctions.isValid(profissionalId)) {
            queryString += makeQueryString(profissionalId, "profissionalId", ConfiguradorAgendamentoEspecial.class);
        }
        if (BasicFunctions.isValid(dataInicio)) {
            queryString += makeQueryString(dataInicio, "dataInicio", ConfiguradorAgendamentoEspecial.class);
        }
        if (BasicFunctions.isValid(dataFim)) {
            queryString += makeQueryString(dataFim, "dataFim", ConfiguradorAgendamentoEspecial.class);
        }
        if (BasicFunctions.isValid(organizacaoId)) {
            queryString += makeQueryString(organizacaoId, "organizacaoId", ConfiguradorAgendamentoEspecial.class);
        }
        return queryString;
    }
}
