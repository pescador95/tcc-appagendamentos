package app.agendamento.filters.endereco;

import app.agendamento.model.endereco.Endereco;
import app.core.utils.BasicFunctions;

import static app.core.utils.StringBuilder.makeQueryString;

public class EnderecoFilters {

    public static String makeEnderecoQueryStringByFilters(String cep, String logradouro, Long numero, String complemento, String cidade, String estado) {
        String queryString = "";

        if (BasicFunctions.isNotEmpty(cep)) {
            queryString += makeQueryString(cep, "cep", Endereco.class);
        }
        if (BasicFunctions.isNotEmpty(logradouro)) {
            queryString += makeQueryString(logradouro, "logradouro", Endereco.class);
        }
        if (BasicFunctions.isValid(numero)) {
            queryString += makeQueryString(numero, "numero", Endereco.class);
        }
        if (BasicFunctions.isNotEmpty(complemento)) {
            queryString += makeQueryString(complemento, "complemento", Endereco.class);
        }
        if (BasicFunctions.isNotEmpty(cidade)) {
            queryString += makeQueryString(cidade, "cidade", Endereco.class);
        }
        if (BasicFunctions.isNotEmpty(estado)) {
            queryString += makeQueryString(estado, "estado", Endereco.class);
        }
        return queryString;
    }
}
