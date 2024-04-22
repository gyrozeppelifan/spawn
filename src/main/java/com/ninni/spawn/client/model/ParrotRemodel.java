package com.ninni.spawn.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ParrotModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Parrot;


@SuppressWarnings("FieldCanBeLocal, unused")
@Environment(EnvType.CLIENT)
public class ParrotRemodel extends ParrotModel {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart tail;
    private final ModelPart leftWing;
    private final ModelPart rightWing;
    private final ModelPart head;
    private final ModelPart feather;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;

    public ParrotRemodel(ModelPart modelPart) {
        super(modelPart);
        this.root = modelPart;
        this.body = modelPart.getChild("body");
        this.tail = modelPart.getChild("tail");
        this.leftWing = modelPart.getChild("left_wing");
        this.rightWing = modelPart.getChild("right_wing");
        this.head = modelPart.getChild("head");
        this.feather = this.head.getChild("feather");
        this.leftLeg = modelPart.getChild("left_leg");
        this.rightLeg = modelPart.getChild("right_leg");
    }
    
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild(
                "head",
                CubeListBuilder.create()
                        .texOffs(0, 9)
                        .addBox(-2.0F, -4.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.02F))
                        .texOffs(18, 10)
                        .addBox(-1.0F, -4.0F, -4.0F, 2.0F, 3.0F, 2.0F)
                        .texOffs(12, 0)
                        .addBox(-1.0F, -1.0F, -4.0F, 2.0F, 1.0F, 1.0F),
                PartPose.offset(0.0F, 17.0F, 0.0F)
        );

        PartDefinition headFeathers = head.addOrReplaceChild(
                "feather",
                CubeListBuilder.create()
                        .texOffs(0, 13)
                        .addBox(0.0F, -5.0F, 0.0F, 0.0F, 5.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, -4.0F, -1.0F, -0.2618F, 0.0F, 0.0F)
        );

        PartDefinition body = partdefinition.addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F),
                PartPose.offset(0.0F, 17.0F, 0.0F)
        );

        PartDefinition leftWing = partdefinition.addOrReplaceChild(
                "left_wing",
                CubeListBuilder.create()
                        .texOffs(13, 14)
                        .addBox(0.0F, 0.0F, -1.0F, 1.0F, 5.0F, 3.0F),
                PartPose.offset(2.0F, 17.0F, 0.0F)
        );

        PartDefinition rightWing = partdefinition.addOrReplaceChild(
                "right_wing",
                CubeListBuilder.create()
                        .texOffs(13, 14)
                        .mirror()
                        .addBox(-1.0F, 0.0F, -1.0F, 1.0F, 5.0F, 3.0F)
                        .mirror(false),
                PartPose.offset(-2.0F, 17.0F, 0.0F)
        );

        PartDefinition tail = partdefinition.addOrReplaceChild(
                "tail",
                CubeListBuilder.create()
                        .texOffs(12, 4)
                        .addBox(-1.0F, 0.0F, 0.0F, 2.0F, 1.0F, 5.0F),
                PartPose.offset(0.0F, 21.0F, 2.0F)
        );

        PartDefinition leftLeg = partdefinition.addOrReplaceChild(
                "left_leg",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-0.5F, 0.0F, 0.0F, 1.0F, 2.0F, 0.0F)
                        .texOffs(0, 0)
                        .addBox(-0.5F, 2.0F, -1.0F, 1.0F, 0.0F, 2.0F),
                PartPose.offset(1.0F, 22.0F, 0.0F)
        );

        PartDefinition rightLeg = partdefinition.addOrReplaceChild(
                "right_leg",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .mirror()
                        .addBox(-0.5F, 0.0F, 0.0F, 1.0F, 2.0F, 0.0F)
                        .mirror(false)
                        .texOffs(0, 0)
                        .mirror()
                        .addBox(-0.5F, 2.0F, -1.0F, 1.0F, 0.0F, 2.0F)
                        .mirror(false),
                PartPose.offset(-1.0F, 22.0F, 0.0F)
        );

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(Parrot parrot, float f, float g, float h, float i, float j) {
        this.setupAnim(getState(parrot), parrot.tickCount, f, g, h, i, j);
    }

    @Override
    public void prepareMobModel(Parrot parrot, float f, float g, float h) {
    }

    public void renderOnShoulder(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int j, float f, float g, float h, float k, int l) {
        this.setupAnim(State.ON_SHOULDER, l, f, g, 0.0f, h, k);
        this.root.render(poseStack, vertexConsumer, i, j);
    }

    private void setupAnim(State state, int i, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        head.xRot = headPitch * ((float) Math.PI / 180f);
        head.yRot = headYaw * ((float) Math.PI / 180f);
        float pi = ((float)Math.PI);

        tail.z = 2;
        tail.x = 0;
        tail.xRot = 1f;

        head.z = 0;
        head.x = 0;
        head.zRot = 0;

        body.z = 0;
        body.x = 0;
        body.xRot = 0;
        body.zRot = 0;

        rightLeg.z = 0;
        rightLeg.x = -1;
        rightLeg.xRot = 0;
        rightLeg.yRot = 0;
        rightLeg.zRot = 0;
        leftLeg.z = 0;
        leftLeg.x = 1;
        leftLeg.xRot = 0;
        leftLeg.yRot = 0;
        leftLeg.zRot = 0;

        rightWing.z = 0;
        rightWing.x = -2;
        rightWing.xRot = 0;
        rightWing.yRot = 0;
        leftWing.z = 0;
        leftWing.x = 2;
        leftWing.xRot = 0;
        leftWing.yRot = 0;


        switch (state) {
            case SITTING: {
                rightWing.z = 0;
                leftWing.z = 0;
                leftWing.xRot = 0;
                leftWing.zRot = 0;
                rightWing.xRot = 0;
                rightWing.zRot = 0;
                body.xRot = 0;

                head.y = 19f;
                tail.y = 23f;
                body.y = 19F;
                rightWing.y = 19F;
                leftWing.y = 19F;

                leftLeg.y = 24F;
                leftLeg.z = -2F;
                leftLeg.xRot = -pi/2 - 0.5f;
                rightLeg.y = 24F;
                rightLeg.z = -2F;
                rightLeg.xRot = -pi/2 - 0.5f;
                break;
            }
            case PARTY: {
                float l = Mth.cos(i);
                float m = Mth.sin(i);
                head.x = l;
                head.y = 17 + m;
                head.xRot = -pi/6;
                head.yRot = 0.0f;
                head.zRot = Mth.sin(i) * 0.4f;
                body.x = l;
                body.y = 17 + m;
                leftWing.zRot = -animationProgress;
                leftWing.x = 2 + l;
                leftWing.y = 17 + m;
                rightWing.zRot = animationProgress;
                rightWing.x = -2 + l;
                rightWing.y = 17 + m;
                tail.x = l;
                tail.y = 21 + m;
                leftLeg.y = 21f + m;
                rightLeg.y = 21f + m;
                leftLeg.x = 1 + l;
                rightLeg.x = -1 + l;
                leftLeg.zRot = -0.34906584f;
                rightLeg.zRot = 0.34906584f;
                break;
            }
            default: {
                float o;
                if (animationProgress != 0) o = Math.min(limbDistance / 0.3f, 1.0f);
                else o = 0;
                float n = animationProgress * 0.3f;

                head.y = 17f + n;
                body.y = 17F + n;
                rightWing.y = 17F + n;
                leftWing.y = 17F + n;

                body.xRot = o * 0.5f;
                tail.z = 2 + o;
                tail.y = 21 - o + n;
                leftWing.z = o;
                rightWing.z = o;
                leftLeg.z = o * 3;
                rightLeg.z = o * 3;
                leftLeg.y = 22 - o + n;
                rightLeg.y = 22 - o + n;
                leftLeg.xRot = o * 0.5f;
                rightLeg.xRot = o * 0.5f;
                leftWing.zRot = -animationProgress;
                rightWing.zRot = animationProgress;
                leftWing.xRot = o * 0.5f;
                rightWing.xRot = o * 0.5f;
            }
        }
    }

    private static ParrotModel.State getState(Parrot parrot) {
        if (parrot.isPartyParrot()) {
            return ParrotModel.State.PARTY;
        }
        if (parrot.isInSittingPose()) {
            return ParrotModel.State.SITTING;
        }
        if (parrot.isFlying()) {
            return ParrotModel.State.FLYING;
        }
        return ParrotModel.State.STANDING;
    }
}
