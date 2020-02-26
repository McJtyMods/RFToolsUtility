package mcjty.rftoolsutility.datagen;

import mcjty.lib.datagen.BaseBlockStateProvider;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.teleporter.TeleporterSetup;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ExistingFileHelper;

public class BlockStates extends BaseBlockStateProvider {

    public BlockStates(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, RFToolsUtility.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
//        singleTextureBlock(VariousSetup.REDSTONE_PROXY.get(), BLOCK_FOLDER + "/redstone_proxy", "block/machine_proxy");
        registerLogicSlabBlock(TeleporterSetup.SIMPLE_DIALER.get(), "simple_dialer", modLoc("block/machinesimpledialer"));
    }
}
