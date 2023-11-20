package app.agendamento.controller.pessoa;

import app.agendamento.model.pessoa.Genero;
import app.agendamento.model.pessoa.Pessoa;
import app.agendamento.model.pessoa.Usuario;
import app.core.model.DTO.Responses;
import app.core.utils.BasicFunctions;
import app.core.utils.Contexto;
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
public class PessoaController {

    @Context
    SecurityContext context;
    private Pessoa pessoa = new Pessoa();
    private Responses responses;
    private Genero genero;

    private Usuario usuarioAuth;

    public Response addPessoa(@NotNull Pessoa pPessoa) {

        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        loadPessoaByCpf(pPessoa);

        if (BasicFunctions.isEmpty(pessoa)) {

            pessoa = new Pessoa();

            loadByPessoa(pPessoa);

            if (!responses.hasMessages()) {

                pessoa = new Pessoa(pPessoa, genero, context);

                responses.setMessages(new ArrayList<>());

                pessoa.persist();

                responses.setStatus(201);
                responses.setData(pessoa);
                responses.getMessages().add("Pessoa cadastrada com sucesso!");

            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } else {

            responses.setStatus(400);
            responses.setData(pessoa);
            responses.getMessages().add("Pessoa já cadastrada com o CPF informado!");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    public Response updatePessoa(@NotNull Pessoa pPessoa) throws BadRequestException {

        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        try {

            loadPessoaById(pPessoa);

            if (BasicFunctions.isNotEmpty(pessoa)) {
                loadByPessoa(pPessoa);
            }

            if (!responses.hasMessages()) {

                pessoa = pessoa.pessoa(pessoa, pPessoa, genero, context);

                pessoa.persistAndFlush();

                responses.setStatus(200);
                responses.setData(pessoa);
                responses.getMessages().add("Cadastro de Pessoa atualizada com sucesso!");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            responses.setData(pessoa);
            responses.getMessages().add("Não foi possível atualizar o cadastro Pessoa.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    public Response addTelegramIdPessoa(Pessoa pPessoa, Long telegramId, Boolean ativo) throws BadRequestException {

        usuarioAuth = Contexto.getContextUser(context);

        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        try {
            Pessoa pessoa;
            pessoa = Pessoa.find("id = ?1 and ativo = ?2", pPessoa.getId(), ativo).firstResult();

            pessoa.setTelegramId(telegramId);

            pessoa.setUsuarioAcao(usuarioAuth);
            pessoa.setDataAcao(Contexto.dataHoraContexto());
            pessoa.persist();

            responses.setStatus(200);
            responses.setData(pessoa);
            responses.getMessages().add("Vínculo do Telegram efetuado com sucesso!");
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            responses.setData(pessoa);
            responses.getMessages().add("Não foi possível vincular o Telegram com o cadastro Pessoa.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    public Response addWhatsappIdPessoa(Pessoa pPessoa, Long whatsappId, Boolean ativo) throws BadRequestException {

        usuarioAuth = Contexto.getContextUser(context);

        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        try {
            Pessoa pessoa;
            pessoa = Pessoa.find("id = ?1 and ativo = ?2", pPessoa.getId(), ativo).firstResult();

            pessoa.setWhatsappId(whatsappId);

            pessoa.setUsuarioAcao(usuarioAuth);
            pessoa.setDataAcao(Contexto.dataHoraContexto());
            pessoa.persist();

            responses.setStatus(200);
            responses.setData(pessoa);
            responses.getMessages().add("Vínculo do Whatsapp efetuado com sucesso!");
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            responses.setData(pessoa);
            responses.getMessages().add("Não foi possível vincular o WhatsApp com o cadastro Pessoa.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    public Response removeTelegramIdPessoa(Long telegramId, Boolean ativo) throws BadRequestException {

        usuarioAuth = Contexto.getContextUser(context);

        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        try {
            Pessoa pessoa;
            pessoa = Pessoa.find("telegramId = ?1 and ativo = ?2", telegramId, ativo).firstResult();

            pessoa.setTelegramId(null);

            pessoa.setUsuarioAcao(usuarioAuth);
            pessoa.setDataAcao(Contexto.dataHoraContexto());
            pessoa.persist();

            responses.setStatus(200);
            responses.setData(pessoa);
            responses.getMessages().add("Removido Vinculo do Telegram com sucesso!");
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            responses.setData(pessoa);
            responses.getMessages().add("Não foi possível reomver o Vinculo do Telegram com o cadastro Pessoa.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    public Response removeWhatsappIdPessoa(Long whatsappId, Boolean ativo) throws BadRequestException {

        usuarioAuth = Contexto.getContextUser(context);

        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        try {
            Pessoa pessoa;
            pessoa = Pessoa.find("whatsappId = ?1 and ativo = ?2", whatsappId, ativo).firstResult();

            pessoa.setWhatsappId(null);

            pessoa.setUsuarioAcao(usuarioAuth);
            pessoa.setDataAcao(Contexto.dataHoraContexto());
            pessoa.persist();

            responses.setStatus(200);
            responses.setData(pessoa);
            responses.getMessages().add("Removido Vinculo do WhatsApp com sucesso!");
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            responses.setData(pessoa);
            responses.getMessages().add("Não foi possível reomver o Vinculo do WhatsApp com o cadastro Pessoa.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    public Response deletePessoa(@NotNull List<Long> pListPessoa) {

        List<Pessoa> pessoas;
        List<Pessoa> pessoasAux = new ArrayList<>();
        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        pessoas = Pessoa.list("id in ?1 and ativo = true", pListPessoa);
        int count = pessoas.size();

        if (pessoas.isEmpty()) {

            responses.setStatus(400);
            responses.getMessages().add("Pessoas não localizadas ou já excuídas.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }

        try {

            pessoas.forEach((pessoa) -> {

                Pessoa pessoaDeleted = pessoa.deletarPessoa(pessoa, context);

                pessoaDeleted.persist();
                pessoasAux.add(pessoaDeleted);
            });

            responses.setStatus(200);
            if (count <= 1) {
                responses.setData(pessoa);
                responses.getMessages().add("Pessoa excluída com sucesso!");
            } else {
                responses.setDatas(Collections.singletonList(pessoasAux));
                responses.getMessages().add(count + " Pessoas excluídas com sucesso!");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            if (count <= 1) {
                responses.setData(pessoa);
                responses.getMessages().add("Pessoa não localizada ou já excluído.");
            } else {
                responses.setDatas(Collections.singletonList(pessoas));
                responses.getMessages().add("Pessoas não localizadas ou já excluídos.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    public Response reactivatePessoa(@NotNull List<Long> pListPessoa) {

        List<Pessoa> pessoas;
        List<Pessoa> pessoasAux = new ArrayList<>();
        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        pessoas = Pessoa.list("id in ?1 and ativo = false", pListPessoa);
        int count = pessoas.size();

        if (pessoas.isEmpty()) {

            responses.setStatus(400);
            responses.getMessages().add("Pessoas não localizadas ou já reativadas.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }

        try {
            pessoas.forEach((pessoa) -> {

                Pessoa pessoasReactivated = pessoa.reativarPessoa(pessoa, context);

                pessoasReactivated.persist();
                pessoasAux.add(pessoasReactivated);
            });

            responses.setStatus(200);
            if (count <= 1) {
                responses.setData(pessoa);
                responses.getMessages().add("Pessoa reativado com sucesso!");
            } else {
                responses.setDatas(Collections.singletonList(pessoasAux));
                responses.getMessages().add(count + " Pessoas reativados com sucesso!");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            if (count <= 1) {
                responses.setData(pessoa);
                responses.getMessages().add("Pessoa não localizada ou já reativado.");
            } else {
                responses.setDatas(Collections.singletonList(pessoas));
                responses.getMessages().add("Pessoas não localizadas ou já reativados.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    private void loadByPessoa(Pessoa pPessoa) {

        genero = new Genero();

        if (BasicFunctions.isNotEmpty(pPessoa.getGenero())) {
            genero = Genero.findById(pPessoa.getGenero().getId());
        }

        validaDadosPessoa(pPessoa);
    }

    private void loadPessoaByCpf(Pessoa pPessoa) {

        pessoa = new Pessoa();

        if (BasicFunctions.isNotEmpty(pPessoa.getCpf())) {
            pessoa = Pessoa.find("cpf = ?1 and ativo = true", pPessoa.getCpf()).firstResult();
        }

        validaDadosPessoa(pPessoa);
    }

    private void loadPessoaById(Pessoa pPessoa) {

        pessoa = new Pessoa();

        if (BasicFunctions.isNotEmpty(pPessoa.getCpf())) {
            pessoa = Pessoa.find("id = ?1 and ativo = true", pPessoa.getId()).firstResult();
            validaDadosPessoa(pessoa);
        }
    }

    private void validaDadosPessoa(Pessoa pPessoa) {

        usuarioAuth = Contexto.getContextUser(context);

        if (!Contexto.dataValida(pPessoa.getDataNascimento(), usuarioAuth.getOrganizacaoDefault())) {
            responses.setStatus(400);
            responses.getMessages().add("Data de nascimento inválida!");
        }
        if (pPessoa.cpfJaUtilizado(pPessoa)) {
            responses.setStatus(400);
            responses.getMessages().add("Já existe uma pessoa cadastrada com o CPF informado!");
        }
    }
}
