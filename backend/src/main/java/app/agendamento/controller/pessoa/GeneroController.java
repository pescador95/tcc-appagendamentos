package app.agendamento.controller.pessoa;

import app.agendamento.model.pessoa.Genero;
import app.core.model.DTO.Responses;
import app.core.utils.BasicFunctions;
import org.jetbrains.annotations.NotNull;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
@Transactional
public class GeneroController {

    private Genero genero = new Genero();

    private Responses responses;

    public Response addGenero(@NotNull Genero pGenero) {

        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        loadByGenero(pGenero);

        if (BasicFunctions.isEmpty(genero)) {

            genero = new Genero();

            if (BasicFunctions.isNotEmpty(pGenero.getGenero())) {
                genero.setGenero(pGenero.getGenero());
            }
            if (!responses.hasMessages()) {
                genero.persist();

                responses.setStatus(201);
                responses.setData(genero);
                responses.getMessages().add("Gênero cadastrado com sucesso!");

            } else {
                return Response.ok(responses).status(responses.getStatus()).build();
            }
            return Response.ok(responses).status(Response.Status.CREATED).build();
        } else {

            responses.setStatus(400);
            responses.setData(genero);
            responses.getMessages().add("Gênero já cadastrado!");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    public Response updateGenero(@NotNull Genero pGenero) {

        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        try {

            loadGeneroById(pGenero);
            if (!responses.hasMessages()) {

                if (BasicFunctions.isNotEmpty(pGenero.getGenero())) {
                    genero.setGenero(pGenero.getGenero());
                }
                genero.persist();

                responses.setStatus(200);
                responses.setData(genero);
                responses.getMessages().add("Gênero atualizado com sucesso!");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            responses.setData(genero);
            responses.getMessages().add("Não foi possível atualizar o cadastro de Gênero.");
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    public Response deleteGenero(@NotNull List<Long> pListIdGenero) {

        List<Genero> generos;
        List<Genero> generosAux = new ArrayList<>();
        responses = new Responses();
        responses.setMessages(new ArrayList<>());

        generos = Genero.list("id in ?1", pListIdGenero);
        int count = generos.size();

        try {

            if (generos.isEmpty()) {

                responses.setStatus(400);
                responses.getMessages().add("Gêneros não localizados ou já excluídos.");
                return Response.ok(responses).status(responses.getStatus()).build();
            }

            generos.forEach((genero) -> {
                genero.delete();
                generosAux.add(genero);
            });

            responses.setStatus(200);
            if (count <= 1) {
                responses.setData(genero);
                responses.getMessages().add("Gênero excluído com sucesso!");
            } else {
                responses.setDatas(Collections.singletonList(generosAux));
                responses.getMessages().add(count + " Gêneros excluídos com sucesso!");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        } catch (Exception e) {

            responses.setStatus(400);
            if (count <= 1) {
                responses.setData(genero);
                responses.getMessages().add("Gênero não localizado ou já excluído.");
            } else {
                responses.setDatas(Collections.singletonList(generos));
                responses.getMessages().add("Gêneros não localizados ou já excluídos.");
            }
            return Response.ok(responses).status(responses.getStatus()).build();
        }
    }

    private void loadByGenero(Genero pGenero) {

        genero = new Genero();

        if (BasicFunctions.isNotEmpty(pGenero.getGenero())) {
            genero = Genero.find("genero = ?1 ", pGenero.getGenero()).firstResult();
        }
        validarGenero(pGenero);
    }

    private void validarGenero(Genero pGenero) {
        if (BasicFunctions.isEmpty(pGenero.getGenero())) {
            responses.setStatus(400);
            responses.getMessages().add("Informe o Gênero a cadastrar.");
        }
    }

    private void loadGeneroById(Genero pGenero) {

        genero = new Genero();

        if (BasicFunctions.isNotEmpty(pGenero)) {
            genero = Genero.find("id = ?1 ", pGenero.getId()).firstResult();
        }
        validarGenero(pGenero);
    }
}
