package app.core.scheduler;

import app.agendamento.model.agendamento.Agendamento;
import app.core.model.scheduler.Lembrete;
import app.core.thread.LembreteThread;
import app.core.trace.Invoker;
import app.core.utils.BasicFunctions;
import app.core.utils.Contexto;
import io.quarkus.scheduler.Scheduled;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class Scheduler {

    @Inject
    LembreteThread lembreteThread;

    @Scheduled(cron = "{counter.cron.expression30s}")
    @Transactional
    public void verifyAndRunSchedulerEvery30seconds() {

        lembreteThread = new LembreteThread();

        lembreteThread.gerarLembretesAdicionarFila();
        
        if (runScheduler()) {
            Invoker.invokerFilaLembreteAgendamentos();
        }
    }

    public Boolean runScheduler() {
        return lembretesPendentes() || agendamentosPendentes();
    }

    public Boolean lembretesPendentes() {

        List<Lembrete> lembretes = Lembrete.listAll();

        List<Lembrete> lembretesFiltrados = lembretes.stream()
                .filter(lembrete -> !lembrete.lembreteEnviado() &&
                        lembrete.getDataLembrete().isEqual(Contexto.dataContexto(lembrete.getAgendamentoLembrete().getOrganizacaoAgendamento()))
                        && !lembrete.getHorarioLembrete().isBefore(Contexto.horarioContexto(lembrete.getAgendamentoLembrete().getOrganizacaoAgendamento())) && (BasicFunctions.isValid(lembrete.getAgendamentoLembrete().getPessoaAgendamento().getTelegramId()) || BasicFunctions.isValid(lembrete.getAgendamentoLembrete().getPessoaAgendamento().getWhatsappId())))
                .collect(Collectors.toList());
        return BasicFunctions.isNotEmpty(lembretesFiltrados);
    }

    public Boolean agendamentosPendentes() {

        List<Agendamento> agendamentos = Agendamento.listAll();
        List<Lembrete> lembretes = Lembrete.listAll();

        List<Agendamento> agendamentosFiltrados = new ArrayList<>();

        List<Lembrete> lembretesFiltrados = new ArrayList<>();

        lembretes.forEach(lembrete -> {
            if (!lembrete.lembreteEnviado() && lembrete.getDataLembrete().isEqual(Contexto.dataContexto(lembrete.getAgendamentoLembrete().getOrganizacaoAgendamento()))
                    && !lembrete.getHorarioLembrete().isBefore(Contexto.horarioContexto(lembrete.getAgendamentoLembrete().getOrganizacaoAgendamento())) && (BasicFunctions.isValid(lembrete.getAgendamentoLembrete().getPessoaAgendamento().getTelegramId()) || BasicFunctions.isValid(lembrete.getAgendamentoLembrete().getPessoaAgendamento().getWhatsappId()))) {

            lembretesFiltrados.add(lembrete);
            }
        });

        agendamentos.forEach(agendamento -> {
            if (agendamento.getStatusAgendamento().agendado() &&
                    agendamento.getDataAgendamento().isEqual(Contexto.dataContexto(agendamento.getOrganizacaoAgendamento())) &&
                    !agendamento.getHorarioAgendamento()
                            .isBefore(Contexto.horarioContexto(agendamento.getOrganizacaoAgendamento()))
                    && (BasicFunctions.isValid(agendamento.getPessoaAgendamento().getTelegramId())
                    || BasicFunctions.isValid(agendamento.getPessoaAgendamento().getWhatsappId()))) {

                agendamentosFiltrados.add(agendamento);
            }
        });

        if (BasicFunctions.isNotEmpty(agendamentosFiltrados)){

            return   agendamentosFiltrados.stream().anyMatch(agendamento ->
                    lembretesFiltrados.stream().noneMatch(lembrete ->
                            agendamento.getId().equals(lembrete.getAgendamentoLembrete().getId())
                    )
            );
        }
        return Boolean.FALSE;
    }
}