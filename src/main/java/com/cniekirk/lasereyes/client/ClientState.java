package com.cniekirk.lasereyes.client;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientState {

    private static ClientState INSTANCE;
    private static final Queue<Integer> activeEyes = new ConcurrentLinkedQueue<>();

    public static ClientState getInstance() {
        if (INSTANCE == null) {
            synchronized (ClientState.class) {
                INSTANCE = new ClientState();
            }
        }
        return INSTANCE;
    }

    public void playerEyesActive(final int playerId) {
        activeEyes.add(playerId);
    }

    public void playerEyesInactive(final int playerId) {
        activeEyes.remove(playerId);
    }

    public Queue<Integer> getActiveEyes() { return activeEyes; }

}
