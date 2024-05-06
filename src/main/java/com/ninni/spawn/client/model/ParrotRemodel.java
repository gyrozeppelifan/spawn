package com.ninni.spawn.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ninni.spawn.client.animation.ParrotReAnimation;
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
    
    public static LayerDefinition createRemodel() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition right_wing = partdefinition.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(24, -3).mirror().addBox(0.0F, 0.0F, -1.5F, 0.0F, 5.0F, 3.0F).mirror(false), PartPose.offsetAndRotation(-1.5F, 19.5F, 0.0F, 0.7854F, -0.1745F, 0.1745F));

        PartDefinition left_wing = partdefinition.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(24, -3).addBox(0.0F, 0.0F, -1.5F, 0.0F, 5.0F, 3.0F), PartPose.offsetAndRotation(1.5F, 19.5F, 0.0F, 0.7854F, 0.1745F, -0.1745F));

        PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -0.5F, -1.0F, 1.0F, 2.0F, 1.0F), PartPose.offset(-1.0F, 22.5F, 0.0F));

        PartDefinition tail = partdefinition.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(12, 0).addBox(-1.0F, -0.5F, 0.0F, 2.0F, 1.0F, 3.0F), PartPose.offset(0.0F, 21.5F, 1.5F));

        PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, -0.5F, -1.0F, 1.0F, 2.0F, 1.0F), PartPose.offset(1.0F, 22.5F, 0.0F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 7).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 4.0F, 3.0F), PartPose.offset(0.0F, 18.5F, 0.0F));

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -3.0F, -2.0F, 4.0F, 3.0F, 4.0F)
                .texOffs(12, 10).addBox(-1.5F, -2.0F, -4.0F, 3.0F, 2.0F, 2.0F)
                .texOffs(16, 4).addBox(-1.5F, 0.0F, -4.0F, 3.0F, 1.0F, 1.0F)
                .texOffs(9, 7).addBox(-2.0F, -3.0F, -4.0F, 4.0F, 1.0F, 2.0F), PartPose.offset(0.0F, 18.5F, 0.0F));

        PartDefinition feather = head.addOrReplaceChild("feather", CubeListBuilder.create().texOffs(21, 3).addBox(0.0F, -3.0F, -0.5F, 0.0F, 4.0F, 3.0F), PartPose.offset(0.0F, -3.0F, 0.5F));

        return LayerDefinition.create(meshdefinition, 32, 16);
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
        this.root().getAllParts().forEach(ModelPart::resetPose);
        if (state != State.ON_SHOULDER) this.animateWalk(ParrotReAnimation.FLY, limbAngle, limbDistance, 3.5f, 8.0f);

        head.xRot += headPitch * ((float) Math.PI / 180f);
        head.yRot += headYaw * ((float) Math.PI / 180f);
        float pi = ((float)Math.PI);

        switch (state) {
            case SITTING: {

                head.y = 20f;
                body.y = 20f;
                tail.y = 22.5f;
                rightWing.y = 21F;
                leftWing.y = 21F;

                leftLeg.y = 24F;
                leftLeg.z = -1.5F;
                leftLeg.xRot = -pi/2 - 0.5f;
                rightLeg.y = 24F;
                rightLeg.z = -1.5F;
                rightLeg.xRot = -pi/2 - 0.5f;
                break;
            }
            case PARTY: {
                float l = Mth.cos(i);
                float m = Mth.sin(i);
                head.x = l;
                head.y = 18 + m;
                head.xRot = -pi/6;
                head.yRot = 0.0f;
                head.zRot = Mth.sin(i) * 0.4f;
                body.x = l;
                body.y = 18 + m;
                leftWing.zRot = -animationProgress;
                leftWing.x = 2 + l;
                leftWing.y = 19 + m;
                rightWing.zRot = animationProgress;
                rightWing.x = -2 + l;
                rightWing.y = 19 + m;
                tail.x = l;
                tail.y = 22 + m;
                leftLeg.y = 22 + m;
                rightLeg.y = 22 + m;
                leftLeg.x = 1 + l;
                rightLeg.x = -1 + l;
                leftLeg.zRot = -0.34906584f;
                rightLeg.zRot = 0.34906584f;
                break;
            }
            default: {
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
