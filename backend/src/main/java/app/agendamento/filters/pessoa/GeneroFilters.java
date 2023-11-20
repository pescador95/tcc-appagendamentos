package app.agendamento.filters.pessoa;

import app.agendamento.model.pessoa.Genero;
import app.core.utils.BasicFunctions;

import static app.core.utils.StringBuilder.makeQueryString;

public class GeneroFilters {

    public static String makeGeneroQueryStringByFilters(Long id, String genero) {

        String queryString = "";

        if (BasicFunctions.isValid(id)) {
            queryString += makeQueryString(id, "id", Genero.class);
        }
        if (BasicFunctions.isNotEmpty(genero)) {
            queryString += makeQueryString(genero, "genero", Genero.class);
        }

        return queryString;
    }
}
