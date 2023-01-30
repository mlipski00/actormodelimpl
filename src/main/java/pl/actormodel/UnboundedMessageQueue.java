package pl.actormodel;

import java.util.concurrent.ConcurrentLinkedQueue;

public class UnboundedMessageQueue extends ConcurrentLinkedQueue<Envelope> implements MessageQueue {

    @Override
    public void enqueue(ActorCell receiver, Envelope handle) {
        add(handle);
    }

    @Override
    public Envelope dequeue() {
        return poll();
    }

    @Override
    public int numberOfMessages() {
        return size();
    }

    @Override
    public boolean hasMessages() {
        return !isEmpty();
    }

    @Override
    public void cleanUp(ActorCell owner, MessageQueue deadLetters) {
        if (hasMessages()) {
            var envelope = dequeue();
            while (envelope != null) {
                deadLetters.enqueue(owner, envelope);
                envelope = dequeue();
            }
        }
    }
}
