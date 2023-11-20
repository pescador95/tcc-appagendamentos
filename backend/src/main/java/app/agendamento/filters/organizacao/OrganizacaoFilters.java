package app.agendamento.filters.organizacao;

import app.agendamento.model.organizacao.Organizacao;
import app.core.utils.BasicFunctions;

import static app.core.utils.StringBuilder.makeQueryString;

public class OrganizacaoFilters {

    public static String makeOrganizacaoQueryStringByFilters(String nome, String cnpj, String telefone, String celular,
                                                             String email, Long enderecoId, Long tipoAgendamento_Id) {
        String queryString = "";

        if (BasicFunctions.isNotEmpty(nome)) {
            queryString += makeQueryString(nome, "nome", Organizacao.class);
        }
        if (BasicFunctions.isNotEmpty(cnpj)) {
            queryString += makeQueryString(cnpj, "cnpj", Organizacao.class);
        }
        if (BasicFunctions.isNotEmpty(telefone)) {
            queryString += makeQueryString(telefone, "telefone", Organizacao.class);
        }
        if (BasicFunctions.isNotEmpty(celular)) {
            queryString += makeQueryString(celular, "celular", Organizacao.class);
        }
        if (BasicFunctions.isNotEmpty(email)) {
            queryString += makeQueryString(email, "email", Organizacao.class);
        }
        if (BasicFunctions.isValid(enderecoId)) {
            queryString += makeQueryString(enderecoId, "enderecoId", Organizacao.class);
        }
        if (BasicFunctions.isValid(tipoAgendamento_Id)) {
            queryString += makeQueryString(tipoAgendamento_Id, "tipoAgendamento_Id", Organizacao.class);
        }
        return queryString;
    }
}
