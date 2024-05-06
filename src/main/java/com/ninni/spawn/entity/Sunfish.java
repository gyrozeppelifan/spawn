package com.ninni.spawn.entity;

import com.ninni.spawn.entity.common.DeepLurker;
import com.ninni.spawn.registry.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import java.util.function.IntFunction;


public class Sunfish extends PathfinderMob implements Bucketable, VariantHolder<Sunfish.Variant>, DeepLurker {
    private static final EntityDataAccessor<Integer> AGE = SynchedEntityData.defineId(Sunfish.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(Sunfish.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(Sunfish.class, EntityDataSerializers.INT);
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState landAnimationState = new AnimationState();
    public final AnimationState flopAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;
    private int landAnimationTimeout = 0;
    private int inLove;
    @Nullable
    private UUID loveCause;

    public Sunfish(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0f);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02f, 0.1f, true);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new PanicGoal(this, 1.8));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.4, Ingredient.of(SpawnTags.SUNFISH_TEMPTS), false));
        this.goalSelector.addGoal(6, new RandomSwimmingGoal(this, 1.0D, 10));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 30.0).add(Attributes.MOVEMENT_SPEED, 0.8f);
    }

    @Override
    public float getWalkTargetValue(BlockPos blockPos, LevelReader levelReader) {
        if (this.level().isDay()) return this.getLurkingPathfindingFavor(blockPos, levelReader);
        return super.getWalkTargetValue(blockPos, levelReader);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
        if (mobSpawnType == MobSpawnType.BUCKET) {
            return spawnGroupData;
        } else {
            int i = serverLevelAccessor.getBiome(blockPosition()).is(SpawnTags.SUNFISH_WARM_VARIANT) ? random.nextInt(0,2) : random.nextInt(2, 4);
            this.setVariant(Variant.byId(i));
            return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
        }
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);

        if (this.isBaby() && Bucketable.bucketMobPickup(player, interactionHand, this).isPresent()) {
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        if (itemStack.is(SpawnTags.SUNFISH_FEEDS)) {
            int i = this.getAge();
            if (!this.level().isClientSide && i == 0 && this.canFallInLove()) {
                if (!player.isCreative()) itemStack.shrink(1);
                this.setInLove(player);
                return InteractionResult.SUCCESS;
            }
            if (this.isBaby()) {
                if (!player.isCreative()) itemStack.shrink(1);
                this.setAge(this.getAge() + 15 * 20);
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
            if (this.level().isClientSide) {
                return InteractionResult.CONSUME;
            }
        }

        return super.mobInteract(player, interactionHand);
    }

    //region Love and Breeding


    public void spawnChildFromBreeding(ServerLevel serverLevel, Sunfish sunfish) {
        Sunfish baby = SpawnEntityType.SpawnFish.SUNFISH.create(serverLevel);
        if (baby == null) return;
        ServerPlayer serverPlayer = this.getLoveCause();
        if (serverPlayer == null && sunfish.getLoveCause() != null) serverPlayer = sunfish.getLoveCause();
        if (serverPlayer != null) {
            serverPlayer.awardStat(Stats.ANIMALS_BRED);
            SpawnCriteriaTriggers.BREED_SUNFISH.trigger(serverPlayer);
        }
        this.setAge(6000);
        sunfish.setAge(6000);
        this.resetLove();
        sunfish.resetLove();

        baby.setPersistenceRequired();
        baby.setAge(-24000);
        baby.moveTo(this.getX(), this.getY(), this.getZ(), 0.0f, 0.0f);
        serverLevel.addFreshEntity(baby);
        serverLevel.broadcastEntityEvent(this, (byte)18);
        if (serverLevel.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            serverLevel.addFreshEntity(new ExperienceOrb(serverLevel, this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(7) + 1));
        }
    }

    @Override
    protected void customServerAiStep() {
        if (this.getAge() != 0) {
            this.inLove = 0;
        }
        super.customServerAiStep();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.getAge() != 0) {
            this.inLove = 0;
        }
        if (this.inLove > 0) {
            --this.inLove;
            if (this.inLove % 10 == 0) {
                double d = this.random.nextGaussian() * 0.02;
                double e = this.random.nextGaussian() * 0.02;
                double f = this.random.nextGaussian() * 0.02;
                this.level().addParticle(ParticleTypes.HEART, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), d, e, f);
            }
        }
    }

    @Override
    public boolean hurt(DamageSource damageSource, float f) {
        if (this.isInvulnerableTo(damageSource)) {
            return false;
        }
        this.inLove = 0;
        return super.hurt(damageSource, f);
    }

    public boolean canFallInLove() {
        return this.inLove <= 0;
    }

    public void setInLove(@Nullable Player player) {
        this.inLove = 600;
        if (player != null) {
            this.loveCause = player.getUUID();
        }
        this.level().broadcastEntityEvent(this, (byte)18);
    }

    @Nullable
    public ServerPlayer getLoveCause() {
        if (this.loveCause == null) {
            return null;
        }
        Player player = this.level().getPlayerByUUID(this.loveCause);
        if (player instanceof ServerPlayer) {
            return (ServerPlayer)player;
        }
        return null;
    }

    public boolean isInLove() {
        return this.inLove > 0;
    }

    public void resetLove() {
        this.inLove = 0;
    }

    public boolean canMate(Sunfish sunfish) {
        if (sunfish == this || sunfish.getClass() != this.getClass()) return false;
        return this.isInLove() && sunfish.isInLove();
    }

    @Override
    public void handleEntityEvent(byte b) {
        if (b == 18) {
            for (int i = 0; i < 7; ++i) {
                double d = this.random.nextGaussian() * 0.02;
                double e = this.random.nextGaussian() * 0.02;
                double f = this.random.nextGaussian() * 0.02;
                this.level().addParticle(ParticleTypes.HEART, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), d, e, f);
            }
        } else {
            super.handleEntityEvent(b);
        }
    }

    public static class BreedGoal extends Goal {
        private static final TargetingConditions PARTNER_TARGETING = TargetingConditions.forNonCombat().range(8.0).ignoreLineOfSight();
        protected final Sunfish sunfish;
        private final Class<? extends Sunfish> partnerClass;
        protected final Level level;
        @Nullable
        protected Sunfish partner;
        private int loveTime;
        private final double speedModifier;

        public BreedGoal(Sunfish sunfish, double d) {
            this(sunfish, d, sunfish.getClass());
        }

        public BreedGoal(Sunfish sunfish, double d, Class<? extends Sunfish> class_) {
            this.sunfish = sunfish;
            this.level = sunfish.level();
            this.partnerClass = class_;
            this.speedModifier = d;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (!this.sunfish.isInLove()) {
                return false;
            }
            this.partner = this.getFreePartner();
            return this.partner != null;
        }

        @Override
        public boolean canContinueToUse() {
            return this.partner.isAlive() && this.partner.isInLove() && this.loveTime < 60;
        }

        @Override
        public void stop() {
            this.partner = null;
            this.loveTime = 0;
        }

        @Override
        public void tick() {
            this.sunfish.getLookControl().setLookAt(this.partner, 10.0f, this.sunfish.getMaxHeadXRot());
            this.sunfish.getNavigation().moveTo(this.partner, this.speedModifier);
            ++this.loveTime;
            if (this.loveTime >= this.adjustedTickDelay(60) && this.sunfish.distanceToSqr(this.partner) < 9.0) {
                this.breed();
            }
        }

        @Nullable
        private Sunfish getFreePartner() {
            List<? extends Sunfish> list = this.level.getNearbyEntities(this.partnerClass, PARTNER_TARGETING, this.sunfish, this.sunfish.getBoundingBox().inflate(8.0));
            double d = Double.MAX_VALUE;
            Sunfish sunfish1 = null;
            for (Sunfish animal2 : list) {
                if (!this.sunfish.canMate(animal2) || !(this.sunfish.distanceToSqr(animal2) < d)) continue;
                sunfish1 = animal2;
                d = this.sunfish.distanceToSqr(animal2);
            }
            return sunfish1;
        }

        protected void breed() {
            for (int i = 0; i <= (this.sunfish.random.nextInt(0,5) + 1); i++) {
                this.sunfish.spawnChildFromBreeding((ServerLevel)this.level, this.partner);
            }
        }
    }

    //endregion

    //region Age and Animation

    @Override
    public void tick() {
        super.tick();

        if (this.tickCount % 10 == 0) {
            if (!this.isBaby()) {
                if (!this.isInWaterOrBubble()) {
                    if (this.getPose() != Pose.STANDING) this.setPose(Pose.STANDING);
                } else {
                    if (this.getPose() != Pose.SWIMMING) this.setPose(Pose.SWIMMING);
                }
            } else {
                SpawnPose pose = this.getSunfishAge() == -2 ? SpawnPose.NEWBORN : SpawnPose.BABY;
                if (this.getPose() != pose.get()) this.setPose(pose.get());
            }

            refreshDimensions();
        }


        if (this.level().isClientSide()) {
            this.setupAnimationStates();
        }
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        if (this.isBaby()) {
            return pose == SpawnPose.NEWBORN.get() ? EntityDimensions.scalable(0.2F, 0.2F) : EntityDimensions.scalable(0.6F, 0.6F);
        } else {
            if (pose == Pose.STANDING) return EntityDimensions.scalable(2.2F, 0.5F);
            else return EntityDimensions.scalable(1.5F, 2.2F);
        }
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
        return entityDimensions.height * 0.5f;
    }

    private void setupAnimationStates() {
        if (!this.isBaby()) {
            if (this.isInWaterOrBubble()) {
                if (this.idleAnimationTimeout <= 0) {
                    this.idleAnimationTimeout = 20 * 4;
                    this.idleAnimationState.start(this.tickCount);
                } else {
                    --this.idleAnimationTimeout;
                }
            } else {
                if (this.landAnimationTimeout <= 0) {
                    this.landAnimationTimeout = 20 * 2;
                    this.landAnimationState.start(this.tickCount);
                } else {
                    --this.landAnimationTimeout;
                }

            }
        }
    }

    public int getSunfishAge() {
        if (this.isBaby()) return this.getAge() < -12000 ? -2 : -1;
        return 0;
    }

    @Override
    public boolean isBaby() {
        return this.getAge() < 0;
    }

    //endregion

    //region Data

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(AGE, 0);
        this.entityData.define(FROM_BUCKET, false);
        this.entityData.define(VARIANT, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putBoolean("FromBucket", this.fromBucket());
        compoundTag.putInt("Age", this.getAge());
        compoundTag.putInt("Variant", this.getVariant().getId());
        compoundTag.putInt("InLove", this.inLove);
        if (this.loveCause != null) {
            compoundTag.putUUID("LoveCause", this.loveCause);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setFromBucket(compoundTag.getBoolean("FromBucket"));
        this.setVariant(Variant.byId(compoundTag.getInt("Variant")));
        this.setAge(compoundTag.getInt("Age"));
        this.inLove = compoundTag.getInt("InLove");
        this.loveCause = compoundTag.hasUUID("LoveCause") ? compoundTag.getUUID("LoveCause") : null;
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
        compoundTag.putInt("Variant", this.getVariant().getId());
        Bucketable.saveDefaultDataToBucketTag(this, itemStack);
    }

    @Override
    public void loadFromBucketTag(CompoundTag compoundTag) {
        if (compoundTag.contains("Age")) this.setAge(compoundTag.getInt("Age"));
        if (compoundTag.contains("Variant")) this.setVariant(Variant.byId(compoundTag.getInt("Variant")));
        Bucketable.loadDefaultDataFromBucketTag(this, compoundTag);
    }

    @Override
    public void setVariant(Variant variant) {
        this.entityData.set(VARIANT, variant.getId());
    }

    @Override
    public Variant getVariant() {
        return Variant.byId(this.entityData.get(VARIANT));
    }


    public enum Variant implements StringRepresentable {
        PLAIN(0, "plain"),
        STRIPED(1, "striped"),
        PLAIN_DARK(2, "plain_dark"),
        STRIPED_DARK(3, "striped_dark");

        private static final IntFunction<Variant> BY_ID = ByIdMap.sparse(Variant::getId, Variant.values(), PLAIN);
        private final int id;
        private final String name;

        Variant(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return this.id;
        }

        public String getSerializedName() {
            return this.name;
        }

        public static Variant byId(int id) {
            return BY_ID.apply(id);
        }
    }

    //endregion

    //region Water Mob code

    @Override
    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.fromBucket();
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
    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level);
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

    @Override
    public ItemStack getBucketItemStack() {
        return SpawnItems.BABY_SUNFISH_BUCKET.getDefaultInstance();
    }

    @Override
    public SoundEvent getPickupSound() {
        return SoundEvents.BUCKET_FILL_FISH;
    }

    //endregion

    //region Sounds

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
        return SoundEvents.EMPTY;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SpawnSoundEvents.FISH_HURT;
    }

    @Override
    protected void playStepSound(BlockPos blockPos, BlockState blockState) {}

    //endregion


    @SuppressWarnings("unused, deprecation")
    public static boolean checkSurfaceWaterAnimalSpawnRules(EntityType<Sunfish> mobEntityType, ServerLevelAccessor serverLevelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource randomSource) {
        int i = serverLevelAccessor.getSeaLevel();
        int j = i - 13;
        return blockPos.getY() >= j && blockPos.getY() <= i && serverLevelAccessor.getFluidState(blockPos.below()).is(FluidTags.WATER) && serverLevelAccessor.getBlockState(blockPos.above()).is(Blocks.WATER);
    }

}
