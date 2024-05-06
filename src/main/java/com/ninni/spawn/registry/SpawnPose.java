package com.ninni.spawn.registry;

import net.minecraft.world.entity.Pose;

public enum SpawnPose {
    NEWBORN,
    BABY;

    public Pose get() {
        return Pose.valueOf(this.name());
    }
}
