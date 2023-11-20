package app.agendamento.filters.pessoa;

import app.agendamento.model.pessoa.Usuario;
import app.core.utils.BasicFunctions;

import static app.core.utils.StringBuilder.makeQueryString;

public class UsuarioFilters {

    public static String makeUsuarioQueryStringByFilters(Long id, String login, Long pessoaId, Long organizacaoDefaultId, String nomeprofissional, String usuario, Boolean bot) {
        String queryString = "";

        if (BasicFunctions.isValid(id)) {
            queryString += makeQueryString(id, "id", Usuario.class);
        }
        if (BasicFunctions.isNotEmpty(login)) {
            queryString += makeQueryString(login, "login", Usuario.class);
        }
        if (BasicFunctions.isValid(pessoaId)) {
            queryString += makeQueryString(pessoaId, "pessoaId", Usuario.class);
        }
        if (BasicFunctions.isValid(organizacaoDefaultId)) {
            queryString += makeQueryString(organizacaoDefaultId, "organizacaoDefaultId", Usuario.class);
        }
        if (BasicFunctions.isNotEmpty(nomeprofissional)) {
            queryString += makeQueryString(nomeprofissional, "nomeprofissional", Usuario.class);
        }
        if (BasicFunctions.isNotEmpty(usuario)) {
            queryString += makeQueryString(usuario, "usuario", Usuario.class);
        }
        if (BasicFunctions.isValid(bot)) {
            queryString += makeQueryString(bot, "bot", Usuario.class);
        }

        return queryString;
    }
}
