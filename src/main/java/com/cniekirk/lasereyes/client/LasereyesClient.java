package com.cniekirk.lasereyes.client;

import com.cniekirk.lasereyes.registry.client.LaserEyesClientCallbacks;
import com.cniekirk.lasereyes.registry.client.LaserEyesNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.util.Identifier;

public class LasereyesClient implements ClientModInitializer {

    private static final String ID = "lasereyes";

    public static Identifier identifier(final String path) {
        return new Identifier(ID, path);
    }

    @Override
    public void onInitializeClient() {
        LaserEyesNetworking.init();
        LaserEyesClientCallbacks.init();
    }
}
