package com.cniekirk.lasereyes.client;

public class ClientState {

    private static ClientState INSTANCE;

    private boolean enabled;

    public static ClientState getInstance() {
        if (INSTANCE == null) {
            synchronized (ClientState.class) {
                INSTANCE = new ClientState();
            }
        }
        return INSTANCE;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() { return enabled; }

}
