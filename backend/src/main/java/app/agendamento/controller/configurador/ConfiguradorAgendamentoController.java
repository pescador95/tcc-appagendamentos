package app.agendamento.controller.configurador;

import app.agendamento.model.agendamento.Agendamento;
import app.agendamento.model.configurador.ConfiguradorAgendamento;
import app.agendamento.model.organizacao.Organizacao;
import app.agendamento.model.pessoa.Usuario;
import app.agendamento.queries.configurador.ConfiguradorAgendamentoQueries;
import app.core.model.DTO.Responses;
import app.core.utils.BasicFunctions;
import org.jetbrains.annotations.NotNull;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
@Transactional
public class ConfiguradorAgendamentoController {

    @Context
    SecurityContext context;
    @Inject
    ConfiguradorAgendamentoQueries configuradorAgendamentoQueries;
    private ConfiguradorAgendamento configuradorAgendamento = new ConfiguradorAgendamento();
    private Responses responses;
    private Organizacao organizacao;
    private Usuario profissionalConfigurador;

    public Response addConfiguradorAgendamento(@NotNull ConfiguradorAgendamento pConfiguradorAgendamento) {

        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        loadConfiguradorAgendamentoById(pConfiguradorAgendamento);

        if (BasicFunctions.isEmpty(configuradorAgendamento)) {

            configuradorAgendamento = new ConfiguradorAgendamento();

            loadByConfiguradorAgendamento(pConfiguradorAgendamento);

            if (!responses.hasMessages()) {
                responses.setMessages(new ArrayList<>());
                configuradorAgendamento = new ConfiguradorAgendamento(pConfiguradorAgendamento, organizacao, profissionalConfigurador, context);
                configuradorAgendamento.persist();

                responses.setStatus(201);
                responses.setData(configuradorAgendamento);
                responses.getMessages().add("Configurador de Agendamento cadastrado com sucesso!");

            } else {
                return Response.ok(responses).status(responses.getStatus()).build();
            }
            return Response.ok(responses).status(Response.Status.CREATED).build();
        } else {

            responses.setStatus(400);
            responses.setData(configuradorAgendamento);
            responses.getMessages().add("Configurador de Agendamento já realizado!");
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }

    public Response updateConfiguradorAgendamento(ConfiguradorAgendamento pConfiguradorAgendamento) {

        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        loadConfiguradorAgendamentoById(pConfiguradorAgendamento);

        try {

            loadByConfiguradorAgendamento(pConfiguradorAgendamento);

            if (BasicFunctions.isNotEmpty(configuradorAgendamentoQueries) && !responses.hasMessages()) {

                    configuradorAgendamento = configuradorAgendamento.configuradorAgendamento(configuradorAgendamento, pConfiguradorAgendamento, organizacao, profissionalConfigurador, context);
                    configuradorAgendamento.persistAndFlush();

                    responses.setStatus(201);
                    responses.setData(configuradorAgendamento);
                    responses.getMessages().add("Configurador de Agendamento atualizado com sucesso!");
                }
            return Response.ok(responses).status(responses.getStatus()).build();

        } catch (Exception e) {

            responses.setStatus(400);
            responses.setData(configuradorAgendamento);
            responses.getMessages().add("Não foi possível remarcar o ConfiguradorAgendamento.");
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }

    public Response deleteConfiguradorAgendamento(@NotNull List<Long> pListIdConfiguradorAgendamento) {

        List<ConfiguradorAgendamento> configuradorAgendamentos;
        List<ConfiguradorAgendamento> agendamentosAux = new ArrayList<>();
        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        configuradorAgendamentos = ConfiguradorAgendamento.list("id in ?1", pListIdConfiguradorAgendamento);
        int count = configuradorAgendamentos.size();

        try {

            if (configuradorAgendamentos.isEmpty()) {

                responses.setStatus(400);
                responses.getMessages().add("Agendamentos não localizados ou já excluídos.");
                return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
            }

            configuradorAgendamentos.forEach((configAgendamento) -> {
                agendamentosAux.add(configAgendamento);
                responses.setData(configAgendamento);
                configAgendamento.delete();

            });

            responses.setStatus(200);
            if (count <= 1) {
                responses.setData(configuradorAgendamento);
                responses.getMessages().add("Configurador de Agendamento excluído com sucesso!");
            } else {
                responses.setDatas(Collections.singletonList(agendamentosAux));
                responses.getMessages().add(count + " Configuradores de Agendamentos excluídos com sucesso!");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            if (count <= 1) {
                responses.setData(configuradorAgendamento);
                responses.getMessages().add("Configurador de Agendamento não localizado ou já excluído.");
            } else {
                responses.setDatas(Collections.singletonList(configuradorAgendamentos));
                responses.getMessages().add("Configuradores de Agendamentos não localizados ou já excluídos.");
            }
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }

    public List<ConfiguradorAgendamento> listConfiguradoresByOrganizacao(Agendamento pAgendamento) {

        List<ConfiguradorAgendamento> configuradorAgendamentoList = new ArrayList<>();

        if (BasicFunctions.isNotEmpty(pAgendamento.getOrganizacaoAgendamento())
                && pAgendamento.getOrganizacaoAgendamento().isValid()) {

            return configuradorAgendamentoQueries.loadListConfiguradorAgendamentoByOrganizacao(pAgendamento);
        }
        return configuradorAgendamentoList;
    }

    public ConfiguradorAgendamento loadConfiguradorByUsuarioOrganizacao(Agendamento pAgendamento) {

        ConfiguradorAgendamento configuradorAgendamento = new ConfiguradorAgendamento();

        if (pAgendamento.getOrganizacaoAgendamento().isValid()
                && pAgendamento.getProfissionalAgendamento().isValid()) {
            return configuradorAgendamentoQueries.loadConfiguradorAgendamentoByOrganizacaoProfissional(pAgendamento);
        }
        return configuradorAgendamento;
    }

    private void loadByConfiguradorAgendamento(@NotNull ConfiguradorAgendamento pConfiguradorAgendamento) {

        organizacao = new Organizacao();
        profissionalConfigurador = new Usuario();

        if (BasicFunctions.isNotEmpty(pConfiguradorAgendamento)
                && BasicFunctions.isNotEmpty(pConfiguradorAgendamento.getOrganizacaoConfigurador())
                && pConfiguradorAgendamento.getOrganizacaoConfigurador().isValid()) {
            configuradorAgendamento = ConfiguradorAgendamento.find("organizacaoId = ?1",
                    pConfiguradorAgendamento.getOrganizacaoConfigurador().getId()).firstResult();
        }

        if (BasicFunctions.isNotEmpty(pConfiguradorAgendamento)
                && BasicFunctions.isNotEmpty(pConfiguradorAgendamento.getOrganizacaoConfigurador())
                && pConfiguradorAgendamento.getOrganizacaoConfigurador().isValid()) {
            organizacao = Organizacao.findById(pConfiguradorAgendamento.getOrganizacaoConfigurador().getId());
        }

        if (BasicFunctions.isNotEmpty(pConfiguradorAgendamento)
                && BasicFunctions.isNotEmpty(pConfiguradorAgendamento.getProfissionalConfigurador())
                && pConfiguradorAgendamento.getProfissionalConfigurador().isValid()) {
            profissionalConfigurador = Usuario.findById(pConfiguradorAgendamento.getProfissionalConfigurador().getId());
        }
        if (BasicFunctions.isEmpty(organizacao)) {
            responses.getMessages().add("Por favor, informe a Organização do Configurador corretamente!");
        }
        if (BasicFunctions.isEmpty(profissionalConfigurador)) {
            responses.getMessages().add("Não foi possível localizar o profissional.");
        }
    }

    private void loadConfiguradorAgendamentoById(ConfiguradorAgendamento pConfiguradorAgendamento) {

        validarConfiguradorAgendamento(pConfiguradorAgendamento);

        configuradorAgendamento = new ConfiguradorAgendamento();

        if (BasicFunctions.isNotEmpty(pConfiguradorAgendamento)) {

            configuradorAgendamento = ConfiguradorAgendamento.findById(pConfiguradorAgendamento.getId());

            validarConfiguradorAgendamento(configuradorAgendamento);
        }
    }

    private void validarConfiguradorAgendamento(ConfiguradorAgendamento pConfiguradorAgendamento) {

        if (BasicFunctions.isEmpty(pConfiguradorAgendamento)) {
            responses.setStatus(400);
            responses.getMessages().add("Informe os dados para atualizar o Configurador de Agendamento.");
        }
    }
}
