package app.core.thread;

import app.agendamento.model.agendamento.Agendamento;
import app.agendamento.model.organizacao.Organizacao;
import app.core.model.scheduler.Lembrete;
import app.core.model.scheduler.Thread;
import app.core.services.scheduler.LembreteServices;
import app.core.trace.Tracer;
import app.core.utils.BasicFunctions;
import app.core.utils.Contexto;
import io.smallrye.common.annotation.RunOnVirtualThread;

import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.transaction.Transactional;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

import static app.core.utils.BasicFunctions.log;
import static app.core.utils.Contexto.dataHoraContextoToString;
import static app.core.utils.Contexto.dataHoraToString;

@ApplicationScoped
@Transactional
public class LembreteThread {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private Queue<Lembrete> filaLembretes = new LinkedList<>();

    private static List<Agendamento> recuperarAgendamentosDoBancoDeDados() {

        List<Organizacao> organizacoes = Organizacao.list("ativo = true");
        List<Agendamento> agendamentos;

        agendamentos = Agendamento.listAll();

        List<Lembrete> lembretes = Lembrete.list("agendamentoLembrete in ?1", agendamentos);

        agendamentos.removeIf(agendamento -> !agendamento.getStatusAgendamento().agendado() ||
                !agendamento.ativo() ||
                !organizacoes.contains(agendamento.getOrganizacaoAgendamento()) ||
                (agendamento.getDataAgendamento().isBefore(Contexto.dataContexto(agendamento.getOrganizacaoAgendamento())))
                || lembretes.stream().anyMatch(y -> y.getAgendamentoLembrete().getId().equals(agendamento.getId())));

        return agendamentos;
    }

    public void statusFilas() {
        log("NÚMERO DE FILAS PENDENTES: " + Thread.count("status = ?1", Thread.STATUS_PENDENTE));
        log("NÚMERO DE FILAS EM EXECUÇÃO: " + Thread.count("status = ?1", Thread.STATUS_EM_EXECUCAO));
        log("NÚMERO DE FILAS FINALIZADAS: " + Thread.count("status = ?1", Thread.STATUS_FINALIZADO));
        log("NÚMERO DE FILAS COM FALHA: " + Thread.count("status = ?1", Thread.STATUS_FALHA));
        log("NÚMERO DE FILAS CANCELADAS: " + Thread.count("status = ?1", Thread.STATUS_CANCELADO));
    }

    public void removeLembreteExpirados() {
        List<Lembrete> lembretesExpirados = Lembrete.listAll();
        if (BasicFunctions.isNotEmpty(lembretesExpirados)) {
            lembretesExpirados.forEach(lembrete -> {
                if (!lembrete.getStatusNotificacao().equals(Lembrete.STATUS_NOTIFICACAO_ENVIADO) && lembrete.getDataLembrete()
                        .isBefore(Contexto.dataContexto(lembrete.getAgendamentoLembrete().getOrganizacaoAgendamento()))) {
                    lembrete.delete();
                    log("Lembrete ID: " + lembrete.getId() + " removido.");
                }
            });
        }
    }

    public void removeFilasExpiradas() {
        List<Thread> filas = Thread.list("status = ?1 and dataAcao < ?2", Thread.STATUS_PENDENTE,
                Contexto.dataHoraContexto());

        if (BasicFunctions.isNotEmpty(filas)) {
            List<Long> filaIds = filas.stream().map(Thread::getId).collect(Collectors.toList());

            List<Lembrete> lembretes = Lembrete.list("thread.id in ?1", filaIds);

            filas.forEach(fila -> {
                boolean hasAssociatedLembrete = lembretes.stream()
                        .anyMatch(lembrete -> lembrete.getThread().getId().equals(fila.getId()));

                if (!hasAssociatedLembrete) {
                    fila.delete();
                    log("Fila ID: " + fila.getId() + " removida.");
                }
            });
        }
    }

    public Queue<Lembrete> loadLembretesPersistidos() {

        List<Lembrete> lembretesPersisted = Lembrete.list("statusNotificacao <> ?1",
                Lembrete.STATUS_NOTIFICACAO_ENVIADO);

        List<Lembrete> lembretesFiltrados = lembretesPersisted.stream()
                .filter(lembrete -> lembrete.getDataLembrete()
                        .isEqual(Contexto.dataContexto(lembrete.getAgendamentoLembrete().getOrganizacaoAgendamento())))
                .toList();

        filaLembretes.addAll(lembretesFiltrados);
        return filaLembretes;
    }

    public Thread buscarFila() {

        Thread filaPendente = Thread.find("status = ?1", Thread.STATUS_PENDENTE).firstResult();

        if (BasicFunctions.isNotEmpty(filaPendente)) {
            return filaPendente;
        }

        Thread filaExecucao = Thread.find("status = ?1", Thread.STATUS_EM_EXECUCAO).firstResult();

        if (BasicFunctions.isNotEmpty(filaExecucao)) {
            return filaExecucao;
        }

        Thread filaFalha = Thread.find("status = ?1", Thread.STATUS_FALHA).firstResult();

        if (BasicFunctions.isNotEmpty(filaFalha)) {
            return filaFalha;
        }
        return criarFila();
    }

    public Thread criarFila() {

        Thread fila = new Thread();
        fila.setStatus(Thread.STATUS_PENDENTE);
        fila.setAtivo(Boolean.TRUE);
        fila.setStatusDescricao(fila.statusDestricao());
        fila.setDataAcao(Contexto.dataHoraContexto());
        fila.persist();

        log("Nova Fila criada. ID: " + fila.getId() + " - Data e hora de criação: " + fila.getDataAcao());


        return fila;
    }

    public void adicionarLembretesAFila(List<Lembrete> lembretes, Thread fila) {

        for (Lembrete lembrete : lembretes) {
            filaLembretes.add(lembrete);
            lembrete.setThread(fila);
            lembrete.persist();
        }
    }

    public void enviarLembretesEAtualizarFila() {

        String telegramApiUrl = System.getenv("TELEGRAM_API_URL");

        String whatsappApiUrl = System.getenv("WHATSAPP_API_URL");

        filaLembretes = loadLembretesPersistidos();

        List<Lembrete> lembretesMantidos = new ArrayList<>();

        for (Lembrete lembrete : filaLembretes) {

            atualizarStatusFila(lembrete.getThread(), Thread.STATUS_EM_EXECUCAO);

            Agendamento agendamento = lembrete.getAgendamentoLembrete();

            if (agendamento.ativo() && agendamento.getStatusAgendamento().agendado() && !lembrete.lembreteEnviado()
                    && enviarLembrete(lembrete.getDataLembrete(), lembrete.getHorarioLembrete(),
                    agendamento.getOrganizacaoAgendamento())) {

                if (BasicFunctions.isValid(lembrete.getAgendamentoLembrete().getPessoaAgendamento().getTelegramId())) {
                    log("Enviando mensagem de notificação para o Telegram... Agendamento: "
                            + agendamento.getId() + " - Lembrete: " + lembrete.getId() + " - Data e hora de envio: "
                            + dataHoraContextoToString(lembrete.getAgendamentoLembrete().getOrganizacaoAgendamento()));
                    enviarLembrete(lembrete.getMensagem(), telegramApiUrl,
                            lembrete.getAgendamentoLembrete().getPessoaAgendamento().getTelegramId(), lembrete);
                }
                if (BasicFunctions.isValid(lembrete.getAgendamentoLembrete().getPessoaAgendamento().getWhatsappId())) {
                    log("Enviando mensagem de notificação para o WhatsApp... Agendamento: "
                            + agendamento.getId() + " - Lembrete: " + lembrete.getId() + " - Data e hora de envio: "
                            + dataHoraContextoToString(lembrete.getAgendamentoLembrete().getOrganizacaoAgendamento()));
                    enviarLembrete(lembrete.getMensagem(), whatsappApiUrl,
                            lembrete.getAgendamentoLembrete().getPessoaAgendamento().getWhatsappId(), lembrete);
                }
                lembretesMantidos.add(lembrete);
            }
        }
        filaLembretes = new LinkedList<>(lembretesMantidos);
    }

    public void atualizarStatusFila(Thread fila, Long status) {

        fila.setStatus(status);
        fila.setStatusDescricao(fila.statusDestricao());

        if (status.equals(Thread.STATUS_EM_EXECUCAO)) {
            fila.setDataHoraInicio(Contexto.dataHoraContexto());
        }
        if (status.equals(Thread.STATUS_FINALIZADO)) {
            fila.setDataHoraFim(Contexto.dataHoraContexto());
        }
        if (status.equals(Thread.STATUS_PENDENTE)) {
            fila.setDataHoraInicio((null));
            fila.setDataHoraFim((null));
        }
        if (status.equals(Thread.STATUS_FALHA)) {
            fila.setDataHoraFim(Contexto.dataHoraContexto());
        }
        if (status.equals(Thread.STATUS_CANCELADO)) {
            fila.setDataHoraFim(Contexto.dataHoraContexto());
        }
        fila.setDataAcao(Contexto.dataHoraContexto());
        fila.persist();
    }

    public void atualizarStatusFilaAutomaticamente() {
        List<Thread> filas = Thread.listAll();
        String message = "";

        for (Thread fila : filas) {
            if (todosLembretesNaoEnviados(fila)) {
                message = "Ainda há lembretes da Fila ID:" + fila.getId()
                        + " com status de não enviado. Atualizando status da Fila para PENDENTE.";
                atualizarStatusFila(fila, Thread.STATUS_PENDENTE);
            } else if (todosLembretesEnviados(fila)) {
                message = "Todos os lembretes da Fila ID:" + fila.getId()
                        + " estão com status de enviado. Atualizando status da Fila para FINALIZADA.";
                atualizarStatusFila(fila, Thread.STATUS_FINALIZADO);
            } else if (todosLembretesComFalha(fila)) {
                message = "Ainda há lembretes da Fila ID:" + fila.getId()
                        + " com status de falha no envio. Atualizando status da Fila para FALHA.";
                atualizarStatusFila(fila, Thread.STATUS_FALHA);
            } else {
                message = "Ainda há lembretes da fila id:" + fila.getId()
                        + " com status de não enviado. Atualizando status da Fila para EM EXECUÇÃO.";
                atualizarStatusFila(fila, Thread.STATUS_EM_EXECUCAO);
            }
        }
        log(message);
        statusFilas();
    }


    private boolean todosLembretesNaoEnviados(Thread fila) {
        List<Lembrete> lembretes = Lembrete.list("thread = ?1", fila);
        return lembretes.stream()
                .filter(lembrete -> lembrete.getThread().getId().equals(fila.getId()))
                .allMatch(lembrete -> lembrete.getStatusNotificacao().equals(Lembrete.STATUS_NOTIFICACAO_NAO_ENVIADO));
    }

    private boolean todosLembretesEnviados(Thread fila) {
        List<Lembrete> lembretes = Lembrete.list("thread = ?1", fila);
        return lembretes.stream()
                .filter(lembrete -> lembrete.getThread().getId().equals(fila.getId()))
                .allMatch(lembrete -> lembrete.getStatusNotificacao().equals(Lembrete.STATUS_NOTIFICACAO_ENVIADO));
    }

    private boolean todosLembretesComFalha(Thread fila) {
        List<Lembrete> lembretes = Lembrete.list("thread = ?1", fila);
        return lembretes.stream()
                .filter(lembrete -> lembrete.getThread().getId().equals(fila.getId()))
                .allMatch(lembrete -> lembrete.getStatusNotificacao().equals(Lembrete.STATUS_NOTIFICACAO_FALHA_ENVIO));
    }

    private Boolean enviarLembrete(LocalDate dataLembrete, LocalTime horarioLembrete, Organizacao organizacaoLembrete) {
        return dataLembrete.isEqual(Contexto.dataContexto(organizacaoLembrete))
                && !Contexto.horarioContexto(organizacaoLembrete).isBefore(horarioLembrete);
    }

    public void enviarLembrete(String mensagem, String apiUrl, Long id, Lembrete lembrete) {

        if (apiUrl == null) {
            throw new IllegalStateException("A variável de ambiente TELEGRAM_API_URL não está definida.");
        }

        String url = apiUrl + "enviarLembrete/" + id;

        JsonObject body = Json.createObjectBuilder()
                .add("mensagem", mensagem)
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        try {

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();

            if (statusCode == 200) {
                atualizarStatusLembrete(lembrete.getId(), Lembrete.STATUS_NOTIFICACAO_ENVIADO);
                log("Mensagem de lembrete enviada com sucesso. Lembrete ID: " + lembrete.getId()
                        + " - Data e hora de envio: "
                        + dataHoraContextoToString(lembrete.getAgendamentoLembrete().getOrganizacaoAgendamento()));
            } else {
                atualizarStatusLembrete(lembrete.getId(), Lembrete.STATUS_NOTIFICACAO_FALHA_ENVIO);
                log("Falha ao enviar mensagem de lembrete. Código de status: " + statusCode
                        + " - Data e hora de tentativa de envio: "
                        + dataHoraContextoToString(lembrete.getAgendamentoLembrete().getOrganizacaoAgendamento()));
            }
        } catch (InterruptedException | IOException e) {
            atualizarStatusLembrete(lembrete.getId(), Lembrete.STATUS_NOTIFICACAO_FALHA_ENVIO);
            log(
                    "Erro ao enviar mensagem de lembrete: " + e.getMessage() + " - Data e hora de tentativa de envio: "
                            + dataHoraContextoToString(lembrete.getAgendamentoLembrete().getOrganizacaoAgendamento()));
        }
    }

    private void atualizarStatusLembrete(Long id, Long status) {

        Lembrete lembrete = Lembrete.findById(id);
        if (lembrete != null) {
            lembrete.setStatusNotificacao(status);
            lembrete.setDataHoraEnvio(Contexto.dataHoraContexto(lembrete.getAgendamentoLembrete().getOrganizacaoAgendamento()));
            lembrete.setStatusLembrete(lembrete.statusLembrete());
            lembrete.persist();
            log("Status do lembrete atualizado. Lembrete ID: " + lembrete.getId() + " - Status: "
                    + lembrete.getStatusLembrete() + " - Data e hora de atualização: "
                    + dataHoraToString(lembrete.getDataHoraEnvio(), lembrete.getAgendamentoLembrete().getOrganizacaoAgendamento()));
        }
    }

    public void pararAgendador() {

        scheduler.shutdown();

    }

    @RunOnVirtualThread
    @Tracer.RastrearExecucaoMetodo
    public void FilaLembreteAgendamentos() {

        log("Disparando Scheduler de Lembretes... ");

        LembreteThread lembreteThread = new LembreteThread();

        String telegramApiUrl = System.getenv("TELEGRAM_API_URL");

        String whatsappApiUrl = System.getenv("WHATSAPP_API_URL");

        if (telegramApiUrl == null) {
            throw new IllegalStateException("A variável de ambiente TELEGRAM_API_URL não está definida.");
        }

        if (whatsappApiUrl == null) {
            throw new IllegalStateException("A variável de ambiente WHATSAPP_API_URL não está definida.");
        }

        gerarLembretesAdicionarFila();

        lembreteThread.enviarLembretesEAtualizarFila();

        atualizarStatusFilaAutomaticamente();

        lembreteThread.pararAgendador();
    }

    public void gerarLembretesAdicionarFila(){

        LembreteThread lembreteThread = new LembreteThread();

        List<Agendamento> agendamentos = recuperarAgendamentosDoBancoDeDados();

        if (BasicFunctions.isNotEmpty(agendamentos)) {

            List<Lembrete> lembretes = LembreteServices.gerarLembretes(agendamentos);

            if (BasicFunctions.isNotEmpty(lembretes)) {

                Thread fila = buscarFila();

                lembreteThread.adicionarLembretesAFila(lembretes, fila);
            }

        }

        removeLembreteExpirados();

        removeFilasExpiradas();

        filaLembretes = loadLembretesPersistidos();
    }
}
