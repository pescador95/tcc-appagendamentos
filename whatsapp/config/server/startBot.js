async function startBot() {
  try {
    console.log("Autenticação bem-sucedida. Iniciando o bot...");
  } catch (error) {
    console.error("Falha na autenticação:", error.message);
  }
}

startBot();
