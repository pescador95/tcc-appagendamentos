package app.agendamento.filters.configurador;

import app.agendamento.model.configurador.ConfiguradorAusencia;
import app.core.utils.BasicFunctions;

import java.time.LocalDate;
import java.time.LocalTime;

import static app.core.utils.StringBuilder.makeQueryString;

public class ConfiguradorAusenciaFilters {

    public static String makeConfiguradorAusenciaQueryStringByFilters(String nomeAusencia, LocalDate dataInicio, LocalDate dataFim, LocalTime horaInicio, LocalTime horaFim, String observacao) {

        String queryString = "";

        if (BasicFunctions.isNotEmpty(nomeAusencia)) {
            queryString += makeQueryString(nomeAusencia, "nomeAusencia", ConfiguradorAusencia.class);
        }
        if (BasicFunctions.isValid(dataInicio)) {
            queryString += makeQueryString(dataInicio, "dataInicio", ConfiguradorAusencia.class);
        }
        if (BasicFunctions.isValid(dataFim)) {
            queryString += makeQueryString(dataFim, "dataFim", ConfiguradorAusencia.class);
        }
        if (BasicFunctions.isValid(horaInicio)) {
            queryString += makeQueryString(horaInicio, "horaInicio", ConfiguradorAusencia.class);
        }
        if (BasicFunctions.isValid(horaFim)) {
            queryString += makeQueryString(horaFim, "horaFim", ConfiguradorAusencia.class);
        }
        if (BasicFunctions.isNotEmpty(observacao)) {
            queryString += makeQueryString(observacao, "observacao", ConfiguradorAusencia.class);
        }

        return queryString;
    }
}
