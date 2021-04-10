package com.cniekirk.lasereyes.registry.common;

import com.cniekirk.lasereyes.common.ServerState;
import com.cniekirk.lasereyes.registry.client.LaserEyesNetworking;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class LaserEyesCommands {

    private static int listenForLasers(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

        ServerState state = ServerState.getInstance(context.getSource().getWorld());
        state.setEnabled(true);

        LaserEyesWorldListener.init(state);

        context.getSource().getMinecraftServer().getPlayerManager().getPlayerList().forEach(serverPlayerEntity ->
                ServerPlayNetworking.send(serverPlayerEntity, LaserEyesNetworking.PLAYER_EYES_ACTIVE_PACKET,
                new PacketByteBuf(Unpooled.buffer())));

        context.getSource().getPlayer().sendMessage(new TranslatableText("text.command.lasereyes.start.success")
                .formatted(Formatting.GREEN), true);

        return 1;

    }

    public static void init() {

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {

            LiteralCommandNode<ServerCommandSource> laserRoot = CommandManager.literal("laser").build();

            LiteralCommandNode<ServerCommandSource> enableLasers =
                    CommandManager.literal("start")
                            .requires((serverCommandSource) -> serverCommandSource.hasPermissionLevel(2))
                            .executes(LaserEyesCommands::listenForLasers)
                            .build();

            laserRoot.addChild(enableLasers);

            dispatcher.getRoot().addChild(laserRoot);

        });

    }

}
