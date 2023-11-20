package app.agendamento.filters.pessoa;

import app.agendamento.model.pessoa.Pessoa;
import app.core.utils.BasicFunctions;

import java.time.LocalDate;

import static app.core.utils.StringBuilder.makeQueryString;

public class PessoaFilters {

    public static String makePessoaQueryStringByFilters(Long id, String nome, Long generoId, LocalDate dataNascimento, String telefone, String celular, String email, Long enderecoId, String cpf, Long telegramId, Long whatsappId) {

        String queryString = "";

        if (BasicFunctions.isValid(id)) {
            queryString += makeQueryString(id, "id", Pessoa.class);
        }
        if (BasicFunctions.isNotEmpty(nome)) {
            queryString += makeQueryString(nome, "nome", Pessoa.class);
        }
        if (BasicFunctions.isValid(generoId)) {
            queryString += makeQueryString(generoId, "generoId", Pessoa.class);
        }
        if (BasicFunctions.isValid(enderecoId)) {
            queryString += makeQueryString(enderecoId, "enderecoId", Pessoa.class);
        }
        if (BasicFunctions.isNotEmpty(email)) {
            queryString += makeQueryString(email, "email", Pessoa.class);
        }
        if (BasicFunctions.isNotEmpty(telefone)) {
            queryString += makeQueryString(telefone, "telefone", Pessoa.class);
        }
        if (BasicFunctions.isNotEmpty(celular)) {
            queryString += makeQueryString(celular, "celular", Pessoa.class);
        }
        if (BasicFunctions.isNotEmpty(dataNascimento)) {
            queryString += makeQueryString(dataNascimento, "dataNascimento", Pessoa.class);
        }
        if (BasicFunctions.isNotEmpty(cpf)) {
            queryString += makeQueryString(cpf, "cpf", Pessoa.class);
        }
        if (BasicFunctions.isValid(telegramId)) {
            queryString += makeQueryString(telegramId, "telegramId", Pessoa.class);
        }
        if (BasicFunctions.isValid(whatsappId)) {
            queryString += makeQueryString(whatsappId, "whatsappId", Pessoa.class);
        }

        return queryString;
    }
}
