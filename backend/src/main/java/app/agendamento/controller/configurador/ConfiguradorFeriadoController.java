package app.agendamento.controller.configurador;

import app.agendamento.model.configurador.ConfiguradorFeriado;
import app.agendamento.model.organizacao.Organizacao;
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
public class ConfiguradorFeriadoController {

    List<Organizacao> organizacoesExcecoes;
    List<Long> organizacoesExcecoesId;
    @Context
    SecurityContext context;
    private ConfiguradorFeriado configuradorFeriado;
    private Responses responses;

    public Response addConfiguradorFeriado(@NotNull ConfiguradorFeriado pConfiguradorFeriado) {

        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        loadConfiguradorFeriadoByDataFeriado(pConfiguradorFeriado);

        if (BasicFunctions.isEmpty(configuradorFeriado)) {

            configuradorFeriado = new ConfiguradorFeriado();

            loadByConfiguradorFeriado(pConfiguradorFeriado);

            if (!responses.hasMessages()) {

               configuradorFeriado = new ConfiguradorFeriado(pConfiguradorFeriado, organizacoesExcecoes, context);

                configuradorFeriado.persist();

                responses.setStatus(201);
                responses.setData(configuradorFeriado);
                responses.getMessages().add("Configurador de Feriado cadastrado com sucesso!");

            } else {
                return Response.ok(responses).status(responses.getStatus()).build();
            }
            return Response.ok(responses).status(Response.Status.CREATED).build();
        } else {

            responses.setStatus(400);
            responses.setData(configuradorFeriado);
            responses.getMessages().add("Configurador de Feriado já existente!");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    public Response updateConfiguradorFeriado(@NotNull ConfiguradorFeriado pConfiguradorFeriado) {

        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        try {

            loadConfiguradorFeriadoById(pConfiguradorFeriado);

            if (BasicFunctions.isNotEmpty(configuradorFeriado)) {

                loadByConfiguradorFeriado(pConfiguradorFeriado);

                    if (!responses.hasMessages()) {

                        configuradorFeriado = configuradorFeriado.configuradorFeriado(configuradorFeriado, pConfiguradorFeriado, organizacoesExcecoes, context);

                        configuradorFeriado.persistAndFlush();

                        responses.setStatus(201);
                        responses.setData(configuradorFeriado);
                        responses.getMessages().add("Configurador de Feriado atualizado com sucesso!");
                    }
                }
                return Response.ok(responses).status(Response.Status.CREATED).build();
        } catch (Exception e) {

            responses.setStatus(400);
            responses.setData(configuradorFeriado);
            responses.getMessages().add("Não foi possível atualizar o Configurador de Feriado.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    public Response deleteConfiguradorFeriado(@NotNull List<Long> pListIdConfiguradorFeriado) {

        List<ConfiguradorFeriado> configuradorFeriados;
        List<ConfiguradorFeriado> configuradorFeriadosAux = new ArrayList<>();
        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        configuradorFeriados = ConfiguradorFeriado.list("id in ?1", pListIdConfiguradorFeriado);
        int count = configuradorFeriados.size();

        try {

            if (configuradorFeriados.isEmpty()) {

                responses.setStatus(400);
                responses.getMessages().add("Configuradores de Feriados não localizados ou já excluídos.");
                return Response.ok(responses).status(responses.getStatus()).build();
            }

            configuradorFeriados.forEach((configFeriado) -> {
                configuradorFeriadosAux.add(configFeriado);
                responses.setData(configFeriado);
                configFeriado.delete();

            });

            responses.setStatus(200);
            if (count <= 1) {
                responses.setData(configuradorFeriado);
                responses.getMessages().add("Configurador de Feriado excluído com sucesso!");
            } else {
                responses.setDatas(Collections.singletonList(configuradorFeriadosAux));
                responses.getMessages().add(count + " Configuradores de Feriados excluídos com sucesso!");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            if (count <= 1) {
                responses.setData(configuradorFeriado);
                responses.getMessages().add("Configurador de Feriado não localizado ou já excluído.");
            } else {
                responses.setDatas(Collections.singletonList(configuradorFeriados));
                responses.getMessages().add("Configuradores de Feriados não localizados ou já excluídos.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    private void loadByConfiguradorFeriado(ConfiguradorFeriado pConfiguradorFeriado) {

        organizacoesExcecoes = new ArrayList<>();
        organizacoesExcecoesId = new ArrayList<>();

        if (BasicFunctions.isNotEmpty(pConfiguradorFeriado.getOrganizacoesFeriado())) {
            pConfiguradorFeriado.getOrganizacoesFeriado().forEach(organizacao -> organizacoesExcecoesId.add(organizacao.getId()));
        }

        organizacoesExcecoes = Organizacao.list("id in ?1", organizacoesExcecoesId);

        validaConfiguradorFeriado(pConfiguradorFeriado);
    }

    private void validaConfiguradorFeriado(ConfiguradorFeriado pConfiguradorFeriado) {

        if (BasicFunctions.isEmpty(pConfiguradorFeriado)) {
            responses.setStatus(400);
            responses.getMessages().add("Informe os dados para atualizar o Configurador de Feriado.");
        }
    }

    private void loadConfiguradorFeriadoByDataFeriado(ConfiguradorFeriado pConfiguradorFeriado) {

        configuradorFeriado = new ConfiguradorFeriado();

        if (BasicFunctions.isNotEmpty(pConfiguradorFeriado.getDataFeriado())
                && BasicFunctions.isNotEmpty(pConfiguradorFeriado.getHoraInicioFeriado())
                && BasicFunctions.isNotEmpty(pConfiguradorFeriado.getHoraFimFeriado())) {
            configuradorFeriado = ConfiguradorFeriado.find("dataFeriado = ?1", pConfiguradorFeriado.getDataFeriado())
                    .firstResult();
        }
    }

    private void loadConfiguradorFeriadoById(ConfiguradorFeriado pConfiguradorFeriado) {

        configuradorFeriado = new ConfiguradorFeriado();

        if (BasicFunctions.isNotEmpty(pConfiguradorFeriado) && pConfiguradorFeriado.isValid()) {
            configuradorFeriado = ConfiguradorFeriado.find("id = ?1",
                    pConfiguradorFeriado.getId()).firstResult();
        }
    }
}
