const { api } = require("../../../config/axios/apiService");
const endpoints = require("../../endpoints/endpoints");

async function getPessoaByPhone(telefone) {
  let response = await api.get(endpoints.pessoa.getByPhone(telefone));
  return response;
}

async function getPessoaByCPF(cpf) {
  let response = await api.get(endpoints.pessoa.getByCPF(cpf));
  return response;
}

async function getPessoaByIdent(ident) {
  let response = await api.get(endpoints.pessoa.getByIdent(ident));
  return response;
}

async function createPessoaByBot(pessoa) {
  let response = await api.post(endpoints.pessoa.createPessoa, pessoa);
  return response;
}

module.exports = {
  getPessoaByPhone,
  getPessoaByCPF,
  getPessoaByIdent,
  createPessoaByBot,
};
