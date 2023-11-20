package app.core.controller.auth;

import app.agendamento.model.pessoa.Pessoa;
import app.agendamento.model.pessoa.Usuario;
import app.core.model.DTO.Responses;
import app.core.utils.BasicFunctions;
import app.core.utils.Contexto;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import net.bytebuddy.utility.RandomString;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;

@ApplicationScoped
@Transactional
public class RecoveryPasswordController {
    @Inject
    Mailer mailer;
    @Context
    SecurityContext context;
    Usuario usuarioAuth;
    private Responses responses;

    public Response sendEmail(String login) {
        responses = new Responses();
        responses.setMessages(new ArrayList<>());


        Usuario usuario = Usuario.find("login = ?1 and ativo = true", login.toLowerCase()).firstResult();
        Pessoa pessoa = Pessoa.findById(usuario.getPessoa().getId());
        if (usuario.getAtivo() && BasicFunctions.isNotEmpty(pessoa)) {
            String token = RandomString.make(20);

            usuario.setDataToken(Contexto.dataHoraContexto(usuario.getOrganizacaoDefault()).plusMinutes(10));
            usuario.setToken(token);
            usuario.setAlterarSenha(Boolean.TRUE);
            usuario.persist();

            String agendafacilUrl = System.getenv("AGENDAFACIL_URL");

            if (BasicFunctions.isEmpty(agendafacilUrl)){
                throw new IllegalStateException("A variável de ambiente AGENDAFACIL_URL não está definida.");
        }

            String recoveryUrl = agendafacilUrl + "redefinir?token=" + token;

            String nome = pessoa.getNome();
            mailer.send(Mail.withText(pessoa.getEmail(), "Agenda Fácil - Recuperação de Senha", "Olá, " + nome + "!\n"
                    + "Acesse o link a seguir para redefinir a sua senha: \n" + recoveryUrl));

            responses.setStatus(200);
            responses.setData(usuario);
            responses.getMessages().add("Enviado uma nova senha para o email informado: " + pessoa.getEmail());
        } else {
            responses = new Responses();

            responses.setStatus(400);
            responses.getMessages().add("Não foi possível localizar um cadastro com o email informado.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
        return Response.ok(responses).status(responses.getStatus()).build();
    }

    public Response updatePassword(Usuario pUsuario) {
        responses = new Responses();
        responses.setMessages(new ArrayList<>());
        usuarioAuth = Usuario.find("token = ?1 and ativo = ?2 and alterarSenha = ?2", pUsuario.getToken(), Boolean.TRUE).firstResult();

        try {

            if (BasicFunctions.isNotEmpty(usuarioAuth) && usuarioAuth.getDataToken().isBefore(Contexto.dataHoraContexto(usuarioAuth.getOrganizacaoDefault()))) {

                usuarioAuth.setPassword(BcryptUtil.bcryptHash(pUsuario.getPassword()));
                usuarioAuth.setUsuarioAcao(usuarioAuth.getLogin());
                usuarioAuth.setDataAcao(Contexto.dataHoraContexto());
                usuarioAuth.setAlterarSenha(Boolean.FALSE);
                usuarioAuth.setToken(null);
                usuarioAuth.setDataToken(null);
                usuarioAuth.persist();

                responses.setStatus(200);
                responses.setData(usuarioAuth);
                responses.getMessages().add("Senha atualizada com sucesso.");
            } else {
                responses.setStatus(400);
                responses.getMessages().add("O prazo de 10 minutos do Token expirou.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {
            responses = new Responses();
            responses.setStatus(400);
            responses.getMessages().add("Não foi possível atualizar a senha.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }
}
