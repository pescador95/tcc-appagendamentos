package app.agendamento.resources.configurador;

import app.agendamento.controller.configurador.ConfiguradorAusenciaController;
import app.agendamento.model.configurador.ConfiguradorAusencia;
import app.agendamento.model.pessoa.Usuario;
import app.core.model.DTO.Responses;
import app.core.utils.BasicFunctions;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import org.jetbrains.annotations.NotNull;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static app.agendamento.filters.configurador.ConfiguradorAusenciaFilters.makeConfiguradorAusenciaQueryStringByFilters;

@SuppressWarnings("RestParamTypeInspection")
@Path("/configuradorAusencia")
public class ConfiguradorAusenciaResources {

    @Inject
    ConfiguradorAusenciaController controller;
    ConfiguradorAusencia configuradorAusencia;

    Responses responses;

    private String query;

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})

    public Response getById(@PathParam("id") Long pId) {
        configuradorAusencia = ConfiguradorAusencia.findById(pId);
        return Response.ok(configuradorAusencia).status(200).build();
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response count() {
        query = "id > 0";
        long count = ConfiguradorAusencia.count(query);
        return Response.ok(count).status(200).build();
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response list(
            @QueryParam("nomeAusencia") String nomeAusencia,
            @QueryParam("dataInicio") LocalDate dataInicio,
            @QueryParam("dataFim") LocalDate dataFim,
            @QueryParam("horaInicio") LocalTime horaInicio,
            @QueryParam("horaFim") LocalTime horaFim,
            @QueryParam("usuarioId") Long usuarioId,
            @QueryParam("observacao") String observacao,
            @QueryParam("sort") @DefaultValue("desc") @NotNull String sortQuery,
            @QueryParam("page") @DefaultValue("0") int pageIndex,
            @QueryParam("size") @DefaultValue("20") int pageSize,
            @QueryParam("ativo") @DefaultValue("true") Boolean ativo,
            @QueryParam("strgOrder") @DefaultValue("id") String strgOrder, @Context @NotNull SecurityContext context) {

        String queryString = makeConfiguradorAusenciaQueryStringByFilters(nomeAusencia, dataInicio, dataFim, horaInicio,
                horaFim, observacao);
        query = "id > 0 " + " " + queryString + " order by " + strgOrder + " " + sortQuery;
        PanacheQuery<ConfiguradorAusencia> configuradorAusencia;
        configuradorAusencia = ConfiguradorAusencia.find(query);

        List<ConfiguradorAusencia> configuradorAusenciaFiltrados = configuradorAusencia
                .page(Page.of(pageIndex, pageSize)).list()
                .stream()
                .filter(x -> BasicFunctions.isEmpty(usuarioId) || x.getProfissionaisAusentes().stream()
                        .map(Usuario::getId)
                        .anyMatch(id -> id.equals(usuarioId)))
                .collect(Collectors.toList());

        return Response.ok(configuradorAusenciaFiltrados).status(200).build();
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response add(ConfiguradorAusencia pConfiguradorAusencia, @Context @NotNull SecurityContext context) {
        try {

            return controller.addConfiguradorAusencia(pConfiguradorAusencia);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            responses.getMessages().add("Não foi possível cadastrar a Configurador de Ausencia.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @PUT
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})

    public Response update(ConfiguradorAusencia pConfiguradorAusencia, @Context @NotNull SecurityContext context) {
        try {

            return controller.updateConfiguradorAusencia(pConfiguradorAusencia);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            responses.getMessages().add("Não foi possível atualizar o Configurador de Ausencia.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @DELETE
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response deleteList(List<Long> pListIdConfiguradorAusencia, @Context @NotNull SecurityContext context) {
        try {
            return controller.deleteConfiguradorAusencia(pListIdConfiguradorAusencia);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            if (pListIdConfiguradorAusencia.size() <= 1) {
                responses.getMessages().add("Não foi possível excluir o Configurador de Ausencia.");
            } else {
                responses.getMessages().add("Não foi possível excluir os o Configuradores de Ausencias.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

}
