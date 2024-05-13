package com.ninni.spawn.client.inventory;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.ninni.spawn.Spawn;
import com.ninni.spawn.registry.SpawnTags;
import com.ninni.spawn.entity.Seahorse;
import com.ninni.spawn.mixin.accessor.TropicalFishAccessor;
import com.ninni.spawn.registry.SpawnEntityType;
import com.ninni.spawn.registry.SpawnItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraft.world.level.Level;
import org.joml.Quaternionf;

import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class PigmentShifterScreen extends AbstractContainerScreen<PigmentShifterMenu> {
    private static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(Spawn.MOD_ID, "textures/gui/container/fish.png");
    private static final Component BODY_PLAN_TOOLTIP = Component.translatable("container.spawn.pigment_shifter.body_plan");
    private static final Component PATTERN_TOOLTIP = Component.translatable("container.spawn.pigment_shifter.pattern");
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
        if (!patternSlot.hasItem()) poseStack.blit(RESOURCE_LOCATION, imgX + patternSlot.x, imgY + patternSlot.y, this.imageWidth + 32, 0, 16, 16);


        if (menu.resultSlot.hasItem() && item.is(SpawnTags.CUSTOMIZABLE_MOB_ITEMS)) {

            CompoundTag compoundTag = item.getTag();
            if (compoundTag != null && compoundTag.contains("BucketVariantTag", 3)) {
                int tag = compoundTag.getInt("BucketVariantTag");

                if (item.is(Items.TROPICAL_FISH_BUCKET)) {
                    TropicalFish tropicalFish = EntityType.TROPICAL_FISH.create(this.level);
                    TropicalFish.Variant variant = new TropicalFish.Variant(TropicalFish.getPattern(tag), TropicalFish.getBaseColor(tag), TropicalFish.getPatternColor(tag));
                    ((TropicalFishAccessor) tropicalFish).callSetPackedVariant(variant.getPackedId());

                    renderMovableEntity(poseStack, imgX + 107, imgY + 54, 40, tropicalFish, true);
                    tropicalFish.discard();
                }

                if (item.is(SpawnItems.SEAHORSE_BUCKET)) {
                    int h;

                    Seahorse seahorse = SpawnEntityType.SpawnFish.SEAHORSE.create(this.level);
                    Seahorse.Pattern pattern = Seahorse.getPattern(tag);
                    Seahorse.Variant variant = new Seahorse.Variant(pattern, Seahorse.getBaseColor(tag), Seahorse.getPatternColor(tag));
                    seahorse.setPackedVariant(variant.getPackedId());
                    if (pattern.base() == Seahorse.Base.LARGE) h = -10;
                    else h = 0;
                    renderMovableEntity(poseStack, imgX + 107, imgY + 64 + h, 40, seahorse, false);
                    seahorse.discard();
                }

            }
        }

    }

    private void renderOnboardingTooltips(GuiGraphics guiGraphics, int i, int j) {
        Optional<Component> optional = Optional.empty();

        if (this.hoveredSlot != null) {
            ItemStack itemStack = this.menu.getSlot(this.menu.patternSlot.index).getItem();
            ItemStack itemStack2 = this.menu.getSlot(this.menu.bodyPlanSlot.index).getItem();
            if (itemStack.isEmpty()) {
                if (this.hoveredSlot.index == this.menu.patternSlot.index) {
                    optional = Optional.of(PATTERN_TOOLTIP);
                }
            }
            if (itemStack2.isEmpty()) {
                if (this.hoveredSlot.index == this.menu.bodyPlanSlot.index) {
                    optional = Optional.of(BODY_PLAN_TOOLTIP);
                }
            }
        }

        optional.ifPresent((component) -> {
            guiGraphics.renderTooltip(this.font, this.font.split(component, 115), i, j);
        });
    }


    public void renderMovableEntity(GuiGraphics poseStack, int x, int y, int scale, LivingEntity entity, boolean rotate) {
        rotationY += rotateY;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        Quaternionf quaternionf = new Quaternionf().rotateX((float) Math.toRadians(180f));
        Quaternionf quaternionf1 = new Quaternionf().rotateY( ((float) rotationY));
        Quaternionf quaternionf2 = new Quaternionf().rotateZ((float) Math.toRadians(90f));
        Quaternionf quaternionf3 = new Quaternionf().rotateX((float) rotationY);

        if (rotate) {
            quaternionf.mul(quaternionf2);
            quaternionf.mul(quaternionf3);
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
