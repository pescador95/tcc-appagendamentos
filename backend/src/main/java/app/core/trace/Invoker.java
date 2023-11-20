package app.core.trace;

import app.core.thread.LembreteThread;

import java.util.ArrayList;
import java.util.List;

import static app.core.trace.Tracer.runThreadsAndLogTracers;

public class Invoker {

    private String methodToInvoke;
    private Object objectToInvoke;
    private Long order;
    public Invoker(String methodToInvoke, Object objectToInvoke, Long order) {
        this.methodToInvoke = methodToInvoke;
        this.objectToInvoke = objectToInvoke;
        this.order = order;
    }

    public String getMethodToInvoke() {
        return methodToInvoke;
    }

    public void setMethodToInvoke(String methodToInvoke) {
        this.methodToInvoke = methodToInvoke;
    }

    public Object getObjectToInvoke() {
        return objectToInvoke;
    }

    public void setObjectToInvoke(Object objectToInvoke) {
        this.objectToInvoke = objectToInvoke;
    }

    public Long getOrder() {
        return order;
    }

    public void setOrder(Long order) {
        this.order = order;
    }

    public static void invokerFilaLembreteAgendamentos(){

        List<Invoker>  invokers = new ArrayList<>();

        invokers.add(new Invoker("FilaLembreteAgendamentos", new LembreteThread(), 1L));

        runThreadsAndLogTracers(invokers);

    }
}
