package app.agendamento.filters.statusAgendamento;

import app.agendamento.model.agendamento.StatusAgendamento;
import app.core.utils.BasicFunctions;

import static app.core.utils.StringBuilder.makeQueryString;

public class StatusFilters {

    public static String makeStatusAgendamentoQueryStringByFilters(Long id, String status) {

        String queryString = "";

        if (BasicFunctions.isValid(id)) {
            queryString += makeQueryString(id, "id", StatusAgendamento.class);
        }
        if (BasicFunctions.isNotEmpty(status)) {
            queryString += makeQueryString(status, "status", StatusAgendamento.class);
        }

        return queryString;
    }
}

