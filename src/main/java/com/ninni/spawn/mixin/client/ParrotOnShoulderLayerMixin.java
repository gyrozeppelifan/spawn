package com.ninni.spawn.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ninni.spawn.client.model.ParrotRemodel;
import com.ninni.spawn.registry.SpawnEntityModelLayers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ParrotModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ParrotOnShoulderLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParrotOnShoulderLayer.class)
@Environment(value= EnvType.CLIENT)
public abstract class ParrotOnShoulderLayerMixin<T extends Player> extends RenderLayer<T, PlayerModel<T>> {
    @Mutable
    @Shadow @Final private ParrotModel model;
    private ParrotRemodel remodel;

    public ParrotOnShoulderLayerMixin(RenderLayerParent<T, PlayerModel<T>> renderLayerParent) {
        super(renderLayerParent);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void S$init(RenderLayerParent<T, PlayerModel<T>> renderLayerParent, EntityModelSet entityModelSet, CallbackInfo ci) {
        this.remodel = new ParrotRemodel(entityModelSet.bakeLayer(SpawnEntityModelLayers.PARROT_REMODEL));
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/player/Player;FFFFZ)V", at = @At("HEAD"), cancellable = true)
    private void S$render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, T player, float f, float g, float h, float j, boolean bl, CallbackInfo ci) {
        if (Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation("textures/entity/parrot/remodel_enabler.txt")).isPresent()) this.model = this.remodel;
    }
}
