const { api } = require("../../../config/axios/apiService");
const endpoints = require("../../endpoints/endpoints");
const moment = require("moment");

async function listarPessoaAgendamentosByBot(pessoa, reagendar) {
  const pAgendamento = {
    pessoaAgendamento: {
      id: pessoa?.id,
    },
  };

  try {
    return await api.post(
      endpoints.agendamento.listarPessoaAgendamentos(reagendar),
      pAgendamento
    );
  } catch (error) {
    console.error(error);
  }
}

async function listarAgendamentosByBot(pAgendamento, reagendar) {
  let response = await api.post(
    endpoints.agendamento.listarAgendamentos(reagendar),
    pAgendamento
  );
  return response;
}

async function marcarAgendamentosByBot(pAgendamento, bot, msg) {
  try {
    const response = await api.post(
      endpoints.agendamento.marcarAgendamento,
      pAgendamento
    );
    await bot.sendText(msg.from, response.data.messages[0]);
  } catch (error) {
    await bot.sendText(msg.from, error.response.data.messages[0]);
  }
}

async function remarcarAgendamentosByBot(pAgendamento, bot, msg) {
  try {
    const parametros = [
      { id: pAgendamento[0]?.agendamento.id },
      {
        tipoAgendamento: {
          id: pAgendamento[1]?.newAgendamento?.tipoAgendamento?.id,
        },
        pessoaAgendamento: {
          id: pAgendamento[1]?.newAgendamento?.pessoaAgendamento?.id,
        },
        dataAgendamento: moment(
          pAgendamento[1]?.newAgendamento?.dataAgendamento
        ).format("YYYY-MM-DD"),
        horarioAgendamento: pAgendamento[1]?.newAgendamento?.horarioAgendamento,
        profissionalAgendamento: {
          id: pAgendamento[1]?.newAgendamento?.profissionalAgendamento?.id,
        },
        organizacaoAgendamento: {
          id: pAgendamento[1]?.newAgendamento?.organizacaoAgendamento?.id,
        },
        statusAgendamento: {
          id: pAgendamento[1]?.newAgendamento?.statusAgendamento?.id,
        },
      },
    ];
    const response = await api.post(
      endpoints.agendamento.remarcarAgendamento,
      parametros
    );
    await bot.sendText(msg.from, response.data.messages[0]);
  } catch (error) {
    await bot.sendText(msg.from, error.response.data.messages[0]);
  }
}

module.exports = {
  listarPessoaAgendamentosByBot,
  listarAgendamentosByBot,
  marcarAgendamentosByBot,
  remarcarAgendamentosByBot,
};
