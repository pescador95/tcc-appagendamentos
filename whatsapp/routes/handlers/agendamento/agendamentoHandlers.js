const {
  listarAgendamentosByBot,
  marcarAgendamentosByBot,
  remarcarAgendamentosByBot,
  listarPessoaAgendamentosByBot,
} = require("../../webhooks/agendamento/agendamentoWebhooks");
const montarTiposAgendamentoByOrganizacao = require("../agendamento/tipoAgendamentoHandlers");
const montarOrganizacoesByAgendamento = require("../organizacao/organizacaoHandlers");
const {
  montarProfissionaisByAgendamento,
  montarPreferenciaProfissional,
} = require("../pessoa/profissionalHandlers");

const {
  formatCelular,
  formatDate,
  formatTime,
  formatDateReverse,
} = require("../../../utils/formatters");
const {
  montarDataAgendamento,
  montarHorarioAgendamento,
} = require("./dataAgendamentoHandlers");
const { setState } = require("../../../config/objetos/setState");
const {
  setAgendamentoConfirmado,
} = require("../../../config/objetos/setAgendamentoConfirmado");
const {
  setAgendamentosReagendar,
  listaAgendamentosReagendar,
} = require("../../../config/objetos/setAgendamentosReagendar");
const send = require("send");
const { log } = require("console");
const {
  limparAgedamentoById,
} = require("../../../config/objetos/limparObjetos");
const { listaOpcoes } = require("../../../commands/opcoes");
require("moment-business-days");
require("moment/locale/pt-br");

async function listarAgendamentoByPessoa(msg, bot, pessoa, reagendar) {
  const response = await listarPessoaAgendamentosByBot(pessoa, reagendar);

  if (response.data.length === 0) {
    let message = `Você não possui agendamentos.`;
    await bot.sendText(msg.from, message);
    await limparAgedamentoById(msg.from);
    return await listaOpcoes(msg, bot, true);
  }

  const agendamentos = response.data.map((agendamento) => {
    let {
      tipoAgendamento,
      profissionalAgendamento,
      organizacaoAgendamento,
      endereco,
      dataAgendamento,
      horarioAgendamento,
      statusAgendamento,
    } = agendamento;

    celularOrganizacao = formatCelular(organizacaoAgendamento.celular);
    dataAgendamento = formatDate(dataAgendamento);
    horarioAgendamento = formatTime(horarioAgendamento);

    return (
      `Tipo de Agendamento: ${tipoAgendamento.tipoAgendamento}\n` +
      `Profissional: ${profissionalAgendamento.nome}\n` +
      `Organização: ${organizacaoAgendamento.nome}\n` +
      `Endereço: ${endereco}\n` +
      `Celular da Organização: ${celularOrganizacao}\n` +
      `Data do Agendamento: ${dataAgendamento}\n` +
      `Horário do Agendamento: ${horarioAgendamento}\n` +
      `Status: ${statusAgendamento.status}\n\n`
    );
  });

  if (reagendar) {
    const opcoes = response.data.map((agendamento) => {
      let {
        id,
        tipoAgendamento,
        profissional,
        organizacao,
        endereco,
        celularOrganizacao,
        dataAgendamento,
        horarioAgendamento,
        status,
      } = agendamento;
      return agendamento;
    });
    for (let i = 0; i < opcoes.length; i++) {
      opcoes[i].idLabel = i + 1;
    }

    opcoes.sort(function (x, y) {
      return x.idLabel - y.idLabel;
    });
    let listaOpcoes = "";
    for (let i = 0; i < opcoes.length; i++) {
      listaOpcoes +=
        "*" +
        opcoes[i].idLabel +
        "* - " +
        formatDate(opcoes[i].dataAgendamento) +
        " às " +
        opcoes[i].horarioAgendamento +
        "\n" +
        "Organização: " +
        opcoes[i].organizacaoAgendamento.nome +
        "\n" +
        "Profissional: " +
        opcoes[i].profissionalAgendamento.nome +
        "\n\n";
    }
    let message =
      `Aqui estão os seus agendamentos disponíveis.\nSelecione qual você deseja reagendar:\n\n` +
      listaOpcoes +
      "```digite o número de uma das opções```";
    await setAgendamentosReagendar(msg.from, opcoes);
    await setState(msg.from, "aguardando_oldAgendamento");
    return await bot.sendText(msg.from, message);
  } else {
    let message = `Aqui estão os agendamentos de ${pessoa.nome}:\n\n`;
    message += agendamentos.join("");

    await bot.sendText(msg.from, message);
    return listaOpcoes(msg, bot, true);
  }
}

async function listarAgendamento(pAgendamento) {
  const response = await listarAgendamentosByBot(pAgendamento);
  return response;
}

async function marcarAgendamento(pAgendamento, bot, msg) {
  const response = await marcarAgendamentosByBot(
    pAgendamento.newAgendamento,
    bot,
    msg
  );
  return response;
}

async function remarcarAgendamento(pOldAgendamento, pNewAgendamento, bot, msg) {
  let agendamentos = [pOldAgendamento, pNewAgendamento];

  const response = await remarcarAgendamentosByBot(agendamentos, bot, msg);
  return response;
}

async function montarAgendamento(
  msg,
  bot,
  organizacao,
  dataAgendamento,
  tipoAgendamento,
  horarioAgendamento,
  profissional,
  comPreferencia,
  pessoa,
  reagendar,
  oldAgendamento
) {
  if (!reagendar) {
    let agendamento = {
      pessoaAgendamento: pessoa,
      organizacaoAgendamento: organizacao,
      tipoAgendamento: tipoAgendamento,
      dataAgendamento: dataAgendamento,
      horarioAgendamento: horarioAgendamento?.horarioAgendamento,
      profissionalAgendamento: horarioAgendamento.profissionalAgendamento,
      comPreferencia: comPreferencia,
      statusAgendamento: {
        id: 1,
      },
    };
    let labelAgendamento = {
      organizacaoNome: organizacao.nome,
      dataAgendamento: dataAgendamento,
      tipoAgendamento: tipoAgendamento.tipoAgendamento,
      pessoaNome: pessoa.nome,
      profissionalNome: horarioAgendamento.profissionalAgendamento.nome,
      horarioAgendamento: horarioAgendamento.horarioAgendamento,
      celularOrganizacao: organizacao.celular,
      endereco: horarioAgendamento.endereco,
    };
    return {
      newAgendamento: agendamento,
      labelAgendamento: labelAgendamento,
    };
  } else {
    let agendamento = {
      id: oldAgendamento.id,
      pessoaAgendamento: oldAgendamento.pessoaAgendamento,
      organizacaoAgendamento: oldAgendamento.organizacaoAgendamento,
      tipoAgendamento: oldAgendamento.tipoAgendamento,
      dataAgendamento: oldAgendamento.dataAgendamento,
      horarioAgendamento: oldAgendamento.horarioAgendamento,
      profissionalAgendamento: oldAgendamento.profissionalAgendamento,
      celularOrganizacao: oldAgendamento.organizacaoAgendamento.celular,
      endereco: oldAgendamento.endereco,
      pessoaNome: pessoa.nome,
    };
    return {
      agendamento,
    };
  }
}

async function selecionarAgendamentoRemarcacao(msg, bot, pessoa, reagendar) {
  const agendamentos = await listarPessoaAgendamentosByBot(pessoa, true);

  if (agendamentos.data.length === 0) {
    let message = `Você não possui agendamentos para realizar a remarcação.`;
    await bot.sendText(msg.from, message);

    return null;
  } else {
    let message = `Aqui estão os seus agendamentos disponíveis.\n Selecione qual você deseja reagendar:\n\n`;

    const opcoes = agendamentos.data.map((agendamento) => {
      let dataFormatada = formatDate(agendamento.dataAgendamento);
      return {
        id: agendamento.id,
        tipoAgendamento: agendamento.tipoAgendamento,
        profissional: agendamento.profissionalAgendamento,
        organizacao: agendamento.organizacaoAgendamento,
        endereco: agendamento.endereco,
        celularOrganizacao: agendamento.organizacaoAgendamento.celular,
        dataAgendamento: agendamento.dataAgendamento,
        horarioAgendamento: agendamento.horarioAgendamento,
        status: agendamento.statusAgendamento.status,
        label: `${agendamento.organizacaoAgendamento.nome} - ${agendamento.profissionalAgendamento.nome} - ${dataFormatada} às ${agendamento.horarioAgendamento}`,
      };
    });

    const keyboard = opcoes.map((opcao) => [opcao.label]);

    const replyMarkup = {
      keyboard,
      one_time_keyboard: true,
      resize_keyboard: true,
    };

    const opcoesMarkup = {
      reply_markup: JSON.stringify(replyMarkup),
    };

    await bot.sendText(msg.from, message, opcoesMarkup);
  }
}

async function confirmarAgendamento(msg, bot, agendamento, pessoa) {
  if (agendamento?.labelAgendamento) {
    const opcoes = [
      { id: 1, nome: "Confirmar" },
      { id: 2, nome: "Voltar ao menu" },
      { id: 3, nome: "Cancelar" },
    ];
    opcoes.sort(function (x, y) {
      return x.id - y.id;
    });
    let listaOpcoes = "";
    for (let i = 0; i < opcoes.length; i++) {
      listaOpcoes += "*" + opcoes[i].id + "* - " + opcoes[i].nome + "\n";
    }

    let dataFormatada = formatDate(
      agendamento.labelAgendamento.dataAgendamento
    );
    let message =
      "Por favor, verifique as seguintes informações antes de confirmar o agendamento:\n\n";
    message +=
      `Cliente: ${pessoa.nome}\n` +
      `Tipo de Agendamento: ${agendamento.newAgendamento.tipoAgendamento.tipoAgendamento}\n` +
      `Profissional: ${agendamento.newAgendamento.profissionalAgendamento.nome}\n` +
      `Organização:  ${agendamento.newAgendamento.organizacaoAgendamento.nome}\n` +
      `Endereço: ${agendamento.labelAgendamento.endereco}\n` +
      `Data do Agendamento: ${dataFormatada}\n` +
      `Horário do Agendamento: ${agendamento.labelAgendamento.horarioAgendamento}\n`;

    let messageOpcoes =
      listaOpcoes + "\n```digite o número de uma das opções```";

    setAgendamentoConfirmado(msg.from, agendamento);
    setState(msg.from, "aguardando_confirmação");
    await bot.sendText(msg.from, message);
    await bot.sendText(msg.from, messageOpcoes);
  }
}

async function confirmarReagendamento(
  msg,
  bot,
  oldAgendamento,
  newAgendamento,
  pessoa
) {
  if (oldAgendamento) {
    const opcoes = [
      { id: 1, nome: "Confirmar" },
      { id: 2, nome: "Alterar" },
      { id: 3, nome: "Cancelar" },
    ];

    opcoes.sort(function (x, y) {
      return x.id - y.id;
    });
    let listaOpcoes = "";
    for (let i = 0; i < opcoes.length; i++) {
      listaOpcoes += "*" + opcoes[i].id + "* - " + opcoes[i].nome + "\n";
    }

    let newDataFormatada = formatDate(
      newAgendamento.newAgendamento.dataAgendamento
    );
    let oldDataFormatada = formatDate(
      oldAgendamento.agendamento.dataAgendamento
    );
    let message = `Por favor, verifique as seguintes informações antes de confirmar o reagendamento:\n\n`;
    message +=
      `*Agendamento anterior:*\n` +
      `Cliente: ${pessoa.nome}\n` +
      `Tipo de Agendamento: ${oldAgendamento.agendamento.tipoAgendamento.tipoAgendamento}\n` +
      `Profissional: ${oldAgendamento.agendamento.profissionalAgendamento.nome}\n` +
      `Organização: ${oldAgendamento.agendamento.organizacaoAgendamento.nome}\n` +
      `Endereço: ${oldAgendamento.agendamento.endereco}\n` +
      `Celular da Organização: ${formatCelular(
        oldAgendamento.agendamento.organizacaoAgendamento.celular
      )}\n` +
      `Data do Agendamento: ${oldDataFormatada}\n` +
      `Horário do Agendamento: ${oldAgendamento.agendamento.horarioAgendamento}\n\n`;

    message +=
      `*Agendamento novo:*\n` +
      `Cliente: ${newAgendamento.labelAgendamento.pessoaNome}\n` +
      `Tipo de Agendamento: ${newAgendamento.labelAgendamento.tipoAgendamento}\n` +
      `Profissional: ${newAgendamento.labelAgendamento.profissionalNome}\n` +
      `Organização: ${newAgendamento.labelAgendamento.organizacaoNome}\n` +
      `Endereço: ${newAgendamento.labelAgendamento.endereco}\n` +
      `Celular da Organização: ${formatCelular(
        newAgendamento.labelAgendamento.celularOrganizacao
      )}\n` +
      `Data do Agendamento: ${newDataFormatada}\n` +
      `Horário do Agendamento: ${newAgendamento.labelAgendamento.horarioAgendamento}\n`;

    let messageOpcoes =
      listaOpcoes + "\n```digite o número de uma das opções```";

    setAgendamentoConfirmado(msg.from, newAgendamento, oldAgendamento);
    setState(msg.from, "aguardando_confirmaçãoReagendar");
    await bot.sendText(msg.from, message);
    await bot.sendText(msg.from, messageOpcoes);
  }
}

module.exports = {
  listarAgendamento,
  marcarAgendamento,
  remarcarAgendamento,
  listarAgendamentoByPessoa,
  montarAgendamento,
  selecionarAgendamentoRemarcacao,
  confirmarAgendamento,
  confirmarReagendamento,
};
