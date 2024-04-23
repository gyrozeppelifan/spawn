package com.ninni.spawn.mixin.accessor;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Player.class)
public interface PlayerAccessor {
    @Accessor
    long getTimeEntitySatOnShoulder();

    @Invoker
    void callRespawnEntityOnShoulder(CompoundTag compoundTag);
}
