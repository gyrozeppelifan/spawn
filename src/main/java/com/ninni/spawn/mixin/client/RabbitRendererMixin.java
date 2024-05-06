package com.ninni.spawn.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ninni.spawn.SpawnRPTweaks;
import com.ninni.spawn.client.model.ParrotRemodel;
import com.ninni.spawn.client.model.RabbitRemodel;
import com.ninni.spawn.registry.SpawnEntityModelLayers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ParrotModel;
import net.minecraft.client.model.RabbitModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.client.renderer.entity.RabbitRenderer;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Rabbit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RabbitRenderer.class)
@Environment(value= EnvType.CLIENT)
public abstract class RabbitRendererMixin extends MobRenderer<Rabbit, RabbitModel<Rabbit>> {
    private RabbitRemodel remodel;

    public RabbitRendererMixin(EntityRendererProvider.Context context, RabbitModel entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void S$init(EntityRendererProvider.Context context, CallbackInfo ci) {
        this.remodel = new RabbitRemodel(context.bakeLayer(SpawnEntityModelLayers.RABBIT_REMODEL));
    }

    @Override
    public void render(Rabbit mob, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        if (SpawnRPTweaks.isPresent(SpawnRPTweaks.Tweaks.RABBIT_REMODEL)) this.model = this.remodel;
        super.render(mob, f, g, poseStack, multiBufferSource, i);
    }
}
