package de.maxhenkel.better_respawn.net;

import de.maxhenkel.corelib.net.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.network.NetworkEvent;

import java.lang.reflect.Method;

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
        Screen s = Minecraft.getInstance().currentScreen;
        if (s instanceof DeathScreen) {
            try {
                Method setDelay = s.getClass().getDeclaredMethod("setDelay", int.class);
                setDelay.invoke(s, delay);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
