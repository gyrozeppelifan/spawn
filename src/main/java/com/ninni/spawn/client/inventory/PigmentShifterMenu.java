package com.ninni.spawn.client.inventory;

import com.ninni.spawn.entity.Clam;
import com.ninni.spawn.entity.variant.ClamVariant;
import com.ninni.spawn.registry.SpawnTags;
import com.ninni.spawn.entity.Seahorse;
import com.ninni.spawn.mixin.accessor.TropicalFishAccessor;
import com.ninni.spawn.registry.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class PigmentShifterMenu extends AbstractContainerMenu {
    private final Level level;
    private final ContainerLevelAccess access;
    final Slot bodyDyeSlot;
    final Slot patternDyeSlot;
    final Slot patternSlot;
    final Slot bodyPlanSlot;
    final Slot bucketSlot;
    final Slot resultSlot;
    long lastSoundTime;

    public final Container inputContainer = new SimpleContainer(5) {
        @Override
        public void setChanged() {
            PigmentShifterMenu.this.slotsChanged(this);
            super.setChanged();
        }
    };
    private final ResultContainer outputContainer = new ResultContainer() {
        @Override
        public void setChanged() {
            PigmentShifterMenu.this.slotsChanged(this);
            super.setChanged();
        }
    };

    public PigmentShifterMenu(int i, Inventory inventory) {
        this(i, inventory, ContainerLevelAccess.NULL);
    }

    public PigmentShifterMenu(int i, Inventory inventory, ContainerLevelAccess containerLevelAccess) {
        super(SpawnMenuTypes.FISH_CUSTOMIZER_MENU, i);

        this.access = containerLevelAccess;
        this.level = inventory.player.level();

        this.bucketSlot = this.addSlot(new Slot(this.inputContainer, 0, 14, 35){
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return itemStack.is(SpawnTags.CUSTOMIZABLE_MOB_ITEMS);
            }
        });
        this.bodyDyeSlot = this.addSlot(new PigmentShifterSlot(this.inputContainer, 1, 57, 26){
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return itemStack.getItem() instanceof DyeItem;
            }
        });
        this.patternDyeSlot = this.addSlot(new PigmentShifterSlot(this.inputContainer, 2, 57, 45){
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return itemStack.getItem() instanceof DyeItem;
            }
        });
        this.bodyPlanSlot = this.addSlot(new PigmentShifterSlot(this.inputContainer, 3, 38, 26){
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return itemStack.is(SpawnTags.CHANGES_BODY_PLAN);
            }
        });
        this.patternSlot = this.addSlot(new PigmentShifterSlot(this.inputContainer, 4, 38, 45){
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return itemStack.is(SpawnTags.CHANGES_PATTERN);
            }
        });
        this.resultSlot = this.addSlot(new Slot(this.outputContainer, 0, 145, 35){

            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack itemStack) {
                PigmentShifterMenu.this.bucketSlot.remove(1);
                PigmentShifterMenu.this.bodyDyeSlot.remove(1);
                PigmentShifterMenu.this.patternDyeSlot.remove(1);
                PigmentShifterMenu.this.bodyPlanSlot.remove(1);
                PigmentShifterMenu.this.patternSlot.remove(1);

                containerLevelAccess.execute((level, blockPos) -> {
                    long l = level.getGameTime();
                    if (PigmentShifterMenu.this.lastSoundTime != l) {
                        level.playSound(null, blockPos, SpawnSoundEvents.FISH_FLOP, SoundSource.BLOCKS, 1.0f, 1.0f);
                        PigmentShifterMenu.this.lastSoundTime = l;
                    }

                });
                super.onTake(player, itemStack);
            }
        });

        int l;
        int m;
        for (l = 0; l < 3; ++l) {
            for (m = 0; m < 9; ++m) {
                this.addSlot(new Slot(inventory, m + l * 9 + 9, 8 + m * 18, 102 + l * 18 - 18));
            }
        }
        for (l = 0; l < 9; ++l) {
            this.addSlot(new Slot(inventory, l, 8 + l * 18, 142));
        }
    }

    @Override
    public void slotsChanged(Container container) {
        ItemStack bucketSlotItem = this.bucketSlot.getItem();
        ItemStack bodyDyeSlotItem = this.bodyDyeSlot.getItem();
        ItemStack patternDyeSlotItem = this.patternDyeSlot.getItem();
        ItemStack patternSlotItem = this.patternSlot.getItem();
        ItemStack bodyPlanSlotItem = this.bodyPlanSlot.getItem();

        CompoundTag compoundTag = bucketSlotItem.getTag();
        if (compoundTag != null) {

            if (bucketSlotItem.is(SpawnTags.CUSTOMIZABLE_MOB_ITEMS)) {
                if (!bucketSlotItem.isEmpty()) {

                    ItemStack resultCopy = bucketSlotItem.copy();
                    resultCopy.setCount(1);

                    if (bucketSlotItem.is(SpawnItems.CLAM) && compoundTag.contains("ItemVariantTag", 3)) {
                        int tag = bucketSlotItem.getTag().getInt("ItemVariantTag");
                        ((PigmentShifterSlot)this.bodyDyeSlot).setDisabled(true);

                        DyeColor patternColor = Clam.getDyeColor(tag);
                        ClamVariant.Pattern pattern = Clam.getPattern(tag);
                        ClamVariant.BaseColor baseColor = Clam.getBaseColor(tag);

                        int shiftedPatternId = pattern.getId() + 1;
                        if (pattern.getId() == 4) shiftedPatternId = 0;
                        int shiftedBaseColorId = getClamBaseColor(baseColor);

                        ClamVariant.Pattern newPattern = ClamVariant.Pattern.byId(shiftedPatternId);
                        ClamVariant.BaseColor newBaseColor = ClamVariant.BaseColor.byId(shiftedBaseColorId);

                        if (patternDyeSlotItem.getItem() instanceof DyeItem dyeItem) patternColor = dyeItem.getDyeColor();
                        if (patternSlotItem.is(SpawnTags.CHANGES_PATTERN) && newPattern != null) pattern = newPattern;
                        if (bodyPlanSlotItem.is(SpawnTags.CHANGES_BODY_PLAN) && newBaseColor != null) baseColor = newBaseColor;

                        ClamVariant.Variant variant = new ClamVariant.Variant(baseColor, pattern, patternColor);
                        Clam clam = SpawnEntityType.SpawnAquaticCreature.CLAM.create(this.level);
                        clam.setPackedVariant(variant.getPackedId());
                        resultCopy.getOrCreateTag().putInt("ItemVariantTag", clam.getPackedVariant());
                        clam.discard();

                        this.outputContainer.setItem(4, resultCopy);
                    } else if (bucketSlotItem.is(Items.AXOLOTL_BUCKET) && compoundTag.contains("Variant", 3)) {
                        int variantId = bucketSlotItem.getTag().getInt("Variant");

                        if (variantId != 4) {
                            ((PigmentShifterSlot)this.bodyDyeSlot).setDisabled(true);
                            ((PigmentShifterSlot)this.patternSlot).setDisabled(true);
                            ((PigmentShifterSlot)this.patternDyeSlot).setDisabled(true);

                            int shiftedId = variantId + 1;
                            if (variantId == 3) shiftedId = 0;

                            if (bodyPlanSlotItem.is(SpawnTags.CHANGES_BODY_PLAN)) resultCopy.getOrCreateTag().putInt("Variant", shiftedId);
                            this.outputContainer.setItem(4, resultCopy);
                        }
                    } else {
                        setAllSlotsDisabled(false);

                        if (bucketSlotItem.is(SpawnItems.SEAHORSE_BUCKET) && compoundTag.contains("BucketVariantTag", 3)) {
                            int tag = bucketSlotItem.getTag().getInt("BucketVariantTag");
                            DyeColor bodyColor = Seahorse.getBaseColor(tag);
                            DyeColor patternColor = Seahorse.getPatternColor(tag);
                            Seahorse.Pattern pattern = Seahorse.getPattern(tag);
                            int shiftedId = getSeahorsePattern(pattern);
                            Seahorse.Pattern newPattern = Seahorse.Pattern.byId(shiftedId);
                            if (bodyDyeSlotItem.getItem() instanceof DyeItem dyeItem) bodyColor = dyeItem.getDyeColor();
                            if (patternDyeSlotItem.getItem() instanceof DyeItem dyeItem) patternColor = dyeItem.getDyeColor();
                            if ((patternSlotItem.is(SpawnTags.CHANGES_PATTERN) || bodyPlanSlotItem.is(SpawnTags.CHANGES_BODY_PLAN)) && newPattern != null) pattern = newPattern;

                            Seahorse.Variant variant = new Seahorse.Variant(pattern, bodyColor, patternColor);
                            Seahorse seahorse = SpawnEntityType.SpawnFish.SEAHORSE.create(this.level);
                            seahorse.setPackedVariant(variant.getPackedId());
                            resultCopy.getOrCreateTag().putInt("BucketVariantTag", seahorse.getPackedVariant());
                            seahorse.discard();
                        }

                        if (bucketSlotItem.is(Items.TROPICAL_FISH_BUCKET) && compoundTag.contains("BucketVariantTag", 3)) {
                            int tag = bucketSlotItem.getTag().getInt("BucketVariantTag");
                            DyeColor bodyColor = TropicalFish.getBaseColor(tag);
                            DyeColor patternColor = TropicalFish.getPatternColor(tag);
                            TropicalFish.Pattern pattern = TropicalFish.getPattern(tag);
                            int shiftedId = getTropicalFishPattern(pattern);
                            TropicalFish.Pattern newPattern = TropicalFish.Pattern.byId(shiftedId);

                            if (bodyDyeSlotItem.getItem() instanceof DyeItem dyeItem) bodyColor = dyeItem.getDyeColor();
                            if (patternDyeSlotItem.getItem() instanceof DyeItem dyeItem) patternColor = dyeItem.getDyeColor();
                            if ((patternSlotItem.is(SpawnTags.CHANGES_PATTERN) || bodyPlanSlotItem.is(SpawnTags.CHANGES_BODY_PLAN)) && newPattern != null) pattern = newPattern;

                            TropicalFish.Variant variant = new TropicalFish.Variant(pattern, bodyColor, patternColor);
                            TropicalFish tropicalFish = EntityType.TROPICAL_FISH.create(this.level);
                            ((TropicalFishAccessor) tropicalFish).callSetPackedVariant(variant.getPackedId());
                            resultCopy.getOrCreateTag().putInt("BucketVariantTag", ((TropicalFishAccessor) tropicalFish).callGetPackedVariant());
                            tropicalFish.discard();
                        }

                        this.outputContainer.setItem(4, resultCopy);
                    }
                } else {
                    setAllSlotsDisabled(false);
                    this.outputContainer.removeItemNoUpdate(4);
                }
            } else {
                setAllSlotsDisabled(false);
                this.outputContainer.removeItemNoUpdate(4);
            }
        } else {
            setAllSlotsDisabled(false);
            this.outputContainer.removeItemNoUpdate(4);
        }
    }

    private void setAllSlotsDisabled(boolean disabled) {
        ((PigmentShifterSlot)this.patternDyeSlot).setDisabled(disabled);
        ((PigmentShifterSlot)this.bodyDyeSlot).setDisabled(disabled);
        ((PigmentShifterSlot)this.bodyPlanSlot).setDisabled(disabled);
        ((PigmentShifterSlot)this.patternSlot).setDisabled(disabled);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.outputContainer.removeItemNoUpdate(4);
        this.access.execute((world, pos) -> this.clearContainer(player, this.inputContainer));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(i);

        if (slot != null && slot.hasItem()) {

            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.copy();

            if (i == this.resultSlot.index) {

                if (!this.moveItemStackTo(itemStack2, 4, 40, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemStack2, itemStack);
            } else if (i == this.bodyDyeSlot.index || i == this.patternDyeSlot.index || i == this.bucketSlot.index || i == this.bodyPlanSlot.index || i == this.patternSlot.index ?
                    !this.moveItemStackTo(itemStack2, 4, 40, false)

                    : itemStack2.getItem() instanceof DyeItem && (((PigmentShifterSlot)this.bodyDyeSlot).isDisabled() || !this.moveItemStackTo(itemStack2, this.bodyDyeSlot.index, this.bodyDyeSlot.index + 1, false)) ?
                    (((PigmentShifterSlot)this.patternDyeSlot).isDisabled() || !this.moveItemStackTo(itemStack2, this.patternDyeSlot.index, this.patternDyeSlot.index + 1, false))

                    : itemStack2.is(SpawnTags.CHANGES_BODY_PLAN) ?
                    (((PigmentShifterSlot)this.bodyPlanSlot).isDisabled() || !this.moveItemStackTo(itemStack2, this.bodyPlanSlot.index, this.bodyPlanSlot.index + 1, false))
                    : itemStack2.is(SpawnTags.CHANGES_PATTERN) ?
                    (((PigmentShifterSlot)this.patternSlot).isDisabled() || !this.moveItemStackTo(itemStack2, this.patternSlot.index, this.patternSlot.index + 1, false))

                    : itemStack2.is(SpawnTags.CUSTOMIZABLE_MOB_ITEMS) ?
                    !this.moveItemStackTo(itemStack2, this.bucketSlot.index, this.bucketSlot.index + 1, false) : i >= 4 && i < 31 ?
                    !this.moveItemStackTo(itemStack2, 31, 40, false) : i >= 31 && i < 40 && !this.moveItemStackTo(itemStack2, 4, 31, false)
            ) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, itemStack2);
        }
        return itemStack;
    }

    public boolean stillValid(Player player) {
        return stillValid(this.access, player, SpawnBlocks.PIGMENT_SHIFTER);
    }

    private static int getClamBaseColor(ClamVariant.BaseColor baseColor) {
        int shiftedBaseColorId = baseColor.getId() + 1;
        int limit = 3;
        int start = 0;
        if (baseColor.base().getId() == 1) {
            start = 4;
            limit = 7;
        }
        if (baseColor.base().getId() == 2) {
            start = 8;
            limit = 11;
        }
        if (baseColor.getId() == limit) shiftedBaseColorId = start;
        return shiftedBaseColorId;
    }

    private int getSeahorsePattern(Seahorse.Pattern pattern) {
        Seahorse.Base bodyPlan = pattern.base();

        int i = this.patternSlot.getItem().is(SpawnTags.CHANGES_PATTERN) ? 1 : 0;
        int newId = (pattern.getPackedId() >> 8) + i;
        int shiftedId = newId << 8;

        if (this.bodyPlanSlot.getItem().is(SpawnTags.CHANGES_BODY_PLAN)) {
            if (bodyPlan == Seahorse.Base.LARGE) {
                if (pattern.getPackedId() >> 8 == 3 && i == 1) shiftedId = 0;
            } else {
                if (pattern.getPackedId() >> 8 == 3 && i == 1) shiftedId = 1;
                else shiftedId += 1;
            }
        } else {
            if (bodyPlan == Seahorse.Base.LARGE) {
                if (pattern.getPackedId() >> 8 == 3 && i == 1) shiftedId = 1;
                else shiftedId += 1;
            }
        }
        return shiftedId;
    }

    private int getTropicalFishPattern(TropicalFish.Pattern pattern) {
        TropicalFish.Base bodyPlan = pattern.base();

        int i = this.patternSlot.getItem().is(SpawnTags.CHANGES_PATTERN) ? 1 : 0;
        int newId = (pattern.getPackedId() >> 8) + i;
        int shiftedId = newId << 8;

        if (this.bodyPlanSlot.getItem().is(SpawnTags.CHANGES_BODY_PLAN)) {
            if (bodyPlan == TropicalFish.Base.LARGE) {
                if (pattern.getPackedId() >> 8 == 5 && i == 1) shiftedId = 0;
            } else {
                if (pattern.getPackedId() >> 8 == 5 && i == 1) shiftedId = 1;
                else shiftedId += 1;
            }
        } else {
            if (bodyPlan == TropicalFish.Base.LARGE) {
                if (pattern.getPackedId() >> 8 == 5 && i == 1) shiftedId = 1;
                else shiftedId += 1;
            }
        }
        return shiftedId;
    }
}
