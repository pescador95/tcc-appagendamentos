package app.core.services.scheduler;

import app.agendamento.model.agendamento.Agendamento;
import app.agendamento.model.configurador.ConfiguradorNotificacao;
import app.core.model.scheduler.Lembrete;
import app.core.utils.BasicFunctions;
import app.core.utils.Contexto;
import app.core.utils.StringBuilder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class LembreteServices {
    public static List<Lembrete> gerarLembretes(List<Agendamento> agendamentos) {

        List<Lembrete> lembretes = new ArrayList<>();

        List<ConfiguradorNotificacao> configuradorNotificacoes = ConfiguradorNotificacao.listAll();

        if (BasicFunctions.isNotEmpty(agendamentos) && BasicFunctions.isNotEmpty(configuradorNotificacoes)) {
            agendamentos.forEach(agendamento -> configuradorNotificacoes.forEach(configurador -> {

                LocalDate dataNotificacao = agendamento.getDataAgendamento().minusDays(configurador.getDataIntervalo());
                LocalTime horarioNotificacao = agendamento.getHorarioAgendamento().minusHours(configurador.getHoraMinutoIntervalo().getHour()).minusMinutes(configurador.getHoraMinutoIntervalo().getMinute());

                if (!dataNotificacao.isBefore(Contexto.dataContexto(agendamento.getOrganizacaoAgendamento()))) {

                            String mensagemTemplate = "Olá, NOME!\n\nVocê tem um agendamento de TIPOAGENDAMENTO marcado para DIA SEMANA às HORARIO na EMPRESA.\n \n Endereço: ENDERECO\n Contato: CONTATO \n Profissional: PROFISSIONAL \n Data do Agendamento: DATA \n Horário do Agendamento: HORARIO\n \n Atensiosamente, \n EMPRESA.";

                    if (BasicFunctions.isValid(agendamento.getPessoaAgendamento().getWhatsappId()) || BasicFunctions.isValid(agendamento.getPessoaAgendamento().getTelegramId())) {
                                Lembrete lembrete = new Lembrete();
                        lembrete.setAgendamentoLembrete(agendamento);
                        lembrete.setDataLembrete(dataNotificacao);
                        lembrete.setDataAcao(Contexto.dataHoraContexto());
                        lembrete.setHorarioLembrete(horarioNotificacao);
                        lembrete.setMensagem(StringBuilder.makeMensagemByNotificacaoTemplate(BasicFunctions.isValid(configurador.getMensagem()) ? configurador.getMensagem() : mensagemTemplate, StringBuilder.notificacaoBuilder(agendamento)));
                        lembrete.setStatusNotificacao(Lembrete.STATUS_NOTIFICACAO_NAO_ENVIADO);
                        lembrete.setStatusLembrete(lembrete.statusLembrete());
                                lembretes.add(lembrete);
                            }
                        }
                    }
            ));
        }
        return lembretes;
    }
}