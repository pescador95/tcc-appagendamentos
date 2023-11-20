import app.core.utils.Contexto;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;

import static app.core.utils.BasicFunctions.log;

@QuarkusMain
public class TCCAgendamentoApiApplication {

    public static void main(String... args) {

        Quarkus.run(args);

        logMemoryInfo();
    }

    private static void logMemoryInfo() {
        Runtime runtime = Runtime.getRuntime();

        final NumberFormat format = NumberFormat.getInstance();

        final long maxMemory = runtime.maxMemory();
        final long allocatedMemory = runtime.totalMemory();
        final long freeMemory = runtime.freeMemory();
        final long mb = 1024 * 1024;
        final String mega = " MB";

        String telegramApiUrl = System.getenv("TELEGRAM_API_URL");
        String whatsappApiUrl = System.getenv("WHATSAPP_API_URL");
        String databaseUrl = System.getenv("DATABASE_URL");
        String scheduler = System.getenv("FILAS_ATIVAS");

        log("=================================================================\n");
        log("TCC Agendamentos - Quarkus");
        log("Conectado no Banco de Dados: " + databaseUrl);
        log("Filas e Schedulers de Lembrete de Agendamentos: " + scheduler);
        log("Telegram API URL: " + telegramApiUrl);
        log("Whatsapp API URL: " + whatsappApiUrl);
        log("=================================================================\n");

        log("=================================================================");
        log("========================== Memory Info ==========================");
        log("Free memory: " + format.format(freeMemory / mb) + mega);
        log("Allocated memory: " + format.format(allocatedMemory / mb) + mega);
        log("Max memory: " + format.format(maxMemory / mb) + mega);
        log("Total free memory: " + format.format((freeMemory + (maxMemory - allocatedMemory)) / mb) + mega);
        log("=================================================================");
        log("Using Quarkus 2.16.6 Final");
        log("Started in : "
                + Contexto.dataHoraContexto().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        log("=================================================================\n");
        log("   _                        _           ___          _ _ \n" +
                "  /_\\   __ _  ___ _ __   __| | __ _    / __\\_ _  ___(_) |\n" +
                " //_\\\\ / _` |/ _ \\ '_ \\ / _` |/ _` |  / _\\/ _` |/ __| | |\n" +
                "/  _  \\ (_| |  __/ | | | (_| | (_| | / / | (_| | (__| | |\n" +
                "\\_/ \\_/\\__, |\\___|_| |_|\\__,_|\\__,_| \\/   \\__,_|\\___|_|_|\n" +
                "       |___/                                             ");
        log("=================================================================");
        log("=================================================================\n");
    }
}