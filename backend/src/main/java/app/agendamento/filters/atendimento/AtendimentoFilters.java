package app.agendamento.filters.atendimento;

import app.agendamento.model.agendamento.Atendimento;
import app.core.utils.BasicFunctions;

import java.time.LocalDate;

import static app.core.utils.StringBuilder.makeQueryString;

public class AtendimentoFilters {

    public static String makeAtendimentoQueryStringByFilters(LocalDate dataAtendimento, LocalDate dataInicio,
                                                             LocalDate dataFim, String usuarioId, String atividade, String evolucaoSintomas, String avaliacao) {

        String queryString = "";

        if (BasicFunctions.isValid(dataAtendimento)) {
            queryString += makeQueryString(dataAtendimento, "dataAtendimento", Atendimento.class);
        }
        if (BasicFunctions.isValid(dataInicio)) {
            queryString += makeQueryString(dataInicio, "dataInicio", Atendimento.class);
        }
        if (BasicFunctions.isValid(dataFim)) {
            queryString += makeQueryString(dataFim, "dataFim", Atendimento.class);
        }
        if (BasicFunctions.isValid(usuarioId)) {
            queryString += makeQueryString(usuarioId, "usuarioId", Atendimento.class);
        }
        if (BasicFunctions.isNotEmpty(atividade)) {
            queryString += makeQueryString(atividade, "atividade", Atendimento.class);
        }
        if (BasicFunctions.isNotEmpty(evolucaoSintomas)) {
            queryString += makeQueryString(evolucaoSintomas, "evolucaoSintomas", Atendimento.class);
        }
        if (BasicFunctions.isNotEmpty(avaliacao)) {
            queryString += makeQueryString(avaliacao, "avaliacao", Atendimento.class);
        }

        return queryString;
    }
}
