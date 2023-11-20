package app.agendamento.resources.pessoa;

import app.agendamento.controller.pessoa.PessoaController;
import app.agendamento.model.pessoa.Pessoa;
import app.core.model.DTO.Responses;
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
import java.util.List;
import java.util.stream.Collectors;

import static app.agendamento.filters.pessoa.PessoaFilters.makePessoaQueryStringByFilters;

@SuppressWarnings("RestParamTypeInspection")
@Path("/pessoa")
public class PessoaResources {

    @Inject
    PessoaController controller;
    Pessoa pessoa;
    Responses responses;
    private String query;

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario", "bot"})
    public Response getById(@PathParam("id") Long pId) {
        pessoa = Pessoa.findById(pId);
        return Response.ok(pessoa).status(200).build();
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario", "bot"})

    public Response count(@QueryParam("ativo") @DefaultValue("true") Boolean ativo,
                          @Context @NotNull SecurityContext context) {

        query = "ativo = " + ativo;
        long count = Pessoa.count(query);
        return Response.ok(count).status(200).build();
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response list(
            @QueryParam("id") Long id,
            @QueryParam("nome") String nome,
            @QueryParam("generoId") Long generoId,
            @QueryParam("dataNascimento") LocalDate dataNascimento,
            @QueryParam("telefone") String telefone,
            @QueryParam("celular") String celular,
            @QueryParam("email") String email,
            @QueryParam("enderecoId") Long enderecoId,
            @QueryParam("cpf") String cpf,
            @QueryParam("telegramId") Long telegramId,
            @QueryParam("whatsappId") Long whatsappId,
            @QueryParam("sort") @DefaultValue("desc") @NotNull String sortQuery,
            @QueryParam("page") @DefaultValue("0") int pageIndex,
            @QueryParam("size") @DefaultValue("20") int pageSize,
            @QueryParam("ativo") @DefaultValue("true") Boolean ativo,
            @QueryParam("strgOrder") @DefaultValue("id") String strgOrder, @Context @NotNull SecurityContext context) {
        String queryString = makePessoaQueryStringByFilters(id, nome, generoId, dataNascimento, telefone, celular, email, enderecoId, cpf, telegramId, whatsappId);
        query = "ativo = " + ativo + " " + queryString + " order by " + strgOrder + " " + sortQuery;
        PanacheQuery<Pessoa> pessoa;
        pessoa = Pessoa.find(query);
        List<Pessoa> pessoasFiltradas = pessoa.page(Page.of(pageIndex, pageSize)).list().stream()
                .filter(c -> (c.getAtivo().equals(ativo)))
                .collect(Collectors.toList());

        return Response.ok(pessoasFiltradas).status(200).build();

    }

    @GET
    @Path("/cpf")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @PermitAll
    public Response listByCPF(@QueryParam("sort") @DefaultValue("desc") @NotNull String sortQuery,
                              @QueryParam("page") @DefaultValue("0") int pageIndex,
                              @QueryParam("size") @DefaultValue("20") int pageSize,
                              @QueryParam("ativo") @DefaultValue("true") Boolean ativo,
                              @QueryParam("cpf") String cpf,
                              @QueryParam("strgOrder") @DefaultValue("id") String strgOrder, @Context @NotNull SecurityContext context) {
        query = "ativo = " + ativo + " and cpf = '" + cpf + "'";

        PanacheQuery<Pessoa> pessoa;
        pessoa = Pessoa.find(query);
        List<Pessoa> pessoasFiltradas = pessoa.page(Page.of(pageIndex, pageSize)).list().stream()
                .filter(c -> (c.getAtivo().equals(ativo)))
                .collect(Collectors.toList());

        return Response.ok(pessoasFiltradas).status(200).build();
    }

    @GET
    @Path("/phone")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @PermitAll
    public Response listByPhone(@QueryParam("sort") @DefaultValue("desc") @NotNull String sortQuery,
                                @QueryParam("page") @DefaultValue("0") int pageIndex,
                                @QueryParam("size") @DefaultValue("20") int pageSize,
                                @QueryParam("ativo") @DefaultValue("true") Boolean ativo,
                                @QueryParam("telefone") String telefone,
                                @QueryParam("strgOrder") @DefaultValue("id") String strgOrder, @Context @NotNull SecurityContext context) {
        query = "ativo = " + ativo + " and telefone = '" + telefone + "' or celular = '" + telefone + "'";

        PanacheQuery<Pessoa> pessoa;
        pessoa = Pessoa.find(query);
        List<Pessoa> pessoasFiltradas = pessoa.page(Page.of(pageIndex, pageSize)).list().stream()
                .filter(c -> (c.getAtivo().equals(ativo)))
                .collect(Collectors.toList());

        return Response.ok(pessoasFiltradas).status(200).build();
    }

    @GET
    @Path("/ident")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @PermitAll
    public Response listByIdent(@QueryParam("sort") @DefaultValue("desc") @NotNull String sortQuery,
                                @QueryParam("page") @DefaultValue("0") int pageIndex,
                                @QueryParam("size") @DefaultValue("20") int pageSize,
                                @QueryParam("ativo") @DefaultValue("true") Boolean ativo,
                                @QueryParam("ident") String ident,
                                @QueryParam("strgOrder") @DefaultValue("id") String strgOrder, @Context @NotNull SecurityContext context) {
        query = "ativo = " + ativo + " and telefone = '" + ident + "' or celular = '" + ident + "'" + " or cpf = '"
                + ident + "'";

        PanacheQuery<Pessoa> pessoa;
        pessoa = Pessoa.find(query);
        List<Pessoa> pessoasFiltradas = pessoa.page(Page.of(pageIndex, pageSize)).list().stream()
                .filter(c -> (c.getAtivo().equals(ativo)))
                .collect(Collectors.toList());

        return Response.ok(pessoasFiltradas).status(200).build();
    }

    @GET
    @Path("/telegram")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @PermitAll
    public Response getByTelegram(@QueryParam("telegramId") Long telegramId,
                                  @QueryParam("ativo") @DefaultValue("true") Boolean ativo) {
        pessoa = Pessoa.find("telegramId = ?1 and ativo = ?2", telegramId, ativo).firstResult();
        return Response.ok(pessoa).status(200).build();
    }

    @GET
    @Path("/whatsapp")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @PermitAll
    public Response getByWhatsapp(@QueryParam("whatsappId") Long whatsappId,
                                  @QueryParam("ativo") @DefaultValue("true") Boolean ativo) {
        pessoa = Pessoa.find("whatsappId = ?1 and ativo = ?2", whatsappId, ativo).firstResult();
        return Response.ok(pessoa).status(200).build();
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response add(Pessoa pPessoa, @Context @NotNull SecurityContext context) {
        try {
            return controller.addPessoa(pPessoa);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            responses.getMessages().add("Não foi possível cadastrar a Pessoa.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @POST
    @Path("/bot/create")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @PermitAll
    public Response addByBot(Pessoa pPessoa, @Context @NotNull SecurityContext context) {
        try {
            return controller.addPessoa(pPessoa);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            responses.getMessages().add("Não foi possível cadastrar a Pessoa.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @POST
    @Path("/telegram")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @PermitAll
    public Response updateByTelegram(@QueryParam("telegramId") Long telegramId,
                                     @QueryParam("ativo") @DefaultValue("true") Boolean ativo, Pessoa pPessoa) {

        return controller.addTelegramIdPessoa(pPessoa, telegramId, ativo);
    }

    @POST
    @Path("/whatsapp")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @PermitAll
    public Response updateByWhatsapp(@QueryParam("whatsappId") Long whatsappId,
                                     @QueryParam("ativo") @DefaultValue("true") Boolean ativo, Pessoa pPessoa) {

        return controller.addWhatsappIdPessoa(pPessoa, whatsappId, ativo);
    }


    @DELETE
    @Path("/telegram")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @PermitAll
    public Response removeByTelegram(@QueryParam("telegramId") Long telegramId,
                                     @QueryParam("ativo") @DefaultValue("true") Boolean ativo) {

        return controller.removeTelegramIdPessoa(telegramId, ativo);
    }

    @DELETE
    @Path("/whatsapp")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @PermitAll
    public Response removeByWhatsapp(@QueryParam("whatsappId") Long whatsappId,
                                     @QueryParam("ativo") @DefaultValue("true") Boolean ativo) {

        return controller.removeWhatsappIdPessoa(whatsappId, ativo);
    }

    @PUT
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response update(Pessoa pPessoa, @Context SecurityContext context) {
        try {

            return controller.updatePessoa(pPessoa);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            responses.getMessages().add("Não foi possível atualizar a Pessoa.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @DELETE
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response deleteList(List<Long> pListPessoa, @Context @NotNull SecurityContext context) {
        try {

            return controller.deletePessoa(pListPessoa);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            if (pListPessoa.size() <= 1) {
                responses.getMessages().add("Não foi possível excluir a Pessoa.");
            } else {
                responses.getMessages().add("Não foi possível excluir as Pessoas.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @PUT
    @Path("/reactivate")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response reactivateList(List<Long> pListPessoa, @Context @NotNull SecurityContext context) {
        try {

            return controller.reactivatePessoa(pListPessoa);
        } catch (Exception e) {
            if (pListPessoa.size() <= 1) {
                responses = new Responses();

                responses.setStatus(400);
                responses.getMessages().add("Não foi possível reativar a Pessoa.");
            } else {
                responses = new Responses();

                responses.setStatus(400);
                responses.getMessages().add("Não foi possível reativar as Pessoas.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }
}
