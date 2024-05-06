package com.ninni.spawn.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.RabbitModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Rabbit;

@SuppressWarnings("FieldCanBeLocal, unused")
@Environment(EnvType.CLIENT)
public class RabbitRemodel extends RabbitModel<Rabbit> {
    private final ModelPart root;
    private final ModelPart leftRearFoot;
    private final ModelPart rightRearFoot;
    private final ModelPart leftHaunch;
    private final ModelPart rightHaunch;
    private final ModelPart body;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart head;
    private final ModelPart rightEar;
    private final ModelPart leftEar;
    private final ModelPart tail;
    private final ModelPart nose;

    public RabbitRemodel(ModelPart modelPart) {
        super(modelPart);
        this.root = modelPart;
        this.leftRearFoot = modelPart.getChild("left_hind_foot");
        this.rightRearFoot = modelPart.getChild("right_hind_foot");
        this.leftHaunch = modelPart.getChild("left_haunch");
        this.rightHaunch = modelPart.getChild("right_haunch");
        this.body = modelPart.getChild("body");
        this.leftFrontLeg = modelPart.getChild("left_front_leg");
        this.rightFrontLeg = modelPart.getChild("right_front_leg");
        this.head = modelPart.getChild("head");
        this.rightEar = modelPart.getChild("right_ear");
        this.leftEar = modelPart.getChild("left_ear");
        this.tail = modelPart.getChild("tail");
        this.nose = modelPart.getChild("nose");
    }

    @Override
    public void setupAnim(Rabbit mob, float limbAngle, float limbDistance, float age, float headYaw, float headPitch) {
        limbDistance = Mth.clamp(limbDistance, -0.25F, 0.25F);
        float pi = (float)Math.PI;

        float tilt = Math.min(limbDistance, 1.5F);

        head.xRot = headPitch * pi/180;
        head.yRot = headYaw * pi/180;
        leftEar.xRot = headPitch * pi/180;
        leftEar.yRot = headYaw * pi/180;
        rightEar.xRot = headPitch * pi/180;
        rightEar.yRot = headYaw * pi/180;

        leftEar.xRot -= tilt * 2;
        rightEar.xRot -= tilt * 2;

        leftEar.zRot = Mth.sin(age * 0.05F) * 0.05F;
        leftEar.xRot += Mth.cos(age * 0.025F) * 0.05F;
        rightEar.zRot = Mth.sin(age * 0.05F + pi) * 0.05F;
        rightEar.xRot += Mth.cos(age  * 0.025F + pi) * 0.05F;

        rightRearFoot.xRot = Mth.cos(limbAngle * 1.4f) * 2.8f * limbDistance;
        leftRearFoot.xRot = Mth.cos(limbAngle * 1.4f) * 2.8f * limbDistance;
        rightFrontLeg.xRot = Mth.cos(limbAngle * 1.4f + pi) * 2.8f * limbDistance;
        leftFrontLeg.xRot = Mth.cos(limbAngle * 1.4f + pi) * 2.8f * limbDistance;

        tail.yRot = Mth.cos(limbAngle * 0.7f + pi/2) * 1.4f * limbDistance;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int j, float f, float g, float h, float k) {

        if (this.young) {
            poseStack.pushPose();
            float l = 1.5f / 2.0f;
            poseStack.scale(l, l, l);
            poseStack.translate(0.0f, 10 / 16.0f, 0.5 / 16.0f);
            this.headParts().forEach(modelPart -> modelPart.render(poseStack, vertexConsumer, i, j, f, g, h, k));
            poseStack.popPose();
            poseStack.pushPose();
            l = 0.5f;
            poseStack.scale(l, l, l);
            poseStack.translate(0.0f, 24.0f / 16.0f, 0.0f);
            this.bodyParts().forEach(modelPart -> modelPart.render(poseStack, vertexConsumer, i, j, f, g, h, k));
            poseStack.popPose();
        } else {
            this.root.render(poseStack, vertexConsumer, i, j, f, g, h, k);
        }
    }


    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of(this.head, this.leftEar, this.rightEar, this.nose);
    }


    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.leftRearFoot, this.rightRearFoot, this.leftHaunch, this.rightHaunch, this.body, this.leftFrontLeg, this.rightFrontLeg, this.tail);
    }

    public static LayerDefinition createRemodel() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("left_haunch", CubeListBuilder.create(), PartPose.offset(0, 0, 0));
        partdefinition.addOrReplaceChild("right_haunch", CubeListBuilder.create(), PartPose.offset(0, 0, 0));
        partdefinition.addOrReplaceChild("nose", CubeListBuilder.create(), PartPose.offset(0, 0, 0));

        PartDefinition head = partdefinition.addOrReplaceChild(
                "head",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-2.5F, -3, -3, 5, 4, 4, new CubeDeformation(0.02F))
                        .texOffs(0, 19)
                        .addBox(-2.5F, -3, -3, 5, 4, 4, new CubeDeformation(0.25F))
                        .texOffs(0, 0)
                        .addBox(-0.5F, -0.5F, -3.5F, 1, 1, 1),
                PartPose.offset(0, 18, -2)
        );


        PartDefinition rightEar = partdefinition.addOrReplaceChild(
                "right_ear",
                CubeListBuilder.create()
                        .texOffs(18, 21)
                        .addBox(-2.5F, -8.0F, -1.0F, 2.0F, 5.0F, 1.0F),
                PartPose.offset(0.0F, 18.0F, -2.0F)
        );

        PartDefinition leftEar = partdefinition.addOrReplaceChild(
                "left_ear",
                CubeListBuilder.create()
                        .texOffs(24, 21)
                        .addBox(0.5F, -8.0F, -1.0F, 2.0F, 5.0F, 1.0F),
                PartPose.offset(0.0F, 18.0F, -2.0F)
        );

        PartDefinition body = partdefinition.addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                        .texOffs(0, 8)
                        .addBox(-2.5F, -1.5F, -5, 5, 4, 7),
                PartPose.offsetAndRotation(0, 20.5F, 2, -0.3927F, 0, 0)
        );

        PartDefinition tail = partdefinition.addOrReplaceChild(
                "tail",
                CubeListBuilder.create()
                        .texOffs(18, 3)
                        .addBox(-1.5F, -2.0F, 1.0F, 3.0F, 3.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, 20.5F, 2.0F, 0.3927F, 0.0F, 0.0F)
        );

        PartDefinition rightLeg = partdefinition.addOrReplaceChild(
                "right_hind_foot",
                CubeListBuilder.create()
                        .texOffs(22, 11)
                        .mirror()
                        .addBox(-1, 0, -3, 2, 1, 3)
                        .mirror(false),
                PartPose.offset(-2, 23, 3)
        );

        PartDefinition leftLeg = partdefinition.addOrReplaceChild(
                "left_hind_foot",
                CubeListBuilder.create()
                        .texOffs(22, 11)
                        .addBox(-1, 0, -3, 2, 1, 3),
                PartPose.offset(2, 23, 3)
        );

        PartDefinition rightArm = partdefinition.addOrReplaceChild(
                "right_front_leg",
                CubeListBuilder.create()
                        .texOffs(17, 8)
                        .mirror()
                        .addBox(-0.92F, 0, -1, 2, 4, 2)
                        .mirror(false),
                PartPose.offset(-1.6F, 20, -2)
        );

        PartDefinition leftArm = partdefinition.addOrReplaceChild(
                "left_front_leg",
                CubeListBuilder.create()
                        .texOffs(17, 8)
                        .addBox(-1.08F, 0, -1, 2, 4, 2),
                PartPose.offset(1.6F, 20, -2)
        );

        return LayerDefinition.create(meshdefinition, 32, 32);
    }
}
