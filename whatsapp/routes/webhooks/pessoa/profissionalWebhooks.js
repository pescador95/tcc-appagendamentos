const { api } = require("../../../config/axios/apiService");
const endpoints = require("../../endpoints/endpoints");
const moment = require("moment");

async function listarProfissionaisByAgendamentoBot(
  organizacao,
  dataAgendamento,
  tipoAgendamento,
  profissional,
  comPreferencia
) {
  const parametros = {
    organizacao: organizacao?.id,
    dataAgendamento: moment(dataAgendamento).format("YYYY-MM-DD"),
    tipoAgendamento: tipoAgendamento?.id,
    profissional: profissional?.id ?? 0,
    comPreferencia: comPreferencia,
  };

  const response = await api.get(
    endpoints.profissional.listaProfissionais(
      parametros.organizacao,
      parametros.dataAgendamento,
      parametros.tipoAgendamento,
      parametros.profissional,
      parametros.comPreferencia
    )
  );

  return response;
}

module.exports = listarProfissionaisByAgendamentoBot;
