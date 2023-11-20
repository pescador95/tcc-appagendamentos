package app.core.controller.auth;

import app.agendamento.model.pessoa.Usuario;
import app.core.model.DTO.Responses;
import app.core.model.auth.Role;
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
public class RoleController {
    Usuario usuarioAuth;
    @Context
    SecurityContext context;
    private Role role;
    private Responses responses;

    public Response addRole(@NotNull Role pRole) {

        responses = new Responses();
        responses.setMessages(new ArrayList<>());
        usuarioAuth = Contexto.getContextUser(context);

        if (pRole.hasPrivilegio()) {
            role = Role.find("privilegio = ?1", pRole.getPrivilegio()).firstResult();
        } else {
            responses.getMessages().add("O campo 'privilegio' é obrigatório!");
        }

        if (BasicFunctions.isEmpty(role)) {

            role = new Role();
            role.setPrivilegio(pRole.getPrivilegio());
            if (BasicFunctions.isNotEmpty(pRole.getAdmin())) {
                role.setAdmin(pRole.getAdmin());
            } else {
                role.setAdmin(Boolean.FALSE);
            }

            if (!responses.hasMessages()) {
                role.persist();

                responses.setStatus(201);
                responses.setData(role);
                responses.getMessages().add("Role cadastrado com sucesso!");

            } else {
                responses.getMessages().add("Por favor, verifique as informações necessárias!");
                return Response.ok(responses).status(responses.getStatus()).build();
            }
            return Response.ok(responses).status(Response.Status.CREATED).build();
        } else {

            responses.setStatus(400);
            responses.setData(role);
            responses.getMessages().add("Verifique as informações!");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    public Response updateRole(@NotNull Role pRole) {

        usuarioAuth = Contexto.getContextUser(context);

        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        try {

            if (pRole.isValid()) {
                role = Role.findById(pRole.getId());
            }

            if (!pRole.hasPrivilegio()) {
                throw new BadRequestException("Informe os nome do privilégio para atualizar o mesmo.");

            } else {
                role.setPrivilegio(pRole.getPrivilegio());
                if (BasicFunctions.isNotEmpty(pRole.getAdmin())) {
                    role.setAdmin(pRole.getAdmin());
                }
                role.persist();

                responses.setStatus(200);
                responses.setData(role);
                responses.getMessages().add("Role atualizado com sucesso!");
                return Response.ok(responses).status(responses.getStatus()).build();
            }
        } catch (Exception e) {

            responses.setStatus(400);
            responses.setData(role);
            responses.getMessages().add("Não foi possível atualizar o Role.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    public Response deleteRole(@NotNull List<Long> pListIdRole) {

        List<Role> roles;
        List<Role> rolesAux = new ArrayList<>();
        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        usuarioAuth = Contexto.getContextUser(context);
        roles = Usuario.list("id in ?1 and ativo = true", pListIdRole);
        int count = roles.size();

        try {

            if (roles.isEmpty()) {

                responses.setStatus(400);
                responses.getMessages().add("Roles não localizados ou já excluídas.");
                return Response.ok(responses).status(responses.getStatus()).build();
            }

            roles.forEach((role) -> {
                rolesAux.add(role);
                role.delete();
            });

            responses.setStatus(200);
            if (count <= 1) {
                responses.setData(role);
                responses.getMessages().add("Role excluída com sucesso!");
            } else {
                responses.setDatas(Collections.singletonList(rolesAux));
                responses.getMessages().add(count + " Roles excluídas com sucesso!");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            if (count <= 1) {
                responses.setData(role);
                responses.getMessages().add("Role não localizado ou já excluída.");
            } else {
                responses.setDatas(Collections.singletonList(roles));
                responses.getMessages().add("Roles não localizados ou já excluídas.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }
}
