package app.agendamento.controller.agendamento;

import app.agendamento.DTO.agendamento.AgendamentoDTO;
import app.agendamento.controller.configurador.ConfiguradorAgendamentoController;
import app.agendamento.controller.pessoa.UsuarioController;
import app.agendamento.model.agendamento.Agendamento;
import app.agendamento.model.agendamento.StatusAgendamento;
import app.agendamento.model.agendamento.TipoAgendamento;
import app.agendamento.model.configurador.ConfiguradorAgendamento;
import app.agendamento.model.configurador.ConfiguradorFeriado;
import app.agendamento.model.organizacao.Organizacao;
import app.agendamento.model.pessoa.Pessoa;
import app.agendamento.model.pessoa.Usuario;
import app.core.model.DTO.Responses;
import app.core.utils.BasicFunctions;
import app.core.utils.Contexto;
import org.jetbrains.annotations.NotNull;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@Transactional
public class AgendamentoAutomaticoController {

    @Inject
    AgendamentoController agendamentoController;
    @Inject
    UsuarioController usuarioController;
    @Inject
    ConfiguradorAgendamentoController configuradorAgendamentoController;

    TipoAgendamento tipoAgendamento;
    Pessoa pessoa;
    Organizacao organizacao;

    public Response listAgendamentosLivres(@NotNull Agendamento pAgendamento, Boolean comPreferencia, Boolean reagendar) {

        List<ConfiguradorAgendamento> configuradorAgendamentoList = new ArrayList<>();
        List<Usuario> listaProfissionais = new ArrayList<>();
        List<Agendamento> agendamentos;
        List<Agendamento> agendamentosLivres;
        List<Agendamento> agendamentosLivresAux = new ArrayList<>();
        List<Agendamento> returnAgendamentosLivres;
        List<AgendamentoDTO> listAgendamentoDTO;
        ConfiguradorAgendamento configuradorAgendamento;
        Usuario profissionalAux;
        StatusAgendamento statusAgendamento;
        Responses responses = new Responses();
        responses.setDatas(new ArrayList<>());
        responses.setMessages(new ArrayList<>());
        tipoAgendamento = TipoAgendamento.findById(pAgendamento.getTipoAgendamento().getId());
        pessoa = Pessoa.findById(pAgendamento.getPessoaAgendamento().getId());
        organizacao = Organizacao.findById(pAgendamento.getOrganizacaoAgendamento().getId());
        statusAgendamento = StatusAgendamento.statusLivre();

        LocalDate dataContexto = Contexto.dataContexto();

        if (pAgendamento.getDataAgendamento().isBefore(dataContexto)) {

            responses.setStatus(400);
            responses.setData(pAgendamento);
            responses.getMessages().add("a data escolhida :" + pAgendamento.getDataAgendamento() + " é anterior a data de hoje: "
                    + dataContexto);
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }

        if (BasicFunctions.isValid(comPreferencia)) {
            if (comPreferencia) {
                configuradorAgendamento = configuradorAgendamentoController
                        .loadConfiguradorByUsuarioOrganizacao(pAgendamento);
                configuradorAgendamentoList.add(configuradorAgendamento);
                agendamentos = new ArrayList<>(
                        agendamentoController.loadListAgendamentosByUsuarioDataAgenda(pAgendamento));
                profissionalAux = usuarioController.loadUsuarioByOrganizacao(pAgendamento);
                if (BasicFunctions.isNotEmpty(profissionalAux)) {
                    listaProfissionais.add(profissionalAux);
                } else {

                    responses.setStatus(400);
                    responses.setData(pAgendamento);
                    responses.getMessages().add("O profissional em questão não estará disponível na data escolhida: "
                            + pAgendamento.getDataAgendamento());
                    return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
                }
            } else {
                configuradorAgendamentoList = configuradorAgendamentoController
                        .listConfiguradoresByOrganizacao(pAgendamento);
                agendamentos = agendamentoController.loadListAgendamentosByDataAgenda(pAgendamento);
                listaProfissionais = usuarioController.loadListUsuariosByOrganizacaoAndDataAgendamento(pAgendamento);
            }

        } else {

            responses.setStatus(400);
            responses.setData(pAgendamento);
            responses.getMessages().add("Por favor, informe se há preferência por usuário ou não no atendimento:");
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }

        if (BasicFunctions.isNotEmpty(pAgendamento.getTipoAgendamento())
                && pAgendamento.getTipoAgendamento().isValid()) {
            tipoAgendamento = TipoAgendamento.findById(pAgendamento.getTipoAgendamento().getId());
        } else {
            return null;
        }
        if (BasicFunctions.isNotEmpty(pAgendamento.getPessoaAgendamento())
                && pAgendamento.getPessoaAgendamento().isValid()) {
            pessoa = Pessoa.findById(pAgendamento.getPessoaAgendamento().getId());
        } else {
            return null;
        }
        if (BasicFunctions.isNotEmpty(pAgendamento.getOrganizacaoAgendamento())
                && pAgendamento.getOrganizacaoAgendamento().isValid()) {
            organizacao = Organizacao.findById(pAgendamento.getOrganizacaoAgendamento().getId());
        } else {
            return null;
        }

        if (BasicFunctions.isNotEmpty(listaProfissionais) && BasicFunctions.isNotEmpty(configuradorAgendamentoList)) {

            for (Usuario profissional : listaProfissionais) {

                ConfiguradorAgendamento forConfiguradorAgendamento;

                forConfiguradorAgendamento = configuradorAgendamentoList.stream()
                        .filter(x -> x.getProfissionalConfigurador().getId().equals(profissional.getId())).findFirst()
                        .orElse(null);

                if (BasicFunctions.isNotEmpty(forConfiguradorAgendamento)
                        && forConfiguradorAgendamento.getProfissionalConfigurador().equals(profissional)) {

                    DayOfWeek agendamentoDayOfWeek = pAgendamento.getDataAgendamento().getDayOfWeek();

                    if (agendamentoDayOfWeek.equals(DayOfWeek.SATURDAY)) {
                        if (!forConfiguradorAgendamento.atendeSabado()) {
                            responses.setStatus(400);
                            responses.setData(pAgendamento);
                            if (comPreferencia) {

                                responses.getMessages().add("O profissional " + profissional.getLogin()
                                        + " em questão não estará disponível para atendimentos no sábado: "
                                        + pAgendamento.getDataAgendamento() + " na Empresa: " + organizacao.getNome() + ".");
                            } else {

                                responses.getMessages()
                                        .add("Não há nenhum profissional disponível para atendimentos no sábado: "
                                                + pAgendamento.getDataAgendamento() + " na Empresa: " + organizacao.getNome()
                                                + ".");
                            }
                            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
                        }
                    } else if (agendamentoDayOfWeek.equals(DayOfWeek.SUNDAY)) {
                        if (!forConfiguradorAgendamento.atendeDomingo()) {
                            responses.setStatus(400);
                            responses.setData(pAgendamento);
                            if (comPreferencia) {

                                responses.getMessages().add("O profissional " + profissional.getLogin()
                                        + " em questão não estará disponível para atendimentos no domingo: "
                                        + pAgendamento.getDataAgendamento() + " na Empresa: " + organizacao.getNome() + ".");
                            } else {

                                responses.getMessages()
                                        .add("Não há nenhum profissional disponível para atendimentos no domingo: "
                                                + pAgendamento.getDataAgendamento() + " na Empresa: " + organizacao.getNome()
                                                + ".");
                            }
                            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
                        }
                    }
                    pAgendamento.setPessoaAgendamento(pessoa);
                    pAgendamento.setTipoAgendamento(tipoAgendamento);
                    pAgendamento.setOrganizacaoAgendamento(organizacao);
                    pAgendamento.setProfissionalAgendamento(profissional);
                    pAgendamento.setStatusAgendamento(statusAgendamento);
                    agendamentosLivres = agendamentoController
                            .makeListAgendamentosByProfissionalAgendamento(pAgendamento, forConfiguradorAgendamento);
                    if (BasicFunctions.isNotEmpty(agendamentosLivres)) {
                        agendamentosLivresAux.addAll(agendamentosLivres);
                    }
                }
            }
            if (BasicFunctions.isNotEmpty(agendamentosLivresAux)) {

                returnAgendamentosLivres = agendamentoController.makeAgendamentosLivres(agendamentosLivresAux,
                        agendamentos, reagendar);

                listAgendamentoDTO = AgendamentoDTO.makeListAgendamentoDTO(returnAgendamentosLivres);
                responses.setStatus(200);
                responses.getMessages().add("Existe horários disponíveis.");
                responses.getDatas().addAll(listAgendamentoDTO);
                return Response.ok(responses).status(responses.getStatus()).build();
            } else {

                responses.setStatus(400);
                responses.setData(pAgendamento);
                responses.getMessages().add("Não existe horários disponíveis para a seguinte data: "
                        + pAgendamento.getDataAgendamento() + " na Empresa: " + organizacao.getNome() + ".");
                return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
            }
        }
        return null;
    }

    public Boolean validarDataAgendamento(Agendamento pAgendamento, Boolean reagendar) {

        List<ConfiguradorAgendamento> configuradoresAgendamento = configuradorAgendamentoController
                .listConfiguradoresByOrganizacao(pAgendamento);

        DayOfWeek agendamentoDayOfWeek = pAgendamento.getDataAgendamento().getDayOfWeek();

        Organizacao organizacao = Organizacao.findById(pAgendamento.getOrganizacaoAgendamento().getId());
        ConfiguradorFeriado configuradorFeriado = ConfiguradorFeriado
                .find("dataFeriado = ?1", pAgendamento.getDataAgendamento()).firstResult();

        LocalDate dataAgendamento = pAgendamento.getDataAgendamento();

        LocalDate dataContexto = Contexto.dataContexto(organizacao);

        if (dataAgendamento.isBefore(dataContexto)) {
            return Boolean.FALSE;
        }

        if (BasicFunctions.isNotEmpty(configuradorFeriado)) {
            if (configuradorFeriado.getOrganizacoesFeriado().isEmpty()
                    || configuradorFeriado.getOrganizacoesFeriado().contains(organizacao)) {
                return Boolean.FALSE;
            }
        }

        boolean algumaOrganizacaoAtende = configuradoresAgendamento.stream()
                .anyMatch(x -> x.atendeSabado() && agendamentoDayOfWeek.equals(DayOfWeek.SATURDAY) || x.atendeDomingo() && agendamentoDayOfWeek.equals(DayOfWeek.SUNDAY));

        if (algumaOrganizacaoAtende) {
            return Boolean.FALSE;
        }

        Response agendamentosLivres = listAgendamentosLivres(pAgendamento, false, reagendar);

        if (agendamentosLivres.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
            return Boolean.FALSE;
        }
        Boolean dataDisponivel = agendamentoController.checkAgendamentoDataHorarioClienteDisponivel(pAgendamento, reagendar);

        if (!dataDisponivel) {
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }
}