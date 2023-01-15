package pl.kielce.tu.mergeservice;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

public class Printer {
    private static volatile Printer instance;
    private static Object mutex = new Object();
    private final LinkedBlockingQueue<String> queue;

    private Printer() {
        this.queue = new LinkedBlockingQueue<String>();
    }

    public static Printer getInstance() {
        Printer result = instance;
        if (result == null) {
            synchronized (mutex) {
                result = instance;
                if (result == null)
                    instance = result = new Printer();
            }
        }
        return result;
    }

    public Printer enqueueLogMessage(String... messages) {
        Arrays.stream(messages).forEach(message -> this.queue.add(message));

        return this;
    }

    public void printMessages() {
        synchronized (this.queue) {
            Iterator<String> iterator = this.queue.iterator();
            while(iterator.hasNext()) {
                System.out.println(iterator.next());
            }
            this.queue.clear();
        }
    }
}
