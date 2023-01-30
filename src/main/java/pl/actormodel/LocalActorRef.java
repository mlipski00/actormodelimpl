package pl.actormodel;

interface ActorRef {
    void sendMessage(Object message);
}

public class LocalActorRef implements ActorRef {

    private final ActorCell actorCell;

    public LocalActorRef(Class<? extends Actor> clazz, Dispatcher dispatcher) {
        this.actorCell = new ActorCell(clazz, dispatcher);
    }


    @Override
    public void sendMessage(Object message) {
        actorCell.sendMessage(message);
    }
}
