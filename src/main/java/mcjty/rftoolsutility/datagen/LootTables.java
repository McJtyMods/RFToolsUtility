package mcjty.rftoolsutility.datagen;

import mcjty.rftoolsutility.modules.crafter.CrafterSetup;
import net.minecraft.data.DataGenerator;

public class LootTables extends BaseLootTableProvider {

    public LootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected void addTables() {
        lootTables.put(CrafterSetup.BLOCK_CRAFTER1, createStandardTable("crafter1", CrafterSetup.BLOCK_CRAFTER1));
        lootTables.put(CrafterSetup.BLOCK_CRAFTER2, createStandardTable("crafter2", CrafterSetup.BLOCK_CRAFTER2));
        lootTables.put(CrafterSetup.BLOCK_CRAFTER3, createStandardTable("crafter3", CrafterSetup.BLOCK_CRAFTER3));
    }
}
