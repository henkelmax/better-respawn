package de.maxhenkel.better_respawn.capabilities;

import de.maxhenkel.better_respawn.Main;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SpawnPointCapabilityProvider implements ICapabilitySerializable<CompoundNBT> {

    private RespawnPosition respawnPosition;

    public SpawnPointCapabilityProvider(RespawnPosition respawnPosition) {
        this.respawnPosition = respawnPosition;
    }

    public SpawnPointCapabilityProvider() {
        this(new RespawnPosition());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(Main.RESPAWN_CAPABILITY)) {
            return LazyOptional.of(() -> (T) respawnPosition);
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        return respawnPosition.toNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        respawnPosition.fromNBT(nbt);
    }
}
