package com.ninni.spawn.client.model;

import net.minecraft.client.model.BatModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ambient.Bat;

public class BatRemodel extends BatModel {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart rightWingTip;
    private final ModelPart leftWingTip;

    public BatRemodel(ModelPart modelPart) {
        super(modelPart);
        this.root = modelPart;
        this.head = modelPart.getChild("head");
        this.body = modelPart.getChild("body");
        this.rightWing = this.body.getChild("right_wing");
        this.rightWingTip = this.rightWing.getChild("right_wing_tip");
        this.leftWing = this.body.getChild("left_wing");
        this.leftWingTip = this.leftWing.getChild("left_wing_tip");
    }

    @Override
    public void setupAnim(Bat bat, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        float pi = (float) Math.PI;

        this.head.xRot = headPitch * (pi / 180);
        this.head.yRot = headYaw * (pi / 180);

        if (bat.isResting()) {
            this.head.xRot = pi;
            this.head.setPos(0.0f, 16.0f, 0.0f);
            this.body.xRot = pi;
            this.rightWing.xRot = -0.2f;
            this.rightWing.yRot = -1.2f;
            this.rightWingTip.yRot = -2f;
            this.leftWing.xRot = this.rightWing.xRot;
            this.leftWing.yRot = -this.rightWing.yRot;
            this.leftWingTip.yRot = -this.rightWingTip.yRot;
        } else {
            this.body.xRot = Mth.sin(animationProgress * 0.3f) * -0.2f + 0.6f;
            this.head.y = Mth.cos(animationProgress * 0.3f) * -0.2f + 16f;
            this.rightWing.xRot = Mth.sin(animationProgress * 0.9f + pi) * 0.25f;
            this.rightWing.yRot = Mth.cos(animationProgress * 0.9f + pi) * 1.25f;
            this.rightWingTip.yRot = Mth.cos(animationProgress * 0.9f + pi) * 0.5f;
            this.leftWing.xRot = Mth.sin(animationProgress * 0.9f) * 0.25f;
            this.leftWing.yRot = Mth.cos(animationProgress * 0.9f) * 1.25f;
            this.leftWingTip.yRot = Mth.cos(animationProgress * 0.9f) * 0.5f;
        }
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    public static LayerDefinition createRemodel() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -4.0F, -2.0F, 5.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(14, 0).addBox(1.5F, -6.0F, 0.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(14, 0).mirror().addBox(-3.5F, -6.0F, 0.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 16.0F, 0.0F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 8).addBox(-2.0F, -0.5F, -1.0F, 4.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(12, 8).addBox(-1.5F, 4.5F, 0.0F, 3.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 16.5F, 0.0F));

        PartDefinition left_wing = body.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(0, 15).addBox(0.0F, -3.0F, 0.0F, 5.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 0.5F, 0.5F));

        PartDefinition left_wing_tip = left_wing.addOrReplaceChild("left_wing_tip", CubeListBuilder.create().texOffs(10, 15).addBox(0.0F, -2.0F, 0.0F, 5.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, -1.0F, 0.0F));

        PartDefinition right_wing = body.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(0, 15).mirror().addBox(-5.0F, -3.0F, 0.0F, 5.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-2.0F, 0.5F, 0.5F));

        PartDefinition right_wing_tip = right_wing.addOrReplaceChild("right_wing_tip", CubeListBuilder.create().texOffs(10, 15).mirror().addBox(-5.0F, -2.0F, 0.0F, 5.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-5.0F, -1.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }
}
