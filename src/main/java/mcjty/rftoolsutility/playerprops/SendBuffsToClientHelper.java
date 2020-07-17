package mcjty.rftoolsutility.playerprops;

import mcjty.rftoolsutility.client.RenderGameOverlayEventHandler;

import java.util.ArrayList;

public class SendBuffsToClientHelper {

    public static void setBuffs(PacketSendBuffsToClient buffs) {
        RenderGameOverlayEventHandler.buffs = new ArrayList<>(buffs.getBuffs());
    }
}
