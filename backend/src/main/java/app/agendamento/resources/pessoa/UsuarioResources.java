package app.agendamento.resources.pessoa;

import app.agendamento.controller.pessoa.UsuarioController;
import app.agendamento.model.agendamento.Agendamento;
import app.agendamento.model.agendamento.TipoAgendamento;
import app.agendamento.model.organizacao.Organizacao;
import app.agendamento.model.pessoa.Usuario;
import app.agendamento.queries.usuario.UsuarioQueries;
import app.core.model.DTO.Responses;
import app.core.model.auth.Role;
import app.core.utils.BasicFunctions;
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
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static app.agendamento.filters.pessoa.UsuarioFilters.makeUsuarioQueryStringByFilters;

@SuppressWarnings("RestParamTypeInspection")
@Path("/usuario")
public class UsuarioResources {

    @Inject
    UsuarioController controller;
    Responses responses;
    Usuario usuarioAuth;
    @Inject
    UsuarioQueries usuarioQueries;
    private String query;

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response getById(@PathParam("id") Long pId) {
        Usuario usuario = Usuario.findById(pId);
        return Response.ok(usuario).status(200).build();
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response count(@QueryParam("ativo") @DefaultValue("true") Boolean ativo) {
        query = "ativo = " + ativo;
        long count = Usuario.count(query);
        return Response.ok(count).status(200).build();
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response list(
            @QueryParam("id") Long id,
            @QueryParam("login") String login,
            @QueryParam("pessoaId") Long pessoaId,
            @QueryParam("organizacaoDefaultId") Long organizacaoDefaultId,
            @QueryParam("roleId") List<Long> roleId,
            @QueryParam("nomeprofissional") String nomeprofissional,
            @QueryParam("usuario") String usuario,
            @QueryParam("organizacaoId") List<Long> organizacaoId,
            @QueryParam("tipoAgendamentoId") List<Long> tipoAgendamentoId,
            @QueryParam("bot") Boolean bot,
            @QueryParam("sort") @DefaultValue("desc") @NotNull String sortQuery,
            @QueryParam("page") @DefaultValue("0") int pageIndex,
            @QueryParam("size") @DefaultValue("20") int pageSize,
            @QueryParam("ativo") @DefaultValue("true") Boolean ativo,
            @QueryParam("strgOrder") @DefaultValue("id") String strgOrder, @Context @NotNull SecurityContext context) {

        usuarioAuth = Contexto.getContextUser(context);

        String queryString = makeUsuarioQueryStringByFilters(id, login, pessoaId, organizacaoDefaultId,
                nomeprofissional, usuario, bot);

        query = "ativo = " + ativo + " " + queryString + " " + "order by " + strgOrder + " " + sortQuery;
        PanacheQuery<Usuario> usuarios;
        usuarios = Usuario.find(query);

        List<Usuario> usuariosFiltrados = usuarios.page(Page.of(pageIndex, pageSize)).list()
                .stream()
                .filter(x -> (BasicFunctions.isEmpty(organizacaoId)
                        || new HashSet<>(x.getOrganizacoes().stream().map(Organizacao::getId).collect(Collectors.toList()))
                        .containsAll(organizacaoId))
                        && (BasicFunctions.isEmpty(tipoAgendamentoId) || new HashSet<>(
                        x.getTiposAgendamentos().stream().map(TipoAgendamento::getId).collect(Collectors.toList()))
                        .containsAll(tipoAgendamentoId))
                        && (BasicFunctions.isEmpty(roleId) || new HashSet<>(
                        x.getPrivilegio().stream().map(Role::getId).collect(Collectors.toList()))
                        .containsAll(roleId)))
                .collect(Collectors.toList());

        return Response.ok(usuariosFiltrados).status(200).build();
    }

    @GET
    @Path("/bot")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @PermitAll
    public Response listForScheduler(
            @QueryParam("dataAgendamento") @DefaultValue("1970-01-01") LocalDate dataAgendamento,
            @QueryParam("organizacao") @DefaultValue("1") Long organizaao,
            @QueryParam("tipoAgendamento") @DefaultValue("1") Long tipoAgendamento,
            @QueryParam("profissional") @DefaultValue("1") Long profissional,
            @QueryParam("comPreferencia") @DefaultValue("true") boolean comPreferencia,
            @QueryParam("sort") @DefaultValue("desc") @NotNull String sortQuery,
            @QueryParam("page") @DefaultValue("0") int pageIndex,
            @QueryParam("size") @DefaultValue("20") int pageSize,
            @QueryParam("ativo") @DefaultValue("true") Boolean ativo,
            @QueryParam("strgOrder") @DefaultValue("id") String strgOrder,
            @QueryParam("roles") @NotNull List<Long> roles,
            @Context @NotNull SecurityContext context) {

        usuarioAuth = Contexto.getContextUser(context);

        List<Usuario> usuarioFiltrados;
        Agendamento agendamento = new Agendamento();
        agendamento.setTipoAgendamento(new TipoAgendamento());
        agendamento.setProfissionalAgendamento(new Usuario());
        agendamento.setOrganizacaoAgendamento(new Organizacao());

        agendamento.setDataAgendamento(dataAgendamento);
        agendamento.getTipoAgendamento().setId(tipoAgendamento);
        agendamento.getProfissionalAgendamento().setId(profissional);
        agendamento.getOrganizacaoAgendamento().setId(organizaao);
        agendamento.setComPreferencia(comPreferencia);

        usuarioFiltrados = usuarioQueries.loadListUsuariosByOrganizacaoAndDataAgendamento(agendamento);

        return Response.ok(usuarioFiltrados).status(200).build();
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response add(Usuario pUsuario, @Context @NotNull SecurityContext context) {
        try {

            return controller.addUser(pUsuario);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            responses.getMessages().add("Não foi possível cadastrar o Usuário.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @PUT
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response update(Usuario pUsuario, @Context @NotNull SecurityContext context) {
        try {

            return controller.updateUser(pUsuario);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            responses.getMessages().add("Não foi possível atualizar o Usuário.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @DELETE
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response deleteList(List<Long> pListIdUsuario, @Context @NotNull SecurityContext context) {
        try {

            return controller.deleteUser(pListIdUsuario);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            if (pListIdUsuario.size() <= 1) {
                responses.getMessages().add("Não foi possível excluir o Usuário.");
            } else {
                responses.getMessages().add("Não foi possível excluir os Usuários.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @PUT
    @Path("/reactivate")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response reactivateList(List<Long> pListIdUsuario, @Context @NotNull SecurityContext context) {
        try {

            return controller.reactivateUser(pListIdUsuario);
        } catch (Exception e) {
            if (pListIdUsuario.size() <= 1) {
                responses = new Responses();

                responses.setStatus(400);
                responses.getMessages().add("Não foi possível reativar o Usuário.");
            } else {
                responses = new Responses();

                responses.setStatus(400);
                responses.getMessages().add("Não foi possível reativar os Usuários.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }
}
