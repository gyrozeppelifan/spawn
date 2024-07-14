package com.ninni.spawn.mixin.tweaks.client;

import com.ninni.spawn.SpawnRPTweaks;
import net.minecraft.client.model.CodModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CodModel.class)
public abstract class CodModelMixin<T extends Entity> extends HierarchicalModel<T> {

    @Unique
    private ModelPart body;
    @Unique
    private ModelPart leftFin;
    @Unique
    private ModelPart rightFin;
    @Unique
    private ModelPart tail;
    @Unique
    private ModelPart all;
    @Shadow @Final private ModelPart root;
    @Shadow @Final private ModelPart tailFin;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(ModelPart modelPart, CallbackInfo ci) {
        this.all = modelPart.getChild("all");
        this.body = this.all.getChild("body");
        this.tail = this.all.getChild("tail");
        this.leftFin = this.body.getChild("left_fin");
        this.rightFin = this.body.getChild("right_fin");
    }

    @Inject(method = "setupAnim", at = @At("HEAD"), cancellable = true)
    private void setupAnim(T cod, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {
        float pi = ((float)Math.PI);

        if (SpawnRPTweaks.isPresent(SpawnRPTweaks.Tweaks.COD_ANIMATIONS)) {
            ci.cancel();

            float speed = cod.isInWater() ? 1 : 4;

            SpawnRPTweaks.addTilting(SpawnRPTweaks.Tweaks.COD_TILTING, this.all, headYaw, headPitch);

            this.all.y = Mth.cos(animationProgress * 0.3f + 3) * 2.4F * 0.25F + 22.0F;
            this.all.z = -3.0F;
            this.all.xRot += Mth.sin(animationProgress * 0.15f + 1) * 0.2F * -0.25F;
            this.tail.yRot = Mth.cos(animationProgress * 0.15f * speed + 1) * 2.8F * 0.25F;
            this.tail.yRot += Mth.cos(limbAngle * 2f + 1) * 2.8F * limbDistance;
            this.rightFin.zRot = Mth.cos(animationProgress * 0.3f + 1f + pi) * 2 * 0.25F - 0.6F;
            this.leftFin.zRot = Mth.cos(animationProgress * 0.3f + 1.5f) * 2F * 0.25F + 0.6F;
        } else {
            ci.cancel();
            SpawnRPTweaks.addTilting(SpawnRPTweaks.Tweaks.COD_TILTING, this.all, headYaw, headPitch);
            this.all.z = -3.0F;

            float k = 1.0f;
            if (!cod.isInWater()) {
                k = 1.5f;
            }
            this.tail.yRot = -k * 0.45f * Mth.sin(0.6f * animationProgress);
        }
    }
    
    @Inject(method = "createBodyLayer", at = @At("HEAD"), cancellable = true)
    private static void createBodyLayer(CallbackInfoReturnable<LayerDefinition> cir) {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition tail_fin = partdefinition.addOrReplaceChild("tail_fin", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition all = partdefinition.addOrReplaceChild("all", CubeListBuilder.create(), PartPose.offset(0.0F, 22.0F, 2.0F));
        PartDefinition tail = all.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(22, 3).addBox(0.0F, -2.0F, 0.0F, 0.0F, 4.0F, 4.0F), PartPose.offset(0.0F, 0.0F, 5.0F));
        PartDefinition top_fin = all.addOrReplaceChild("top_fin", CubeListBuilder.create().texOffs(20, -6).addBox(0.0F, -1.0F, -1.0F, 0.0F, 1.0F, 6.0F), PartPose.offset(0.0F, -2.0F, -2.0F));
        PartDefinition head = all.addOrReplaceChild("head", CubeListBuilder.create().texOffs(11, 0).addBox(-1.0F, -2.0F, -3.0F, 2.0F, 4.0F, 3.0F), PartPose.offset(0.0F, 0.0F, -2.0F));
        PartDefinition nose = head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 3.0F, 1.0F), PartPose.offset(0.0F, 0.0F, -3.0F));
        PartDefinition body = all.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 4.0F, 7.0F), PartPose.offset(0.0F, 0.0F, -2.0F));
        PartDefinition right_fin = body.addOrReplaceChild("right_fin", CubeListBuilder.create().texOffs(24, 1).addBox(-2.0F, 0.0F, -1.0F, 2.0F, 0.0F, 2.0F), PartPose.offsetAndRotation(-1.0F, 1.0F, 0.0F, 0.0F, 0.0F, -0.7854F));
        PartDefinition left_fin = body.addOrReplaceChild("left_fin", CubeListBuilder.create().texOffs(24, 4).addBox(0.0F, 0.0F, -1.0F, 2.0F, 0.0F, 2.0F), PartPose.offsetAndRotation(1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

        cir.setReturnValue(LayerDefinition.create(meshdefinition, 32, 32));
    }
}
