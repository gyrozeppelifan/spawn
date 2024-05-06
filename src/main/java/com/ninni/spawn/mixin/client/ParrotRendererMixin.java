package com.ninni.spawn.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ninni.spawn.SpawnRPTweaks;
import com.ninni.spawn.client.model.ParrotRemodel;
import com.ninni.spawn.registry.SpawnEntityModelLayers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ParrotModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Parrot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParrotRenderer.class)
@Environment(value= EnvType.CLIENT)
public abstract class ParrotRendererMixin extends MobRenderer<Parrot, ParrotModel> {
    private ParrotRemodel remodel;

    public ParrotRendererMixin(EntityRendererProvider.Context context, ParrotModel entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void S$init(EntityRendererProvider.Context context, CallbackInfo ci) {
        this.remodel = new ParrotRemodel(context.bakeLayer(SpawnEntityModelLayers.PARROT_REMODEL));
    }

    @Override
    public void render(Parrot mob, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        if (SpawnRPTweaks.isPresent(SpawnRPTweaks.Tweaks.PARROT_MODEL)) this.model = this.remodel;
        super.render(mob, f, g, poseStack, multiBufferSource, i);
    }
}
