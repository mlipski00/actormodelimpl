package pl.actormodel;

import java.lang.reflect.InvocationTargetException;

public class ActorCell {
    private final Dispatcher dispatcher;
    private final Mailbox mailBox;
    private final Actor receive;

    public Dispatcher getDispatcher() {
        return dispatcher;
    }

    public Mailbox getMailBox() {
        return mailBox;
    }

    public ActorCell(Class<? extends Actor> clazz, Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
        this.mailBox = new Mailbox(new UnboundedMessageQueue());
        try {
            receive = clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        mailBox.setActor(this);
    }


    void receiveMessage(Envelope messageHandle) {
        receive.receive(messageHandle.message());
    }

    void invoke(Envelope messageHandle) {
        receiveMessage(messageHandle);
    }

    void sendMessage(Object message) {
        dispatcher.dispatch(this, new Envelope(message));
    }
}