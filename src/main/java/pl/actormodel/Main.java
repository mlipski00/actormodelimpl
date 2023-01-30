package pl.actormodel;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private final static ExecutorService executor = Executors.newFixedThreadPool(10);
    final static CountDownLatch countDownLatch = new CountDownLatch(1);
    final static ActorSystem system = new ActorSystem();

    public static void main(String[] args) throws InterruptedException {
        var actorRef = system.actorOf(ActorInstance.class);
        var actorRef2 = system.actorOf(ActorInstance2.class);
        for (int i = 0; i < 100; i++) {
            int finalI = i;

            executor.submit(() -> {
                        System.out.println("hello number: " + finalI + " Sending thread: " + Thread.currentThread().getName());
                        actorRef.sendMessage(finalI);
                        actorRef2.sendMessage(finalI);
                    }
            );
        }

        countDownLatch.await();
    }
}

class ActorInstance2 implements Actor {

    private static int iterator = 0;

    @Override
    public void receive(Object message) {
        System.out.println("ActorInstance2 Received message: " + message + " Receiver thread: " + Thread.currentThread().getName());
        ++iterator;
        if (iterator == 100) {
            var resultActor = Main.system.actorOf(ActorResult.class);
            resultActor.sendMessage("ActorInstance2 otrzymał 100 wiadomości");
        }
    }
}

class ActorInstance implements Actor {

    private static int iterator = 0;

    @Override
    public void receive(Object message) {
        System.out.println("ActorInstance Received message: " + message + " Receiver thread: " + Thread.currentThread().getName());
        ++iterator;
        if (iterator == 100) {
            var resultActor = Main.system.actorOf(ActorResult.class);
            resultActor.sendMessage("ActorInstance otrzymał 100 wiadomości");
        }
    }
}

class ActorResult implements Actor {

    @Override
    public void receive(Object message) {
        System.out.println("ActorResult Received message: " + message + " Receiver thread: " + Thread.currentThread().getName());
    }
}