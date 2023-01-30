package pl.actormodel;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class ActorSystem {
    Dispatcher dispatcher = new Dispatcher(new ForkJoinPool(Runtime.getRuntime().availableProcessors()));

    boolean awaitTermination(int value, TimeUnit unit) {
        try {
            return dispatcher.executorService().awaitTermination(value, unit);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    LocalActorRef actorOf(Class<? extends Actor> clazz) {
        return new LocalActorRef(clazz, dispatcher);
    }
}
