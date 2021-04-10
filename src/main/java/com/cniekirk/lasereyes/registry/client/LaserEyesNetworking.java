package com.cniekirk.lasereyes.registry.client;

import com.cniekirk.lasereyes.client.LasereyesClient;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

public class LaserEyesNetworking {

    public static Identifier PLAYER_EYES_ACTIVE_PACKET = LasereyesClient.identifier("active");
    public static Identifier PLAYER_EYES_POSITION_PACKET = LasereyesClient.identifier("position");
    public static Identifier PLAYER_EYES_ENTITY_PACKET = LasereyesClient.identifier("entity");

    public static void init() {

        ClientPlayNetworking.registerGlobalReceiver(PLAYER_EYES_ACTIVE_PACKET,
                (minecraftClient, clientPlayNetworkHandler, packetByteBuf, packetSender) -> {
            System.out.println("Eyes active received");
        });

    }

}
