package com.ninni.spawn.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ninni.spawn.client.model.SunfishModel;
import com.ninni.spawn.entity.Sunfish;
import com.ninni.spawn.registry.SpawnEntityModelLayers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import static com.ninni.spawn.Spawn.MOD_ID;

@Environment(EnvType.CLIENT)
public class SunfishRenderer extends MobRenderer<Sunfish, SunfishModel> {
    public static final ResourceLocation BABY_TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/sunfish/sunfish_baby.png");
    public static final ResourceLocation NEWBORN_TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/sunfish/sunfish_newborn.png");
    private final SunfishModel modelAdult = this.getModel();
    private final SunfishModel modelBaby;
    private final SunfishModel modelNewborn;

    public SunfishRenderer(EntityRendererProvider.Context context) {
        super(context, new SunfishModel(context.bakeLayer(SpawnEntityModelLayers.SUNFISH)), 0.0F);
        this.modelBaby = new SunfishModel(context.bakeLayer(SpawnEntityModelLayers.SUNFISH_BABY));
        this.modelNewborn = new SunfishModel(context.bakeLayer(SpawnEntityModelLayers.SUNFISH_NEWBORN));
    }

    @Override
    public ResourceLocation getTextureLocation(Sunfish sunfish) {
        return switch (sunfish.getSunfishAge()) {
            case -2 -> NEWBORN_TEXTURE;
            case -1 -> BABY_TEXTURE;
            default ->  new ResourceLocation(MOD_ID, "textures/entity/sunfish/sunfish_" + sunfish.getVariant().getSerializedName() + ".png");
        };
    }

    @Override
    public void render(Sunfish sunfish, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        this.model = switch (sunfish.getSunfishAge()) {
            case -2 -> this.modelNewborn;
            case -1 -> this.modelBaby;
            default ->  this.modelAdult;
        };

        super.render(sunfish, f, g, poseStack, multiBufferSource, i);
    }

}