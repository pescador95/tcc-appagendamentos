const { api } = require("../../../config/axios/apiService");
const endpoints = require("../../endpoints/endpoints");

async function getPessoaByPhone(telefone) {
  try {
    return await api.get(endpoints.pessoa.getByPhone(telefone));
  } catch (error) {
    console.log(error);
  }
}

async function getPessoaByCPF(cpf) {
  try {
    return await api.get(endpoints.pessoa.getByCPF(cpf));
  } catch (error) {
    console.log(error);
  }
}

async function getPessoaByIdent(ident) {
  try {
    return await api.get(endpoints.pessoa.getByIdent(ident));
  } catch (error) {
    console.log(error);
  }
}

async function getPessoaByTelegram(telegramId) {
  try {
    let response = await api.get(endpoints.pessoa.getByTelegram(telegramId));
    return response;
  } catch (error) {
    console.log(error);
  }
}

async function createPessoaByBot(pessoa) {
  let response = await api.post(endpoints.pessoa.createPessoa, pessoa);
  return response;
}

async function updatePessoaByBot(pessoa, telegramId) {
  let response = await api.post(
    endpoints.pessoa.updatePessoa(telegramId),
    pessoa
  );
  return response;
}

async function removeTelegramIdPessoaByBot(pessoa, telegramId) {
  let response = await api.delete(
    endpoints.pessoa.removeTelegramId(telegramId),
    pessoa
  );
  return response;
}

module.exports = {
  getPessoaByPhone,
  getPessoaByCPF,
  getPessoaByIdent,
  getPessoaByTelegram,
  createPessoaByBot,
  updatePessoaByBot,
  removeTelegramIdPessoaByBot,
};
