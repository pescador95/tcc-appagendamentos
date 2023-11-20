package app.core.filters.auth;

import app.core.utils.BasicFunctions;

import javax.management.relation.Role;

import static app.core.utils.StringBuilder.makeQueryString;

public class RoleFilters {

    public static String makeRoleQueryStringByFilters(Long id, String nome, String privilegio, Boolean admin, Long usuarioId) {

        String queryString = "";

        if (BasicFunctions.isValid(id)) {
            queryString += makeQueryString(id, "id", Role.class);
        }
        if (BasicFunctions.isNotEmpty(nome)) {
            queryString += makeQueryString(nome, "nome", Role.class);
        }
        if (BasicFunctions.isNotEmpty(privilegio)) {
            queryString += makeQueryString(privilegio, "privilegio", Role.class);
        }
        if (BasicFunctions.isValid(admin)) {
            queryString += makeQueryString(admin, "admin", Role.class);
        }
        if (BasicFunctions.isValid(usuarioId)) {
            queryString += makeQueryString(usuarioId, "usuarioId", Role.class);
        }


        return queryString;
    }
}
