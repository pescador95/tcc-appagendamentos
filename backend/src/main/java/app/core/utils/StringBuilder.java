package app.core.utils;

import app.agendamento.model.agendamento.Agendamento;
import app.agendamento.model.agendamento.Atendimento;
import app.agendamento.model.configurador.ConfiguradorAusencia;
import app.agendamento.model.configurador.ConfiguradorFeriado;
import app.agendamento.model.endereco.Endereco;
import app.agendamento.model.organizacao.Organizacao;
import app.agendamento.model.pessoa.Pessoa;
import app.core.model.contrato.Contrato;

import javax.swing.text.MaskFormatter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class StringBuilder {

    public static String makeMaskCnpjFormatter(String pCnpj) throws ParseException {

        MaskFormatter mask;
        mask = new MaskFormatter("###.###.###/####-##");
        mask.setValueContainsLiteralCharacters(false);
        return mask.valueToString(pCnpj);

    }

    public static String removeChars(String pText) {

        String text = pText.replaceAll("^\\[", "");
        return text.replaceAll("]", "");
    }

    public static List<String> removeCharsList(List<String> pText) {
        List<String> returnTexts = new ArrayList<>();

        pText.forEach(text -> {
            String textReturn;
            textReturn = text.replaceAll("]", "");
            returnTexts.add(textReturn);
        });
        return returnTexts;
    }

    public static String makeOnlyNumbers(String text) {
        return text.replaceAll("[^0-9]+", "");
    }

    public static String makeEnderecoString(Organizacao organizacao) {

        Endereco endereco = Endereco.find("organizacaoId = ?1", organizacao.getId()).firstResult();

        if (BasicFunctions.isNotEmpty(endereco)) {
            return endereco.getLogradouro() + ", " + endereco.getNumero() + ". " + endereco.getCidade() + " - " + endereco.getEstado();
        }
        return "";
    }

    public static String makeMaskCelularFormatter(String celular) throws ParseException {
        MaskFormatter mask;
        mask = new MaskFormatter("(##) # ####-####");
        mask.setValueContainsLiteralCharacters(false);
        try {
            return mask.valueToString(celular);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> String makeQueryString(T parameterValue, String parameterName, Class<?> entity) {

        String queryString = "";

        if (parameterValue instanceof String) {
            return queryString + (" AND LOWER(" + parameterName + ") LIKE '%" + parameterValue.toString().toLowerCase()
                    + "%'");
        }
        if (parameterValue instanceof Integer || parameterValue instanceof Long || parameterValue instanceof Boolean
                || parameterValue instanceof Double || parameterValue instanceof Float
                || parameterValue instanceof BigDecimal || parameterValue instanceof LocalDateTime) {
            return queryString + (" AND " + parameterName + " = " + parameterValue);
        }
        if (parameterValue instanceof LocalDate) {
            return queryString + makeEntityAtributeLocalDate(parameterValue, parameterName, entity);
        }
        if (parameterValue instanceof LocalTime) {
            return queryString + makeEntityAtributeLocalTime(parameterValue, parameterName, entity);
        }
        if (parameterValue instanceof List<?> listValues) {
            StringJoiner joiner = new StringJoiner(",", "", "");
            for (Object listValue : listValues) {
                String string = listValue.toString();
                joiner.add(string);
            }
            String paramList = joiner.toString();
            return queryString + " AND " + parameterName + " IN (" + paramList + ")";
        }
        return queryString;
    }

    public static String makeEntityAtributeLocalDate(Object parameterValue, String parameterName, Class<?> entity) {

        if (entity.equals(Agendamento.class)) {
            if (parameterName.equals("dataInicio")) {
                return " AND dataAgendamento >= " + "'" + parameterValue + "'";
            }
            if (parameterName.equals("dataFim")) {
                return " AND dataAgendamento <= " + "'" + parameterValue + "'";
            }
            return " AND dataAgendamento = " + "'" + parameterValue + "'";
        }
        if (entity.equals(Contrato.class)) {
            if (parameterName.equals("dataInicio")) {
                return " AND dataContrato >= " + "'" + parameterValue + "'";
            }
            if (parameterName.equals("dataFim")) {
                return " AND dataContrato <= " + "'" + parameterValue + "'";
            }
            return " AND dataContrato = " + "'" + parameterValue + "'";
        }
        if (entity.equals(Atendimento.class)) {
            if (parameterName.equals("dataInicio")) {
                return "AND dataAtendimento >= " + "'" + parameterValue + "'";
            }
            if (parameterName.equals("dataFim")) {
                return " AND dataAtendimento <= " + "'" + parameterValue + "'";
            }
            return " AND dataAtendimento = " + "'" + parameterValue + "'";
        }
        if (entity.equals(ConfiguradorAusencia.class)) {
            if (parameterName.equals("dataInicio")) {
                return "AND dataInicioAusencia >= " + "'" + parameterValue + "'";
            }
            if (parameterName.equals("dataFim")) {
                return " AND dataFimAusencia <= " + "'" + parameterValue + "'";
            }
        }

        if (entity.equals(ConfiguradorFeriado.class)) {
            if (parameterName.equals("dataInicio")) {
                return "AND dataFeriado >= " + "'" + parameterValue + "'";
            }
            if (parameterName.equals("dataFim")) {
                return " AND dataFeriado <= " + "'" + parameterValue + "'";
            }
            return " AND dataFeriado = " + "'" + parameterValue + "'";
        }
        if (entity.equals(Pessoa.class)) {
            if (parameterName.equals("dataNascimento")) {
                return " AND dataNascimento = " + "'" + parameterValue + "'";
            }
        }
        return "";
    }

    public static String makeEntityAtributeLocalTime(Object parameterValue, String parameterName, Class<?> entity) {

        if (entity.equals(ConfiguradorAusencia.class)) {
            if (parameterName.equals("horaInicio")) {
                return " AND horaInicioAusencia >= " + "'" + parameterValue + "'";
            }
            if (parameterName.equals("horaFim")) {
                return " AND horaFimAusencia <= " + "'" + parameterValue + "'";
            }
        }
        if (entity.equals(ConfiguradorFeriado.class)) {
            if (parameterName.equals("horaInicio")) {
                return " AND horaInicioFeriado >= " + "'" + parameterValue + "'";
            }
            if (parameterName.equals("horaFim")) {
                return " AND horaFimFeriado <= " + "'" + parameterValue + "'";
            }
        }
        if (entity.equals(Agendamento.class)) {
            if (parameterName.equals("horarioInicio")) {
                return " AND horarioAgendamento >= " + "'" + parameterValue + "'";
            }
            if (parameterName.equals("horarioFim")) {
                return " AND horarioAgendamento <= " + "'" + parameterValue + "'";
            }
            return " AND horarioAgendamento = " + "'" + parameterValue + "'";
        }
        return "";
    }

    public static String dataFormatter(LocalDate pData) {
        return pData.getDayOfMonth() + "/" + pData.getMonthValue() + "/" + pData.getYear();
    }

    public static Map<String, String> notificacaoBuilder(Agendamento agendamento) {

        Map<String, String> substituicoes = new java.util.HashMap<>();
        substituicoes.put("NOME", agendamento.getPessoaAgendamento().getNome());
        substituicoes.put("DIA", agendamento.getDataAgendamento().isEqual(Contexto.dataContexto(agendamento.getOrganizacaoAgendamento())) ? "hoje"
                : dataFormatter(agendamento.getDataAgendamento()));
        substituicoes.put("SEMANA", nomeDiaSemana(agendamento.getDataAgendamento()));
        substituicoes.put("DATA", dataFormatter(agendamento.getDataAgendamento()));
        substituicoes.put("HORARIO", agendamento.getHorarioAgendamento().toString());
        substituicoes.put("EMPRESA", agendamento.getOrganizacaoAgendamento().getNome());
        substituicoes.put("ENDERECO", makeEnderecoString(agendamento.getOrganizacaoAgendamento()));
        substituicoes.put("PROFISSIONAL", agendamento.getProfissionalAgendamento().getNomeProfissional());
        substituicoes.put("TIPOAGENDAMENTO", agendamento.getTipoAgendamento().getTipoAgendamento());
        try {
            substituicoes.put("CONTATO", makeMaskCelularFormatter(agendamento.getOrganizacaoAgendamento().getCelular()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return substituicoes;
    }

    public static String makeMensagemByNotificacaoTemplate(String mensagem, Map<String, String> substituicoes) {
        for (Map.Entry<String, String> entrada : substituicoes.entrySet()) {
            mensagem = mensagem.replace(entrada.getKey(), entrada.getValue());
        }
        return mensagem;
    }

    public static String nomeDiaSemana(LocalDate dataAgendamento) {
        DayOfWeek dayOfWeek = dataAgendamento.getDayOfWeek();
        Map<DayOfWeek, String> DIAS_DA_SEMANA;

        DIAS_DA_SEMANA = new EnumMap<>(DayOfWeek.class);
        DIAS_DA_SEMANA.put(DayOfWeek.SATURDAY, "Sábado");
        DIAS_DA_SEMANA.put(DayOfWeek.SUNDAY, "Domingo");
        DIAS_DA_SEMANA.put(DayOfWeek.MONDAY, "Segunda-feira");
        DIAS_DA_SEMANA.put(DayOfWeek.TUESDAY, "Terça-feira");
        DIAS_DA_SEMANA.put(DayOfWeek.WEDNESDAY, "Quarta-feira");
        DIAS_DA_SEMANA.put(DayOfWeek.THURSDAY, "Quinta-feira");
        DIAS_DA_SEMANA.put(DayOfWeek.FRIDAY, "Sexta-feira");

        return DIAS_DA_SEMANA.getOrDefault(dayOfWeek, "");
    }

    public String makeMaskCpfFormatter(String pCpf) throws ParseException {

        MaskFormatter mask;
        mask = new MaskFormatter("###.###.###-##");
        mask.setValueContainsLiteralCharacters(false);
        return mask.valueToString(pCpf);
    }

    public String makeMaskRgFormatter(String pRg) throws ParseException {

        MaskFormatter mask;
        mask = new MaskFormatter("##.###.###-#");
        mask.setValueContainsLiteralCharacters(false);
        return mask.valueToString(pRg);
    }
}