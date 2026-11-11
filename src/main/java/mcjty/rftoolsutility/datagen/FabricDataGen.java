package mcjty.rftoolsutility.datagen;

import mcjty.lib.datagen.DataGen;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.client.UtilityItemModels;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class FabricDataGen implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        var pack = generator.createPack();
        pack.addProvider((output, lookup) -> {
            var data = new DataGen(output, lookup);
            RFToolsUtility.instance.modules.datagen(data, lookup);
            data.generate();
            return data;
        });
        pack.addProvider((output, lookup) -> UtilityItemModels.provider(output));
    }
}
