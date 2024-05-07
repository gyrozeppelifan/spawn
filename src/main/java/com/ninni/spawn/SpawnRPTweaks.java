package com.ninni.spawn;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public class SpawnRPTweaks {

    public enum Tweaks {
        PARROT_MODEL("parrot_remodel"),
        RABBIT_REMODEL("rabbit_remodel"),
        BAT_REMODEL("bat_remodel"),
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
}
