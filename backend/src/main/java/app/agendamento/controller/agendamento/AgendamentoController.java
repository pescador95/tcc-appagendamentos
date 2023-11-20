package app.agendamento.controller.agendamento;

import app.agendamento.controller.configurador.ConfiguradorAgendamentoController;
import app.agendamento.controller.pessoa.UsuarioController;
import app.agendamento.model.agendamento.Agendamento;
import app.agendamento.model.agendamento.StatusAgendamento;
import app.agendamento.model.agendamento.TipoAgendamento;
import app.agendamento.model.configurador.ConfiguradorAgendamento;
import app.agendamento.model.organizacao.Organizacao;
import app.agendamento.model.pessoa.Pessoa;
import app.agendamento.model.pessoa.Usuario;
import app.agendamento.queries.agendamento.AgendamentoQueries;
import app.core.event.AgendamentoEventPublisher;
import app.core.model.DTO.Responses;
import app.core.utils.BasicFunctions;
import app.core.utils.Contexto;
import org.jetbrains.annotations.NotNull;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@Transactional
public class AgendamentoController {

    @Inject
    AgendamentoQueries agendamentoQueries;
    @Context
    SecurityContext context;
    @Inject
    UsuarioController usuarioController;
    @Inject
    ConfiguradorAgendamentoController configuradorAgendamentoController;
    ConfiguradorAgendamento configuradorAgendamento;
    @Inject
    AgendamentoAutomaticoController agendamentoAutomaticoController;
    @Inject
    AgendamentoController agendamentoController;
    @Inject
    AgendamentoEventPublisher eventPublisher;
    private Agendamento agendamento;

    private Agendamento agendamentoOld;
    private Usuario profissional;
    private Pessoa pessoa;
    private Organizacao organizacao;
    private TipoAgendamento tipoAgendamento;
    private StatusAgendamento statusAgendamento;

    private Responses responses;

    public static void setStatusAgendamentoAtendidoByAgendamento(Agendamento pAgendamento) {

        pAgendamento.setStatusAgendamento(StatusAgendamento.findById(StatusAgendamento.ATENDIDO));
        pAgendamento.persist();
    }

    public Response addAgendamento(@NotNull Agendamento pAgendamento, Boolean reagendar) {

        responses = new Responses();
        responses.setMessages(new ArrayList<>());
        organizacao = new Organizacao();

        loadAgendamentoByPessoaData(pAgendamento);

        if (BasicFunctions.isEmpty(agendamento)) {

            agendamento = new Agendamento();

            loadPessoaProfissionalOrganizacaoTipoAgendamentoByAgendamento(pAgendamento, reagendar);

            if (!responses.hasMessages()) {

                agendamento = new Agendamento(pAgendamento, null, tipoAgendamento, pessoa, profissional, statusAgendamento, organizacao, context);

                agendamento.persist();

                responses.setStatus(201);
                responses.setData(agendamento);
                responses.getMessages().add("Agendamento cadastrado com sucesso!");

            } else {
                return Response.ok(responses).status(responses.getStatus()).build();
            }
            eventPublisher.onCreate(agendamento);
            return Response.ok(responses).status(Response.Status.CREATED).build();
        } else {

            responses.setStatus(400);
            responses.setData(agendamento);
            responses.getMessages().add("Agendamento já realizado!");
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }

    public Response updateAgendamento(@NotNull Agendamento pAgendamento, Boolean reagendar) {

        Responses responses;

        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        loadAgendamentoById(pAgendamento);

        loadPessoaProfissionalOrganizacaoTipoAgendamentoByAgendamento(pAgendamento, reagendar);

        loadAgendamentoOldById(pAgendamento);

        try {

                agendamento = agendamento.agendamento(agendamentoOld, pAgendamento, tipoAgendamento, pessoa, profissional, statusAgendamento, organizacao, context);

                agendamento.persistAndFlush();

                responses.setStatus(200);
                responses.setData(agendamento);
                responses.getMessages().add("Agendamento remarcado com sucesso!");

            eventPublisher.onUpdate(agendamento);
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            responses.setData(agendamento);
            responses.getMessages().add("Não foi possível remarcar o Agendamento.");
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }

    public Response deleteAgendamento(@NotNull List<Long> pListIdAgendamento) {

        Responses responses;
        List<Agendamento> agendamentos;
        List<Agendamento> agendamentosAux = new ArrayList<>();
        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        agendamentos = Agendamento.list("id in ?1 and ativo = true", pListIdAgendamento);
        int count = agendamentos.size();

        try {

            if (agendamentos.isEmpty()) {

                responses.setStatus(400);
                responses.getMessages().add("Agendamentos não localizados ou já excluídos.");
                return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
            }

            agendamentos.forEach((agendamento) -> {

                Agendamento agendamentoDeleted = agendamento.cancelarAgendamento(agendamento, context);

                agendamentoDeleted.persist();
                agendamentosAux.add(agendamentoDeleted);
                eventPublisher.onDelete(agendamentoDeleted);
            });

            responses.setStatus(200);
            if (count <= 1) {
                responses.setData(agendamento);
                responses.getMessages().add("Agendamento excluído com sucesso!");
            } else {
                responses.setDatas(Collections.singletonList(agendamentosAux));
                responses.getMessages().add(count + " Agendamentos excluídos com sucesso!");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            if (count <= 1) {
                responses.setData(agendamento);
                responses.getMessages().add("Agendamento não localizada ou já excluído.");
            } else {
                responses.setDatas(Collections.singletonList(agendamentos));
                responses.getMessages().add("Agendamentos não localizadas ou já excluídos.");
            }
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }

    public Response reactivateAgendamento(@NotNull List<Long> pListIdAgendamento) {

        Responses responses;
        List<Agendamento> agendamentos;
        List<Agendamento> agendamentosAux = new ArrayList<>();
        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        agendamentos = Agendamento.list("id in ?1 and ativo = false", pListIdAgendamento);
        int count = agendamentos.size();

        try {

            if (agendamentos.isEmpty()) {

                responses.setStatus(400);
                responses.getMessages().add("Agendamentos não localizados ou já excluídos.");
                return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
            }

            agendamentos.forEach((agendamento) -> {

                Agendamento agendamentoReactivated = agendamento.marcarComoLivre(agendamento, context);

                agendamentoReactivated.persist();
                agendamentosAux.add(agendamentoReactivated);
                eventPublisher.onUpdate(agendamento);
            });

            responses.setStatus(200);
            if (count <= 1) {
                responses.setData(agendamento);
                responses.getMessages().add("Agendamento reativado com sucesso!");
            } else {
                responses.setDatas(Collections.singletonList(agendamentosAux));
                responses.getMessages().add(count + " Agendamentos reativados com sucesso!");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            if (count <= 1) {
                responses.setData(agendamento);
                responses.getMessages().add("Agendamento não localizado ou já reativado.");
            } else {
                responses.setDatas(Collections.singletonList(agendamentos));
                responses.getMessages().add("Agendamentos não localizados ou já reativados.");
            }
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }


    public Response marcarAgendamento(@NotNull Agendamento pAgendamento) {

        Responses responses;
        responses = new Responses();
        responses.setMessages(null);
        organizacao = new Organizacao();

        if (checkDataRemarcacaoInvalida(responses, pAgendamento))
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();

        loadProfissionalConfiguradorAgendamentoPessoaOrganizacaoByAgendamento(pAgendamento);

        agendamento = pAgendamento;

        if (BasicFunctions.isValid(pAgendamento.getDataAgendamento())) {
            DayOfWeek agendamentoDayOfWeek = pAgendamento.getDataAgendamento().getDayOfWeek();

            Boolean dataValida = agendamentoAutomaticoController.validarDataAgendamento(pAgendamento, Boolean.FALSE);

            if (!dataValida) {
                String dia = "";
                if (agendamentoDayOfWeek.equals(DayOfWeek.SATURDAY)) {
                    dia = "Sábado";
                } else if (agendamentoDayOfWeek.equals(DayOfWeek.SUNDAY)) {
                    dia = "Domingo";
                }

                responses.setStatus(400);
                responses.setMessages(new ArrayList<>());
                responses.getMessages()
                        .add("Não foi possível marcar o Agendamento, pois o profissional " + profissional.getLogin()
                                + " não está disponível para atendimento nesse " + dia + ": "
                                + pAgendamento.getDataAgendamento());
                return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
            }
        } else {
            responses.getMessages().add("Por favor, informe a Data da Consulta corretamente!");
        }
        if (BasicFunctions.isEmpty(pAgendamento.getHorarioAgendamento())) {
            responses.getMessages().add("Por favor, informe o horário da Consulta corretamente!");
        }

        if (BasicFunctions.isEmpty(organizacao)) {
            responses.getMessages().add("Por favor, selecione o Local do Atendimento corretamente!");
        }
        if (!responses.hasMessages()) {

            agendamento = new Agendamento(agendamento, null, tipoAgendamento, pessoa, profissional, statusAgendamento, organizacao, context);

            agendamento.persist();

            responses.setStatus(201);
            responses.setDatas(new ArrayList<>());
            responses.getDatas().add(agendamento);
            responses.setMessages(new ArrayList<>());
            responses.getMessages().add("Agendamento marcado com sucesso!");
        } else {
            return Response.ok(responses).status(responses.getStatus()).build();
        }
        eventPublisher.onCreate(agendamento);
        return Response.ok(responses).status(Response.Status.CREATED).build();
    }
    @Transactional
    public Response remarcarAgendamento(@NotNull List<Agendamento> pListAgendamento) {

        Responses responses;
        responses = new Responses();
        responses.setMessages(null);
        organizacao = new Organizacao();
        Agendamento agendamentoOld = new Agendamento();
        Agendamento agendamentoNew = new Agendamento();
        Agendamento agendamentoOldAux;
        Agendamento agendamentoOldPersisted;

        for (Agendamento agendamento : pListAgendamento) {
            if (!agendamento.isValid()) {
                agendamentoNew = agendamento;
            } else {
                agendamentoOld = Agendamento.findById(agendamento.getId());
            }
        }

        if (checkDataRemarcacaoInvalida(responses, agendamentoNew))
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();

        if (BasicFunctions.isNotEmpty(agendamentoOld) && agendamentoOld.hasAgendamentoOld()) {
            agendamentoOldAux = agendamentoOld.getAgendamentoOld();
            if (agendamentoOldAux.getStatusAgendamento().remarcado()) {

                responses.setStatus(400);
                responses.setMessages(new ArrayList<>());
                responses.getMessages()
                        .add("Não foi possível remarcar o Agendamento, pois o mesmo já possui uma remarcação.");
                return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
            }
        }

        loadProfissionalConfiguradorAgendamentoPessoaOrganizacaoByAgendamento(agendamentoNew);

        agendamento = agendamentoNew;

        if (BasicFunctions.isValid(agendamentoNew.getDataAgendamento())) {
            DayOfWeek agendamentoDayOfWeek = agendamentoNew.getDataAgendamento().getDayOfWeek();
            LocalDate dataAgendamento = agendamentoNew.getDataAgendamento();
            Boolean dataValida = agendamentoAutomaticoController.validarDataAgendamento(agendamentoNew, Boolean.TRUE);

            if (!dataValida) {
                String dia = "";
                if (agendamentoDayOfWeek.equals(DayOfWeek.SATURDAY)) {
                    dia = "Sábado";
                } else if (agendamentoDayOfWeek.equals(DayOfWeek.SUNDAY)) {
                    dia = "Domingo";
                }

                responses.setStatus(400);
                responses.setMessages(new ArrayList<>());
                responses.getMessages()
                        .add("Não foi possível remarcar o Agendamento, pois o profissional " + profissional.getLogin()
                                + " não está disponível para atendimento nesse " + dia + ": " + dataAgendamento);
                return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
            }
        } else {
            responses.getMessages().add("Por favor, informe a Data da Consulta corretamente!");
        }
        if (BasicFunctions.isInvalid(agendamentoNew.getHorarioAgendamento())) {
            responses.getMessages().add("Por favor, informe o horário da Consulta corretamente!");
        }

        if (BasicFunctions.isNotEmpty(agendamentoOld)) {
            agendamentoOldPersisted = alterarStatusAgendamentoRemarcado(agendamentoOld);
            eventPublisher.onDelete(agendamentoOldPersisted);
        }
        if (!responses.hasMessages()) {

            agendamento = new Agendamento(agendamentoNew, agendamentoOld, tipoAgendamento, pessoa, profissional, statusAgendamento, organizacao, context);

            agendamento.persist();

            responses.setStatus(201);
            responses.setDatas(new ArrayList<>());
            responses.getDatas().add(agendamento);
            responses.getDatas().add(agendamentoOld);
            responses.setMessages(new ArrayList<>());
            responses.getMessages().add("Agendamento remarcado com sucesso!");
        } else {
            return Response.ok(responses).status(responses.getStatus()).build();
        }
        eventPublisher.onCreate(agendamento);
        return Response.ok(responses).status(Response.Status.CREATED).build();
    }

    private boolean checkDataRemarcacaoInvalida(Responses responses, Agendamento agendamentoNew) {
        List<Agendamento> agendamentosProfissional;
        agendamentosProfissional = agendamentoController
                .loadListAgendamentosByUsuarioDataAgenda(agendamentoNew);

        Boolean checkDatahorario = checkDatahorarioDisponivelByDataHorarioProfissional(agendamentosProfissional,
                agendamentoNew);

        if (checkDatahorario) {

            responses.setStatus(400);
            responses.setMessages(new ArrayList<>());
            responses.getMessages().add(
                    "Não foi possível remarcar o Agendamento, pois o Profissional já possui um Agendamento para a mesma data e horário.");
            return true;
        }
        return false;
    }


    private void loadProfissionalConfiguradorAgendamentoPessoaOrganizacaoByAgendamento(@NotNull Agendamento pAgendamento) {

        profissional = new Usuario();
        configuradorAgendamento = new ConfiguradorAgendamento();
        pessoa = new Pessoa();
        organizacao = new Organizacao();
        tipoAgendamento = new TipoAgendamento();

        if (BasicFunctions.isNotEmpty(pAgendamento.getProfissionalAgendamento())
                && pAgendamento.getProfissionalAgendamento().isValid()) {
            profissional = usuarioController.loadUsuarioByOrganizacao(pAgendamento);
            configuradorAgendamento = configuradorAgendamentoController
                    .loadConfiguradorByUsuarioOrganizacao(pAgendamento);
        }

        if (BasicFunctions.isNotEmpty(pAgendamento.getPessoaAgendamento())
                && pAgendamento.getPessoaAgendamento().isValid()) {
            pessoa = Pessoa.findById(pAgendamento.getPessoaAgendamento().getId());
        }
        if (BasicFunctions.isNotEmpty(pAgendamento.getOrganizacaoAgendamento())
                && pAgendamento.getOrganizacaoAgendamento().isValid()) {
            organizacao = Organizacao.findById(pAgendamento.getOrganizacaoAgendamento().getId());
        }

        if (BasicFunctions.isNotEmpty(pAgendamento.getTipoAgendamento())
                && pAgendamento.getTipoAgendamento().isValid()) {
            tipoAgendamento = TipoAgendamento.findById(pAgendamento.getTipoAgendamento().getId());
        }
    }

    private void loadAgendamentoById(Agendamento pAgendamento) {

        agendamento = new Agendamento();

        if (BasicFunctions.isNotEmpty(pAgendamento)) {
            agendamento = Agendamento.find("id = ?1 and ativo = true", pAgendamento.getId()).firstResult();
        }
    }

    private void loadAgendamentoByPessoaData(Agendamento pAgendamento) {

        agendamento = new Agendamento();

        agendamento = Agendamento.find("pessoaAgendamento = ?1 and dataAgendamento = ?2 and ativo = true",
                pAgendamento.getPessoaAgendamento(), pAgendamento.getDataAgendamento()).firstResult();
    }


    public Agendamento alterarStatusAgendamentoRemarcado(Agendamento pAgendamento) {
        Agendamento agendamento = new Agendamento();
        if (pAgendamento.isValid()) {
            agendamento = Agendamento.findById(pAgendamento.getId());
            if (agendamento.isValid()) {
                agendamento.setStatusAgendamento(StatusAgendamento.statusRemarcado());
                agendamento.persist();
                eventPublisher.onUpdate(agendamento);
            }
        }
        return agendamento;
    }

    public List<Agendamento> loadListAgendamentosByUsuarioDataAgenda(Agendamento pAgendamento) {
        List<Agendamento> agendamentos = new ArrayList<>();
        if (BasicFunctions.isEmpty(pAgendamento) || BasicFunctions.isNotEmpty(pAgendamento.getOrganizacaoAgendamento())
                && BasicFunctions.isNotEmpty(pAgendamento.getProfissionalAgendamento())
                && pAgendamento.getProfissionalAgendamento().isValid()) {
            return agendamentoQueries.loadListAgendamentosByUsuarioDataAgenda(pAgendamento);
        }
        return agendamentos;
    }

    public List<Agendamento> loadListAgendamentosByDataAgenda(Agendamento pAgendamento) {
        List<Agendamento> agendamentos = new ArrayList<>();
        if (BasicFunctions.isValid(pAgendamento.getDataAgendamento())) {
            return agendamentoQueries.loadListAgendamentosByDataAgenda(pAgendamento);
        }
        return agendamentos;
    }

    public Agendamento loadAgendamentoByPessoaDataAgendaHorario(Agendamento pAgendamento) {
        if (BasicFunctions.isValid(pAgendamento.getDataAgendamento()) && BasicFunctions.isValid(pAgendamento.getHorarioAgendamento()) && BasicFunctions.isValid(pAgendamento.getPessoaAgendamento())) {
            return agendamentoQueries.loadAgendamentoByPessoaDataAgendaHorario(pAgendamento);
        }
        return null;
    }

    public List<Agendamento> makeListAgendamentosByProfissionalAgendamento(Agendamento pAgendamento,
            ConfiguradorAgendamento pConfigurador) {

        List<Agendamento> agendamentosNew;
        List<Agendamento> agendamentos = new ArrayList<>();

        if (BasicFunctions.isNotEmpty(pConfigurador) && BasicFunctions.isValid(pConfigurador.getHorarioInicioManha())
                && BasicFunctions.isValid(pConfigurador.getHorarioFimManha())) {

            LocalTime inicioManha = LocalTime.of(pConfigurador.getHorarioInicioManha().getHour(),
                    pConfigurador.getHorarioInicioManha().getMinute());
            LocalTime inicioCfgManha = LocalTime.of(pConfigurador.getHorarioInicioManha().getHour(),
                    pConfigurador.getHorarioInicioManha().getMinute());
            LocalTime fimManha = LocalTime.of(pConfigurador.getHorarioFimManha().getHour(),
                    pConfigurador.getHorarioFimManha().getMinute());

            Boolean agendaManha = pConfigurador.getAgendaManha();

            DayOfWeek agendamentoDayOfWeek = pAgendamento.getDataAgendamento().getDayOfWeek();

            if (agendamentoDayOfWeek.equals(DayOfWeek.SATURDAY)) {
                agendaManha = pConfigurador.getAgendaSabadoManha();
            }
            if (agendamentoDayOfWeek.equals(DayOfWeek.SUNDAY)) {
                agendaManha = pConfigurador.getAgendaDomingoManha();
            }
            if (verificaMakeHorario(agendamentoDayOfWeek, pConfigurador)) {

                agendamentosNew = makeHorariosDisponiveis(pAgendamento, pConfigurador, inicioManha, inicioCfgManha,
                        fimManha, agendaManha);

                if (BasicFunctions.isNotEmpty(agendamentosNew)) {

                    agendamentos.addAll(agendamentosNew);
                }
            }
        }

        if (BasicFunctions.isNotEmpty(pConfigurador) && BasicFunctions.isValid(pConfigurador.getHorarioInicioTarde())
                && BasicFunctions.isValid(pConfigurador.getHorarioFimTarde())) {

            LocalTime inicioTarde = LocalTime.of(pConfigurador.getHorarioInicioTarde().getHour(),
                    pConfigurador.getHorarioInicioTarde().getMinute());
            LocalTime fimTarde = LocalTime.of(pConfigurador.getHorarioFimTarde().getHour(),
                    pConfigurador.getHorarioFimTarde().getMinute());
            LocalTime inicioCfgTarde = LocalTime.of(pConfigurador.getHorarioInicioTarde().getHour(),
                    pConfigurador.getHorarioInicioTarde().getMinute());

            Boolean agendaTarde = pConfigurador.getAgendaTarde();

            DayOfWeek agendamentoDayOfWeek = pAgendamento.getDataAgendamento().getDayOfWeek();

            if (agendamentoDayOfWeek.equals(DayOfWeek.SATURDAY)) {

                agendaTarde = pConfigurador.getAgendaSabadoTarde();
            }
            if (agendamentoDayOfWeek.equals(DayOfWeek.SUNDAY)) {

                agendaTarde = pConfigurador.getAgendaDomingoTarde();
            }
            if (verificaMakeHorario(agendamentoDayOfWeek, pConfigurador)) {

                agendamentosNew = makeHorariosDisponiveis(pAgendamento, pConfigurador, inicioTarde, inicioCfgTarde,
                        fimTarde, agendaTarde);

                if (BasicFunctions.isNotEmpty(agendamentosNew)) {

                    agendamentos.addAll(agendamentosNew);
                }
            }
        }

        if (BasicFunctions.isNotEmpty(pConfigurador) && BasicFunctions.isValid(pConfigurador.getHorarioInicioNoite())
                && BasicFunctions.isValid(pConfigurador.getHorarioFimNoite())) {
            LocalTime inicioNoite = LocalTime.of(pConfigurador.getHorarioInicioNoite().getHour(),
                    pConfigurador.getHorarioInicioNoite().getMinute());
            LocalTime fimNoite = LocalTime.of(pConfigurador.getHorarioFimNoite().getHour(),
                    pConfigurador.getHorarioFimNoite().getMinute());
            LocalTime inicioCfgNoite = LocalTime.of(pConfigurador.getHorarioInicioNoite().getHour(),
                    pConfigurador.getHorarioInicioNoite().getMinute());

            Boolean agendaNoite = pConfigurador.getAgendaNoite();

            DayOfWeek agendamentoDayOfWeek = pAgendamento.getDataAgendamento().getDayOfWeek();

            if (agendamentoDayOfWeek.equals(DayOfWeek.SATURDAY)) {

                agendaNoite = pConfigurador.getAgendaSabadoNoite();
            }
            if (agendamentoDayOfWeek.equals(DayOfWeek.SUNDAY)) {

                agendaNoite = pConfigurador.getAgendaDomingoNoite();
            }
            if (verificaMakeHorario(agendamentoDayOfWeek, pConfigurador)) {

                agendamentosNew = makeHorariosDisponiveis(pAgendamento, pConfigurador, inicioNoite, inicioCfgNoite,
                        fimNoite, agendaNoite);

                if (BasicFunctions.isNotEmpty(agendamentosNew)) {

                    agendamentos.addAll(agendamentosNew);
                }
            }
        }

        if (BasicFunctions.isNotEmpty(agendamentos)) {
            return agendamentos;
        }
        return null;
    }

    public List<Agendamento> makeAgendamentosLivres(List<Agendamento> plistaAgendamentosFree,
                                                    List<Agendamento> pListaAgendamentosPersisted, Boolean reagendar) {

        if (reagendar) {

            return plistaAgendamentosFree.stream()
                    .filter(x -> pListaAgendamentosPersisted.stream()
                            .noneMatch(y -> y.getProfissionalAgendamento().getId().equals(x.getProfissionalAgendamento().getId())
                                    && (y.getDataAgendamento().isEqual(x.getDataAgendamento())
                                    && y.getHorarioAgendamento().equals(x.getHorarioAgendamento())
                                    && y.getStatusAgendamento().agendado())))
                    .collect(Collectors.toList());
        }

        return plistaAgendamentosFree.stream()
                .filter(x -> pListaAgendamentosPersisted.stream()
                        .noneMatch(y -> y.getDataAgendamento().isEqual(x.getDataAgendamento())
                                && y.getHorarioAgendamento().equals(x.getHorarioAgendamento())
                                && y.getStatusAgendamento().agendado()))
                .collect(Collectors.toList());
    }

    public Boolean verificaMakeHorario(DayOfWeek agendamentoDayOfWeek, ConfiguradorAgendamento pConfigurador) {
        if ((agendamentoDayOfWeek.equals(DayOfWeek.SATURDAY) && pConfigurador.getAtendeSabado()) ||
                (agendamentoDayOfWeek.equals(DayOfWeek.SUNDAY) && pConfigurador.getAtendeDomingo()) ||
                (!agendamentoDayOfWeek.equals(DayOfWeek.SATURDAY) && !agendamentoDayOfWeek.equals(DayOfWeek.SUNDAY))) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public List<Agendamento> makeHorariosDisponiveis(Agendamento pAgendamento, ConfiguradorAgendamento pConfigurador,
            LocalTime horaInicio, LocalTime horaInicioCfg, LocalTime horaFim, Boolean agenda) {

        List<Agendamento> agendamentos = new ArrayList<>();
        Agendamento agendamentoNew;
        LocalDate dataContexto = Contexto.dataContexto(pAgendamento.getOrganizacaoAgendamento());
        LocalTime horaContexto = Contexto.horarioContexto(pAgendamento.getOrganizacaoAgendamento());

        if (BasicFunctions.isNotEmpty(pConfigurador) && BasicFunctions.isValid(pConfigurador.getHorarioInicioManha())
                && BasicFunctions.isValid(pConfigurador.getHorarioFimManha())) {

            int hora = pConfigurador.getHoraMinutoIntervalo().getHour();
            int minuto = pConfigurador.getHoraMinutoIntervalo().getMinute();
            int horaTolerancia = pConfigurador.getHoraMinutoTolerancia().getHour();
            int minutoTolerancia = pConfigurador.getHoraMinutoTolerancia().getMinute();

            LocalTime horaMinutoTolerancia = horaContexto.truncatedTo(ChronoUnit.HOURS).plusHours(horaTolerancia)
                    .plusMinutes(minutoTolerancia);

            if (dataContexto.isEqual(pAgendamento.getDataAgendamento())) {
                if (horaContexto.isAfter(horaInicio)) {
                    horaInicio = horaContexto.truncatedTo(ChronoUnit.HOURS).plusHours(hora).plusMinutes(minuto);
                    if (horaContexto.isAfter(horaMinutoTolerancia)) {
                        horaInicio = horaInicio.plusHours(hora).plusMinutes(minuto);
                    }
                }
            }

            if (!horaInicio.isBefore(horaInicioCfg) || horaContexto.isBefore(horaInicio)) {
                if (BasicFunctions.isValid(horaInicio) && BasicFunctions.isValid(horaFim) && agenda) {

                    for (LocalTime horaAtual = horaInicio; horaAtual
                            .isBefore(horaFim); horaAtual = horaAtual.plusHours(hora).plusMinutes(minuto)) {
                        agendamentoNew = new Agendamento();
                        agendamentoNew.setStatusAgendamento(pAgendamento.getStatusAgendamento());
                        agendamentoNew.setDataAgendamento(pAgendamento.getDataAgendamento());
                        agendamentoNew.setHorarioAgendamento(horaAtual);
                        agendamentoNew.setProfissionalAgendamento(pAgendamento.getProfissionalAgendamento());
                        agendamentoNew.setPessoaAgendamento(pAgendamento.getPessoaAgendamento());
                        agendamentoNew.setOrganizacaoAgendamento(pAgendamento.getOrganizacaoAgendamento());
                        agendamentoNew.setTipoAgendamento(pAgendamento.getTipoAgendamento());
                        agendamentoNew.setNomePessoa(pAgendamento.getPessoaAgendamento().getNome());
                        agendamentoNew.setNomeProfissional(pAgendamento.getProfissionalAgendamento().getLogin());
                        agendamentoNew.setComPreferencia(Boolean.TRUE);
                        agendamentos.add(agendamentoNew);
                    }
                }
            }
        }
        return agendamentos;
    }

    public Boolean checkDatahorarioDisponivelByDataHorarioProfissional(List<Agendamento> agendamentosProfissional,
            Agendamento agendamentoNew) {
        if (BasicFunctions.isEmpty(agendamentosProfissional)) {
            return Boolean.FALSE;
        }
        return agendamentosProfissional.stream()
                .anyMatch(agendamento -> agendamento.getHorarioAgendamento().equals(agendamentoNew.getHorarioAgendamento())
                        && agendamento.getDataAgendamento().isEqual(agendamentoNew.getDataAgendamento())
                        && agendamento.getStatusAgendamento().agendado());
    }

    public Boolean checkAgendamentoDataHorarioClienteDisponivel(Agendamento pAgendamento, Boolean reagendar) {

        Agendamento agendamentoExistente = loadAgendamentoByPessoaDataAgendaHorario(pAgendamento);

        if (BasicFunctions.isNotEmpty(agendamentoExistente) && !agendamentoExistente.getId().equals(pAgendamento.getId()) && !reagendar) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    private boolean checkDataInvalidaAgendamento(@NotNull Agendamento pAgendamento, Boolean reagendar, Responses responses) {
        DayOfWeek agendamentoDayOfWeek = pAgendamento.getDataAgendamento().getDayOfWeek();

        Boolean dataValida = agendamentoAutomaticoController.validarDataAgendamento(pAgendamento, reagendar);

        if (!dataValida) {
            String dia = "";
            if (agendamentoDayOfWeek.equals(DayOfWeek.SATURDAY)) {
                dia = "Sábado";
            } else if (agendamentoDayOfWeek.equals(DayOfWeek.SUNDAY)) {
                dia = "Domingo";
            }

            responses.setStatus(400);
            responses.setMessages(new ArrayList<>());
            responses.getMessages()
                    .add("Não foi possível atualizar o Agendamento, pois o profissional " + profissional.getLogin()
                            + " não está disponível para atendimento nesse " + dia + ": "
                            + pAgendamento.getDataAgendamento());
            return true;
        }
        return false;
    }

    private void checkDataHorarioDisponivelByProfissionalAgendamento(@NotNull Agendamento pAgendamento, Responses responses) {
        List<Agendamento> agendamentosProfissional;

        agendamentosProfissional = agendamentoController
                .loadListAgendamentosByUsuarioDataAgenda(pAgendamento);

        Boolean checkDatahorario = checkDatahorarioDisponivelByDataHorarioProfissional(agendamentosProfissional,
                pAgendamento);

        if (checkDatahorario) {

            responses.setStatus(400);
            responses.setMessages(new ArrayList<>());
            responses.getMessages().add(
                    "Não foi possível atualizar o Agendamento, pois o Profissional já possui um Agendamento para a mesma data e horário.");
        }
    }

    private void loadPessoaProfissionalOrganizacaoTipoAgendamentoByAgendamento(@NotNull Agendamento pAgendamento, Boolean reagendar) {

        profissional = new Usuario();
        pessoa = new Pessoa();
        organizacao = new Organizacao();
        tipoAgendamento = new TipoAgendamento();
        statusAgendamento = new StatusAgendamento();

        if (BasicFunctions.isNotEmpty(pAgendamento.getStatusAgendamento())
                && BasicFunctions.isValid(pAgendamento.getStatusAgendamento().getId())) {
            statusAgendamento = StatusAgendamento.findById(pAgendamento.getStatusAgendamento().getId());
        }

        if (BasicFunctions.isNotEmpty(pAgendamento.getProfissionalAgendamento())
                && pAgendamento.getProfissionalAgendamento().isValid()) {
            profissional = Usuario.findById(pAgendamento.getProfissionalAgendamento().getId());
        }

        if (BasicFunctions.isNotEmpty(pAgendamento.getPessoaAgendamento())
                && pAgendamento.getPessoaAgendamento().isValid()) {
            pessoa = Pessoa.findById(pAgendamento.getPessoaAgendamento().getId());
        }
        if (BasicFunctions.isNotEmpty(pAgendamento.getOrganizacaoAgendamento())
                && pAgendamento.getOrganizacaoAgendamento().isValid()) {
            organizacao = Organizacao.findById(pAgendamento.getOrganizacaoAgendamento().getId());
        }

        if (BasicFunctions.isNotEmpty(pAgendamento.getTipoAgendamento())
                && BasicFunctions.isValid(pAgendamento.getTipoAgendamento().getId())) {
            tipoAgendamento = TipoAgendamento.findById(pAgendamento.getTipoAgendamento().getId());
        }

        validaAgendamento(pAgendamento, reagendar);
    }

    private void loadAgendamentoOldById(Agendamento pAgendamento) {

        agendamentoOld = new Agendamento();

        if (BasicFunctions.isNotEmpty(pAgendamento) && BasicFunctions.isValid(pAgendamento.getId())) {
            agendamentoOld = Agendamento.findById(pAgendamento.getId());
        }
    }

    private void validaAgendamento(Agendamento pAgendamento, Boolean reagendar) {

        if (BasicFunctions.isEmpty(agendamento) || BasicFunctions.isNotEmpty(agendamento) && !agendamento.getHorarioAgendamento().equals(pAgendamento.getHorarioAgendamento()) ||
                !agendamento.getDataAgendamento().isEqual(pAgendamento.getDataAgendamento()) ||
                !agendamento.getProfissionalAgendamento().getId().equals(pAgendamento.getProfissionalAgendamento().getId()) ||
                !agendamento.getOrganizacaoAgendamento().getId().equals(pAgendamento.getOrganizacaoAgendamento().getId())) {

            checkDataHorarioDisponivelByProfissionalAgendamento(pAgendamento, responses);

        }
        if (BasicFunctions.isEmpty(pAgendamento) || BasicFunctions.isEmpty(pAgendamento.getDataAgendamento())
                && BasicFunctions.isEmpty(pAgendamento.getProfissionalAgendamento())
                && BasicFunctions.isEmpty(pAgendamento.getPessoaAgendamento())
                && BasicFunctions.isEmpty(pAgendamento.getOrganizacaoAgendamento())) {
            responses.setStatus(400);
            responses.getMessages().add("Informe os dados para remarcar o Agendamento.");

        }

        if (BasicFunctions.isValid(pAgendamento.getDataAgendamento())) {

            checkDataInvalidaAgendamento(pAgendamento, reagendar, responses);

        } else {
            responses.setStatus(400);
            responses.getMessages().add("Por favor, informe a Data da Consulta corretamente!");
        }
        if (BasicFunctions.isInvalid(pAgendamento.getHorarioAgendamento())) {
            responses.setStatus(400);
            responses.getMessages().add("Por favor, informe o horário da Consulta corretamente!");
        }
        if (BasicFunctions.isEmpty(organizacao)) {
            responses.setStatus(400);
            responses.getMessages().add("Por favor, selecione o Local do Atendimento corretamente!");
        }

    }
}
