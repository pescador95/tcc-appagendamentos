package app.core.filters.profile;

import app.core.model.profile.Rotina;
import app.core.utils.BasicFunctions;

import static app.core.utils.StringBuilder.makeQueryString;

public class RotinaFilters {

    public static String makeRotinaQueryStringByFilters(Long id, String nome, String icon, String path, String titulo) {
        String queryString = "";

        if (BasicFunctions.isValid(id)) {
            queryString += makeQueryString(id, "id", Rotina.class);
        }
        if (BasicFunctions.isNotEmpty(nome)) {
            queryString += makeQueryString(nome, "nome", Rotina.class);
        }
        if (BasicFunctions.isNotEmpty(icon)) {
            queryString += makeQueryString(icon, "icon", Rotina.class);
        }
        if (BasicFunctions.isNotEmpty(path)) {
            queryString += makeQueryString(path, "path", Rotina.class);
        }
        if (BasicFunctions.isNotEmpty(titulo)) {
            queryString += makeQueryString(titulo, "titulo", Rotina.class);
        }
        return queryString;
    }
}
