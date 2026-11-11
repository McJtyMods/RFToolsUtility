package mcjty.rftoolsutility.modules.tank.client;

import mcjty.rftoolsutility.modules.tank.TankModule;
import net.neoforged.neoforge.client.event.ModelEvent;

public class TankModelLoader {

    public static void register(ModelEvent.ModifyBakingResult event) {
        TankBakedModel model = new TankBakedModel();
        event.getBakingResult().blockStateModels().replaceAll((state, original) ->
                state.is(TankModule.TANK.block().get()) ? model : original);
    }
}
