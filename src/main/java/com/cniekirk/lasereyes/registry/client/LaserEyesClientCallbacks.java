package com.cniekirk.lasereyes.registry.client;

import com.cniekirk.lasereyes.client.util.RaycastingUtils;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;

public class LaserEyesClientCallbacks {

    // TODO: Adjust according to performance impact
    private final static long RAYCAST_CHECK_INTERVAL = 80;

    private static KeyBinding keyBinding;
    private static long lastRaycastCheck;
    private static BlockPos blockPos;

    public static void init() {

        lastRaycastCheck = System.currentTimeMillis();

        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.lasereyes.keybinding",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "key.category.g"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed()) {
                // Don't perform raycast too often, 16ms is every frame at 60fps
                if (System.currentTimeMillis() - lastRaycastCheck > RAYCAST_CHECK_INTERVAL) {
                    // Send packet to server, with pixel raycast result
                    HitResult hitResult = RaycastingUtils.pixelRaycast(client);

                    if (hitResult != null) {
                        if (hitResult instanceof BlockHitResult) {
                            System.out.println("Is a block!");
                            BlockHitResult blockHitResult = (BlockHitResult) hitResult;

                            BlockPos pos = blockHitResult.getBlockPos();
                            if (blockPos != null) {
                                if (!((pos.getX() == blockPos.getX()) && (pos.getY() == blockPos.getY())
                                        && (pos.getZ() == blockPos.getZ()))) {
                                    PacketByteBuf blockPosBuffer = new PacketByteBuf(Unpooled.buffer());
                                    blockPosBuffer.writeBlockPos(blockHitResult.getBlockPos());
                                    // Send to server
                                    ClientPlayNetworking.send(LaserEyesNetworking.PLAYER_EYES_POSITION_PACKET, blockPosBuffer);
                                    blockPos = pos;
                                }
                            } else {
                                PacketByteBuf blockPosBuffer = new PacketByteBuf(Unpooled.buffer());
                                blockPosBuffer.writeBlockPos(blockHitResult.getBlockPos());
                                // Send to server
                                ClientPlayNetworking.send(LaserEyesNetworking.PLAYER_EYES_POSITION_PACKET, blockPosBuffer);
                                blockPos = pos;
                            }
                        } else if (hitResult instanceof EntityHitResult) {
                            EntityHitResult entityHitResult = (EntityHitResult) hitResult;
                            PacketByteBuf damageEntityBuffer = new PacketByteBuf(Unpooled.buffer());
                            damageEntityBuffer.writeInt(entityHitResult.getEntity().getEntityId());
                            // Send to server
                            ClientPlayNetworking.send(LaserEyesNetworking.PLAYER_EYES_ENTITY_PACKET, damageEntityBuffer);
                        }
                    }
                }
            }
        });

    }

}
