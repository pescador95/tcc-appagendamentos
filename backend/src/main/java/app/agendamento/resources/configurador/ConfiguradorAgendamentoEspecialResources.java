package app.agendamento.resources.configurador;

import app.agendamento.controller.configurador.ConfiguradorAgendamentoEspecialController;
import app.agendamento.model.configurador.ConfiguradorAgendamentoEspecial;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static app.agendamento.filters.configurador.ConfiguradorAgendamentoEspecialFilters.makeConfiguradorAgendamentoEspecialQueryStringByFilters;

@SuppressWarnings("RestParamTypeInspection")
@Path("/configuradorAgendamentoEspecial")
public class ConfiguradorAgendamentoEspecialResources {

    @Inject
    ConfiguradorAgendamentoEspecialController controller;
    ConfiguradorAgendamentoEspecial configuradorAgendamentoEspecial;

    Responses responses;
    private Usuario usuarioAuth;
    private String query;

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})

    public Response getById(@PathParam("id") Long pId) {
        configuradorAgendamentoEspecial = ConfiguradorAgendamentoEspecial.findById(pId);
        return Response.ok(configuradorAgendamentoEspecial).status(200).build();
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response count(@QueryParam("ativo") @DefaultValue("true") Boolean ativo) {
        query = "id > 0";
        long count = ConfiguradorAgendamentoEspecial.count(query);
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
            @QueryParam("dataInicio") LocalDate dataInicio,
            @QueryParam("dataFim") LocalDate dataFim,
            @QueryParam("organizacaoId") Long organizacaoId,
            @QueryParam("tipoAgendamentoId") Long tipoAgendamentoId,
            @QueryParam("sort") @DefaultValue("desc") @NotNull String sortQuery,
            @QueryParam("page") @DefaultValue("0") int pageIndex,
            @QueryParam("size") @DefaultValue("20") int pageSize,
            @QueryParam("ativo") @DefaultValue("true") Boolean ativo,
            @QueryParam("strgOrder") @DefaultValue("id") String strgOrder, @Context @NotNull SecurityContext context) {
        usuarioAuth = Contexto.getContextUser(context);
        String queryString = makeConfiguradorAgendamentoEspecialQueryStringByFilters(nome, profissionalId, dataInicio,
                dataFim, organizacaoId);
        query = "id > 0 " + " " + queryString + " order by " + strgOrder + " " + sortQuery;
        PanacheQuery<ConfiguradorAgendamentoEspecial> configuradorAgendamentoEspecial;
        configuradorAgendamentoEspecial = ConfiguradorAgendamentoEspecial.find(query);
        List<ConfiguradorAgendamentoEspecial> configuradorAgendamentoFiltrados = configuradorAgendamentoEspecial
                .page(Page.of(pageIndex, pageSize)).list().stream()
                .filter(c -> (usuarioAuth.getOrganizacoes().contains(c.getOrganizacaoConfigurador())
                        && BasicFunctions.isEmpty(tipoAgendamentoId)
                        || c.getTiposAgendamentos().stream().anyMatch(t -> t.getId().equals(tipoAgendamentoId))))
                .collect(Collectors.toList());

        return Response.ok(configuradorAgendamentoFiltrados).status(200).build();
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response add(ConfiguradorAgendamentoEspecial pConfiguradorAgendamentoEspecial,
                        @Context @NotNull SecurityContext context) {
        try {

            return controller.addConfiguradorAgendamentoEspecial(pConfiguradorAgendamentoEspecial);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            responses.setMessages(new ArrayList<>());
            responses.getMessages().add("Não foi possível cadastrar a Configurador de Agendamento Especial.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @PUT
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})

    public Response update(ConfiguradorAgendamentoEspecial pConfiguradorAgendamentoEspecial,
                           @Context @NotNull SecurityContext context) {
        try {
            return controller.updateConfiguradorAgendamentoEspecial(pConfiguradorAgendamentoEspecial);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            responses.getMessages().add("Não foi possível atualizar o Configurador de Agendamento Especial.");
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
            return controller.deleteConfiguradorAgendamentoEspecial(pListIdConfiguradorAgendamento);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            if (pListIdConfiguradorAgendamento.size() <= 1) {
                responses.getMessages().add("Não foi possível excluir o Configurador de Agendamento Especial.");
            } else {
                responses.getMessages().add("Não foi possível excluir os o Configuradores de Agendamentos Especiais.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

}
