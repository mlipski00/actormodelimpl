package pl.actormodel;

import java.time.Duration;
import java.util.concurrent.ForkJoinPool;

public record Dispatcher(ForkJoinPool executorService) {

    public Duration getThroughputDeadlineTime() {
        return Duration.ZERO;
    }

    public boolean isThroughputDeadlineTimeDefined() {
        return true;
    }

    public int getThroughput() {
        return 10;
    }

    void dispatch(ActorCell receiver, Envelope invocation) {
        Mailbox mbox = receiver.getMailBox();
        mbox.enqueue(receiver, invocation);
        registerForExecution(mbox, true, false);
    }

    /**
     * mailbox (forkjointask) jest przekazywany do wykonania do forkjoinpool gdy żaden wątek na nim nie pracuje i posiada wiadomości w kolejce
     */
    void registerForExecution(Mailbox mbox, Boolean hasMessageHint, Boolean hasSystemMessageHint) {
        if (mbox.canBeScheduled() && mbox.isIdle()) {
            if (mbox.setAsScheduled()) {
                executorService.execute(mbox);
            }
        }
    }
}
