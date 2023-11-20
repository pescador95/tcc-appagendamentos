package app.agendamento.filters.configurador;

import app.agendamento.model.configurador.ConfiguradorAgendamento;
import app.core.utils.BasicFunctions;

import java.time.LocalTime;

import static app.core.utils.StringBuilder.makeQueryString;

public class ConfiguradorAgendamentoFilters {

    public static String makeConfiguradorAgendamentoQueryStringByFilters(String nome, Long profissionalId, Long organizacaoId,
                                                                         Boolean configuradorOrganizacao, LocalTime horarioInicioManha, LocalTime horarioFimManha, LocalTime horarioInicioTarde, LocalTime horarioFimTarde,
                                                                         LocalTime horarioInicioNoite, LocalTime horarioFimNoite, LocalTime horaMinutoIntervalo, LocalTime horaMinutoTolerancia, Boolean agendaManha,
                                                                         Boolean agendaTarde, Boolean agendaNoite, Boolean atendeSabado, Boolean atendeDomingo, Boolean agendaSabadoManha, Boolean agendaSabadoTarde,
                                                                         Boolean agendaSabadoNoite, Boolean agendaDomingoManha, Boolean agendaDomingoTarde, Boolean agendaDomingoNoite) {
        String queryString = "";

        if (BasicFunctions.isNotEmpty(nome)) {
            queryString += makeQueryString(nome, "nome", ConfiguradorAgendamento.class);
        }
        if (BasicFunctions.isValid(profissionalId)) {
            queryString += makeQueryString(profissionalId, "profissionalId", ConfiguradorAgendamento.class);
        }
        if (BasicFunctions.isValid(organizacaoId)) {
            queryString += makeQueryString(organizacaoId, "organizacaoId", ConfiguradorAgendamento.class);
        }
        if (BasicFunctions.isValid(configuradorOrganizacao)) {
            queryString += makeQueryString(configuradorOrganizacao, "configuradorOrganizacao", ConfiguradorAgendamento.class);
        }
        if (BasicFunctions.isValid(horarioInicioManha)) {
            queryString += makeQueryString(horarioInicioManha, "horarioInicioManha", ConfiguradorAgendamento.class);
        }
        if (BasicFunctions.isValid(horarioFimManha)) {
            queryString += makeQueryString(horarioFimManha, "horarioFimManha", ConfiguradorAgendamento.class);
        }
        if (BasicFunctions.isValid(horarioInicioTarde)) {
            queryString += makeQueryString(horarioInicioTarde, "horarioInicioTarde", ConfiguradorAgendamento.class);
        }
        if (BasicFunctions.isValid(horarioFimTarde)) {
            queryString += makeQueryString(horarioFimTarde, "horarioFimTarde", ConfiguradorAgendamento.class);
        }
        if (BasicFunctions.isValid(horarioInicioNoite)) {
            queryString += makeQueryString(horarioInicioNoite, "horarioInicioNoite", ConfiguradorAgendamento.class);
        }
        if (BasicFunctions.isValid(horarioFimNoite)) {
            queryString += makeQueryString(horarioFimNoite, "horarioFimNoite", ConfiguradorAgendamento.class);
        }
        if (BasicFunctions.isValid(horaMinutoIntervalo)) {
            queryString += makeQueryString(horaMinutoIntervalo, "horaMinutoIntervalo", ConfiguradorAgendamento.class);
        }
        if (BasicFunctions.isValid(horaMinutoTolerancia)) {
            queryString += makeQueryString(horaMinutoTolerancia, "horaMinutoTolerancia", ConfiguradorAgendamento.class);
        }
        if (BasicFunctions.isValid(agendaManha)) {
            queryString += makeQueryString(agendaManha, "agendaManha", ConfiguradorAgendamento.class);
        }
        if (BasicFunctions.isValid(agendaTarde)) {
            queryString += makeQueryString(agendaTarde, "agendaTarde", ConfiguradorAgendamento.class);
        }
        if (BasicFunctions.isValid(agendaNoite)) {
            queryString += makeQueryString(agendaNoite, "agendaNoite", ConfiguradorAgendamento.class);
        }
        if (BasicFunctions.isValid(atendeSabado)) {
            queryString += makeQueryString(atendeSabado, "atendeSabado", ConfiguradorAgendamento.class);
        }
        if (BasicFunctions.isValid(atendeDomingo)) {
            queryString += makeQueryString(atendeDomingo, "atendeDomingo", ConfiguradorAgendamento.class);
        }
        if (BasicFunctions.isValid(agendaSabadoManha)) {
            queryString += makeQueryString(agendaSabadoManha, "agendaSabadoManha", ConfiguradorAgendamento.class);
        }
        if (BasicFunctions.isValid(agendaSabadoTarde)) {
            queryString += makeQueryString(agendaSabadoTarde, "agendaSabadoTarde", ConfiguradorAgendamento.class);
        }
        if (BasicFunctions.isValid(agendaSabadoNoite)) {
            queryString += makeQueryString(agendaSabadoNoite, "agendaSabadoNoite", ConfiguradorAgendamento.class);
        }
        if (BasicFunctions.isValid(agendaDomingoManha)) {
            queryString += makeQueryString(agendaDomingoManha, "agendaDomingoManha", ConfiguradorAgendamento.class);
        }
        if (BasicFunctions.isValid(agendaDomingoTarde)) {
            queryString += makeQueryString(agendaDomingoTarde, "agendaDomingoTarde", ConfiguradorAgendamento.class);
        }
        if (BasicFunctions.isValid(agendaDomingoNoite)) {
            queryString += makeQueryString(agendaDomingoNoite, "agendaDomingoNoite", ConfiguradorAgendamento.class);
        }

        return queryString;
    }

}
