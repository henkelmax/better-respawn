package de.maxhenkel.better_respawn.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class RespawnPosition {

    private Map<ResourceLocation, BlockPos> posmap;

    public RespawnPosition() {
        this.posmap = new HashMap<>();
    }

    public void copyFrom(RespawnPosition respawnPosition) {
        posmap = respawnPosition.posmap;
    }

    public BlockPos getPos(World world) {
        return posmap.get(world.dimension().location());
    }

    public void setPos(World world, BlockPos pos) {
        posmap.put(world.dimension().location(), pos);
    }

    public CompoundNBT toNBT() {
        CompoundNBT compound = new CompoundNBT();
        ListNBT spawnList = new ListNBT();
        posmap.forEach((dim, pos) -> {
            if (pos != null) {
                CompoundNBT data = new CompoundNBT();
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

    public void fromNBT(CompoundNBT compound) {
        compound.getList("Spawns", 10).forEach(e -> {
            CompoundNBT data = (CompoundNBT) e;
            posmap.put(new ResourceLocation(data.getString("Dim")), new BlockPos(data.getInt("SpawnX"), data.getInt("SpawnY"), data.getInt("SpawnZ")));
        });
    }

    @Override
    public String toString() {
        return "RespawnPosition{" + posmap + "}";
    }

}
