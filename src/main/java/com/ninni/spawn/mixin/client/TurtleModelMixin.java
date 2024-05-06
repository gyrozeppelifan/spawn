package com.ninni.spawn.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ninni.spawn.SpawnRPTweaks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.TurtleModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Turtle;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TurtleModel.class)
public abstract class TurtleModelMixin<T extends Turtle> extends QuadrupedModel<T> {

    @Shadow @Final private ModelPart eggBelly;

    protected TurtleModelMixin(ModelPart modelPart, boolean bl, float f, float g, float h, float i, int j) {
        super(modelPart, bl, f, g, h, i, j);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int j, float f, float g, float h, float k) {
        if (SpawnRPTweaks.isPresent(SpawnRPTweaks.Tweaks.TURTLE_BABY_HEAD_SCALE)) {

            if (this.young) {
                poseStack.pushPose();
                float scaleHead = 0.4f;
                poseStack.scale(scaleHead, scaleHead, scaleHead);
                poseStack.translate(0.0f, 2.2, 4.0f / 16.0f);
                this.headParts().forEach(modelPart -> modelPart.render(poseStack, vertexConsumer, i, j, f, g, h, k));
                poseStack.popPose();

                poseStack.pushPose();
                float scaleBody = 0.2f;
                poseStack.scale(scaleBody, scaleBody, scaleBody);
                poseStack.translate(0.0f, 6, 0.0f);
                this.bodyParts().forEach(modelPart -> modelPart.render(poseStack, vertexConsumer, i, j, f, g, h, k));
                poseStack.popPose();
            } else {
                super.renderToBuffer(poseStack, vertexConsumer, i, j, f, g, h, k);
            }
        } else {
            super.renderToBuffer(poseStack, vertexConsumer, i, j, f, g, h, k);
        }

    }

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/animal/Turtle;FFFFF)V", at = @At("HEAD"), cancellable = true)
    private void setupAnim(T turtle, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {
        if (SpawnRPTweaks.isPresent(SpawnRPTweaks.Tweaks.TURTLE_ANIMATIONS)) {
            ci.cancel();

            float pi = (float) Math.PI;
            limbDistance = Mth.clamp(limbDistance, -0.45F, 0.45F);
            this.head.xRot = headPitch * (pi / 180);
            this.head.yRot = headYaw * (pi / 180);
            float o = Math.min(limbDistance / 0.1f, 1.0f);

            if (!turtle.isInWaterOrBubble()) {

                //idle anim

                this.body.y = 11;
                this.head.y = 19;
                this.head.z = -10;
                this.leftFrontLeg.y = 21;
                this.rightFrontLeg.y =21;
                this.leftHindLeg.y = 22;
                this.rightHindLeg.y = 22;

                this.body.xRot = pi/2 - 0.05f;

                this.leftFrontLeg.zRot = 0.15f;
                this.leftFrontLeg.z = -8.0f;
                this.leftFrontLeg.x = 6.0f;
                this.leftFrontLeg.yRot = -0.4f;

                this.rightFrontLeg.zRot = -0.15f;
                this.rightFrontLeg.z = -8.0f;
                this.rightFrontLeg.x = -6.0f;
                this.rightFrontLeg.yRot = 0.4f;

                this.leftHindLeg.yRot = 0.4f;
                this.leftHindLeg.zRot = 0.15f;
                this.leftHindLeg.xRot = 0;

                this.rightHindLeg.yRot = -0.4f;
                this.rightHindLeg.zRot = -0.15f;
                this.rightHindLeg.xRot = 0;

                //move anim
                float k = turtle.isLayingEgg() ? 4.0f : 1.0f;
                float l = turtle.isLayingEgg() ? 2.0f : 1.0f;

                this.rightFrontLeg.yRot += Mth.cos(k * limbAngle * 5.0f + pi) * 8.0f * limbDistance * l;
                this.rightFrontLeg.zRot += Mth.sin(limbAngle * 5.0f + pi) * 6.0f * limbDistance + (0.4f * o);

                this.leftFrontLeg.yRot += Mth.cos(k * limbAngle * 5.0f) * 8.0f * limbDistance * l;
                this.leftFrontLeg.zRot += Mth.sin(limbAngle * 5.0f) * 6.0f * limbDistance - (0.4f * o);

                this.body.y += Mth.sin(limbAngle * 5.0f) * 6.0f * limbDistance;

                this.rightHindLeg.yRot += Mth.sin(limbAngle * 5.0f + pi) * 3.0f * limbDistance;
                this.leftHindLeg.yRot += Mth.sin(limbAngle * 5.0f) * 3.0f * limbDistance;

            } else {

                //idle anim
                this.body.y = Mth.sin(animationProgress * 0.1F) * 0.25F + 11;
                this.head.y = Mth.cos(animationProgress * 0.1F) * 0.25F + 19;
                this.head.z = -10 + Mth.sin(animationProgress * 0.1F) * 1.5F * -0.2F;
                this.leftFrontLeg.y = Mth.sin(animationProgress * 0.1F) * 3 * 0.25F + 23;
                this.rightFrontLeg.y = Mth.sin(animationProgress * 0.1F) * 3 * 0.25F + 23;
                this.leftHindLeg.y = Mth.sin(animationProgress * 0.1F) * 2.5F * 0.25F + 22;
                this.rightHindLeg.y = Mth.sin(animationProgress * 0.1F) * 2.5F * 0.25F + 22;

                this.body.xRot = pi/2 - Mth.sin(animationProgress * 0.1F) * 0.5F * 0.1F;

                this.leftFrontLeg.zRot = Mth.cos(animationProgress * 0.1F) * 0.5F * -0.25F + 0.4f;
                this.leftFrontLeg.z = -8.0f;
                this.leftFrontLeg.x = 6.0f;
                this.leftFrontLeg.yRot = -0.2f;

                this.rightFrontLeg.zRot = Mth.cos(animationProgress * 0.1F + pi) * 0.5F * -0.25F - 0.4f;
                this.rightFrontLeg.z = -8.0f;
                this.rightFrontLeg.x = -6.0f;
                this.rightFrontLeg.yRot = 0.2f;

                this.leftHindLeg.yRot = 0.2f;
                this.leftHindLeg.zRot = 0f;
                this.leftHindLeg.xRot = Mth.cos(animationProgress * 0.1F + 0.2f) * 0.25F;

                this.rightHindLeg.yRot = -0.2f;
                this.rightHindLeg.zRot = 0f;
                this.rightHindLeg.xRot = Mth.cos(animationProgress * 0.1F+ 0.4f) * 0.25F;

                //swim anim

                this.body.y += Mth.sin(limbAngle * 0.4F) * 0.25F * limbDistance;
                this.head.y += Mth.cos(limbAngle * 0.4F) * 0.25F * limbDistance;
                this.head.z += Mth.sin(limbAngle * 0.4F) * 1.5F * -limbDistance;
                this.leftHindLeg.y += Mth.sin(limbAngle * 0.4F) * 8 * limbDistance;
                this.rightHindLeg.y += Mth.sin(limbAngle * 0.4F) * 8 * limbDistance;

                this.body.xRot += - Mth.sin(limbAngle * 0.4F) * 0.25F * limbDistance;

                this.leftFrontLeg.zRot += Mth.cos(limbAngle * 0.4F) * 1.75F * limbDistance;
                this.rightFrontLeg.zRot += Mth.cos(limbAngle * 0.4F + pi) * 1.75F * limbDistance;

                this.leftHindLeg.xRot += Mth.cos(limbAngle * 0.4F + 0.2f) * 0.5f * limbDistance;
                this.rightHindLeg.xRot += Mth.cos(limbAngle * 0.4F + 0.4f) * 0.5f * limbDistance;

            }
            this.eggBelly.visible = !this.young && turtle.hasEgg();
        }
    }
}
