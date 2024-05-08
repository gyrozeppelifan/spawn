package com.ninni.spawn.entity;

import com.ninni.spawn.entity.common.BoidFishEntity;
import com.ninni.spawn.registry.SpawnItems;
import com.ninni.spawn.registry.SpawnSoundEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

public class Herring extends BoidFishEntity {
    private static final EntityDataAccessor<Integer> SCHOOL_SIZE = SynchedEntityData.defineId(Herring.class, EntityDataSerializers.INT);
    public Herring(EntityType<? extends BoidFishEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
        if (mobSpawnType != MobSpawnType.BUCKET) {
            this.setSchoolSize(random.nextInt(15) + 5);
        }
        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 2.0);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getMaxSchoolSize() != this.getSchoolSize()) this.SetMaxSchoolSize(this.getSchoolSize());
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SCHOOL_SIZE, 0);
    }

    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("SchoolSize", this.getSchoolSize());
    }

    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setSchoolSize(compoundTag.getInt("SchoolSize"));
    }

    public void saveToBucketTag(ItemStack itemStack) {
        super.saveToBucketTag(itemStack);
        CompoundTag compoundTag = itemStack.getOrCreateTag();
        compoundTag.putInt("SchoolSize", this.getSchoolSize());
    }

    @Override
    public void loadFromBucketTag(CompoundTag compoundTag) {
        super.loadFromBucketTag(compoundTag);
        if (compoundTag.contains("SchoolSize")) this.setSchoolSize(compoundTag.getInt("SchoolSize"));
    }

    public void setSchoolSize(int i) {
        this.entityData.set(SCHOOL_SIZE, i);
    }

    public int getSchoolSize() {
        return this.entityData.get(SCHOOL_SIZE);
    }


    @Override
    public int getMaxSchoolSize() {
        return this.getSchoolSize();
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(SpawnItems.HERRING_BUCKET);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.EMPTY;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SpawnSoundEvents.FISH_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SpawnSoundEvents.FISH_HURT;
    }

    @Override
    protected SoundEvent getFlopSound() {
        return SpawnSoundEvents.FISH_FLOP;
    }

}
