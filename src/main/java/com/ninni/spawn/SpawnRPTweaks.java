package com.ninni.spawn;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;

public class SpawnRPTweaks {

    public enum Tweaks {
        PARROT_MODEL("parrot_remodel"),
        RABBIT_REMODEL("rabbit_remodel"),
        BAT_REMODEL("bat_remodel"),
        SALMON_ANIMATIONS("salmon_animations"),
        SALMON_TILTING("salmon_tilting"),
        COD_ANIMATIONS("cod_animations"),
        COD_TILTING("cod_tilting"),
        TROPICAL_FISH_ANIMATIONS("tropical_fish_animations"),
        TROPICAL_FISH_TILTING("tropical_fish_tilting"),
        TURTLE_ANIMATIONS("turtle_animations"),
        TURTLE_BABY_HEAD_SCALE("turtle_baby_head_scale");

        final String name;

        Tweaks(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static boolean isPresent(Tweaks tweak) {
        return Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation("tweaks/" + tweak.getName() +".txt")).isPresent();
    }

    public static void addTilting(Tweaks tweak, ModelPart all, float headYaw, float headPitch) {
        float pi = ((float)Math.PI);
        if (SpawnRPTweaks.isPresent(tweak)) {
            all.xRot = headPitch * (pi / 180);
            all.yRot = headYaw * (pi / 180);
        } else {
            all.xRot = 0;
            all.yRot = 0;
        }
    }
}
