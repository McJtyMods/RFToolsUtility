package mcjty.rftoolsutility.datagen;

import mcjty.lib.datagen.BaseItemModelProvider;
import mcjty.rftoolsutility.RFToolsUtility;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ExistingFileHelper;

public class Items extends BaseItemModelProvider {

    public Items(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, RFToolsUtility.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
//        parentedItem(VariousSetup.REDSTONE_PROXY_ITEM.get(), "block/redstone_proxy");
    }

    @Override
    public String getName() {
        return "RFTools Utility Item Models";
    }
}
