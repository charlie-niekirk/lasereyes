package com.cniekirk.lasereyes.registry.common;

import com.cniekirk.lasereyes.common.ServerState;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class LaserEyesWorldListener {

    public static void init(final ServerState serverState) {

        ServerTickEvents.START_SERVER_TICK.register(server -> {

        });

    }

}
