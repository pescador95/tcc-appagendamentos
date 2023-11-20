package app.core.filters.contrato;

import app.core.model.contrato.Contrato;
import app.core.utils.BasicFunctions;

import java.time.LocalDate;

import static app.core.utils.StringBuilder.makeQueryString;

public class ContratoFilters {

    public static String makeContratoQueryStringByFilters(Long organizacaoContrato, Long responsavelContrato,
                                                          Integer numeroMaximoSessoes,
                                                          String consideracoes,
                                                          LocalDate dataContrato, LocalDate dataInicio, LocalDate dataFim) {
        String queryString = "";

        if (BasicFunctions.isValid(organizacaoContrato)) {
            queryString += makeQueryString(organizacaoContrato, "organizacaoContrato", Contrato.class);
        }
        if (BasicFunctions.isValid(responsavelContrato)) {
            queryString += makeQueryString(responsavelContrato, "responsavelContrato", Contrato.class);
        }
        if (BasicFunctions.isValid(numeroMaximoSessoes)) {
            queryString += makeQueryString(numeroMaximoSessoes, "numeroMaximoSessoes", Contrato.class);
        }
        if (BasicFunctions.isNotEmpty(consideracoes)) {
            queryString += makeQueryString(consideracoes, "consideracoes", Contrato.class);
        }
        if (BasicFunctions.isNotEmpty(dataContrato)) {
            queryString += makeQueryString(dataContrato, "dataContrato", Contrato.class);
        }
        if (BasicFunctions.isValid(dataInicio)) {
            queryString += makeQueryString(dataInicio, "dataInicio", Contrato.class);
        }
        if (BasicFunctions.isValid(dataFim)) {
            queryString += makeQueryString(dataFim, "dataFim", Contrato.class);
        }
        return queryString;
    }
}
