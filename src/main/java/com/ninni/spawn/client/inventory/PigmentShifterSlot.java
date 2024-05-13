package com.ninni.spawn.client.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

public class PigmentShifterSlot extends Slot {
    public boolean disabled;
    public PigmentShifterSlot(Container container, int i, int j, int k) {
        super(container, i, j, k);
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
