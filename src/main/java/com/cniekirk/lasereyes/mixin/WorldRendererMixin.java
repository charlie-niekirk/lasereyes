package com.cniekirk.lasereyes.mixin;

import com.cniekirk.lasereyes.Lasereyes;
import com.cniekirk.lasereyes.client.ClientState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * All rendering logic for the lasers goes here
 */
@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    // Required fields from WorldRenderer
    @Shadow @Final private MinecraftClient client;
    @Shadow @Final private BufferBuilderStorage bufferBuilders;

    private static final Identifier LASER_BEAM_TEXTURE = Lasereyes.identifier("textures/red.png");
    private static final RenderLayer LASER_BEAM_LAYER = RenderLayer.getEntityTranslucent(LASER_BEAM_TEXTURE);
    // Best that I've got so far
    private static final float EYE_BOTTOM = 2.0F * (1.0F / 16.0F);
    private static final float EYE_TOP = 0F * (1.0F / 16.0F);
    private static final float EYE_START = 1.0F / 16.0F;
    private static final float EYE_END = 3.0F * (1.0F / 16.0F);
    private long lastFrameNanoTime = -1;

    /**
     * Mixin to the render method when all world rendering setup is complete
     *
     * @param matrices the {@link MatrixStack} that we'll manipulate
     * @param uselessTickDelta heh?
     * @param limitTime unused
     * @param renderBlockOutline unused
     * @param camera may use this to fix looking up/down issue otherwise unused
     * @param gameRenderer unused
     * @param lightmapTextureManager unused
     * @param matrix4f unused
     * @param ci no need to alter control flow, unused
     */
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BufferBuilderStorage;getEntityVertexConsumers()Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;"), method = "render")
    void active_eyes_render(MatrixStack matrices, float uselessTickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo ci) {

        if (lastFrameNanoTime == -1) {
            lastFrameNanoTime = System.nanoTime();
        }

        // Found this in another mods source, not really sure why I've used it here, can probably be replaced with 'uselessTickDelta'
        float tickDelta = Math.min(0.375F, (System.nanoTime() - lastFrameNanoTime) / 4_000_0000F);

        // Capture current render state
        ClientState state = ClientState.getInstance();

        state.getActiveEyes().forEach(entityId -> {
            PlayerEntity entity = (PlayerEntity) client.world.getEntityById(entityId);
            if (entity != null) {
                if (entity.distanceTo(client.cameraEntity) <= 200) {

                    Vec3d pos = entity.getPos().add(0, entity.getEyeHeight(entity.getPose()), 0);
                    Vec3d rotation = entity.getRotationVec(tickDelta);
                    Vec3d reachLimit = pos.add(rotation.x * 200, rotation.y * 200, rotation.z * 200);

                    float dx = (float) (reachLimit.getX() - pos.x);
                    float dy = (float) (reachLimit.getY() - pos.y);
                    float dz = (float) (reachLimit.getZ() - pos.z);

                    float f = MathHelper.sqrt(dx * dx + dz * dz);
                    float g = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);

                    matrices.push();

                    // TODO: Fix looking up and down (Y needs changing here)
                    matrices.translate(entity.getPos().x - client.player.getPos().x, entity.getPos().y - client.player.getPos().y, entity.getPos().z - client.player.getPos().z);

                    // Move the lasers very slightly in front onf the players' eyes
                    matrices.translate(rotation.x * 0.15, rotation.y * 0.15, rotation.z * 0.15);
                    matrices.multiply(Vector3f.POSITIVE_Y.getRadialQuaternion((float)(-Math.atan2(dz, dx)) - 1.5707964F));
                    matrices.multiply(Vector3f.POSITIVE_X.getRadialQuaternion((float)(-Math.atan2(f, dy)) - 1.5707964F));
                    VertexConsumer vertexConsumer = bufferBuilders.getEntityVertexConsumers().getBuffer(LASER_BEAM_LAYER);
                    float h = 0.0F - (entity.age + tickDelta) * 0.01F;
                    float i = g / 32.0F - (entity.age + tickDelta) * 0.01F;
                    float k = 0.0F;
                    float l = 0.75F;
                    float m = 0.0F;
                    MatrixStack.Entry entry = matrices.peek();
                    Matrix4f matrix4 = entry.getModel();
                    Matrix3f matrix3f = entry.getNormal();
                    float q = (float) 1 / 4.0F;

                    // TODO: Put in some sort of loop or something it's pretty messy
                    // TODO: Add laser noise and dynamically changing translucency
                    // Right eye
                    vertexConsumer.vertex(matrix4, EYE_END, EYE_TOP, 0F).color(255, 0, 0, 255).texture(m, h).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f, 0.0F, -1.0F, 0.0F).next();
                    vertexConsumer.vertex(matrix4, EYE_END, EYE_TOP, g).color(255, 0, 0, 255).texture(m, i).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f, 0.0F, -1.0F, 0.0F).next();
                    vertexConsumer.vertex(matrix4, EYE_END, EYE_BOTTOM, g).color(255, 0, 0, 255).texture(q, i).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f, 0.0F, -1.0F, 0.0F).next();
                    vertexConsumer.vertex(matrix4, EYE_END, EYE_BOTTOM, 0F).color(255, 0, 0, 255).texture(q, h).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f, 0.0F, -1.0F, 0.0F).next();

                    vertexConsumer.vertex(matrix4, EYE_START, EYE_TOP, 0F).color(255, 0, 0, 255).texture(m, h).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f, 0.0F, -1.0F, 0.0F).next();
                    vertexConsumer.vertex(matrix4, EYE_START, EYE_TOP, g).color(255, 0, 0, 255).texture(m, i).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f, 0.0F, -1.0F, 0.0F).next();
                    vertexConsumer.vertex(matrix4, EYE_START, EYE_BOTTOM, g).color(255, 0, 0, 255).texture(q, i).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f, 0.0F, -1.0F, 0.0F).next();
                    vertexConsumer.vertex(matrix4, EYE_START, EYE_BOTTOM, 0F).color(255, 0, 0, 255).texture(q, h).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f, 0.0F, -1.0F, 0.0F).next();

                    vertexConsumer.vertex(matrix4, EYE_START, EYE_TOP, 0F).color(255, 0, 0, 255).texture(m, h).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f, 0.0F, -1.0F, 0.0F).next();
                    vertexConsumer.vertex(matrix4, EYE_START, EYE_TOP, g).color(255, 0, 0, 255).texture(m, i).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f, 0.0F, -1.0F, 0.0F).next();
                    vertexConsumer.vertex(matrix4, EYE_END, EYE_TOP, g).color(255, 0, 0, 255).texture(q, i).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f, 0.0F, -1.0F, 0.0F).next();
                    vertexConsumer.vertex(matrix4, EYE_END, EYE_TOP, 0F).color(255, 0, 0, 255).texture(q, h).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f, 0.0F, -1.0F, 0.0F).next();

                    vertexConsumer.vertex(matrix4, EYE_START, EYE_BOTTOM, 0F).color(255, 0, 0, 255).texture(m, h).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f, 0.0F, -1.0F, 0.0F).next();
                    vertexConsumer.vertex(matrix4, EYE_START, EYE_BOTTOM, g).color(255, 0, 0, 255).texture(m, i).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f, 0.0F, -1.0F, 0.0F).next();
                    vertexConsumer.vertex(matrix4, EYE_END, EYE_BOTTOM, g).color(255, 0, 0, 255).texture(q, i).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f, 0.0F, -1.0F, 0.0F).next();
                    vertexConsumer.vertex(matrix4, EYE_END, EYE_BOTTOM, 0F).color(255, 0, 0, 255).texture(q, h).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f, 0.0F, -1.0F, 0.0F).next();

                    // Left Eye
                    vertexConsumer.vertex(matrix4, -EYE_END, EYE_TOP, 0F).color(255, 0, 0, 255).texture(m, h).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f, 0.0F, -1.0F, 0.0F).next();
                    vertexConsumer.vertex(matrix4, -EYE_END, EYE_TOP, g).color(255, 0, 0, 255).texture(m, i).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f, 0.0F, -1.0F, 0.0F).next();
                    vertexConsumer.vertex(matrix4, -EYE_END, EYE_BOTTOM, g).color(255, 0, 0, 255).texture(q, i).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f, 0.0F, -1.0F, 0.0F).next();
                    vertexConsumer.vertex(matrix4, -EYE_END, EYE_BOTTOM, 0F).color(255, 0, 0, 255).texture(q, h).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f, 0.0F, -1.0F, 0.0F).next();

                    vertexConsumer.vertex(matrix4, -EYE_START, EYE_TOP, 0F).color(255, 0, 0, 255).texture(m, h).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f, 0.0F, -1.0F, 0.0F).next();
                    vertexConsumer.vertex(matrix4, -EYE_START, EYE_TOP, g).color(255, 0, 0, 255).texture(m, i).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f, 0.0F, -1.0F, 0.0F).next();
                    vertexConsumer.vertex(matrix4, -EYE_START, EYE_BOTTOM, g).color(255, 0, 0, 255).texture(q, i).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f, 0.0F, -1.0F, 0.0F).next();
                    vertexConsumer.vertex(matrix4, -EYE_START, EYE_BOTTOM, 0F).color(255, 0, 0, 255).texture(q, h).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f, 0.0F, -1.0F, 0.0F).next();

                    vertexConsumer.vertex(matrix4, -EYE_START, EYE_TOP, 0F).color(255, 0, 0, 255).texture(m, h).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f, 0.0F, -1.0F, 0.0F).next();
                    vertexConsumer.vertex(matrix4, -EYE_START, EYE_TOP, g).color(255, 0, 0, 255).texture(m, i).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f, 0.0F, -1.0F, 0.0F).next();
                    vertexConsumer.vertex(matrix4, -EYE_END, EYE_TOP, g).color(255, 0, 0, 255).texture(q, i).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f, 0.0F, -1.0F, 0.0F).next();
                    vertexConsumer.vertex(matrix4, -EYE_END, EYE_TOP, 0F).color(255, 0, 0, 255).texture(q, h).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f, 0.0F, -1.0F, 0.0F).next();

                    vertexConsumer.vertex(matrix4, -EYE_START, EYE_BOTTOM, 0F).color(255, 0, 0, 255).texture(m, h).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f, 0.0F, -1.0F, 0.0F).next();
                    vertexConsumer.vertex(matrix4, -EYE_START, EYE_BOTTOM, g).color(255, 0, 0, 255).texture(m, i).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f, 0.0F, -1.0F, 0.0F).next();
                    vertexConsumer.vertex(matrix4, -EYE_END, EYE_BOTTOM, g).color(255, 0, 0, 255).texture(q, i).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f, 0.0F, -1.0F, 0.0F).next();
                    vertexConsumer.vertex(matrix4, -EYE_END, EYE_BOTTOM, 0F).color(255, 0, 0, 255).texture(q, h).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix3f, 0.0F, -1.0F, 0.0F).next();

                    matrices.pop();

                }
            }
        });

    }

}
