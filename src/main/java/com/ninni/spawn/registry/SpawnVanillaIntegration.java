package com.ninni.spawn.registry;

import com.google.common.reflect.Reflection;
import com.ninni.spawn.block.entity.ChameleonShedBlockEntity;
import com.ninni.spawn.client.inventory.PigmentShifterScreen;
import com.ninni.spawn.client.inventory.HamsterInventoryMenu;
import com.ninni.spawn.client.inventory.HamsterInventoryScreen;
import com.ninni.spawn.client.particles.KrillParticle;
import com.ninni.spawn.client.particles.SandCloudParticle;
import com.ninni.spawn.client.particles.TunaEggParticle;
import com.ninni.spawn.client.renderer.block.ClamLauncherRenderer;
import com.ninni.spawn.client.renderer.block.PigmentShifterRenderer;
import com.ninni.spawn.client.renderer.block.WhaleUvulaRenderer;
import com.ninni.spawn.client.renderer.entity.*;
import com.ninni.spawn.entity.*;
import com.ninni.spawn.entity.variant.ClamVariant;
import com.ninni.spawn.item.ChameleonShedBlockItem;
import com.ninni.spawn.item.ClamCaseItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.particle.GlowParticle;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.GenerationStep;

import java.util.Optional;

import static com.ninni.spawn.Spawn.MOD_ID;

public class SpawnVanillaIntegration {
    public static final ResourceLocation OPEN_HAMSTER_SCREEN = new ResourceLocation(MOD_ID, "open_hamster_screen");

    public static void serverInit() {
        registerBiomeModifications();
        registerStrippables();
        registerFlammables();
        registerCompostables();
        addResourcePacks();
    }

    public static void addResourcePacks() {
        ModContainer modContainer = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow(() -> new IllegalStateException("Spawn's ModContainer couldn't be found!"));
        ResourceManagerHelper.registerBuiltinResourcePack(new ResourceLocation(MOD_ID, "animation_tweaks"), modContainer, "Animation Tweaks", ResourcePackActivationType.DEFAULT_ENABLED);
        ResourceManagerHelper.registerBuiltinResourcePack(new ResourceLocation(MOD_ID, "model_tweaks"), modContainer, "Model Tweaks", ResourcePackActivationType.NORMAL);
    }

    private static void registerBiomeModifications() {
        BiomeModifications.create(new ResourceLocation(MOD_ID, "replace_sunflower_patch")).add(ModificationPhase.REPLACEMENTS, biomeSelectionContext -> biomeSelectionContext.hasPlacedFeature(VegetationPlacements.PATCH_SUNFLOWER), biomeModificationContext -> {
            BiomeModificationContext.GenerationSettingsContext generationSettings = biomeModificationContext.getGenerationSettings();
            if (generationSettings.removeFeature(VegetationPlacements.PATCH_SUNFLOWER)) {
                generationSettings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, SpawnPlacedFeatures.PATCH_SUNFLOWER);
            }
        });
        BiomeModifications.addFeature(BiomeSelectors.tag(SpawnTags.SMALL_ANTHILL_GENERATES), GenerationStep.Decoration.VEGETAL_DECORATION, SpawnPlacedFeatures.SMALL_ANTHILL);
    }

    private static void registerStrippables() {
        StrippableBlockRegistry.register(SpawnBlocks.ROTTEN_LOG, SpawnBlocks.STRIPPED_ROTTEN_LOG);
        StrippableBlockRegistry.register(SpawnBlocks.ROTTEN_WOOD, SpawnBlocks.STRIPPED_ROTTEN_WOOD);
    }

    private static void registerFlammables() {

        FlammableBlockRegistry defaultInstance = FlammableBlockRegistry.getDefaultInstance();
        defaultInstance.add(SpawnBlocks.FALLEN_LEAVES, 60, 100);
        defaultInstance.add(SpawnBlocks.SUNFLOWER, 60, 100);
        defaultInstance.add(SpawnBlocks.ROTTEN_LOG, 5, 5);
        defaultInstance.add(SpawnBlocks.ROTTEN_WOOD, 5, 5);
        defaultInstance.add(SpawnBlocks.STRIPPED_ROTTEN_LOG, 5, 5);
        defaultInstance.add(SpawnBlocks.STRIPPED_ROTTEN_WOOD, 5, 5);
        defaultInstance.add(SpawnBlocks.CRACKED_ROTTEN_PLANKS, 5, 20);
        defaultInstance.add(SpawnBlocks.ROTTEN_PLANKS, 5, 20);
        defaultInstance.add(SpawnBlocks.ROTTEN_SLAB, 5, 20);
        defaultInstance.add(SpawnBlocks.ROTTEN_STAIRS, 5, 20);
        defaultInstance.add(SpawnBlocks.ROTTEN_FENCE, 5, 20);
        defaultInstance.add(SpawnBlocks.ROTTEN_FENCE_GATE, 5, 20);
    }

    private static void registerCompostables() {
        CompostingChanceRegistry defaultInstance = CompostingChanceRegistry.INSTANCE;
        defaultInstance.add(SpawnItems.FALLEN_LEAVES, 0.25F);
        defaultInstance.add(SpawnItems.SUNFLOWER, 0.65F);
        defaultInstance.add(SpawnItems.TUNA_SANDWICH, 0.8F);
        defaultInstance.add(SpawnItems.SNAIL_SHELL, 0.8F);
        defaultInstance.add(SpawnItems.ROTTEN_LOG, 1F);
        defaultInstance.add(SpawnItems.ROTTEN_WOOD, 1F);
        defaultInstance.add(SpawnItems.STRIPPED_ROTTEN_LOG, 1F);
        defaultInstance.add(SpawnItems.STRIPPED_ROTTEN_WOOD, 1F);
        defaultInstance.add(SpawnItems.CRACKED_ROTTEN_PLANKS, 1F);
        defaultInstance.add(SpawnItems.ROTTEN_PLANKS, 1F);
    }

    @Environment(EnvType.CLIENT)
    public static class Client {

        public static void clientInit() {
            registerModelLayers();
            registerParticles();
            registerBlockRenderLayers();
            registerScreens();
            registerBlockTints();
            registerItemModelPredicates();
        }

        private static void registerScreens() {

            MenuScreens.register(SpawnMenuTypes.FISH_CUSTOMIZER_MENU, PigmentShifterScreen::new);

            ClientPlayNetworking.registerGlobalReceiver(OPEN_HAMSTER_SCREEN, (client, handler, buf, responseSender) -> {
                int id = buf.readInt();
                Level level = client.level;
                Optional.ofNullable(level).ifPresent(world -> {
                    Entity entity = world.getEntity(id);
                    if (entity instanceof Hamster hamster) {
                        int slotCount = buf.readInt();
                        int syncId = buf.readInt();
                        LocalPlayer clientPlayerEntity = client.player;
                        SimpleContainer simpleInventory = new SimpleContainer(slotCount);
                        assert clientPlayerEntity != null;
                        HamsterInventoryMenu hamsterInventoryMenu = new HamsterInventoryMenu(syncId, clientPlayerEntity.getInventory(), simpleInventory, hamster);
                        clientPlayerEntity.containerMenu = hamsterInventoryMenu;
                        client.execute(() -> client.setScreen(new HamsterInventoryScreen(hamsterInventoryMenu, clientPlayerEntity.getInventory(), hamster)));
                    }
                });
            });
        }

        private static void registerBlockRenderLayers() {
            BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.translucent(),
                    SpawnBlocks.MUCUS,
                    SpawnBlocks.MUCUS_BLOCK,
                    SpawnBlocks.GHOSTLY_MUCUS_BLOCK,
                    SpawnBlocks.SNAIL_EGGS
            );
            BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutout(),
                    SpawnBlocks.FALLEN_LEAVES,
                    SpawnBlocks.ANT_FARM,
                    SpawnBlocks.POTTED_SWEET_BERRY_BUSH,
                    SpawnBlocks.SUNFLOWER,
                    SpawnBlocks.SUNFLOWER_PLANT,
                    SpawnBlocks.TRIMMED_SEAGRASS
            );
        }

        private static void registerItemModelPredicates() {

            ItemProperties.register(SpawnItems.BABY_SUNFISH_BUCKET, new ResourceLocation("age"), (itemStack, clientLevel, livingEntity, i) -> {
                CompoundTag compoundTag = itemStack.getTag();
                if (compoundTag != null && compoundTag.contains("Age", 3)) {
                    int a = compoundTag.getInt("Age");

                    if (a < -24000) return 0f;
                    else return 1f;
                }
                return 0f;
            });

            ItemProperties.register(SpawnItems.CLAM_CASE, new ResourceLocation("filled"), (itemStack, clientLevel, livingEntity, i) -> ClamCaseItem.getFullnessDisplay(itemStack));

            ItemProperties.register(SpawnItems.CLAM, new ResourceLocation("variant"), (itemStack, clientLevel, livingEntity, i) -> {
                CompoundTag compoundTag = itemStack.getTag();
                if (compoundTag != null && compoundTag.contains("ItemVariantTag", 3)) {
                    int a = compoundTag.getInt("ItemVariantTag");
                    ClamVariant.Base base = Clam.getBaseColor(a).base();

                    if (base == ClamVariant.Base.WEDGE_SHELL) return 0.25f;
                    if (base == ClamVariant.Base.SCALLOP) return 0.5f;
                    if (base == ClamVariant.Base.GIANT_CLAM) return 0.75f;
                }

                return 0.0F;
            });

            ItemProperties.register(SpawnItems.CAPTURED_OCTOPUS, new ResourceLocation("item"), (itemStack, clientLevel, livingEntity, i) -> {
                CompoundTag compoundTag = itemStack.getTag();
                if (compoundTag != null && compoundTag.contains("Item", 3)) {
                    return (float)compoundTag.getInt("Item") / 20 + 0.1f;
                }
                return 0.0F;
            });

            ItemProperties.register(SpawnItems.CAPTURED_OCTOPUS, new ResourceLocation("tooting"), (itemStack, clientLevel, livingEntity, i) -> livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == itemStack ? 1.0f : 0.0f);

            ColorProviderRegistry.ITEM.register((itemStack, i) -> i > 0 ? -1 : ((ClamCaseItem) itemStack.getItem()).getColor(itemStack), SpawnItems.CLAM_CASE);

            ColorProviderRegistry.ITEM.register((itemStack, i) -> ((ChameleonShedBlockItem) itemStack.getItem()).getColor(itemStack), SpawnItems.CHAMELEON_SHED_BLOCK);

        }

        private static void registerBlockTints() {
            ColorProviderRegistry.BLOCK.register((blockState, blockAndTintGetter, blockPos, i) -> {
                        if (blockAndTintGetter.getBlockEntity(blockPos) instanceof ChameleonShedBlockEntity blockEntity) {
                            return blockEntity.getColor();
                        }
                        return 0xFFFFFF;
                    },
                    SpawnBlocks.CHAMELEON_SHED_BLOCK
            );
        }

        private static void registerModelLayers() {
            Reflection.initialize(SpawnEntityModelLayers.class);
            EntityRendererRegistry.register(SpawnEntityType.SpawnFish.ANGLER_FISH, AnglerFishRenderer::new);
            EntityRendererRegistry.register(SpawnEntityType.SpawnFish.TUNA, TunaRenderer::new);
            EntityRendererRegistry.register(SpawnEntityType.SpawnFish.TUNA_EGG, TunaEggRenderer::new);
            EntityRendererRegistry.register(SpawnEntityType.SpawnFish.SEAHORSE, SeahorseRenderer::new);
            EntityRendererRegistry.register(SpawnEntityType.SpawnLandCreature.SNAIL, SnailRenderer::new);
            EntityRendererRegistry.register(SpawnEntityType.SpawnLandCreature.HAMSTER, HamsterRenderer::new);
            EntityRendererRegistry.register(SpawnEntityType.SpawnLandCreature.ANT, AntRenderer::new);
            EntityRendererRegistry.register(SpawnEntityType.SpawnFish.HERRING, HerringRenderer::new);
            EntityRendererRegistry.register(SpawnEntityType.SpawnAquaticCreature.WHALE, WhaleRenderer::new);
            EntityRendererRegistry.register(SpawnEntityType.SpawnAquaticCreature.KRILL_SWARM, KrillSwarmRenderer::new);
            EntityRendererRegistry.register(SpawnEntityType.SpawnAquaticCreature.SEA_COW, SeaCowRenderer::new);
            EntityRendererRegistry.register(SpawnEntityType.SpawnAquaticCreature.CLAM, ClamRenderer::new);
            EntityRendererRegistry.register(SpawnEntityType.SpawnCephalopod.OCTOPUS, OctopusRenderer::new);
            EntityRendererRegistry.register(SpawnEntityType.SpawnAquaticCreature.SEA_LION, SeaLionRenderer::new);
            EntityRendererRegistry.register(SpawnEntityType.SpawnFish.SUNFISH, SunfishRenderer::new);

            BlockEntityRenderers.register(SpawnBlockEntityTypes.WHALE_UVULA, WhaleUvulaRenderer::new);
            BlockEntityRenderers.register(SpawnBlockEntityTypes.PIGMENT_SHIFTER, PigmentShifterRenderer::new);
            BlockEntityRenderers.register(SpawnBlockEntityTypes.CLAM_LAUNCHER, ClamLauncherRenderer::new);
        }

        private static void registerParticles() {
            ParticleFactoryRegistry.getInstance().register(SpawnParticleTypes.ANGLER_FISH_LANTERN_GLOW, GlowParticle.GlowSquidProvider::new);
            ParticleFactoryRegistry.getInstance().register(SpawnParticleTypes.TUNA_EGG, TunaEggParticle.Factory::new);
            ParticleFactoryRegistry.getInstance().register(SpawnParticleTypes.KRILL, KrillParticle.Factory::new);
            ParticleFactoryRegistry.getInstance().register(SpawnParticleTypes.SAND_CLOUD, SandCloudParticle.Factory::new);
        }

    }
}
