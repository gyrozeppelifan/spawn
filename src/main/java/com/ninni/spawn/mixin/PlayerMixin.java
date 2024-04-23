package com.ninni.spawn.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    @Shadow private long timeEntitySatOnShoulder;
    @Shadow protected abstract void respawnEntityOnShoulder(CompoundTag compoundTag);
    @Shadow public abstract CompoundTag getShoulderEntityLeft();
    @Shadow public abstract CompoundTag getShoulderEntityRight();
    @Shadow protected abstract void setShoulderEntityLeft(CompoundTag compoundTag);
    @Shadow protected abstract void setShoulderEntityRight(CompoundTag compoundTag);

    @Shadow @Final private Abilities abilities;

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("HEAD"), method = "removeEntitiesOnShoulder", cancellable = true)
    private void S$removeEntitiesOnShoulder(CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(at = @At("TAIL"), method = "aiStep")
    private void S$aiStep(CallbackInfo ci) {
        if (!this.level().isClientSide && this.isUnderWater() || this.abilities.flying || this.isSleeping() || this.isInPowderSnow) {
            this.newRemoveEntitiesOnShoulder();
        }
    }

    @Inject(at = @At("HEAD"), method = "jumpFromGround")
    private void S$jumpFromGround(CallbackInfo ci) {
        if (this.isShiftKeyDown()) this.newRemoveEntitiesOnShoulder();
    }


    protected void newRemoveEntitiesOnShoulder() {
        if (this.timeEntitySatOnShoulder + 20L < this.level().getGameTime()) {
            this.respawnEntityOnShoulder(this.getShoulderEntityLeft());
            this.setShoulderEntityLeft(new CompoundTag());
            this.respawnEntityOnShoulder(this.getShoulderEntityRight());
            this.setShoulderEntityRight(new CompoundTag());
        }
    }
}
