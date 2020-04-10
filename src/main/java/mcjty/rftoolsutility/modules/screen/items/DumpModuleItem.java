package mcjty.rftoolsutility.modules.screen.items;

import mcjty.lib.varia.BlockTools;
import mcjty.lib.varia.Logging;
import mcjty.rftoolsbase.api.screens.IModuleGuiBuilder;
import mcjty.rftoolsbase.api.storage.IStorageScanner;
import mcjty.rftoolsbase.tools.GenericModuleItem;
import mcjty.rftoolsbase.tools.ModuleTools;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import mcjty.rftoolsutility.modules.screen.modules.DumpScreenModule;
import mcjty.rftoolsutility.modules.screen.modulesclient.DumpClientScreenModule;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DumpModuleItem extends GenericModuleItem {

    public DumpModuleItem() {
        super(new Properties().defaultMaxDamage(1).group(RFToolsUtility.setup.getTab()));
    }

    @Override
    protected int getUses(ItemStack stack) {
        return ScreenConfiguration.DUMP_RFPERTICK.get();
    }

    @Override
    protected boolean hasGoldMessage(ItemStack stack) {
        return !ModuleTools.hasModuleTarget(stack);
    }

    @Override
    protected String getInfoString(ItemStack stack) {
        return ModuleTools.getTargetString(stack);
    }


    //    @Override
//    public int getMaxItemUseDuration(ItemStack stack) {
//        return 1;
//    }

    @Override
    public Class<DumpScreenModule> getServerScreenModule() {
        return DumpScreenModule.class;
    }

    @Override
    public Class<DumpClientScreenModule> getClientScreenModule() {
        return DumpClientScreenModule.class;
    }

    @Override
    public String getModuleName() {
        return "Dump";
    }

    @Override
    public void createGui(IModuleGuiBuilder guiBuilder) {
        int index = 0;
        for (int y = 0 ; y < DumpScreenModule.ROWS ; y++) {
            for (int x = 0 ; x < DumpScreenModule.COLS ; x++) {
                guiBuilder.ghostStack("stack" + index);
                index++;
            }
            guiBuilder.nl();
        }
        guiBuilder
                .label("Label:").text("text", "Label text").color("color", "Label color").nl()
                .toggle("matchingTag", "Matching Tag", "If enabled use common tags", "to match items");
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        ItemStack stack = context.getItem();
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof IStorageScanner) {
            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            String name = "<invalid>";
            if (block != null && !block.isAir(state, world, pos)) {
                name = BlockTools.getReadableName(world, pos);
            }
            ModuleTools.setPositionInModule(stack, world.getDimension().getType(), pos, name);
            if (world.isRemote) {
                Logging.message(context.getPlayer(), "Storage module is set to block '" + name + "'");
            }
        } else {
            ModuleTools.clearPositionInModule(stack);
            if (world.isRemote) {
                Logging.message(context.getPlayer(), "Storage module is cleared");
            }
        }
        return ActionResultType.SUCCESS;
    }
}