const { api } = require("../../../config/axios/apiService");
const endpoints = require("../../endpoints/endpoints");
const {
  formatCelular,
  formatDate,
  formatTime,
  formatDateReverse,
} = require("../../../utils/formatters");
const moment = require("moment");
require("moment-business-days");
require("moment/locale/pt-br");

const {
  listarAgendamentosByBot,
} = require("../../webhooks/agendamento/agendamentoWebhooks");
const {
  setState,
  getPreferencia,
} = require("../../../config/objetos/setState");
const { setDataDisponivel } = require("../../../config/objetos/setData");
const { setHorarioDisponivel } = require("../../../config/objetos/setHorario");
const {
  getAgendamentoTipo,
} = require("../../../config/objetos/setAgendamento");

async function validarDataAgendamento(agendamentoVerificar) {
  try {
    let response = await api.post(
      endpoints.agendamento.verificarDataAgendamento,
      agendamentoVerificar
    );
    if (response) {
      return true;
    }
  } catch (e) {
    return false;
  }
}

async function montarDataAgendamento(
  msg,
  bot,
  organizacao,
  pessoa,
  tipoAgendamento,
  reincidente
) {
  async function generateDateOptions() {
    let next6Days = [];

    let i = 0;
    let hoje = {
      dataAgendamento: moment()
        .utcOffset(organizacao.timeZoneOffset)
        .format("YYYY-MM-DD"),
      organizacaoAgendamento: organizacao,
      tipoAgendamento: tipoAgendamento,
      pessoaAgendamento: pessoa,
    };

    const dataAgendamentoDisponivel = await validarDataAgendamento(hoje);

    while (i <= 7) {
      let numWeekDay = moment()
        .utcOffset(organizacao.timeZoneOffset)
        .add(i, "days")
        .weekday();
      let dia = moment()
        .utcOffset(organizacao.timeZoneOffset)
        .add(i, "days")
        .format("DD/MM/YYYY");
      let diaSemana = moment()
        .utcOffset(organizacao.timeZoneOffset)
        .weekday(numWeekDay)
        .format("dddd");
      let label = dia + " - (" + diaSemana + ")";
      let id = i;

      const dataAgenda = {
        id: id,
        dia: dia,
        diaSemana: diaSemana,
        label: label,
      };

      if (
        dataAgenda.dia ===
        moment().utcOffset(organizacao.timeZoneOffset).format("DD/MM/YYYY")
      ) {
        dataAgenda.label = "" + dataAgenda.dia + " - (Hoje)";
      }

      if (numWeekDay >= 1 && numWeekDay <= 5) {
        next6Days.push(dataAgenda);
      }

      i++;
    }

    let contagem = 0;
    const novoObjeto = {
      id: 7,
      label: "Inserir data específica",
    };
    next6Days.push(novoObjeto);

    const todayDate = moment()
      .utcOffset(organizacao.timeZoneOffset)
      .format("DD/MM/YYYY");

    if (!dataAgendamentoDisponivel) {
      next6Days = next6Days.filter(
        (dataAgenda) => dataAgenda.dia !== todayDate
      );
    }

    const opcoes = next6Days.map((dataAgenda) => {
      contagem++;
      return { id: contagem, dia: dataAgenda.dia, label: dataAgenda.label };
    });

    let listaOpcoes = "";
    for (let i = 0; i < opcoes.length; i++) {
      listaOpcoes += "*" + opcoes[i].id + "* - " + opcoes[i].label + "\n";
    }
    setDataDisponivel(msg.from, opcoes);
    return listaOpcoes;
  }

  const options = await generateDateOptions();

  let message = "Selecione uma ";
  if (reincidente) {
    message += "*nova* ";
  }
  message +=
    "*data* para o atendimento:\n\n" +
    options +
    "\n```digite o número de uma das opções```";
  setState(msg.from, "aguardando_data");
  await bot.sendText(msg.from, message);
}

async function listenForDateSelection(
  msg,
  bot,
  selectedOption,
  isPersonalizada,
  organizacao,
  pessoa,
  tipoAgendamento
) {
  let dataAgendamento = moment()
    .utcOffset(organizacao.timeZoneOffset)
    .format("YYYY-MM-DD");
  let selectedDate;
  if (!isPersonalizada) {
    selectedDate = selectedOption.dia;
  } else {
    selectedDate = selectedOption;
  }

  dataAgendamento = formatDateReverse(selectedDate);
  const parametros = {
    dataAgendamento: dataAgendamento,
    organizacaoAgendamento: organizacao,
    tipoAgendamento: tipoAgendamento,
    pessoaAgendamento: pessoa,
  };
  try {
    if (
      !moment(dataAgendamento, "YYYY-MM-DD")
        .utcOffset(organizacao.timeZoneOffset)
        .isValid() ||
      dataAgendamento <
        moment().utcOffset(organizacao.timeZoneOffset).format("YYYY-MM-DD")
    ) {
      await bot.sendText(
        msg.from,
        "Data inválida.\n\nPor favor, selecione ou informe outra data."
      );
      return montarDataAgendamento(
        msg,
        bot,
        organizacao,
        pessoa,
        tipoAgendamento
      );
    } else if (
      dataAgendamento >
      moment()
        .utcOffset(organizacao.timeZoneOffset)
        .add(1, "year")
        .format("YYYY-MM-DD")
    ) {
      await bot.sendText(
        msg.from,
        "Só é permitido fazer agendamentos com até um ano de antecedência.\n\nPor favor, selecione outra data."
      );
      return montarDataAgendamento(
        msg,
        bot,
        organizacao,
        pessoa,
        tipoAgendamento
      );
    } else {
      const dataAgendamentoDisponivel = await validarDataAgendamento(
        parametros
      );

      if (dataAgendamentoDisponivel) {
        return dataAgendamento;
      } else {
        await bot.sendText(
          msg.from,
          "Data indisponível, selecione outra data:\n"
        );
        return montarDataAgendamento(
          msg,
          bot,
          organizacao,
          pessoa,
          tipoAgendamento
        );
      }
    }
  } catch (error) {
    console.error(
      "Erro ao verificar a disponibilidade da data de agendamento:",
      error
    );
  }
}

async function montarHorarioAgendamento(
  msg,
  bot,
  organizacao,
  dataAgendamento,
  tipoAgendamento,
  comPreferencia,
  pessoa,
  profissional,
  reagendar
) {
  try {
    let pAgendamento = {
      pessoaAgendamento: pessoa,
      organizacaoAgendamento: organizacao,
      tipoAgendamento: tipoAgendamento,
      dataAgendamento: dataAgendamento,
      profissionalAgendamento: profissional,
      comPreferencia: comPreferencia,
    };
    const horariosAgendamentos = await listarAgendamentosByBot(
      pAgendamento,
      reagendar
    );

    if (horariosAgendamentos.data.datas.length === 0) {
      return null;
    }

    const opcoes = horariosAgendamentos.data.datas.map((horarioAgendamento) => {
      let dataFormatada = formatDate(horarioAgendamento.dataAgendamento);
        return {
          dataAgendamento: formatDateReverse(
            horarioAgendamento.dataAgendamento
          ),
          horarioAgendamento: dataFormatada,
          profissionalAgendamento: horarioAgendamento.profissionalAgendamento,
          organizacaoAgendamento: horarioAgendamento.organizacaoAgendamento,
          label: `${horarioAgendamento.horarioAgendamento} - ${horarioAgendamento.profissionalAgendamento.nome}`,
          profissionalId: horarioAgendamento.profissionalAgendamento.id,
          organizacaoId: horarioAgendamento.organizacaoAgendamento.id,
          endereco: horarioAgendamento.endereco,
          celularOrganizacao: horarioAgendamento.organizacaoAgendamento.celular,
        };
    });
    let contagem = 0;
    const opcoesId = opcoes.map((opc) => {
      contagem++;
      return {
        id: contagem,
        profissionalAgendamento: opc.profissionalAgendamento,
        organizacaoAgendamento: opc.organizacaoAgendamento,
        horarioAgendamento: opc.horarioAgendamento,
        celularOrganizacao: opc.celularOrganizacao,
        endereco: opc.endereco,
        label: opc.label,
      };
    });

    let listaOpcoes = "";
    for (let i = 0; i < opcoesId.length; i++) {
      listaOpcoes += "*" + opcoesId[i].id + "* - " + opcoesId[i].label + "\n";
    }
    let message =
      "Aqui estão os nossos horários de agendamentos disponíveis para a data *" +
      formatDate(dataAgendamento) +
      "* na empresa *" +
      horariosAgendamentos.data.datas[0].organizacaoAgendamento.nome +
      "*";

    if (comPreferencia) {
      message +=
        " com *" +
        horariosAgendamentos.data.datas[0].profissionalAgendamento.nome +
        "*";
    }
    message +=
      ".\n\nSelecione qual o melhor *horário* para o atendimento:\n\n" +
      listaOpcoes +
      "\n```digite o número de uma das opções```";

    await setHorarioDisponivel(msg.from, opcoesId);
    await setState(msg.from, "Aguardando_horario");
    await bot.sendText(msg.from, message);
  } catch (error) {
    let message = `Não há nenhum horário para agendamento na organização, profissional e data solicitada disponível para atendimento.`;
    await bot.sendText(msg.from, message);
    return montarDataAgendamento(
      msg,
      bot,
      organizacao,
      pessoa,
      tipoAgendamento
    );
  }
}

async function montarDataAgendamentoIgual(msg, bot, dataAgendamento) {
  const opcoes = [
    { id: 1, nome: "Sim" },
    { id: 2, nome: "Não" },
  ];
  opcoes.sort(function (x, y) {
    return x.id - y.id;
  });
  let listaOpcoes = "";
  for (let i = 0; i < opcoes.length; i++) {
    listaOpcoes += "*" + opcoes[i].id + "* - " + opcoes[i].nome + "\n";
  }

  let message =
    "Já existe um agendamento para o dia *" +
    formatDate(dataAgendamento) +
    "*.\n\nDeseja realizar um *novo agendamento* para a *mesma data*?\n\n" +
    listaOpcoes +
    "\n```digite o número de uma das opções```";

  await setState(msg.from, "aguardando_dataAgendamentoIgual");
  await bot.sendText(msg.from, message);
}

module.exports = {
  montarDataAgendamento,
  montarDataAgendamentoIgual,
  montarHorarioAgendamento,
  listenForDateSelection,
};
