package app.agendamento.queries.agendamento;

import app.agendamento.model.agendamento.Agendamento;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
@Transactional
public class AgendamentoQueries {

    @PersistenceContext
    EntityManager entityManager;

    public List<Agendamento> loadListAgendamentosByUsuarioDataAgenda(Agendamento pAgendamento) {

        TypedQuery<Agendamento> query = entityManager
                .createNamedQuery("qloadListAgendamentosByUsuarioDataAgenda", Agendamento.class);
        query.setParameter("profissionalId", pAgendamento.getProfissionalAgendamento().getId());
        query.setParameter("dataAgendamento", pAgendamento.getDataAgendamento());

        return query.getResultList();
    }

    public List<Agendamento> loadListAgendamentosByDataAgenda(Agendamento pAgendamento) {
        TypedQuery<Agendamento> query = entityManager.createNamedQuery("qloadListAgendamentosByDataAgenda",
                Agendamento.class);
        query.setParameter("dataAgendamento", pAgendamento.getDataAgendamento());
        return query.getResultList();
    }

    public Agendamento loadAgendamentoByPessoaDataAgendaHorario(Agendamento pAgendamento) {
        TypedQuery<Agendamento> query = entityManager.createNamedQuery("qloadAgendamentoByPessoaDataAgendaHorario",
                Agendamento.class);
        query.setParameter("pessoaId", pAgendamento.getPessoaAgendamento().getId());
        query.setParameter("dataAgendamento", pAgendamento.getDataAgendamento());
        query.setParameter("horarioAgendamento", pAgendamento.getHorarioAgendamento());
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
