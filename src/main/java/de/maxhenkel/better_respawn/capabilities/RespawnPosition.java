package de.maxhenkel.better_respawn.capabilities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.HashMap;
import java.util.Map;

public class RespawnPosition implements INBTSerializable<CompoundTag> {

    private Map<ResourceLocation, BlockPos> posmap;

    public RespawnPosition() {
        this.posmap = new HashMap<>();
    }

    public void copyFrom(RespawnPosition respawnPosition) {
        posmap = respawnPosition.posmap;
    }

    public BlockPos getPos(Level world) {
        return posmap.get(world.dimension().location());
    }

    public void setPos(Level world, BlockPos pos) {
        posmap.put(world.dimension().location(), pos);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compound = new CompoundTag();
        ListTag spawnList = new ListTag();
        posmap.forEach((dim, pos) -> {
            if (pos != null) {
                CompoundTag data = new CompoundTag();
                data.putString("Dim", dim.toString());
                data.putInt("SpawnX", pos.getX());
                data.putInt("SpawnY", pos.getY());
                data.putInt("SpawnZ", pos.getZ());
                spawnList.add(data);
            }
        });
        compound.put("Spawns", spawnList);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        nbt.getList("Spawns", 10).forEach(e -> {
            CompoundTag data = (CompoundTag) e;
            posmap.put(new ResourceLocation(data.getString("Dim")), new BlockPos(data.getInt("SpawnX"), data.getInt("SpawnY"), data.getInt("SpawnZ")));
        });
    }

    @Override
    public String toString() {
        return "RespawnPosition{" + posmap + "}";
    }

}
