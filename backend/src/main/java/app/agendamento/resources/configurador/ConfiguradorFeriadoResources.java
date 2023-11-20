package app.agendamento.resources.configurador;

import app.agendamento.controller.configurador.ConfiguradorFeriadoController;
import app.agendamento.model.configurador.ConfiguradorFeriado;
import app.agendamento.model.organizacao.Organizacao;
import app.agendamento.model.pessoa.Usuario;
import app.core.model.DTO.Responses;
import app.core.utils.BasicFunctions;
import app.core.utils.Contexto;
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

import static app.agendamento.filters.configurador.ConfiguradorFeriadoFilters.makeConfiguradorFeriadoQueryStringByFilters;

@SuppressWarnings("RestParamTypeInspection")
@Path("/configuradorFeriado")
public class ConfiguradorFeriadoResources {

    @Inject
    ConfiguradorFeriadoController controller;
    ConfiguradorFeriado configuradorFeriado;

    Responses responses;
    Usuario usuarioAuth;
    private String query;

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})

    public Response getById(@PathParam("id") Long pId) {
        configuradorFeriado = ConfiguradorFeriado.findById(pId);
        return Response.ok(configuradorFeriado).status(200).build();
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response count() {
        query = "id > 0";
        long count = ConfiguradorFeriado.count(query);
        return Response.ok(count).status(200).build();
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response list(
            @QueryParam("nomeFeriado") String nomeFeriado,
            @QueryParam("dataFeriado") LocalDate dataFeriado,
            @QueryParam("dataInicio") LocalDate dataInicio,
            @QueryParam("dataFim") LocalDate dataFim,
            @QueryParam("horaInicio") LocalTime horaInicio,
            @QueryParam("horaFim") LocalTime horaFim,
            @QueryParam("organizacaoId") Long organizacaoId,
            @QueryParam("observacao") String observacao,
            @QueryParam("sort") @DefaultValue("desc") @NotNull String sortQuery,
            @QueryParam("page") @DefaultValue("0") int pageIndex,
            @QueryParam("size") @DefaultValue("20") int pageSize,
            @QueryParam("ativo") @DefaultValue("true") Boolean ativo,
            @QueryParam("strgOrder") @DefaultValue("id") String strgOrder, @Context @NotNull SecurityContext context) {

        usuarioAuth = Contexto.getContextUser(context);
        String queryString = makeConfiguradorFeriadoQueryStringByFilters(nomeFeriado, dataFeriado, dataInicio, dataFim,
                horaInicio, horaFim, organizacaoId, observacao);
        query = "id > 0 " + " " + queryString + " order by " + strgOrder + " " + sortQuery;
        PanacheQuery<ConfiguradorFeriado> configuradorFeriado;
        configuradorFeriado = ConfiguradorFeriado.find(query);

        List<ConfiguradorFeriado> configuradorFeriadoFiltrados = configuradorFeriado.page(Page.of(pageIndex, pageSize))
                .list()
                .stream()
                .filter(x -> BasicFunctions.isEmpty(organizacaoId) || x.getOrganizacoesFeriado().stream()
                        .map(Organizacao::getId)
                        .anyMatch(id -> id.equals(organizacaoId)))
                .collect(Collectors.toList());

        return Response.ok(configuradorFeriadoFiltrados).status(200).build();
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response add(ConfiguradorFeriado pConfiguradorFeriado, @Context @NotNull SecurityContext context) {
        try {

            return controller.addConfiguradorFeriado(pConfiguradorFeriado);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            responses.getMessages().add("Não foi possível cadastrar a Configurador de Feriado.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @PUT
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})

    public Response update(ConfiguradorFeriado pConfiguradorFeriado, @Context @NotNull SecurityContext context) {
        try {

            return controller.updateConfiguradorFeriado(pConfiguradorFeriado);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            responses.getMessages().add("Não foi possível atualizar o Configurador de Feriado.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @DELETE
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response deleteList(List<Long> pListIdConfiguradorFeriado, @Context @NotNull SecurityContext context) {
        try {
            return controller.deleteConfiguradorFeriado(pListIdConfiguradorFeriado);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            if (pListIdConfiguradorFeriado.size() <= 1) {
                responses.getMessages().add("Não foi possível excluir o Configurador de Feriado.");
            } else {
                responses.getMessages().add("Não foi possível excluir os o Configuradores de Feriados.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

}
