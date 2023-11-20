package app.core.trace;

import app.core.utils.BasicFunctions;
import app.core.utils.Contexto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static app.core.utils.BasicFunctions.log;

public class Tracer {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface RastrearExecucaoMetodo {
        String classe() default "";
        String metodo() default "";
    }

    private static final Tracer TRACER = new Tracer();

    public static void runThreadsAndLogTracers(List<Invoker> invokers) {

        executarMetodosComRastreamento(invokers);

    }

    public static void executarMetodosComRastreamento(List<Invoker> invokers) {
        invokers.sort(Comparator.comparing(Invoker::getOrder));

        invokers.forEach(invoker -> {
            Object obj = invoker.getObjectToInvoke();
            Class<?> clazz = obj.getClass();

            List<Method> annotatedMethods = Arrays.stream(clazz.getDeclaredMethods())
                    .filter(method -> method.isAnnotationPresent(RastrearExecucaoMetodo.class) && isMethodInClass(method, clazz))
                    .toList();

            annotatedMethods.forEach(method -> rastrearExecucaoMetodo(obj, clazz, method));
        });
    }

    private String getMethodInfo() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length > 3) {
            StackTraceElement element = stackTrace[3];
            String className = element.getClassName();
            String methodName = element.getMethodName();
            return className + " - " + methodName;
        }
        return "Unknown Method";
    }

    public static void logMethodExecution(String methodInfo) {
        String startTimeKey = methodInfo + "-START-TIME";
        System.setProperty(startTimeKey, Contexto.dataHoraContextoToString());
        log("\n=================================================================\n");
        log(methodInfo + " - Executing at " + Contexto.dataHoraContextoToString() + "\n");
    }

    public static void logMethodCompletion(String methodInfo) {
        String startTimeKey = methodInfo + "-START-TIME";

        String startTimeStr = System.getProperty(startTimeKey);

        if (BasicFunctions.isNotEmpty(startTimeStr)) {
            LocalDateTime startTime = LocalDateTime.parse(startTimeStr, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSS"));
            LocalDateTime endTime = Contexto.dataHoraContexto();

            Duration duration = Duration.between(startTime, endTime);
            long seconds = duration.getSeconds();
            long millis = duration.toMillis();

            log("\n" + methodInfo + " - Completed at " + Contexto.dataHoraContextoToString());
            log("\n" + methodInfo + " - Execution time: " + seconds + " seconds and " + millis + " milliseconds");
            log("\n=================================================================\n");
        } else {
            log(methodInfo + " - Start time not found. \n");
        }
    }

    public static void rastrearExecucaoMetodo(Object obj, Class<?> clazz, Method method) {
        String className = clazz.getName();
        String methodName = method.getName();
        String methodInfo = "Class: " + className + " - " + "Method: " + methodName + "()";

        try {
            logMethodExecution(methodInfo);
            method.invoke(obj);
            logMethodCompletion(methodInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isMethodInClass(Method method, Class<?> clazz) {
        String methodName = method.getName();
        return Arrays.stream(clazz.getDeclaredMethods()).anyMatch(m -> m.getName().equals(methodName));
    }
}
