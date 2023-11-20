const { api } = require("../../../config/axios/apiService");
const endpoints = require("../../endpoints/endpoints");

async function listarOrganizacoesByAgendamentoBot() {
  return await api.get(endpoints.organizacao.listaOrganizacoes);
}

module.exports = listarOrganizacoesByAgendamentoBot;
