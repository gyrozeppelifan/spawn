package com.ninni.spawn.client.model;

import com.ninni.spawn.entity.Sunfish;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

@Environment(value= EnvType.CLIENT)
@SuppressWarnings("FieldCanBeLocal, unused")
public class SunfishModel extends HierarchicalModel<Sunfish> {
    private final ModelPart root;
    private final ModelPart all;
    private final ModelPart topFin;
    private final ModelPart bottomFin;
    private final ModelPart tailFin;
    private final ModelPart leftFin;
    private final ModelPart rightFin;

    public SunfishModel(ModelPart root) {

        this.root = root;
        
        this.all = root.getChild("all");
        
        this.topFin = this.all.getChild("topFin");
        this.bottomFin = this.all.getChild("bottomFin");
        this.tailFin = this.all.getChild("tailFin");
        this.leftFin = this.all.getChild("leftFin");
        this.rightFin = this.all.getChild("rightFin");
    }


    @Override
    public void setupAnim(Sunfish entity, float f, float g, float h, float i, float j) {

    }

    @Override
    public ModelPart root() {
        return this.root;
    }
    
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition all = partdefinition.addOrReplaceChild("all", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -15.0F, -25.0F, 6.0F, 28.0F, 28.0F)
                .texOffs(0, 56).addBox(-3.0F, -2.0F, -26.0F, 6.0F, 2.0F, 1.0F)
                .texOffs(14, 56).addBox(-3.0F, 2.0F, -26.0F, 6.0F, 2.0F, 1.0F), PartPose.offset(0.0F, 11.0F, 11.0F));
        all.addOrReplaceChild("topFin", CubeListBuilder.create().texOffs(40, 0).addBox(-1.0F, -18.0F, -5.0F, 2.0F, 18.0F, 10.0F), PartPose.offset(0.0F, -15.0F, -3.25F));
        all.addOrReplaceChild("bottomFin", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -5.0F, 2.0F, 18.0F, 10.0F), PartPose.offset(0.0F, 13.0F, -3.25F));
        all.addOrReplaceChild("tailFin", CubeListBuilder.create().texOffs(68, 25).addBox(0.0F, -14.0F, 0.0F, 0.0F, 28.0F, 3.0F), PartPose.offset(0.0F, -1.0F, 3.0F));
        all.addOrReplaceChild("leftFin", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -3.0F, 0.0F, 0.0F, 4.0F, 4.0F), PartPose.offsetAndRotation(3.0F, -1.0F, -11.5F, 0.0F, 0.7854F, 0.0F));
        all.addOrReplaceChild("rightFin", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(0.0F, -3.0F, 0.0F, 0.0F, 4.0F, 4.0F).mirror(false), PartPose.offsetAndRotation(-3.0F, -1.0F, -11.5F, 0.0F, -0.7854F, 0.0F));

        return LayerDefinition.create(meshdefinition, 80, 64);
    }

    public static LayerDefinition createBabyBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition all = partdefinition.addOrReplaceChild("all", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -5.0F, -10.5F, 4.0F, 10.0F, 13.0F)
                .texOffs(0, 23).addBox(-1.5F, -2.0F, -11.5F, 3.0F, 5.0F, 1.0F), PartPose.offset(0.0F, 19.0F, 4.0F));
        all.addOrReplaceChild("topFin", CubeListBuilder.create().texOffs(21, 0).addBox(-0.5F, -7.0F, -1.5F, 1.0F, 7.0F, 3.0F), PartPose.offset(0.0F, -5.0F, 0.0F));
        all.addOrReplaceChild("bottomFin", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 0.0F, -1.5F, 1.0F, 7.0F, 3.0F), PartPose.offset(0.0F, 5.0F, 0.0F));
        all.addOrReplaceChild("tailFin", CubeListBuilder.create().texOffs(8, 0).addBox(0.0F, -5.0F, 0.0F, 0.0F, 10.0F, 2.0F), PartPose.offset(0.0F, 0.0F, 2.5F));
        all.addOrReplaceChild("leftFin", CubeListBuilder.create().texOffs(8, 20).addBox(0.0F, -3.0F, 0.0F, 0.0F, 4.0F, 3.0F), PartPose.offsetAndRotation(2.0F, 1.0F, -4.5F, 0.0F, 0.3927F, 0.0F));
        all.addOrReplaceChild("rightFin", CubeListBuilder.create().texOffs(8, 20).mirror().addBox(0.0F, -3.0F, 0.0F, 0.0F, 4.0F, 3.0F).mirror(false), PartPose.offsetAndRotation(-2.0F, 1.0F, -4.5F, 0.0F, -0.3927F, 0.0F));

        return LayerDefinition.create(meshdefinition, 48, 32);
    }
    
    public static LayerDefinition createNewbornBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition all = partdefinition.addOrReplaceChild("all", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -1.5F, -2.0F, 3.0F, 3.0F, 3.0F)
                .texOffs(0, 2).addBox(0.0F, -2.5F, -2.0F, 0.0F, 5.0F, 4.0F), PartPose.offset(0.0F, 22.5F, 0.5F));
        all.addOrReplaceChild("leftFin", CubeListBuilder.create().texOffs(0, -1).addBox(0.0F, -0.5F, 0.0F, 0.0F, 1.0F, 1.0F), PartPose.offsetAndRotation(1.5F, 0.5F, -0.5F, 0.0F, 0.3927F, 0.0F));
        all.addOrReplaceChild("rightFin", CubeListBuilder.create().texOffs(0, -1).mirror().addBox(0.0F, -0.5F, 0.0F, 0.0F, 1.0F, 1.0F).mirror(false), PartPose.offsetAndRotation(-1.5F, 0.5F, -0.5F, 0.0F, -0.3927F, 0.0F));
        all.addOrReplaceChild("bottomFin", CubeListBuilder.create(), PartPose.offset(0.0F, 1.5F, -0.5F));
        all.addOrReplaceChild("topFin", CubeListBuilder.create(), PartPose.offset(0.0F, 1.5F, -0.5F));
        all.addOrReplaceChild("tailFin", CubeListBuilder.create(), PartPose.offset(0.0F, 1.5F, -0.5F));

        return LayerDefinition.create(meshdefinition, 16, 16);
    }


}