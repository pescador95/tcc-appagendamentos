package app.core.filters.profile;

import app.core.model.profile.PerfilAcesso;
import app.core.utils.BasicFunctions;

import static app.core.utils.StringBuilder.makeQueryString;

public class PerfilAcessoFilters {

    public static String makePerfilAcessoQueryStringByFilters(Long id, String nome, Boolean criar, Boolean ler, Boolean atualizar, Boolean apagar, Long usuarioId) {
        String queryString = "";

        if (BasicFunctions.isValid(id)) {
            queryString += makeQueryString(id, "id", PerfilAcesso.class);
        }
        if (BasicFunctions.isNotEmpty(nome)) {
            queryString += makeQueryString(nome, "nome", PerfilAcesso.class);
        }
        if (BasicFunctions.isValid(criar)) {
            queryString += makeQueryString(criar, "criar", PerfilAcesso.class);
        }
        if (BasicFunctions.isValid(ler)) {
            queryString += makeQueryString(ler, "ler", PerfilAcesso.class);
        }
        if (BasicFunctions.isValid(atualizar)) {
            queryString += makeQueryString(atualizar, "atualizar", PerfilAcesso.class);
        }
        if (BasicFunctions.isValid(apagar)) {
            queryString += makeQueryString(apagar, "apagar", PerfilAcesso.class);
        }
        if (BasicFunctions.isValid(usuarioId)) {
            queryString += makeQueryString(usuarioId, "usuarioId", PerfilAcesso.class);
        }
        return queryString;
    }
}
