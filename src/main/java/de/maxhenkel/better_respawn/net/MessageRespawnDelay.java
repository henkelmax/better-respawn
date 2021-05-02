package de.maxhenkel.better_respawn.net;

import de.maxhenkel.better_respawn.IBetterDeathScreen;
import de.maxhenkel.corelib.net.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageRespawnDelay implements Message {

    private int delay;

    public MessageRespawnDelay() {

    }

    public MessageRespawnDelay(int delay) {
        this.delay = delay;
    }

    @Override
    public Dist getExecutingSide() {
        return Dist.CLIENT;
    }

    @Override
    public void executeClientSide(NetworkEvent.Context context) {
        Screen s = Minecraft.getInstance().screen;
        if (s instanceof IBetterDeathScreen) {
            IBetterDeathScreen deathScreen = (IBetterDeathScreen) s;
            deathScreen.setDelay(delay);
        }
    }

    @Override
    public Message fromBytes(PacketBuffer packetBuffer) {
        delay = packetBuffer.readInt();
        return this;
    }

    @Override
    public void toBytes(PacketBuffer packetBuffer) {
        packetBuffer.writeInt(delay);
    }
}
