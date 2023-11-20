package app.agendamento.controller.agendamento;

import app.agendamento.model.agendamento.TipoAgendamento;
import app.agendamento.model.organizacao.Organizacao;
import app.agendamento.model.pessoa.Usuario;
import app.core.model.DTO.Responses;
import app.core.utils.BasicFunctions;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.jetbrains.annotations.NotNull;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@Transactional
public class TipoAgendamentoController {

    private TipoAgendamento tipoAgendamento = new TipoAgendamento();

    private Responses responses;

    public Response addTipoAgendamento(@NotNull TipoAgendamento pTipoAgendamento) {

        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        loadTipoAgendamentoByTipoAgendamento(pTipoAgendamento);

        if (BasicFunctions.isEmpty(tipoAgendamento)) {

            tipoAgendamento = new TipoAgendamento();

            loadByTipoAgendamento(pTipoAgendamento);

            if (!responses.hasMessages()) {
                tipoAgendamento.persist();

                responses.setStatus(201);
                responses.setData(tipoAgendamento);
                responses.getMessages().add("Tipo do Agendamento cadastrado com sucesso!");

            } else {
                return Response.ok(responses).status(responses.getStatus()).build();
            }
            return Response.ok(responses).status(Response.Status.CREATED).build();
        } else {

            responses.setStatus(400);
            responses.setData(tipoAgendamento);
            responses.getMessages().add("Tipo do Agendamento já cadastrado!");
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }

    public Response updateTipoAgendamento(@NotNull TipoAgendamento pTipoAgendamento) {

        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        loadTipoAgendamentoById(pTipoAgendamento);
        try {
            if (BasicFunctions.isNotEmpty(tipoAgendamento)) {

                loadByTipoAgendamento(pTipoAgendamento);

                if (!responses.hasMessages()) {
                    tipoAgendamento.persistAndFlush();

                    responses.setStatus(200);
                    responses.setData(tipoAgendamento);
                    responses.getMessages().add("Tipo do Agendamento atualizado com sucesso!");
                }
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            responses.setData(tipoAgendamento);
            responses.getMessages().add("Não foi possível atualizar o cadastro de Tipo do Agendamento.");
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }

    public Response deleteTipoAgendamento(@NotNull List<Long> pListIdTipoAgendamento) {

        List<TipoAgendamento> tipoAgendamentos;
        List<TipoAgendamento> tipoAgendamentosAux = new ArrayList<>();
        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        tipoAgendamentos = TipoAgendamento.list("id in ?1", pListIdTipoAgendamento);
        int count = tipoAgendamentos.size();

        try {

            if (tipoAgendamentos.isEmpty()) {

                responses.setStatus(400);
                responses.getMessages().add("Tipos de Agendamentos não localizados ou já excluídos.");
                return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
            }

            tipoAgendamentos.forEach((TipoAgendamento) -> {
                TipoAgendamento.delete();
                tipoAgendamentosAux.add(TipoAgendamento);
            });

            responses.setStatus(200);
            if (count <= 1) {
                responses.setData(tipoAgendamento);
                responses.getMessages().add("Tipo de Agendamento excluído com sucesso!");
            } else {
                responses.setDatas(Collections.singletonList(tipoAgendamentosAux));
                responses.getMessages().add(count + " Tipos de Agendamentos excluídos com sucesso!");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            if (count <= 1) {
                responses.setData(tipoAgendamento);
                responses.getMessages().add("Tipo de Agendamento não localizado ou já excluído.");
            } else {
                responses.setDatas(Collections.singletonList(tipoAgendamentos));
                responses.getMessages().add("Tipos deAgendamentos não localizados ou já excluídos.");
            }
            return Response.ok(responses).status(Response.Status.BAD_REQUEST).build();
        }
    }

    public List<TipoAgendamento> tiposAgendamentosByOrganizacaoId(List<Long> organizacoes) {

        List<TipoAgendamento> tiposAgendamentosFiltrados = new ArrayList<>();

        List<Usuario> usuarios = Usuario.list("ativo = ?1", Boolean.TRUE);

        if (BasicFunctions.isNotEmpty(organizacoes)) {
            Organizacao organizacao = Organizacao.find("id in ?1", organizacoes).firstResult();

            usuarios = usuarios.stream().filter(x -> x.getOrganizacoes().contains(organizacao)).collect(Collectors.toList());

            usuarios.forEach(usuario -> usuario.getTiposAgendamentos().forEach(tipoAgendamento -> {
                if (!tiposAgendamentosFiltrados.contains(tipoAgendamento)) {
                    tiposAgendamentosFiltrados.add(tipoAgendamento);
                }
            }));

        }
        return tiposAgendamentosFiltrados;
    }

    private void loadByTipoAgendamento(@NotNull TipoAgendamento pTipoAgendamento) {

        List<Long> organizacoesId = new ArrayList<>();
        List<Organizacao> organizacoes;

        if (BasicFunctions.isNotEmpty(pTipoAgendamento.getOrganizacoes())) {
            pTipoAgendamento.getOrganizacoes().forEach(organizacao -> organizacoesId.add(organizacao.getId()));
        }
        organizacoes = Organizacao.list("id in ?1", organizacoesId);

            if (BasicFunctions.isNotEmpty(pTipoAgendamento.getTipoAgendamento())) {
                tipoAgendamento.setTipoAgendamento(pTipoAgendamento.getTipoAgendamento());
            }

            if (BasicFunctions.isNotEmpty(organizacoes)) {
                tipoAgendamento.setOrganizacoes(new ArrayList<>());
                tipoAgendamento.getOrganizacoes().addAll(organizacoes);
            }
        validaTipoAgendamento(pTipoAgendamento);
    }

    private void validaTipoAgendamento(TipoAgendamento pTipoAgendamento) {

        if (!pTipoAgendamento.isValid() && BasicFunctions.isEmpty(pTipoAgendamento.getTipoAgendamento())) {
            responses.setStatus(400);
            responses.getMessages().add("Informe os dados para atualizar o cadastro do Tipo de Agendamento.");
        }
    }

    private void loadTipoAgendamentoById(TipoAgendamento pTipoAgendamento) {

        tipoAgendamento = new TipoAgendamento();

        if (BasicFunctions.isNotEmpty(pTipoAgendamento)) {
            tipoAgendamento = PanacheEntityBase.findById(pTipoAgendamento.getId());
        }
    }

    private void loadTipoAgendamentoByTipoAgendamento(TipoAgendamento pTipoAgendamento) {

        tipoAgendamento = new TipoAgendamento();

        List<Long> organizacoesId = new ArrayList<>();

        if (BasicFunctions.isNotEmpty(pTipoAgendamento) && BasicFunctions.isNotEmpty(pTipoAgendamento.getOrganizacoes())) {

            pTipoAgendamento.getOrganizacoes().forEach(organizacao -> {
                if (BasicFunctions.isNotEmpty(organizacao) && organizacao.isValid()) {
                    organizacoesId.add(organizacao.getId());
                }
            });

            tipoAgendamento = TipoAgendamento.find("tipoAgendamento = ?1", pTipoAgendamento.getTipoAgendamento()).firstResult();
        }
    }
}
