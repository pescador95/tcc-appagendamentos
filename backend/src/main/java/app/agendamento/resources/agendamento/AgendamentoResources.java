package app.agendamento.resources.agendamento;

import app.agendamento.DTO.agendamento.AgendamentoDTO;
import app.agendamento.controller.agendamento.AgendamentoController;
import app.agendamento.model.agendamento.Agendamento;
import app.agendamento.model.pessoa.Usuario;
import app.core.model.DTO.Responses;
import app.core.utils.Contexto;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import org.jetbrains.annotations.NotNull;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static app.agendamento.filters.agendamento.AgendamentoFilters.makeAgendamentoQueryStringByFilters;

@SuppressWarnings("RestParamTypeInspection")
@Path("/agendamento")
public class AgendamentoResources {

    @Inject
    AgendamentoController controller;

    Agendamento agendamento;

    Responses responses;
    private Usuario usuarioAuth;

    private String query;

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})

    public Response getById(@PathParam("id") Long pId) {
        agendamento = Agendamento.findById(pId);
        return Response.ok(agendamento).status(200).build();
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response count(@QueryParam("ativo") @DefaultValue("true") Boolean ativo) {
        query = "ativo = " + ativo;
        long count = Agendamento.count(query);
        return Response.ok(count).status(200).build();
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response list(
            @QueryParam("dataAgendamento") LocalDate dataAgendamento,
            @QueryParam("dataInicio") LocalDate dataInicio,
            @QueryParam("dataFim") LocalDate dataFim,
            @QueryParam("horarioAgendamento") LocalTime horarioAgendamento,
            @QueryParam("horarioInicio") LocalTime horarioInicio,
            @QueryParam("HorarioFim") LocalTime horarioFim,
            @QueryParam("pessoaId") Long pessoaId,
            @QueryParam("nomePessoa") String nomePessoa,
            @QueryParam("nomeProfissional") String nomeProfissional,
            @QueryParam("idStatus") @DefaultValue("1") Long idStatus,
            @QueryParam("organizacaoId") Long organizacaoId,
            @QueryParam("tipoAgendamentoId") Long tipoAgendamentoId,
            @QueryParam("profissionalId") Long profissionalId,
            @QueryParam("sort") @DefaultValue("desc") @NotNull String sortQuery,
            @QueryParam("page") @DefaultValue("0") int pageIndex,
            @QueryParam("size") @DefaultValue("20") int pageSize,
            @QueryParam("ativo") @DefaultValue("true") Boolean ativo,
            @QueryParam("strgOrder") @DefaultValue("id") String strgOrder, @Context @NotNull SecurityContext context) {
        usuarioAuth = Contexto.getContextUser(context);

        String queryString = makeAgendamentoQueryStringByFilters(dataAgendamento, dataInicio, dataFim,
                horarioAgendamento, horarioInicio, horarioFim, pessoaId, nomePessoa, nomeProfissional, idStatus,
                organizacaoId, tipoAgendamentoId, profissionalId);

        query = "ativo = " + ativo + " " + queryString + " order by " + strgOrder + " " + sortQuery;
        PanacheQuery<Agendamento> agendamento;
        agendamento = Agendamento.find(query);
        List<Agendamento> agendamentosFiltrados = agendamento.page(Page.of(pageIndex, pageSize)).list().stream()
                .filter(c -> (usuarioAuth.getOrganizacoes().contains(c.getOrganizacaoAgendamento())))
                .collect(Collectors.toList());

        return Response.ok(agendamentosFiltrados).status(200).build();
    }

    @GET
    @Path("/mobile")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @PermitAll
    public Response listMobile(@QueryParam("sort") @DefaultValue("desc") @NotNull String sortQuery,
                               @QueryParam("page") @DefaultValue("0") int pageIndex,
                               @QueryParam("dataAgendamento") @DefaultValue("1970-01-01") LocalDate dataAgendamento,
                               @QueryParam("horarioAgendamento") @DefaultValue("00:00") LocalTime horarioAgendamento,
                               @QueryParam("size") @DefaultValue("20") int pageSize,
                               @QueryParam("ativo") @DefaultValue("true") Boolean ativo,
                               @QueryParam("strgOrder") @DefaultValue("id") String strgOrder) {
        List<Agendamento> agendamentos = Agendamento.list("id > 0");

        List<AgendamentoDTO> agendamentosDTO = AgendamentoDTO.makeListAgendamentoDTO(agendamentos);

        return Response.ok(agendamentosDTO).status(200).build();
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response add(Agendamento pAgendamento,
                        @QueryParam("reagendar") @DefaultValue("false") Boolean reagendar,
                        @Context @NotNull SecurityContext context) {
        try {
            return controller.addAgendamento(pAgendamento, reagendar);
        } catch (Exception e) {
            responses = new Responses();
            responses.setStatus(400);
            responses.setMessages(new ArrayList<>());
            responses.getMessages().add("Não foi possível cadastrar o Agendamento.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @PUT
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})

    public Response update(Agendamento pAgendamento,
                           @QueryParam("reagendar") @DefaultValue("false") Boolean reagendar) {
        try {
            return controller.updateAgendamento(pAgendamento, reagendar);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            responses.getMessages().add("Não foi possível atualizar o Agendamento.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @DELETE
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response deleteList(List<Long> pListIdAgendamento, @Context @NotNull SecurityContext context) {
        try {

            return controller.deleteAgendamento(pListIdAgendamento);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            if (pListIdAgendamento.size() <= 1) {
                responses.getMessages().add("Não foi possível excluir o Agendamento.");
            } else {
                responses.getMessages().add("Não foi possível excluir os Organizações.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @PUT
    @Path("/reactivate")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response reactivateList(List<Long> pListIdAgendamento, @Context @NotNull SecurityContext context) {
        try {

            return controller.reactivateAgendamento(pListIdAgendamento);
        } catch (Exception e) {
            if (pListIdAgendamento.size() <= 1) {
                responses = new Responses();

                responses.setStatus(400);
                responses.getMessages().add("Não foi possível reativar o Agendamento.");
            } else {
                responses = new Responses();

                responses.setStatus(400);
                responses.getMessages().add("Não foi possível reativar os Organizações.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }
}
