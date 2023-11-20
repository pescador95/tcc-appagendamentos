package app.agendamento.controller.configurador;

import app.agendamento.model.configurador.ConfiguradorAusencia;
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
public class ConfiguradorAusenciaController {

    List<Usuario> profissionaisAusentes;
    List<Long> profissionaisAusentesId;
    @Context
    SecurityContext context;
    private ConfiguradorAusencia configuradorAusencia;
    private Responses responses;

    public Response addConfiguradorAusencia(@NotNull ConfiguradorAusencia pConfiguradorAusencia) {

        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        loadByConfiguradorAusenciaByDataInicioFim(pConfiguradorAusencia);

        if (BasicFunctions.isEmpty(configuradorAusencia)) {

            configuradorAusencia = new ConfiguradorAusencia();

            if (!responses.hasMessages()) {
                responses.setMessages(new ArrayList<>());

                configuradorAusencia = new ConfiguradorAusencia(pConfiguradorAusencia, profissionaisAusentes, context);

                configuradorAusencia.persist();

                responses.setStatus(201);
                responses.setData(configuradorAusencia);
                responses.getMessages().add("Configurador de Ausência cadastrado com sucesso!");

            } else {
                return Response.ok(responses).status(responses.getStatus()).build();
            }
            return Response.ok(responses).status(Response.Status.CREATED).build();
        } else {

            responses.setStatus(400);
            responses.setData(configuradorAusencia);
            responses.getMessages().add("Configurador de Ausência já existente!");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    public Response updateConfiguradorAusencia(@NotNull ConfiguradorAusencia pConfiguradorAusencia) {

        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        try {

                loadConfiguradorAusenciaById(pConfiguradorAusencia);

                if (BasicFunctions.isNotEmpty(configuradorAusencia)) {

                    loadByConfiguradorAusencia(pConfiguradorAusencia);

                    if (!responses.hasMessages()) {
                        responses.setMessages(new ArrayList<>());

                        configuradorAusencia = configuradorAusencia.configuradorAusencia(configuradorAusencia, pConfiguradorAusencia, profissionaisAusentes, context);

                        configuradorAusencia.persistAndFlush();

                        responses.setStatus(201);
                        responses.setData(configuradorAusencia);
                        responses.getMessages().add("Configurador de Ausência atualizado com sucesso!");
                    }
                } else {

                    responses.setStatus(400);
                    responses.setData(configuradorAusencia);
                    responses.getMessages().add("Não foi possível localizar o Configurador de Ausência.");
                    return Response.ok(responses).status(responses.getStatus()).build();
                }
        } catch (Exception e) {

            responses.setStatus(400);
            responses.setData(configuradorAusencia);
            responses.getMessages().add("Não foi possível atualizar o Configurador de Ausência.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
        return Response.ok(responses).status(Response.Status.CREATED).build();
    }

    public Response deleteConfiguradorAusencia(@NotNull List<Long> pListIdConfiguradorAusencia) {

        List<ConfiguradorAusencia> configuradorAusencias;
        List<ConfiguradorAusencia> configuradorAusenciasAux = new ArrayList<>();
        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        configuradorAusencias = ConfiguradorAusencia.list("id in ?1", pListIdConfiguradorAusencia);
        int count = configuradorAusencias.size();

        try {

            if (configuradorAusencias.isEmpty()) {

                responses.setStatus(400);
                responses.getMessages().add("Configuradores de Ausência não localizados ou já excluídos.");
                return Response.ok(responses).status(responses.getStatus()).build();
            }

            configuradorAusencias.forEach((configAusencia) -> {
                configuradorAusenciasAux.add(configAusencia);
                responses.setData(configAusencia);
                configAusencia.delete();

            });

            responses.setStatus(200);
            if (count <= 1) {
                responses.setData(configuradorAusencia);
                responses.getMessages().add("Configurador de Ausência excluído com sucesso!");
            } else {
                responses.setDatas(Collections.singletonList(configuradorAusenciasAux));
                responses.getMessages().add(count + " Configuradores de Ausência excluídos com sucesso!");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            if (count <= 1) {
                responses.setData(configuradorAusencia);
                responses.getMessages().add("Configurador de Ausência não localizado ou já excluído.");
            } else {
                responses.setDatas(Collections.singletonList(configuradorAusencias));
                responses.getMessages().add("Configuradores de Ausência não localizados ou já excluídos.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    private void loadByConfiguradorAusencia(ConfiguradorAusencia pConfiguradorAusencia) {

        profissionaisAusentes = new ArrayList<>();
        profissionaisAusentesId = new ArrayList<>();

        if (BasicFunctions.isNotEmpty(pConfiguradorAusencia.getProfissionaisAusentes())) {
            pConfiguradorAusencia.getProfissionaisAusentes()
                    .forEach(profissional -> profissionaisAusentesId.add(profissional.getId()));
        }

        profissionaisAusentes = Usuario.list("id in ?1", profissionaisAusentesId);

        validarConfiguradorAusencia(pConfiguradorAusencia);
    }

    private void loadConfiguradorAusenciaById(ConfiguradorAusencia pConfiguradorAusencia) {

        configuradorAusencia = new ConfiguradorAusencia();

        if (BasicFunctions.isNotEmpty(pConfiguradorAusencia)) {
            configuradorAusencia = ConfiguradorAusencia.find("id = ?1",
                    pConfiguradorAusencia.getDataInicioAusencia(), pConfiguradorAusencia.getId()).firstResult();
        }
    }

    private void loadByConfiguradorAusenciaByDataInicioFim(ConfiguradorAusencia pConfiguradorAusencia) {

        configuradorAusencia = new ConfiguradorAusencia();

        if (BasicFunctions.isNotEmpty(pConfiguradorAusencia)) {
            configuradorAusencia = ConfiguradorAusencia.find("dataInicioAusencia = ?1 and dataFimAusencia = ?2",
                    pConfiguradorAusencia.getDataInicioAusencia(), pConfiguradorAusencia.getDataFimAusencia()).firstResult();
        }
    }

    private void validarConfiguradorAusencia(ConfiguradorAusencia pConfiguradorAusencia) {

        if (BasicFunctions.isEmpty(pConfiguradorAusencia)) {
            responses.setStatus(400);
            responses.getMessages().add("Informe os dados para atualizar o Configurador de Ausência.");
        }
    }
}
