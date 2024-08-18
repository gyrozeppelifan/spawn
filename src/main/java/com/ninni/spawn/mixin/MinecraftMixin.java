package com.ninni.spawn.mixin;

import com.ninni.spawn.registry.SpawnBiomes;
import com.ninni.spawn.registry.SpawnSoundEvents;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Shadow @Nullable public LocalPlayer player;
    @Shadow public abstract MusicManager getMusicManager();
    @Shadow @Nullable public Screen screen;

    @Inject(method = "getSituationalMusic", at = @At("HEAD"), cancellable = true)
    public void S$getSituationalMusic(CallbackInfoReturnable<Music> cir) {
        Music music = Optionull.map(this.screen, Screen::getBackgroundMusic);

        if (music != null) cir.setReturnValue(music);

        if (this.player != null) {
            Holder<Biome> holder = this.player.level().getBiome(this.player.blockPosition());
            if (this.getMusicManager().isPlayingMusic(SpawnSoundEvents.SEAGRASS_MEADOWS) || this.player.isUnderWater() && holder.is(SpawnBiomes.SEAGRASS_MEADOW)) {
                cir.setReturnValue(SpawnSoundEvents.SEAGRASS_MEADOWS);
            }
        }
    }
}
