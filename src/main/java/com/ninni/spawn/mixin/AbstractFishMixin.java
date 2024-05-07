package com.ninni.spawn.mixin;

import com.ninni.spawn.entity.common.FlopConditionable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFish.class)
public abstract class AbstractFishMixin extends WaterAnimal implements Bucketable {
    private AbstractFishMixin(EntityType<? extends WaterAnimal> entityType, Level world) {
        super(entityType, world);
    }


    @Inject(method = "<init>", at = @At("TAIL"))
    private void S$init(EntityType<? extends AbstractFish> entityType, Level level, CallbackInfo ci) {
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02f, 0.1f, true);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
    }

    @Inject(method = "aiStep", at = @At("HEAD"), cancellable = true)
    private void S$aiStep(CallbackInfo ci) {
        AbstractFish that = AbstractFish.class.cast(this);
        if (that instanceof FlopConditionable conditionable) {
            if (!conditionable.doesFlopWhileOutOfWater()) {
                super.aiStep();
                ci.cancel();
            }
        }
    }
}
