package app.agendamento.filters.configurador;

import app.agendamento.model.configurador.ConfiguradorFeriado;
import app.core.utils.BasicFunctions;

import java.time.LocalDate;
import java.time.LocalTime;

import static app.core.utils.StringBuilder.makeQueryString;

public class ConfiguradorFeriadoFilters {

    public static String makeConfiguradorFeriadoQueryStringByFilters(String nomeFeriado, LocalDate dataFeriado, LocalDate dataInicio, LocalDate dataFim, LocalTime horaInicio, LocalTime horaFim, Long organizacaoId, String observacao) {
        String queryString = "";

        if (BasicFunctions.isNotEmpty(nomeFeriado)) {
            queryString += makeQueryString(nomeFeriado, "nomeFeriado", ConfiguradorFeriado.class);
        }
        if (BasicFunctions.isValid(dataFeriado)) {
            queryString += makeQueryString(dataFeriado, "dataFeriado", ConfiguradorFeriado.class);
        }
        if (BasicFunctions.isValid(dataInicio)) {
            queryString += makeQueryString(dataInicio, "dataInicio", ConfiguradorFeriado.class);
        }
        if (BasicFunctions.isValid(dataFim)) {
            queryString += makeQueryString(dataFim, "dataFim", ConfiguradorFeriado.class);
        }
        if (BasicFunctions.isValid(horaInicio)) {
            queryString += makeQueryString(horaInicio, "horaInicio", ConfiguradorFeriado.class);
        }
        if (BasicFunctions.isValid(horaFim)) {
            queryString += makeQueryString(horaFim, "horaFim", ConfiguradorFeriado.class);
        }
        if (BasicFunctions.isNotEmpty(observacao)) {
            queryString += makeQueryString(observacao, "observacao", ConfiguradorFeriado.class);
        }

        if (BasicFunctions.isValid(organizacaoId)) {
            queryString += makeQueryString(organizacaoId, "organizacaoId", ConfiguradorFeriado.class);
        }
        return queryString;
    }
}
