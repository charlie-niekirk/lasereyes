package com.cniekirk.lasereyes.common;

import net.minecraft.world.World;

public class ServerState {

    private static ServerState INSTANCE;

    private final World world;
    private boolean enabled;

    private ServerState(final World world) {
        this.world = world;
    }

    public static ServerState getInstance(final World world) {
        if (INSTANCE == null) {
            synchronized (ServerState.class) {
                INSTANCE = new ServerState(world);
            }
        }
        return INSTANCE;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() { return enabled; }

}
