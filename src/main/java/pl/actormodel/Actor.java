package pl.actormodel;

import java.util.function.Consumer;

interface Actor {

    void receive(Object message);
}
