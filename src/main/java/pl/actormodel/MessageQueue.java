package pl.actormodel;

interface MessageQueue {

    void enqueue(ActorCell receiver, Envelope handle);

    Envelope dequeue();

    int numberOfMessages();

    boolean hasMessages();

    void cleanUp(ActorCell owner, MessageQueue deadLetters);
}
