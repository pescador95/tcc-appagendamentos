const { api } = require("../../../config/axios/apiService");
const endpoints = require("../../endpoints/endpoints");

async function listarTiposAgendamentoByOrganizacaoBot(organizacoes) {
  return await api.get(
    endpoints.tipoAgendamentos.listarTipoAgendamentos(organizacoes)
  );
}

module.exports = listarTiposAgendamentoByOrganizacaoBot;
