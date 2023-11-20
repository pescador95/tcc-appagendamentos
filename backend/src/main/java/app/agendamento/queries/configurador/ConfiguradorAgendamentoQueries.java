package app.agendamento.queries.configurador;

import app.agendamento.model.agendamento.Agendamento;
import app.agendamento.model.configurador.ConfiguradorAgendamento;
import org.jetbrains.annotations.NotNull;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
@Transactional
public class ConfiguradorAgendamentoQueries {

    @PersistenceContext
    EntityManager entityManager;

    public List<ConfiguradorAgendamento> loadListConfiguradorAgendamentoByOrganizacao(
            @NotNull Agendamento pAgendamento) {
        TypedQuery<ConfiguradorAgendamento> query = entityManager
                .createNamedQuery("qListConfiguradorAgendamentoByOrganizacao", ConfiguradorAgendamento.class);
        query.setParameter("organizacaoId", pAgendamento.getOrganizacaoAgendamento().getId());
        return query.getResultList();
    }

    public ConfiguradorAgendamento loadConfiguradorAgendamentoByOrganizacaoProfissional(
            @NotNull Agendamento pAgendamento) {
        TypedQuery<ConfiguradorAgendamento> query = entityManager
                .createNamedQuery("qConfiguradorAgendamentoByOrganizacaoProfissional", ConfiguradorAgendamento.class);
        query.setParameter("organizacaoId", pAgendamento.getOrganizacaoAgendamento().getId());
        query.setParameter("profissionalId", pAgendamento.getProfissionalAgendamento().getId());
        return query.getSingleResult();

    }
}
