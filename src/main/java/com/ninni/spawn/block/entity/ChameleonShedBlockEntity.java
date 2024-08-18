package com.ninni.spawn.block.entity;

import com.ninni.spawn.registry.SpawnBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;


public class ChameleonShedBlockEntity extends BlockEntity {
    String TAG_COLOR = "color";
    public int color = 0xFFFFFF;

    public ChameleonShedBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(SpawnBlockEntityTypes.CHAMELEON_SHED_BLOCK, blockPos, blockState);
    }

    public int getColor() {
        return this.color;
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);

        if (nbt.contains(TAG_COLOR)) this.color = nbt.getInt(TAG_COLOR);
        else this.color = 0xFFFFFF;
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt(TAG_COLOR, color);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putInt(TAG_COLOR, color);
        return compoundTag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return super.getUpdatePacket();
    }
}
