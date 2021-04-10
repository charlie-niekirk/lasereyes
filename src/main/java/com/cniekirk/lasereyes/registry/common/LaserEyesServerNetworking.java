package com.cniekirk.lasereyes.registry.common;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.explosion.Explosion;

import static com.cniekirk.lasereyes.registry.client.LaserEyesNetworking.PLAYER_EYES_ENTITY_PACKET;
import static com.cniekirk.lasereyes.registry.client.LaserEyesNetworking.PLAYER_EYES_POSITION_PACKET;

public class LaserEyesServerNetworking {

    public static void init() {

        ServerPlayNetworking.registerGlobalReceiver(PLAYER_EYES_ENTITY_PACKET,
                (server, player, handler, buf, responseSender) -> {
            int entityId = buf.readInt();
            Entity entity = server.getOverworld().getEntityById(entityId);
            if (entity != null) {
                if (!entity.isOnFire()) {
                    entity.setOnFireFor(1);
                }
//                entity.damage(DamageSource.ON_FIRE, 2);
//                player.getEntityWorld().createExplosion(entity, entity.getX(), entity.getY(), entity.getZ(),
//                        0.0f, true, Explosion.DestructionType.BREAK);
                entity.damage(DamageSource.LIGHTNING_BOLT, 3.0F);
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(PLAYER_EYES_POSITION_PACKET,
                (server, player, handler, buf, responseSender) -> {
            BlockPos target = buf.readBlockPos();
            BlockState blockState = player.getEntityWorld().getBlockState(target);
            if (blockState.getBlock() instanceof LeavesBlock ||
                    blockState.getBlock() instanceof GrassBlock ||
                blockState.getBlock() instanceof FlowerBlock) {
                player.getEntityWorld().setBlockState(target.add(1, 1, 1), Blocks.FIRE.getDefaultState());
            }
            TntEntity entity = new TntEntity(player.getEntityWorld(), target.getX(), target.getY(), target.getZ(),
                    player);
            player.getEntityWorld().createExplosion(entity, target.getX(), target.getY(), target.getZ(),
                    0.0f, true, Explosion.DestructionType.BREAK);
            player.getEntityWorld().breakBlock(target, true);
        });

    }

}
