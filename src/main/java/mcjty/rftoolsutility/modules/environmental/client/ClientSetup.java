package mcjty.rftoolsutility.modules.environmental.client;

import net.minecraft.resources.Identifier;

import java.util.Collections;
import java.util.List;

public class ClientSetup {

    public static void initClient() {
    }

    public static List<Identifier> onTextureStitch() {
        return Collections.singletonList(EnvironmentalRenderer.HALO);
    }
}
