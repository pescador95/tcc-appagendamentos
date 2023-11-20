package app.agendamento.resources.configurador;

import app.agendamento.controller.configurador.ConfiguradorAgendamentoController;
import app.agendamento.model.configurador.ConfiguradorAgendamento;
import app.agendamento.model.pessoa.Usuario;
import app.core.model.DTO.Responses;
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
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static app.agendamento.filters.configurador.ConfiguradorAgendamentoFilters.makeConfiguradorAgendamentoQueryStringByFilters;

@SuppressWarnings("RestParamTypeInspection")
@Path("/configuradorAgendamento")
public class ConfiguradorAgendamentoResources {

    @Inject
    ConfiguradorAgendamentoController controller;
    ConfiguradorAgendamento configuradorAgendamento;

    Responses responses;
    private Usuario usuarioAuth;

    private String query;

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})

    public Response getById(@PathParam("id") Long pId) {
        configuradorAgendamento = ConfiguradorAgendamento.findById(pId);
        return Response.ok(configuradorAgendamento).status(200).build();
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response count(@QueryParam("ativo") @DefaultValue("true") Boolean ativo) {
        query = "id > 0";
        long count = ConfiguradorAgendamento.count(query);
        return Response.ok(count).status(200).build();
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response list(
            @QueryParam("nome") String nome,
            @QueryParam("profissionalId") Long profissionalId,
            @QueryParam("organizacaoId") Long organizacaoId,
            @QueryParam("configuradorOrganizacao") Boolean configuradorOrganizacao,
            @QueryParam("horarioInicioManha") LocalTime horarioInicioManha,
            @QueryParam("horarioFimManha") LocalTime horarioFimManha,
            @QueryParam("horarioInicioTarde") LocalTime horarioInicioTarde,
            @QueryParam("horarioFimTarde") LocalTime horarioFimTarde,
            @QueryParam("horarioInicioNoite") LocalTime horarioInicioNoite,
            @QueryParam("horarioFimNoite") LocalTime horarioFimNoite,
            @QueryParam("horaMinutoIntervalo") LocalTime horaMinutoIntervalo,
            @QueryParam("horaMinutoTolerancia") LocalTime horaMinutoTolerancia,
            @QueryParam("agendaManha") Boolean agendaManha,
            @QueryParam("agendaTarde") Boolean agendaTarde,
            @QueryParam("agendaNoite") Boolean agendaNoite,
            @QueryParam("atendeSabado") Boolean atendeSabado,
            @QueryParam("atendeDomingo") Boolean atendeDomingo,
            @QueryParam("agendaSabadoManha") Boolean agendaSabadoManha,
            @QueryParam("agendaSabadoTarde") Boolean agendaSabadoTarde,
            @QueryParam("agendaSabadoNoite") Boolean agendaSabadoNoite,
            @QueryParam("agendaDomingoManha") Boolean agendaDomingoManha,
            @QueryParam("agendaDomingoTarde") Boolean agendaDomingoTarde,
            @QueryParam("agendaDomingoNoite") Boolean agendaDomingoNoite,
            @QueryParam("sort") @DefaultValue("desc") @NotNull String sortQuery,
            @QueryParam("page") @DefaultValue("0") int pageIndex,
            @QueryParam("size") @DefaultValue("20") int pageSize,
            @QueryParam("ativo") @DefaultValue("true") Boolean ativo,
            @QueryParam("strgOrder") @DefaultValue("id") String strgOrder, @Context @NotNull SecurityContext context) {

        usuarioAuth = Contexto.getContextUser(context);
        String queryString = makeConfiguradorAgendamentoQueryStringByFilters(nome, profissionalId, organizacaoId,
                configuradorOrganizacao, horarioInicioManha, horarioFimManha, horarioInicioTarde, horarioFimTarde,
                horarioInicioNoite, horarioFimNoite, horaMinutoIntervalo, horaMinutoTolerancia, agendaManha,
                agendaTarde, agendaNoite, atendeSabado, atendeDomingo, agendaSabadoManha, agendaSabadoTarde,
                agendaSabadoNoite, agendaDomingoManha, agendaDomingoTarde, agendaDomingoNoite);
        query = "id > 0 " + " " + queryString + " order by " + strgOrder + " " + sortQuery;
        PanacheQuery<ConfiguradorAgendamento> configuradorAgendamento;
        configuradorAgendamento = ConfiguradorAgendamento.find(query);
        List<ConfiguradorAgendamento> configuradorAgendamentoFiltrados = configuradorAgendamento
                .page(Page.of(pageIndex, pageSize)).list().stream()
                .filter(c -> (usuarioAuth.getOrganizacoes().contains(c.getOrganizacaoConfigurador())))
                .collect(Collectors.toList());

        return Response.ok(configuradorAgendamentoFiltrados).status(200).build();
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response add(ConfiguradorAgendamento pConfiguradorAgendamento, @Context @NotNull SecurityContext context) {
        try {

            return controller.addConfiguradorAgendamento(pConfiguradorAgendamento);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            responses.getMessages().add("Não foi possível cadastrar a Configurador de Agendamento.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @PUT
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})

    public Response update(ConfiguradorAgendamento pConfiguradorAgendamento,
                           @Context @NotNull SecurityContext context) {
        try {

            return controller.updateConfiguradorAgendamento(pConfiguradorAgendamento);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            responses.getMessages().add("Não foi possível atualizar o Configurador de Agendamento.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @DELETE
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response deleteList(List<Long> pListIdConfiguradorAgendamento, @Context @NotNull SecurityContext context) {
        try {
            return controller.deleteConfiguradorAgendamento(pListIdConfiguradorAgendamento);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            if (pListIdConfiguradorAgendamento.size() <= 1) {
                responses.getMessages().add("Não foi possível excluir o Configurador de Agendamento.");
            } else {
                responses.getMessages().add("Não foi possível excluir os o Configuradores de Agendamentos.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

}
