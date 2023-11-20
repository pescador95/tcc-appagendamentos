package app.agendamento.resources.endereco;

import app.agendamento.controller.endereco.EnderecoController;
import app.agendamento.model.endereco.Endereco;
import app.core.model.DTO.Responses;
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
import java.util.List;
import java.util.stream.Collectors;

import static app.agendamento.filters.endereco.EnderecoFilters.makeEnderecoQueryStringByFilters;

@Path("/endereco")
public class EnderecoResources {

    @Inject
    EnderecoController controller;
    Endereco endereco;
    Responses responses;
    private String query;

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response getById(@PathParam("id") Long pId) {
        endereco = Endereco.findById(pId);
        return Response.ok(endereco).status(200).build();
    }

    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response count(@QueryParam("ativo") @DefaultValue("true") Boolean ativo,
                          @Context @NotNull SecurityContext context) {

        query = "ativo = " + ativo;
        long count = Endereco.count(query);
        return Response.ok(count).status(200).build();
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response list(
            @QueryParam("cep") String cep,
            @QueryParam("logradouro") String logradouro,
            @QueryParam("numero") Long numero,
            @QueryParam("complemento") String complemento,
            @QueryParam("cidade") String cidade,
            @QueryParam("estado") String estado,
            @QueryParam("sort") @DefaultValue("desc") @NotNull String sortQuery,
            @QueryParam("page") @DefaultValue("0") int pageIndex,
            @QueryParam("size") @DefaultValue("20") int pageSize,
            @QueryParam("ativo") @DefaultValue("true") Boolean ativo,
            @QueryParam("strgOrder") @DefaultValue("id") String strgOrder, @Context @NotNull SecurityContext context) {
        String queryString = makeEnderecoQueryStringByFilters(cep, logradouro, numero, complemento, cidade, estado);
        query = "ativo = " + ativo + " " + queryString + " order by " + strgOrder + " " + sortQuery;
        PanacheQuery<Endereco> endereco;
        endereco = Endereco.find(query);
        return Response.ok(endereco.page(Page.of(pageIndex, pageSize)).list().stream().filter(c -> (c.getAtivo().equals(ativo)))
                .collect(Collectors.toList())).status(responses.getStatus()).build();

    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response add(Endereco pEndereco, @Context @NotNull SecurityContext context) {
        try {
            return controller.addEndereco(pEndereco);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            responses.getMessages().add("Não foi possível cadastrar a Pessoa.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @PUT
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response update(Endereco pEndereco, @Context @NotNull SecurityContext context) {
        try {
            return controller.updateEndereco(pEndereco);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            responses.getMessages().add("Não foi possível atualizar o Endereço.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @DELETE
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response deleteList(List<Long> pListEndereco, @Context @NotNull SecurityContext context) {
        try {
            return controller.deleteEndereco(pListEndereco);
        } catch (Exception e) {
            responses = new Responses();

            responses.setStatus(400);
            if (pListEndereco.size() <= 1) {
                responses.getMessages().add("Não foi possível excluir o Endereço.");
            } else {
                responses.getMessages().add("Não foi possível excluir os Endereços.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    @PUT
    @Path("/reactivate")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("application/json")
    @RolesAllowed({"usuario"})
    public Response reactivateList(List<Long> pListEndereco, @Context @NotNull SecurityContext context) {
        try {

            return controller.reactivateEndereco(pListEndereco);
        } catch (Exception e) {
            if (pListEndereco.size() <= 1) {
                responses = new Responses();

                responses.setStatus(400);
                responses.getMessages().add("Não foi possível reativar o Endereço.");
            } else {
                responses = new Responses();

                responses.setStatus(400);
                responses.getMessages().add("Não foi possível reativar os Endereços.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }
}
