package com.ninni.spawn.entity.ai.goal;

import com.ninni.spawn.entity.common.BoidFishEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.ai.goal.Goal;

// Code took and modified from https://github.com/Tomate0613/boids,
// I went ahead and used it because the project's license is MIT,
// but if you are the author or someone that knows the author reading this
// and you are not ok with me using it, please put me in contact with the author directly and I will act accordingly by removing it

public class LimitSpeedAndLookInVelocityDirectionGoal extends Goal {
    private final BoidFishEntity mob;
    private final float speed;

    public LimitSpeedAndLookInVelocityDirectionGoal(BoidFishEntity mob, float speed) {
        this.mob = mob;
        this.speed = speed;
    }

    @Override
    public boolean canUse() {
        return  mob.isInWaterOrBubble() && (mob.isFollower() || mob.hasFollowers());
    }

    @Override
    public void tick() {
        var velocity = mob.getDeltaMovement().normalize().scale(0.2).scale(speed);
        mob.setDeltaMovement(velocity);
        mob.lookAt(EntityAnchorArgument.Anchor.EYES, mob.position().add(velocity.scale(3)));
    }
}