package app.agendamento.controller.endereco;

import app.agendamento.model.endereco.Endereco;
import app.agendamento.model.organizacao.Organizacao;
import app.agendamento.model.pessoa.Pessoa;
import app.core.model.DTO.Responses;
import app.core.utils.BasicFunctions;
import org.jetbrains.annotations.NotNull;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
@Transactional
public class EnderecoController {

    @Context
    SecurityContext context;
    private Endereco endereco = new Endereco();
    private Responses responses;

    public Response addEndereco(@NotNull Endereco pEndereco) {

        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        loadEnderecoById(pEndereco);

        if (BasicFunctions.isEmpty(endereco)) {

            endereco = new Endereco();

            loadByEndereco(pEndereco);

        }

        if (!responses.hasMessages()) {

            endereco = new Endereco(pEndereco, context);

            endereco.persist();

            responses.setStatus(201);
            responses.setData(endereco);
            responses.getMessages().add("Endereco cadastrado com sucesso!");
            return Response.ok(responses).status(Response.Status.CREATED).build();
        }
        return Response.ok(responses).status(responses.getStatus()).build();
    }

    public Response updateEndereco(@NotNull Endereco pEndereco) throws BadRequestException {

        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        try {

            loadEnderecoById(pEndereco);

            if (BasicFunctions.isNotEmpty(endereco)) {
                loadByEndereco(pEndereco);
            }
            if (!responses.hasMessages()) {
                endereco = endereco.endereco(endereco, pEndereco, context);

                endereco.persistAndFlush();

                responses.setStatus(200);
                responses.setData(endereco);
                responses.getMessages().add("Endereço atualizado com sucesso!");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            responses.setData(endereco);
            responses.getMessages().add("Não foi possível atualizar o Endereço.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    public Response deleteEndereco(@NotNull List<Long> pListEndereco) {

        List<Endereco> enderecos;
        List<Endereco> enderecosAux = new ArrayList<>();
        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        enderecos = Endereco.list("id in ?1 and ativo = true", pListEndereco);
        int count = enderecos.size();

        if (enderecos.isEmpty()) {

            responses.setStatus(400);
            responses.getMessages().add("Pessoas não localizadas ou já excuídas.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }

        try {

            enderecos.forEach((endereco) -> {

                Endereco enderecoDeleted = endereco.deletarEndereco(endereco, context);

                enderecoDeleted.persist();
                enderecosAux.add(enderecoDeleted);
            });

            responses.setStatus(200);
            if (count <= 1) {
                responses.setData(endereco);
                responses.getMessages().add("Endereço excluído com sucesso!");
            } else {
                responses.setDatas(Collections.singletonList(enderecosAux));
                responses.getMessages().add(count + " Endereços excluídos com sucesso!");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            if (count <= 1) {
                responses.setData(endereco);
                responses.getMessages().add("Endereço não localizado ou já excluído.");
            } else {
                responses.setDatas(Collections.singletonList(enderecos));
                responses.getMessages().add("Endereços não localizados ou já excluídos.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    public Response reactivateEndereco(@NotNull List<Long> pListaIdEndereco) {

        List<Endereco> enderecos;
        List<Endereco> enderecosAux = new ArrayList<>();
        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        enderecos = Endereco.list("id in ?1 and ativo = false", pListaIdEndereco);
        int count = enderecos.size();

        if (enderecos.isEmpty()) {

            responses.setStatus(400);
            responses.getMessages().add("Pessoas não localizadas ou já reativadas.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }

        try {
            enderecos.forEach((endereco) -> {

                Endereco enderecoReactivated = endereco.reativarEndereco(endereco, context);

                enderecoReactivated.persist();
                enderecosAux.add(enderecoReactivated);
            });

            responses.setStatus(200);
            if (count <= 1) {
                responses.setData(endereco);
                responses.getMessages().add("Endereço reativado com sucesso!");
            } else {
                responses.setDatas(Collections.singletonList(enderecosAux));
                responses.getMessages().add(count + " Endereços reativados com sucesso!");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            if (count <= 1) {
                responses.setData(endereco);
                responses.getMessages().add("Endereço não localizado ou já reativado.");
            } else {
                responses.setDatas(Collections.singletonList(enderecos));
                responses.getMessages().add("Endereços não localizados ou já reativados.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    private void loadByEndereco(Endereco pEndereco) {

        endereco.setPessoa(new Pessoa());
        endereco.setOrganizacao(new Organizacao());
        if (BasicFunctions.isNotEmpty(pEndereco.getPessoa()) && BasicFunctions.isValid(pEndereco.getPessoa().getId())) {
            Pessoa pessoa = Pessoa.findById(pEndereco.getPessoa().getId());
            if (BasicFunctions.isNotEmpty(pessoa)) {
                endereco.setPessoa(pessoa);
            }
        }
        if (BasicFunctions.isNotEmpty(pEndereco.getOrganizacao()) && BasicFunctions.isValid(pEndereco.getOrganizacao().getId())) {
            Organizacao organizacao = Organizacao.findById(pEndereco.getOrganizacao().getId());
            if (BasicFunctions.isNotEmpty(organizacao)) {
                endereco.setOrganizacao(organizacao);
            }
        }

        validaEndereco(pEndereco);
    }

    private void validaEndereco(Endereco pEndereco) {

        if (BasicFunctions.isEmpty(pEndereco)) {
            responses.setStatus(400);
            responses.getMessages().add("Não foi possível localizar o Endereço.");
        }

        if (BasicFunctions.isEmpty(endereco.getPessoa())) {
            responses.setStatus(400);
            responses.getMessages().add("Pessoa não localizada.");
        }
        if (BasicFunctions.isEmpty(endereco.getOrganizacao())) {
            responses.setStatus(400);
            responses.getMessages().add("Organização não localizada.");
        }
    }

    private void loadEnderecoById(Endereco pEndereco) {

        endereco = new Endereco();

        if (BasicFunctions.isNotEmpty(pEndereco)) {
            endereco = Endereco.findById(pEndereco.getId());
        }
    }
}
