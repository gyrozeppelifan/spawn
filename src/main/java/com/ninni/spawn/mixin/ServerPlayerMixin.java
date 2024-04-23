package com.ninni.spawn.mixin;

import com.mojang.authlib.GameProfile;
import com.ninni.spawn.client.inventory.HamsterInventoryMenu;
import com.ninni.spawn.entity.Hamster;
import com.ninni.spawn.entity.HamsterOpenContainer;
import com.ninni.spawn.mixin.accessor.PlayerAccessor;
import com.ninni.spawn.registry.SpawnVanillaIntegration;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundHorseScreenOpenPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player implements HamsterOpenContainer {
    @Shadow protected abstract void nextContainerCounter();
    @Shadow protected abstract void initMenu(AbstractContainerMenu abstractContainerMenu);
    @Shadow private int containerCounter;

    public ServerPlayerMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(level, blockPos, f, gameProfile);
    }

    @Inject(at = @At("TAIL"), method = "die")
    private void S$die(CallbackInfo ci) {
        this.newRemoveEntitiesOnShoulder();
    }

    @Inject(at = @At("HEAD"), method = "setGameMode")
    private void S$setGameMode(GameType gameType, CallbackInfoReturnable<Boolean> cir) {
        if (gameType == GameType.SPECTATOR) {
            this.newRemoveEntitiesOnShoulder();
        }
    }

    @Override
    public void openHamsterInventory(Hamster hamster, Container container) {
        ServerPlayer $this = (ServerPlayer) (Object) this;
        if ($this.containerMenu != $this.inventoryMenu) {
            $this.closeContainer();
        }
        this.nextContainerCounter();
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeInt(hamster.getId());
        buf.writeInt(container.getContainerSize());
        buf.writeInt(this.containerCounter);
        ServerPlayNetworking.send($this, SpawnVanillaIntegration.OPEN_HAMSTER_SCREEN, buf);
        $this.containerMenu = new HamsterInventoryMenu(this.containerCounter, $this.getInventory(), container, hamster);
        this.initMenu($this.containerMenu);
    }

    protected void newRemoveEntitiesOnShoulder() {
        if (((PlayerAccessor)this).getTimeEntitySatOnShoulder() + 20L < this.level().getGameTime()) {
            ((PlayerAccessor)this).callRespawnEntityOnShoulder(this.getShoulderEntityLeft());
            this.setShoulderEntityLeft(new CompoundTag());
            ((PlayerAccessor)this).callRespawnEntityOnShoulder(this.getShoulderEntityRight());
            this.setShoulderEntityRight(new CompoundTag());
        }
    }
}
