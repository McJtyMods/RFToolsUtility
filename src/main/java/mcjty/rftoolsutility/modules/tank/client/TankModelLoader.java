package mcjty.rftoolsutility.modules.tank.client;

import mcjty.lib.client.ModelTools;
import mcjty.rftoolsutility.modules.tank.TankModule;

public class TankModelLoader {

    public static void register() {
        TankBakedModel model = new TankBakedModel();
        ModelTools.registerModelBakeEvent(models -> models.replaceAll((state, original) ->
                state.is(TankModule.TANK.block().get()) ? model : original));
    }
}
