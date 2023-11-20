package app.agendamento.controller.configurador;

import app.agendamento.model.agendamento.Agendamento;
import app.agendamento.model.agendamento.TipoAgendamento;
import app.agendamento.model.configurador.ConfiguradorAgendamentoEspecial;
import app.agendamento.model.organizacao.Organizacao;
import app.agendamento.model.pessoa.Usuario;
import app.core.model.DTO.Responses;
import app.core.utils.BasicFunctions;
import org.jetbrains.annotations.NotNull;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
@Transactional
public class ConfiguradorAgendamentoEspecialController {

    @Context
    SecurityContext context;
    List<TipoAgendamento> tiposAgendamentos;
    List<Long> tiposAgendamentosId;
    private ConfiguradorAgendamentoEspecial configuradorAgendamentoEspecial;
    private Responses responses;
    private Usuario usuario;
    private Organizacao organizacao;

    public Response addConfiguradorAgendamentoEspecial(
            @NotNull ConfiguradorAgendamentoEspecial pConfiguradorAgendamentoEspecial) {

        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        loadByOrganizacaoProfissionalData(pConfiguradorAgendamentoEspecial);

        loadByConfiguradorAgendamentoEspecial(pConfiguradorAgendamentoEspecial);

        if (BasicFunctions.isEmpty(configuradorAgendamentoEspecial)) {

            configuradorAgendamentoEspecial = new ConfiguradorAgendamentoEspecial();

            if (!responses.hasMessages()) {

                configuradorAgendamentoEspecial = new ConfiguradorAgendamentoEspecial(pConfiguradorAgendamentoEspecial,
                        tiposAgendamentos, organizacao, usuario, context);

                configuradorAgendamentoEspecial.persist();

                responses.setStatus(201);
                responses.setData(configuradorAgendamentoEspecial);
                responses.getMessages().add("Configurador de Agendamento cadastrado com sucesso!");
            } else {
                return Response.ok(responses).status(responses.getStatus()).build();
            }
        } else {

            responses.setStatus(400);
            responses.setData(configuradorAgendamentoEspecial);
            responses.getMessages().add("Configurador de Agendamento Especial já existente!");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
        return Response.ok(responses).status(Response.Status.CREATED).build();
    }

    public Response updateConfiguradorAgendamentoEspecial(
            @NotNull ConfiguradorAgendamentoEspecial pConfiguradorAgendamentoEspecial) {

        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        loadConfiguradorEspecialById(pConfiguradorAgendamentoEspecial);

        try {

            if (BasicFunctions.isNotEmpty(configuradorAgendamentoEspecial)) {
                loadByConfiguradorAgendamentoEspecial(pConfiguradorAgendamentoEspecial);
            }

                if (!responses.hasMessages()) {
                    configuradorAgendamentoEspecial = configuradorAgendamentoEspecial.configuradorAgendamentoEspecial(configuradorAgendamentoEspecial, pConfiguradorAgendamentoEspecial,
                            tiposAgendamentos, organizacao, usuario, context);

                    configuradorAgendamentoEspecial.persistAndFlush();

                    responses.setStatus(201);
                    responses.setData(configuradorAgendamentoEspecial);
                    responses.getMessages().add("Configurador de Agendamento atualizado com sucesso!");
                }
            return Response.ok(responses).status(responses.getStatus()).build();

        } catch (Exception e) {

            responses.setStatus(400);
            responses.setData(configuradorAgendamentoEspecial);
            responses.getMessages().add("Não foi possível remarcar o ConfiguradorAgendamentoEspecial.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    public Response deleteConfiguradorAgendamentoEspecial(@NotNull List<Long> pListIdConfiguradorAgendamento) {

        List<ConfiguradorAgendamentoEspecial> configuradorAgendamentos;
        List<ConfiguradorAgendamentoEspecial> agendamentosAux = new ArrayList<>();
        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        configuradorAgendamentos = ConfiguradorAgendamentoEspecial.list("id in ?1", pListIdConfiguradorAgendamento);
        int count = configuradorAgendamentos.size();

        try {

            if (configuradorAgendamentos.isEmpty()) {

                responses.setStatus(400);
                responses.getMessages().add("Configuradores Agendamentos Especiais não localizados ou já excluídos.");
                return Response.ok(responses).status(responses.getStatus()).build();
            }

            configuradorAgendamentos.forEach((configAendamento) -> {
                agendamentosAux.add(configAendamento);
                responses.setData(configAendamento);
                configAendamento.delete();

            });

            responses.setStatus(200);
            if (count <= 1) {
                responses.setData(configuradorAgendamentoEspecial);
                responses.setMessages(new ArrayList<>());
                responses.getMessages().add("Configurador de Agendamento Especial excluído com sucesso!");
            } else {
                responses.setDatas(Collections.singletonList(agendamentosAux));
                responses.setMessages(new ArrayList<>());
                responses.getMessages().add(count + " Configuradores de Agendamentos excluídos com sucesso!");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            if (count <= 1) {
                responses.setData(configuradorAgendamentoEspecial);
                responses.setMessages(new ArrayList<>());
                responses.getMessages().add("Configurador de Agendamento Especial não localizado ou já excluído.");
            } else {
                responses.setDatas(Collections.singletonList(configuradorAgendamentos));
                responses.setMessages(new ArrayList<>());
                responses.getMessages().add("Configuradores de Agendamentos Especiais não localizados ou já excluídos.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    public ConfiguradorAgendamentoEspecial loadConfiguradorEspecialByUsuarioOrganizacao(Agendamento pAgendamento) {

        ConfiguradorAgendamentoEspecial configuradorAgendamentoEspecial = new ConfiguradorAgendamentoEspecial();

        if (BasicFunctions.isNotEmpty(pAgendamento.getOrganizacaoAgendamento())
                && pAgendamento.getOrganizacaoAgendamento().isValid()) {

            return ConfiguradorAgendamentoEspecial
                    .find("organizacaoId = ?1 and profissionalId = ?2 and ?3 between dataInicio and dataFim",
                            pAgendamento.getOrganizacaoAgendamento().getId(), pAgendamento.getProfissionalAgendamento().getId(),
                            pAgendamento.getDataAgendamento())
                    .firstResult();
        }
        return configuradorAgendamentoEspecial;
    }

    public List<ConfiguradorAgendamentoEspecial> loadListConfiguradoresEspeciaisByOrganizacao(
            Agendamento pAgendamento) {
        List<ConfiguradorAgendamentoEspecial> configuradoresEspeciais = new ArrayList<>();
        if (BasicFunctions.isNotEmpty(pAgendamento.getOrganizacaoAgendamento())
                && pAgendamento.getOrganizacaoAgendamento().isValid()) {
            return ConfiguradorAgendamentoEspecial.list("organizacaoId = ?1 and ?2 between dataInicio and dataFim",
                    pAgendamento.getOrganizacaoAgendamento().getId(), pAgendamento.getDataAgendamento());
        }
        return configuradoresEspeciais;
    }

    private void loadByConfiguradorAgendamentoEspecial(@NotNull ConfiguradorAgendamentoEspecial pConfiguradorAgendamentoEspecial) {

        organizacao = new Organizacao();
        usuario = new Usuario();
        tiposAgendamentosId = new ArrayList<>();
        tiposAgendamentos = new ArrayList<>();

        if (BasicFunctions.isNotEmpty(pConfiguradorAgendamentoEspecial.getTiposAgendamentos())) {
            pConfiguradorAgendamentoEspecial.getTiposAgendamentos()
                    .forEach(tipoAgendamento -> tiposAgendamentosId.add(tipoAgendamento.getId()));
        }

        tiposAgendamentos = TipoAgendamento.list("id in ?1", tiposAgendamentosId);

        if (BasicFunctions.isNotEmpty(pConfiguradorAgendamentoEspecial)
                && BasicFunctions.isNotEmpty(pConfiguradorAgendamentoEspecial.getOrganizacaoConfigurador())
                && pConfiguradorAgendamentoEspecial.getOrganizacaoConfigurador().isValid()) {
            organizacao = Organizacao.findById(pConfiguradorAgendamentoEspecial.getOrganizacaoConfigurador().getId());
        }

        if (BasicFunctions.isNotEmpty(pConfiguradorAgendamentoEspecial)
                && BasicFunctions.isNotEmpty(pConfiguradorAgendamentoEspecial.getProfissionalConfigurador())
                && pConfiguradorAgendamentoEspecial.getProfissionalConfigurador().isValid()) {
            usuario = Usuario.findById(pConfiguradorAgendamentoEspecial.getProfissionalConfigurador().getId());
        }

        validaConfiguradorEspecial(pConfiguradorAgendamentoEspecial);

    }

    private void loadConfiguradorEspecialById(ConfiguradorAgendamentoEspecial pConfiguradorAgendamentoEspecial) {

        configuradorAgendamentoEspecial = new ConfiguradorAgendamentoEspecial();

        if (BasicFunctions.isNotEmpty(pConfiguradorAgendamentoEspecial)
                && BasicFunctions.isNotEmpty(pConfiguradorAgendamentoEspecial.getOrganizacaoConfigurador())
                && pConfiguradorAgendamentoEspecial.getOrganizacaoConfigurador().isValid()) {
            configuradorAgendamentoEspecial = ConfiguradorAgendamentoEspecial.find("id = ?1",
                    pConfiguradorAgendamentoEspecial.getId()).firstResult();
        }

        validaConfiguradorEspecial(pConfiguradorAgendamentoEspecial);
    }

    private void validaConfiguradorEspecial(ConfiguradorAgendamentoEspecial pConfiguradorAgendamentoEspecial) {
        if (BasicFunctions.isEmpty(organizacao)) {
            responses.setStatus(400);
            responses.getMessages().add("Por favor, informe a Organização do Configurador Especial corretamente!");
        }
        if (BasicFunctions.isEmpty(usuario)) {
            responses.setStatus(400);
            responses.getMessages().add("Por favor, informe o profissional do Configurador Especial corretamente!");
        }
        if (BasicFunctions.isEmpty(pConfiguradorAgendamentoEspecial)) {
            responses.setStatus(400);
            responses.getMessages().add("Informe os dados para atualizar o Configurador de Agendamento Especial.");
        }
    }

    private void loadByOrganizacaoProfissionalData(ConfiguradorAgendamentoEspecial pConfiguradorAgendamentoEspecial) {

        configuradorAgendamentoEspecial = new ConfiguradorAgendamentoEspecial();

        if (BasicFunctions.isNotEmpty(pConfiguradorAgendamentoEspecial)
                && BasicFunctions.isNotEmpty(pConfiguradorAgendamentoEspecial.getOrganizacaoConfigurador())
                && pConfiguradorAgendamentoEspecial.getOrganizacaoConfigurador().isValid()) {
            configuradorAgendamentoEspecial = ConfiguradorAgendamentoEspecial
                    .find("organizacaoId = ?1 and profissionalId = ?2 and dataInicio = ?3 and dataFim = ?4",
                            pConfiguradorAgendamentoEspecial.getOrganizacaoConfigurador().getId(),
                            pConfiguradorAgendamentoEspecial.getProfissionalConfigurador().getId(),
                            pConfiguradorAgendamentoEspecial.getDataInicio(), pConfiguradorAgendamentoEspecial.getDataFim())
                    .firstResult();
        }
    }
}
