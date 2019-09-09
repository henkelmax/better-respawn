package de.maxhenkel.better_respawn.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class SpawnPointCapabilityStorage implements Capability.IStorage<RespawnPosition> {

    @Nullable
    @Override
    public INBT writeNBT(Capability<RespawnPosition> capability, RespawnPosition instance, Direction side) {
        return instance.toNBT();
    }

    @Override
    public void readNBT(Capability<RespawnPosition> capability, RespawnPosition instance, Direction side, INBT nbt) {
        instance.fromNBT((CompoundNBT) nbt);
    }
}
