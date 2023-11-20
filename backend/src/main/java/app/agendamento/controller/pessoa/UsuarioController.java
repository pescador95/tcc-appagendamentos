package app.agendamento.controller.pessoa;

import app.agendamento.model.agendamento.Agendamento;
import app.agendamento.model.agendamento.TipoAgendamento;
import app.agendamento.model.organizacao.Organizacao;
import app.agendamento.model.pessoa.Pessoa;
import app.agendamento.model.pessoa.Usuario;
import app.agendamento.queries.usuario.UsuarioQueries;
import app.core.model.DTO.Responses;
import app.core.model.auth.Role;
import app.core.utils.BasicFunctions;
import org.jetbrains.annotations.NotNull;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
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
public class UsuarioController {
    @Inject
    UsuarioQueries usuarioQueries;
    @Context
    SecurityContext context;
    private Usuario usuario;
    private Responses responses;
    private Pessoa pessoa;
    private List<Organizacao> organizacoes = new ArrayList<>();
    private List<Role> roles = new ArrayList<>();
    private List<TipoAgendamento> tiposAgendamentos = new ArrayList<>();

    public Response addUser(@NotNull Usuario pUsuario) {

        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        loadUsuarioByLogin(pUsuario);

        if (BasicFunctions.isEmpty(usuario) && (!BasicFunctions.isEmpty(organizacoes))
                && (BasicFunctions.isNotEmpty(pessoa))
                && (pUsuario.bot() || !(BasicFunctions.isEmpty(roles) && !pUsuario.bot()))) {

            usuario = new Usuario();

            loadByUsuario(pUsuario);

            if (!responses.hasMessages()) {

                usuario = new Usuario(pUsuario, roles, organizacoes, tiposAgendamentos, context);

                usuario.persist();

                responses.setStatus(201);
                responses.setData(usuario);
                responses.getMessages().add("Usuário cadastrado com sucesso!");

            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } else {

            responses.setStatus(400);
            responses.setData(usuario);
            responses.getMessages().add("Verifique as informações!");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    public Response updateUser(@NotNull Usuario pUsuario) {

        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        loadUsuarioById(pUsuario);

        try {

            if (BasicFunctions.isNotEmpty(usuario)) {

                loadByUsuario(pUsuario);

                if (!responses.hasMessages()) {

                    usuario = usuario.usuario(usuario, pUsuario, roles, organizacoes, tiposAgendamentos, context);

                    usuario.persistAndFlush();

                    responses.setStatus(200);
                    responses.setData(usuario);
                    responses.getMessages().add("Usuário atualizado com sucesso!");
                }
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            responses.setData(usuario);
            responses.getMessages().add("Não foi possível atualizar o Usuário.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    public Response deleteUser(@NotNull List<Long> pListIdUsuario) {

        List<Usuario> usuarios;
        List<Usuario> usuariosAux = new ArrayList<>();
        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        usuarios = Usuario.list("id in ?1 and ativo = true", pListIdUsuario);
        int count = usuarios.size();

        try {

            if (usuarios.isEmpty()) {

                responses.setStatus(400);
                responses.getMessages().add("Usuários não localizados ou já excluídos.");
                return Response.ok(responses).status(responses.getStatus()).build();
            }

            usuarios.forEach((usuario) -> {

                Usuario usuarioDeleted = usuario.deletarUsuario(usuario, context);

                usuarioDeleted.persist();
                usuariosAux.add(usuarioDeleted);
            });

            responses.setStatus(200);
            if (count <= 1) {
                responses.setData(usuario);
                responses.getMessages().add("Usuário excluído com sucesso!");
            } else {
                responses.setDatas(Collections.singletonList(usuariosAux));
                responses.getMessages().add(count + " Usuários excluídos com sucesso!");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            if (count <= 1) {
                responses.setData(usuario);
                responses.getMessages().add("Usuário não localizado ou já excluído.");
            } else {
                responses.setDatas(Collections.singletonList(usuarios));
                responses.getMessages().add("Usuários não localizados ou já excluídos.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    public Response reactivateUser(@NotNull List<Long> pListIdUsuario) {

        List<Usuario> usuarios;
        List<Usuario> usuariosAux = new ArrayList<>();
        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        usuarios = Usuario.list("id in ?1 and ativo = false", pListIdUsuario);
        int count = usuarios.size();

        if (usuarios.isEmpty()) {

            responses.setStatus(400);
            responses.getMessages().add("Usuários não localizados ou já reativados.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }

        try {

            usuarios.forEach((usuario) -> {

                Usuario usuarioReactivated = usuario.reativarUsuario(usuario, context);

                usuarioReactivated.persist();
                usuariosAux.add(usuarioReactivated);
            });

            responses.setStatus(200);
            if (count <= 1) {
                responses.setData(usuario);
                responses.getMessages().add("Usuário reativado com sucesso!");
            } else {
                responses.setDatas(Collections.singletonList(usuariosAux));
                responses.getMessages().add(count + " Usuários reativados com sucesso!");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            if (count <= 1) {
                responses.setData(usuario);
                responses.getMessages().add("Usuário não localizado ou já reativado.");
            } else {
                responses.setDatas(Collections.singletonList(usuarios));
                responses.getMessages().add("Usuários não localizados ou já reativados.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    public List<Usuario> loadListUsuariosByOrganizacaoAndDataAgendamento(Agendamento pAgendamento) {
        return usuarioQueries.loadListUsuariosByOrganizacaoAndDataAgendamento(pAgendamento);
    }

    public Usuario loadUsuarioByOrganizacao(Agendamento pAgendamento) {
        return usuarioQueries.loadUsuarioByOrganizacaoAndProfissionalAndTipoAgendamento(pAgendamento);
    }

    private void loadByUsuario(Usuario pUsuario) {

        List<Long> organizacoesId = new ArrayList<>();
        List<Long> privilegioId = new ArrayList<>();
        List<Long> tiposAgendamentosId = new ArrayList<>();

        pessoa = new Pessoa();
        roles = new ArrayList<>();
        organizacoes = new ArrayList<>();
        tiposAgendamentos = new ArrayList<>();

        if (BasicFunctions.isNotEmpty(pUsuario.getPessoa()) && pUsuario.getPessoa().isValid()) {
            pessoa = Pessoa.findById(pUsuario.getPessoa().getId());
        }

        if (pUsuario.hasRole() && !pUsuario.bot()) {
            pUsuario.getPrivilegio().forEach(privilegio -> privilegioId.add(privilegio.getId()));
            roles = Role.list("id in ?1", privilegioId);
            roles.removeIf(x -> x.getId().equals(Usuario.BOT));
        }

        if (BasicFunctions.isNotEmpty(pUsuario.getOrganizacoes())) {
            pUsuario.getOrganizacoes().forEach(organizacao -> organizacoesId.add(organizacao.getId()));
            organizacoes = Organizacao.list("id in ?1", organizacoesId);
        }

        if (BasicFunctions.isNotEmpty(pUsuario.getTiposAgendamentos())) {
            pUsuario.getTiposAgendamentos().forEach(tipoAgendamento -> tiposAgendamentosId.add(tipoAgendamento.getId()));
            tiposAgendamentos = TipoAgendamento.list("id in ?1", tiposAgendamentosId);
        }

        validaUsuario(pUsuario);
    }

    private void loadUsuarioByLogin(Usuario pUsuario) {

        usuario = new Usuario();

        if (BasicFunctions.isNotEmpty(pUsuario) && BasicFunctions.isNotEmpty(pUsuario.getLogin())) {

            usuario = Usuario.find("login = ?1 and ativo = true", pUsuario.getLogin().toLowerCase()).firstResult();
        }
        validaUsuario(pUsuario);
    }

    private void loadUsuarioById(Usuario pUsuario) {

        usuario = new Usuario();

        if (BasicFunctions.isNotEmpty(pUsuario) && pUsuario.isValid()) {
            usuario = Usuario.findById(pUsuario.getId());
        }
        validaUsuario(pUsuario);
    }

    private void validaUsuario(Usuario pUsuario) {

        if (BasicFunctions.isEmpty(pUsuario)) {
            responses.setStatus(400);
            responses.getMessages().add("Por favor, verifique as informações!");
        }

        if (BasicFunctions.isNotEmpty(pUsuario) && BasicFunctions.isEmpty(pUsuario.getLogin())) {
            responses.setStatus(400);
            responses.getMessages().add("Por favor, verifique o login!");
        }

        if (BasicFunctions.isEmpty(pUsuario.getLogin()) && BasicFunctions.isEmpty(pUsuario.getPassword())
                && BasicFunctions.isEmpty(pUsuario.getPrivilegio()) && BasicFunctions.isEmpty(pUsuario.getOrganizacoes())
                && BasicFunctions.isEmpty(pUsuario.getPessoa())
                && BasicFunctions.isEmpty(organizacoes) && BasicFunctions.isEmpty(tiposAgendamentos)) {
            throw new BadRequestException("Informe os dados para atualizar o Usuário.");

        }
    }
}
