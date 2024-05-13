package com.ninni.spawn.client.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.ninni.spawn.Spawn;
import com.ninni.spawn.entity.Clam;
import com.ninni.spawn.entity.variant.ClamVariant;
import com.ninni.spawn.registry.SpawnTags;
import com.ninni.spawn.entity.Seahorse;
import com.ninni.spawn.mixin.accessor.TropicalFishAccessor;
import com.ninni.spawn.registry.SpawnEntityType;
import com.ninni.spawn.registry.SpawnItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.joml.Quaternionf;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public class PigmentShifterScreen extends AbstractContainerScreen<PigmentShifterMenu> {
    private static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(Spawn.MOD_ID, "textures/gui/container/fish.png");
    private static final Component BODY_PLAN_TOOLTIP = Component.translatable("container.spawn.pigment_shifter.body_plan");
    private static final Component BODY_PLAN_TOOLTIP_PRESET_COLOR = Component.translatable("container.spawn.pigment_shifter.body_plan.color");
    private static final Component PATTERN_TOOLTIP = Component.translatable("container.spawn.pigment_shifter.pattern");
    private static final Component BODY_DYE_TOOLTIP = Component.translatable("container.spawn.pigment_shifter.body_dye");
    private static final Component PATTERN_DYE_TOOLTIP = Component.translatable("container.spawn.pigment_shifter.pattern_dye");
    private final PigmentShifterMenu menu;
    private final Level level;
    private double rotationY = 0;
    private double rotateY = 0;

    public PigmentShifterScreen(PigmentShifterMenu customizerMenu, Inventory inventory, Component component) {
        super(customizerMenu, inventory, component);
        this.menu = customizerMenu;
        this.level = inventory.player.level();
    }

    @Override
    public void render(GuiGraphics poseStack, int i, int j, float f) {
        this.renderBackground(poseStack);
        super.render(poseStack, i, j, f);
        this.renderTooltip(poseStack, i, j);
        this.renderOnboardingTooltips(poseStack, i, j);
    }

    @Override
    protected void renderBg(GuiGraphics poseStack, float partialTicks, int x, int y) {
        ItemStack item = menu.resultSlot.getItem();
        int imgX = (this.width - this.imageWidth) / 2;
        int imgY = (this.height - this.imageHeight) / 2;

        this.renderBackground(poseStack);
        poseStack.blit(RESOURCE_LOCATION, imgX, imgY, 0, 0, this.imageWidth, this.imageHeight);

        Slot bodyDyeSlot = this.menu.bodyDyeSlot;
        Slot patternDyeSlot = this.menu.patternDyeSlot;
        Slot bucketSlot = this.menu.bucketSlot;
        Slot patternSlot = this.menu.patternSlot;
        Slot bodyPlanSlot = this.menu.bodyPlanSlot;

        if (!bodyDyeSlot.hasItem()) poseStack.blit(RESOURCE_LOCATION, imgX + bodyDyeSlot.x, imgY + bodyDyeSlot.y, this.imageWidth, 0, 16, 16);
        if (!patternDyeSlot.hasItem()) poseStack.blit(RESOURCE_LOCATION, imgX + patternDyeSlot.x, imgY + patternDyeSlot.y, this.imageWidth, 0, 16, 16);
        if (!bucketSlot.hasItem()) poseStack.blit(RESOURCE_LOCATION, imgX + bucketSlot.x, imgY + bucketSlot.y, this.imageWidth + 16, 0, 16, 16);
        if (!bodyPlanSlot.hasItem()) poseStack.blit(RESOURCE_LOCATION, imgX + bodyPlanSlot.x, imgY + bodyPlanSlot.y, this.imageWidth + 32, 0, 16, 16);
        if (!patternSlot.hasItem()) poseStack.blit(RESOURCE_LOCATION, imgX + patternSlot.x, imgY + patternSlot.y, this.imageWidth + 48, 0, 16, 16);
        if (bodyDyeSlot instanceof PigmentShifterSlot pigmentShifterSlot && pigmentShifterSlot.isDisabled()) poseStack.blit(RESOURCE_LOCATION, imgX + bodyDyeSlot.x, imgY + bodyDyeSlot.y, this.imageWidth + 64, 0, 16, 16);
        if (patternDyeSlot instanceof PigmentShifterSlot pigmentShifterSlot && pigmentShifterSlot.isDisabled()) poseStack.blit(RESOURCE_LOCATION, imgX + patternDyeSlot.x, imgY + patternDyeSlot.y, this.imageWidth + 64, 0, 16, 16);
        if (patternSlot instanceof PigmentShifterSlot pigmentShifterSlot && pigmentShifterSlot.isDisabled()) poseStack.blit(RESOURCE_LOCATION, imgX + patternSlot.x, imgY + patternSlot.y, this.imageWidth + 64, 0, 16, 16);
        if (bodyPlanSlot instanceof PigmentShifterSlot pigmentShifterSlot && pigmentShifterSlot.isDisabled()) poseStack.blit(RESOURCE_LOCATION, imgX + bodyPlanSlot.x, imgY + bodyPlanSlot.y, this.imageWidth + 64, 0, 16, 16);


        if (menu.resultSlot.hasItem() && item.is(SpawnTags.CUSTOMIZABLE_MOB_ITEMS)) {
            CompoundTag compoundTag = item.getTag();

            if (compoundTag != null && compoundTag.contains("ItemVariantTag", 3) && item.is(SpawnItems.CLAM)) {
                int tag = compoundTag.getInt("ItemVariantTag");
                Clam clam = SpawnEntityType.SpawnAquaticCreature.CLAM.create(this.level);
                clam.noPhysics = true;
                ClamVariant.Variant variant = new ClamVariant.Variant(Clam.getBaseColor(tag), Clam.getPattern(tag), Clam.getDyeColor(tag));
                clam.setPackedVariant(variant.getPackedId());
                int id = clam.getBaseColor().base().getId();
                int i = id + 2;
                int scale = 40;
                if (id == 2) scale = 30;
                int h = id == 0 ? 54 : id == 1 ? 50 : 60;

                renderMovableEntity(poseStack, imgX + 107, imgY + h, scale, clam, i);
                clam.discard();
            }

            if (compoundTag != null && compoundTag.contains("Variant", 3) && item.is(Items.AXOLOTL_BUCKET) && compoundTag.getInt("Variant") != 4) {
                int variant = compoundTag.getInt("Variant");

                Axolotl axolotl = EntityType.AXOLOTL.create(this.level);
                axolotl.noPhysics = true;
                axolotl.setVariant(Axolotl.Variant.byId(variant));
                renderMovableEntity(poseStack, imgX + 107, imgY + 60, 30, axolotl, 0);
                axolotl.discard();
            }

            if (compoundTag != null && compoundTag.contains("BucketVariantTag", 3)) {
                int tag = compoundTag.getInt("BucketVariantTag");

                if (item.is(Items.TROPICAL_FISH_BUCKET)) {
                    TropicalFish tropicalFish = EntityType.TROPICAL_FISH.create(this.level);
                    tropicalFish.noPhysics = true;
                    TropicalFish.Variant variant = new TropicalFish.Variant(TropicalFish.getPattern(tag), TropicalFish.getBaseColor(tag), TropicalFish.getPatternColor(tag));
                    ((TropicalFishAccessor) tropicalFish).callSetPackedVariant(variant.getPackedId());
                    renderMovableEntity(poseStack, imgX + 107, imgY + 50, 40, tropicalFish, 1);
                    tropicalFish.discard();
                }

                if (item.is(SpawnItems.SEAHORSE_BUCKET)) {
                    int h;

                    Seahorse seahorse = SpawnEntityType.SpawnFish.SEAHORSE.create(this.level);
                    seahorse.noPhysics = true;
                    Seahorse.Pattern pattern = Seahorse.getPattern(tag);
                    Seahorse.Variant variant = new Seahorse.Variant(pattern, Seahorse.getBaseColor(tag), Seahorse.getPatternColor(tag));
                    seahorse.setPackedVariant(variant.getPackedId());
                    if (pattern.base() == Seahorse.Base.LARGE) h = -10;
                    else h = 0;
                    renderMovableEntity(poseStack, imgX + 107, imgY + 64 + h, 40, seahorse, 0);
                    seahorse.discard();
                }

            }
        }

    }

    private void renderOnboardingTooltips(GuiGraphics guiGraphics, int i, int j) {
        Optional<Component> optional = Optional.empty();

        if (this.hoveredSlot != null) {
            if (this.menu.getSlot(this.menu.patternSlot.index).getItem().isEmpty() && !((PigmentShifterSlot)this.menu.patternSlot).isDisabled()) {
                if (this.hoveredSlot.index == this.menu.patternSlot.index) {
                    optional = Optional.of(PATTERN_TOOLTIP);
                }
            }
            if (this.menu.getSlot(this.menu.bodyPlanSlot.index).getItem().isEmpty() && !((PigmentShifterSlot)this.menu.bodyPlanSlot).isDisabled()) {
                if (this.hoveredSlot.index == this.menu.bodyPlanSlot.index) {
                    if (this.menu.bucketSlot.getItem().is(SpawnItems.CLAM) || this.menu.bucketSlot.getItem().is(Items.AXOLOTL_BUCKET)) optional = Optional.of(BODY_PLAN_TOOLTIP_PRESET_COLOR);
                    else optional = Optional.of(BODY_PLAN_TOOLTIP);
                }
            }
            if (this.menu.getSlot(this.menu.bodyDyeSlot.index).getItem().isEmpty() && !((PigmentShifterSlot)this.menu.bodyDyeSlot).isDisabled()) {
                if (this.hoveredSlot.index == this.menu.bodyDyeSlot.index) {
                    optional = Optional.of(BODY_DYE_TOOLTIP);
                }
            }
            if (this.menu.getSlot(this.menu.patternDyeSlot.index).getItem().isEmpty() && !((PigmentShifterSlot)this.menu.patternDyeSlot).isDisabled()) {
                if (this.hoveredSlot.index == this.menu.patternDyeSlot.index) {
                    optional = Optional.of(PATTERN_DYE_TOOLTIP);
                }
            }
        }

        optional.ifPresent((component) -> {
            guiGraphics.renderTooltip(this.font, this.font.split(component, 115), i, j);
        });
    }


    public void renderMovableEntity(GuiGraphics poseStack, int x, int y, int scale, LivingEntity entity, int rotate) {
        rotationY += rotateY;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        Quaternionf quaternionf = new Quaternionf().rotateX((float) Math.toRadians(180f));
        Quaternionf quaternionf1 = new Quaternionf().rotateY( ((float) rotationY));

        if (rotate == 1) {
            quaternionf.mul(new Quaternionf().rotateZ((float) Math.toRadians(90f)));
            quaternionf.mul(new Quaternionf().rotateX((float) rotationY));
        } else if (rotate == 2) {
            quaternionf.mul( new Quaternionf().rotateX((float) Math.toRadians(90f)));
            quaternionf.mul(new Quaternionf().rotateZ((float) -rotationY));
        } else if (rotate == 3) {
            quaternionf.mul(new Quaternionf().rotateX((float) Math.toRadians(-90f)));
            quaternionf.mul(new Quaternionf().rotateZ((float) rotationY));
        } else quaternionf.mul(quaternionf1);

        InventoryScreen.renderEntityInInventory(poseStack, x, y, scale, quaternionf, quaternionf1, entity);
    }


    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        rotateY = pDragX / 40f;
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        rotateY = 0.01;
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }
}
