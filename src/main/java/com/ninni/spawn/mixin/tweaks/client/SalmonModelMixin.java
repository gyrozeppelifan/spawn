package com.ninni.spawn.mixin.tweaks.client;

import com.ninni.spawn.SpawnRPTweaks;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.SalmonModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SalmonModel.class)
public abstract class SalmonModelMixin<T extends Entity> extends HierarchicalModel<T> {

    @Unique
    private ModelPart backFin;
    @Unique
    private ModelPart leftFin;
    @Unique
    private ModelPart body;
    @Unique
    private ModelPart bodyFront;
    @Unique
    private ModelPart rightFin;
    @Unique
    private ModelPart all;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(ModelPart modelPart, CallbackInfo ci) {
        this.all = modelPart.getChild("all");
        this.body = this.all.getChild("body");
        this.bodyFront = this.all.getChild("body_front");
        this.backFin = body.getChild("back_fin");
        this.leftFin = this.bodyFront.getChild("left_fin");
        this.rightFin = this.bodyFront.getChild("right_fin");
    }

    @Inject(method = "setupAnim", at = @At("HEAD"), cancellable = true)
    private void setupAnim(T salmon, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {
        float pi = ((float)Math.PI);

        if (SpawnRPTweaks.isPresent(SpawnRPTweaks.Tweaks.SALMON_ANIMATIONS)) {
            ci.cancel();

            float speed = salmon.isInWater() ? 1 : 4;
            SpawnRPTweaks.addTilting(SpawnRPTweaks.Tweaks.SALMON_TILTING, this.all, headYaw, headPitch);

            this.all.y = Mth.cos(animationProgress * 0.3f + 3) * 2.4F * 0.25F + 21.0F;
            this.all.z = 6.0F;
            this.all.xRot += Mth.sin(animationProgress * 0.15f + 1) * 0.2F * -0.25F;
            this.body.yRot = Mth.cos(animationProgress * 0.15f * speed + 2) * 0.8F * 0.25F;
            this.backFin.yRot = Mth.cos(animationProgress * 0.15f * speed + 1) * 2.8F * 0.25F;
            this.body.yRot += Mth.cos(limbAngle * 2f + 2) * 0.8F * limbDistance;
            this.backFin.yRot += Mth.cos(limbAngle * 2f + 1) * 2.8F * limbDistance;
            this.rightFin.zRot = Mth.cos(animationProgress * 0.3f + 1f + pi) * 2 * 0.25F - 0.6F;
            this.leftFin.zRot = Mth.cos(animationProgress * 0.3f + 1.5f) * 2F * 0.25F + 0.6F;
        } else {
            ci.cancel();
            SpawnRPTweaks.addTilting(SpawnRPTweaks.Tweaks.SALMON_TILTING, this.all, headYaw, headPitch);
            this.all.z = 6.0F;

            float k = 1.0f;
            float l = 1.0f;
            if (!salmon.isInWater()) {
                k = 1.3f;
                l = 1.7f;
            }
            this.body.yRot = -k * 0.25f * Mth.sin(l * 0.6f * animationProgress);
        }
    }
    @Inject(method = "createBodyLayer", at = @At("HEAD"), cancellable = true)
    private static void createBodyLayer(CallbackInfoReturnable<LayerDefinition> cir) {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("body_back", CubeListBuilder.create(), PartPose.offset(0, 0, 0));
        PartDefinition all = partdefinition.addOrReplaceChild("all", CubeListBuilder.create(), PartPose.offset(0.0F, 21.0F, -1.0F));
        all.addOrReplaceChild("tob_back_fin", CubeListBuilder.create().texOffs(0, 2).addBox(0.0F, -2.0F, -1.0F, 0.0F, 2.0F, 4.0F), PartPose.offset(0.0F, -2.5F, 0.0F));
        all.addOrReplaceChild("top_front_fin", CubeListBuilder.create().texOffs(4, 2).addBox(0.0F, -4.0F, 6.0F, 0.0F, 2.0F, 2.0F), PartPose.offset(0.0F, -0.5F, -8.0F));
        all.addOrReplaceChild("head", CubeListBuilder.create().texOffs(22, 0).addBox(-1.0F, -2.0F, -3.0F, 2.0F, 4.0F, 3.0F), PartPose.offset(0.0F, 0.0F, -8.0F));
        PartDefinition body_back = all.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 13).addBox(-1.5F, -2.5F, 0.0F, 3.0F, 5.0F, 8.0F), PartPose.offset(0.0F, 0.0F, 0.0F));
        body_back.addOrReplaceChild("back_fin", CubeListBuilder.create().texOffs(20, 10).addBox(0.0F, -2.5F, 0.0F, 0.0F, 5.0F, 6.0F), PartPose.offset(0.0F, 0.0F, 8.0F));
        PartDefinition body_front = all.addOrReplaceChild("body_front", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -2.5F, 0.0F, 3.0F, 5.0F, 8.0F), PartPose.offset(0.0F, 0.0F, -8.0F));
        body_front.addOrReplaceChild("right_fin", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -1.0F, 0.0F, 2.0F, 2.0F, 0.0F), PartPose.offsetAndRotation(-1.5F, 1.5F, 1.0F, -1.5708F, 0.0F, -0.7854F));
        body_front.addOrReplaceChild("left_fin", CubeListBuilder.create().texOffs(4, 0).addBox(0.0F, -1.0F, 0.0F, 2.0F, 2.0F, 0.0F), PartPose.offsetAndRotation(1.5F, 1.5F, 1.0F, -1.5708F, 0.0F, 0.7854F));

        cir.setReturnValue(LayerDefinition.create(meshdefinition, 32, 32));
    }
}
