package com.ninni.spawn.entity.common;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;

public interface PathFindingFavors {
    default float getDepthPathfindingFavor(BlockPos pos, LevelReader world) {
        int y = pos.getY() + Math.abs(world.getMinBuildHeight());
        return 1f / (y == 0 ? 1 : y);
    }
    default float getSurfacePathfindingFavor(BlockPos pos, LevelReader world) {
        int y = Math.abs(world.getMaxBuildHeight()) - pos.getY();
        return 1f / (y == 0 ? 1 : y);
    }
}
