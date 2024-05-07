package com.ninni.spawn.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ninni.spawn.SpawnRPTweaks;
import com.ninni.spawn.client.model.BatRemodel;
import com.ninni.spawn.client.model.RabbitRemodel;
import com.ninni.spawn.registry.SpawnEntityModelLayers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.BatModel;
import net.minecraft.client.model.RabbitModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.BatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RabbitRenderer;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Rabbit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BatRenderer.class)
@Environment(value= EnvType.CLIENT)
public abstract class BatRendererMixin extends MobRenderer<Bat, BatModel> {
    private BatRemodel remodel;

    public BatRendererMixin(EntityRendererProvider.Context context, BatModel entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void S$init(EntityRendererProvider.Context context, CallbackInfo ci) {
        this.remodel = new BatRemodel(context.bakeLayer(SpawnEntityModelLayers.BAT_REMODEL));
    }

    @Inject(method = "scale(Lnet/minecraft/world/entity/ambient/Bat;Lcom/mojang/blaze3d/vertex/PoseStack;F)V", at = @At("HEAD"), cancellable = true)
    private void S$scale(Bat bat, PoseStack poseStack, float f, CallbackInfo ci) {
        if (SpawnRPTweaks.isPresent(SpawnRPTweaks.Tweaks.BAT_REMODEL)) ci.cancel();
    }

    @Override
    public void render(Bat mob, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        if (SpawnRPTweaks.isPresent(SpawnRPTweaks.Tweaks.BAT_REMODEL)) this.model = this.remodel;
        super.render(mob, f, g, poseStack, multiBufferSource, i);
    }
}
