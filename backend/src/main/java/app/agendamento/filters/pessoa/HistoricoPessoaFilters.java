package app.agendamento.filters.pessoa;

import app.agendamento.model.pessoa.HistoricoPessoa;
import app.core.utils.BasicFunctions;

import static app.core.utils.StringBuilder.makeQueryString;

public class HistoricoPessoaFilters {

    public static String makeHistoricoPessoaQueryStringByFilters(Long id, String queixaPrincipal, String medicamentos, String diagnosticoClinico, String comorbidades, String ocupacao, String responsavelContato, String nomePessoa) {
        String queryString = "";

        if (BasicFunctions.isValid(id)) {
            queryString += makeQueryString(id, "id", HistoricoPessoa.class);
        }
        if (BasicFunctions.isNotEmpty(queixaPrincipal)) {
            queryString += makeQueryString(queixaPrincipal, "queixaPrincipal", HistoricoPessoa.class);
        }
        if (BasicFunctions.isNotEmpty(medicamentos)) {
            queryString += makeQueryString(medicamentos, "medicamentos", HistoricoPessoa.class);
        }
        if (BasicFunctions.isNotEmpty(diagnosticoClinico)) {
            queryString += makeQueryString(diagnosticoClinico, "diagnosticoClinico", HistoricoPessoa.class);
        }
        if (BasicFunctions.isNotEmpty(comorbidades)) {
            queryString += makeQueryString(comorbidades, "comorbidades", HistoricoPessoa.class);
        }
        if (BasicFunctions.isNotEmpty(ocupacao)) {
            queryString += makeQueryString(ocupacao, "ocupacao", HistoricoPessoa.class);
        }
        if (BasicFunctions.isNotEmpty(responsavelContato)) {
            queryString += makeQueryString(responsavelContato, "responsavelContato", HistoricoPessoa.class);
        }
        if (BasicFunctions.isNotEmpty(nomePessoa)) {
            queryString += makeQueryString(nomePessoa, "nomePessoa", HistoricoPessoa.class);
        }
        return queryString;
    }
}
