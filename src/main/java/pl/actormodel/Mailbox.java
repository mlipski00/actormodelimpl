package pl.actormodel;

import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class Mailbox extends ForkJoinTask<Void> {
    private final MessageQueue messageQueue;

    public Mailbox(MessageQueue messageQueue) {
        this.messageQueue = messageQueue;
    }

    private final AtomicBoolean atomicIdle = new AtomicBoolean(true);

    public boolean isIdle() {
        return atomicIdle.get();
    }

    boolean setAsScheduled() {
        return atomicIdle.compareAndSet(true, false);
//        synchronized (this) {
//            if (idle) {
//                idle = false;
//                return true;
//            } else {
//                return false;
//            }
//        }
    }

//    private boolean setAsIdle() {
//        synchronized (this) {
//            if (!idle) {
//                idle = true;
//                return true;
//            } else {
//                return false;
//            }
//        }
//    }


    boolean canBeScheduled() {
        return messageQueue.hasMessages();
    }

    private ActorCell actor;
    private Dispatcher dispatcher;

    void setActor(ActorCell cell) {
        actor = cell;
        dispatcher = actor.getDispatcher();
    }

    boolean shouldProcessMessage = true;

    void enqueue(ActorCell receiver, Envelope msg) {
        messageQueue.enqueue(receiver, msg);
    }

    Envelope dequeue() {
        return messageQueue.dequeue();
    }

    void processMailbox(int left, long deadlineNs) {
        if (shouldProcessMessage) {
            var next = dequeue();
            if (next != null) {

                actor.invoke(next);

                if (Thread.interrupted()) {
                    throw new RuntimeException("Interrupted while processing actor messages");
                }

                if ((left > 1) && (!dispatcher.isThroughputDeadlineTimeDefined() || (deadlineNs - System.nanoTime()) < 0))
                    processMailbox(left - 1, deadlineNs);
            }
        }
    }

    void run() {
        long deadlineNs = 0L;
        if (dispatcher.isThroughputDeadlineTimeDefined()) {
            deadlineNs = System.nanoTime() + dispatcher.getThroughputDeadlineTime().toNanos();
        }
        processMailbox(Math.max(dispatcher.getThroughput(), 1), deadlineNs);
    }

    @Override
    public Void getRawResult() {
        return null;
    }

    @Override
    protected void setRawResult(Void value) {

    }

    @Override
    protected boolean exec() {
        try {
            run(); // kod wywołujący callback na danym aktorze
        } finally {
            atomicIdle.compareAndSet(false, true); // sygnalizujemy zakończenie pracy na danym aktorze
            dispatcher.registerForExecution(this, false, false); // ponownie rejestrujemy mailbox do egzekucji na forkjoinpoolu
        }
        return false; //this is critical to tell forkjoinpool that the task is not completed.
    }
}


