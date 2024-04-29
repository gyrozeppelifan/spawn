package com.ninni.spawn.entity;

import com.ninni.spawn.SpawnTags;
import com.ninni.spawn.registry.SpawnItems;
import com.ninni.spawn.registry.SpawnSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class Sunfish extends PathfinderMob implements Bucketable {
    private static final EntityDataAccessor<Integer> AGE = SynchedEntityData.defineId(Sunfish.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(Sunfish.class, EntityDataSerializers.BOOLEAN);

    public Sunfish(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0f);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02f, 0.1f, true);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new PanicGoal(this, 1.8));
        //this.goalSelector.addGoal(3, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.4, Ingredient.of(SpawnTags.SUNFISH_TEMPTS), false));
        //this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.1));
        this.goalSelector.addGoal(6, new RandomSwimmingGoal(this, 1.0D, 10));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 30.0).add(Attributes.MOVEMENT_SPEED, 0.2f);
    }

    //@Override
    //public boolean isFood(ItemStack itemStack) {
    //    return itemStack.is(SpawnTags.SUNFISH_FEEDS);
    //}


    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand interactionHand) {
        if (this.isBaby() && Bucketable.bucketMobPickup(player, interactionHand, this).isPresent()) {
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, interactionHand);
    }

    public int getSunfishAge() {
        if (this.isBaby()) return this.getAge() < -12000 ? -2 : -1;
        return 0;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(AGE, 0);
        this.entityData.define(FROM_BUCKET, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putBoolean("FromBucket", this.fromBucket());
        compoundTag.putInt("Age", this.getAge());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setFromBucket(compoundTag.getBoolean("FromBucket"));
        this.setAge(compoundTag.getInt("Age"));
    }

    public int getAge() {
        return this.entityData.get(AGE);
    }

    public void setAge(int i) {
        this.entityData.set(AGE, i);
    }

    @Override
    public boolean fromBucket() {
        return this.entityData.get(FROM_BUCKET);
    }

    @Override
    public void setFromBucket(boolean bl) {
        this.entityData.set(FROM_BUCKET, bl);
    }

    @Override
    public void saveToBucketTag(ItemStack itemStack) {
        CompoundTag compoundTag = itemStack.getOrCreateTag();
        compoundTag.putInt("Age", this.getAge());
        Bucketable.saveDefaultDataToBucketTag(this, itemStack);
    }

    @Override
    public void loadFromBucketTag(CompoundTag compoundTag) {
        if (compoundTag.contains("Age")) {
            this.setAge(compoundTag.getInt("Age"));
        }
        Bucketable.loadDefaultDataFromBucketTag(this, compoundTag);
    }

    @Override
    public void travel(Vec3 vec3) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(0.01f, vec3);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
            if (this.getTarget() == null) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.005, 0.0));
            }
        } else {
            super.travel(vec3);
        }
    }


    @Override
    public void baseTick() {
        int i = this.getAirSupply();
        super.baseTick();
        if (this.getAge() < 0) this.setAge(this.getAge() + 1);
        this.handleAirSupply(i);
    }
    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level);
    }
    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
        return entityDimensions.height * 0.5f;
    }
    protected void handleAirSupply(int i) {
        if (this.isAlive() && !this.isInWaterOrBubble()) {
            this.setAirSupply(i - 1);
            if (this.getAirSupply() == -20) {
                this.setAirSupply(0);
                this.hurt(this.damageSources().drown(), 2.0f);
            }
        } else {
            this.setAirSupply(300);
        }
    }

    @Override
    public boolean removeWhenFarAway(double d) {
        return !this.fromBucket() && !this.hasCustomName();
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public MobType getMobType() {
        return MobType.WATER;
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader levelReader) {
        return levelReader.isUnobstructed(this);
    }

    @Override
    public int getAmbientSoundInterval() {
        return 120;
    }

    @Override
    public int getExperienceReward() {
        return 1 + this.level().random.nextInt(3);
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean canBeLeashed(Player player) {
        return true;
    }

    @Override
    protected boolean canRide(Entity entity) {
        return false;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SpawnSoundEvents.FISH_DEATH;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SpawnSoundEvents.BIG_FISH_SWIM;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SpawnSoundEvents.FISH_AMBIENT;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SpawnSoundEvents.FISH_HURT;
    }


    @Override
    protected void playStepSound(BlockPos blockPos, BlockState blockState) {}

    @SuppressWarnings("unused, deprecation")
    public static boolean checkSurfaceWaterAnimalSpawnRules(EntityType<Sunfish> mobEntityType, ServerLevelAccessor serverLevelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource) {
        int i = serverLevelAccessor.getSeaLevel();
        int j = i - 13;
        return blockPos.getY() >= j && blockPos.getY() <= i && serverLevelAccessor.getFluidState(blockPos.below()).is(FluidTags.WATER) && serverLevelAccessor.getBlockState(blockPos.above()).is(Blocks.WATER);
    }

    @Override
    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.fromBucket();
    }

    @Override
    public ItemStack getBucketItemStack() {
        return SpawnItems.BABY_SUNFISH_BUCKET.getDefaultInstance();
    }

    @Override
    public SoundEvent getPickupSound() {
        return SoundEvents.BUCKET_FILL_FISH;
    }

    @Override
    public boolean isBaby() {
        return this.getAge() < 0;
    }
}
