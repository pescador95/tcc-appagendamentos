package app.agendamento.filters.tipoAgendamento;

import app.agendamento.model.agendamento.TipoAgendamento;
import app.core.utils.BasicFunctions;
import io.quarkus.panache.common.Parameters;

import java.util.List;

import static app.core.utils.StringBuilder.makeQueryString;
import static io.quarkus.hibernate.orm.panache.PanacheEntityBase.find;

public class TipoAgendamentoFilters {

    public static String makeTipoAgendamentoQueryStringByFilters(Long id, String tipoAgendamento) {

        String queryString = "";

        if (BasicFunctions.isValid(id)) {
            queryString += makeQueryString(id, "id", TipoAgendamento.class);
        }
        if (BasicFunctions.isNotEmpty(tipoAgendamento)) {
            queryString += makeQueryString(tipoAgendamento, "tipoAgendamento", TipoAgendamento.class);
        }
        return queryString;
    }


    public static List<TipoAgendamento> findByOrganizacaoIdAndQueryString(List<Long> organizacaoId, String queryString) {
        String fullQuery = "organizacaoId in :orgId";
        fullQuery += " " + queryString;
        return find(fullQuery, Parameters.with("orgId", organizacaoId)).list();
    }
}
