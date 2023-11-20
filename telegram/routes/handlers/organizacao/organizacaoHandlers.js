const listarOrganizacoesByAgendamentoBot = require("../../webhooks/organizacao/organizacaoWebhooks");

async function montarOrganizacoesByAgendamento(
  msg,
  bot,
  pessoa,
  listaOpcoesComandos
) {
  const organizacoes = await listarOrganizacoesByAgendamentoBot();

  if (organizacoes.data.length === 0) {
    let message = `Não há nenhuma organização disponível para atendimento.`;
    bot.sendMessage(msg.chat.id, message).then(() => {
      return null;
    });
  } else {
    bot.removeTextListener(/^(.*)$/);
    let message = `Aqui estão as nossas organizações disponíveis.\n Selecione qual você deseja agendar o seu atendimento:\n\n`;

    const opcoes = organizacoes.data.map((organizacao) => {
      return { id: organizacao.id, nome: organizacao.nome, celular: organizacao.celular, zoneId: organizacao.zoneId, timeZoneOffset: organizacao.timeZoneOffset };
    });

    const keyboard = opcoes.map((opcao) => [opcao.nome]);

    const replyMarkup = {
      keyboard,
      one_time_keyboard: true,
      resize_keyboard: true,
    };

    const opcoesMarkup = {
      reply_markup: JSON.stringify(replyMarkup),
    };

    bot.sendMessage(msg.chat.id, message, opcoesMarkup);

    return new Promise((resolve) => {
      bot.onText(/^(.*)$/, async (msg, match) => {
        const selectedOptionLabel = match[1];

        const selectedOption = opcoes.find(
          (opcao) => opcao.nome === selectedOptionLabel
        );

        if (selectedOption) {
          resolve(selectedOption);
        } else {
          resolve(null);
        }
      });
    });
  }
}

module.exports = montarOrganizacoesByAgendamento;
