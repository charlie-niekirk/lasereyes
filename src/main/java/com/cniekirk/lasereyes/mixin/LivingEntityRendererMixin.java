package com.cniekirk.lasereyes.mixin;

import com.cniekirk.lasereyes.Lasereyes;
import dev.monarkhes.myron.api.Myron;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {

    @Inject(at = @At(target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V",
            value = "INVOKE", shift = At.Shift.AFTER), method = "render")
    void onRender(T livingEntity, float f, float g, MatrixStack matrixStack,
                  VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
//        VertexConsumer consumer;
//
//        matrixStack.push();
//
//        BakedModel eyeModel = Myron.getModel(Lasereyes.identifier("models/misc/laser_eye"));
//        float eyeHeight = livingEntity.getEyeHeight(livingEntity.getPose());
//
//        livingEntity.get
//
//        livingEntity.getRotationVector()
//
//        eyeModel.getQuads(null, null, livingEntity.getRandom()).forEach(bakedQuad -> {
//            consumer.;
//        });
//
//        matrixStack.pop();

    }

}
