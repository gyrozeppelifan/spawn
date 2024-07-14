package com.ninni.spawn.mixin.tweaks.client;

import com.ninni.spawn.SpawnRPTweaks;
import net.minecraft.client.model.ColorableHierarchicalModel;
import net.minecraft.client.model.TropicalFishModelA;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TropicalFishModelA.class)
public abstract class TropicalFishModelAMixin<T extends Entity> extends ColorableHierarchicalModel<T> {

    @Unique
    private ModelPart tailFin;
    @Unique
    private ModelPart leftFin;
    @Unique
    private ModelPart rightFin;
    @Unique
    private ModelPart all;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(ModelPart modelPart, CallbackInfo ci) {
        this.all = modelPart.getChild("all");
        this.leftFin = this.all.getChild("left_fin");
        this.rightFin = this.all.getChild("right_fin");
        this.tailFin = this.all.getChild("tail_fin");
    }

    @Inject(method = "setupAnim", at = @At("HEAD"), cancellable = true)
    private void setupAnim(T tropicalFish, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {
        float pi = ((float)Math.PI);
        if (tropicalFish.noPhysics) {
            ci.cancel();
            this.all.xRot = 0;
            this.all.yRot = 0;
        } else {
            if (SpawnRPTweaks.isPresent(SpawnRPTweaks.Tweaks.TROPICAL_FISH_ANIMATIONS) && !tropicalFish.noPhysics) {
                ci.cancel();

                float speed = tropicalFish.isInWater() ? 1 : 4;

                SpawnRPTweaks.addTilting(SpawnRPTweaks.Tweaks.TROPICAL_FISH_TILTING, this.all, headYaw, headPitch);

                this.all.y = Mth.cos(animationProgress * 0.3f + 3) * 2.4F * 0.25F + 21.5F;
                this.all.z = -1.0F;
                this.all.xRot += Mth.sin(animationProgress * 0.15f + 1) * 0.2F * -0.25F;
                this.tailFin.yRot = Mth.cos(animationProgress * 0.15f * speed + 1) * 2.8F * 0.25F;
                this.tailFin.yRot += Mth.cos(limbAngle * 2f + 1) * 2.8F * limbDistance;
                this.rightFin.yRot = Mth.cos(animationProgress * 0.3f + 1f + pi) * 2 * 0.25F + 0.6F;
                this.leftFin.yRot = Mth.cos(animationProgress * 0.3f + 1.5f) * 2F * 0.25F - 0.6F;
            } else {
                ci.cancel();
                SpawnRPTweaks.addTilting(SpawnRPTweaks.Tweaks.TROPICAL_FISH_TILTING, this.all, headYaw, headPitch);
                this.all.z = -1.0F;

                float k = 1.0f;
                if (!tropicalFish.isInWater()) {
                    k = 1.5f;
                }
                this.tailFin.yRot = -k * 0.45f * Mth.sin(0.6f * animationProgress);
            }
        }
    }

    @Inject(method = "createBodyLayer", at = @At("HEAD"), cancellable = true)
    private static void createBodyLayer(CallbackInfoReturnable<LayerDefinition> cir) {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition all = partdefinition.addOrReplaceChild("all", CubeListBuilder.create(), PartPose.offset(0.0F, 22.5F, 0.0F));
        all.addOrReplaceChild("left_fin", CubeListBuilder.create().texOffs(2, 12).addBox(0.0F, -2.0F, 0.0F, 2.0F, 2.0F, 0.0F), PartPose.offsetAndRotation(1.0F, 1.5F, 0.0F, 0.0F, -0.7854F, 0.0F));
        all.addOrReplaceChild("right_fin", CubeListBuilder.create().texOffs(2, 16).addBox(-2.0F, -2.0F, 0.0F, 2.0F, 2.0F, 0.0F), PartPose.offsetAndRotation(-1.0F, 1.5F, 0.0F, 0.0F, 0.7854F, 0.0F));
        all.addOrReplaceChild("tail_fin", CubeListBuilder.create().texOffs(24, -4).addBox(0.0F, -1.5F, 0.0F, 0.0F, 3.0F, 4.0F), PartPose.offset(0.0F, 0.0F, 3.0F));
        all.addOrReplaceChild("top_fin", CubeListBuilder.create().texOffs(10, -6).addBox(0.0F, -4.0F, 0.0F, 0.0F, 4.0F, 6.0F), PartPose.offset(0.0F, -1.5F, -3.0F));
        all.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -1.5F, -3.0F, 2.0F, 3.0F, 6.0F), PartPose.offset(0.0F, 0.0F, 0.0F));

        partdefinition.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        cir.setReturnValue(LayerDefinition.create(meshdefinition, 32, 32));
    }
}
