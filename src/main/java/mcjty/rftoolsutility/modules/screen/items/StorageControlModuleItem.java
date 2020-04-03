package mcjty.rftoolsutility.modules.screen.items;

import mcjty.lib.crafting.INBTPreservingIngredient;
import mcjty.lib.varia.BlockTools;
import mcjty.lib.varia.Logging;
import mcjty.rftoolsbase.api.screens.IModuleGuiBuilder;
import mcjty.rftoolsbase.api.storage.IStorageScanner;
import mcjty.rftoolsbase.tools.GenericModuleItem;
import mcjty.rftoolsbase.tools.ModuleTools;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import mcjty.rftoolsutility.modules.screen.modules.StorageControlScreenModule;
import mcjty.rftoolsutility.modules.screen.modulesclient.StorageControlClientScreenModule;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collection;

public class StorageControlModuleItem extends GenericModuleItem implements INBTPreservingIngredient {

    @Override
    protected int getUses(ItemStack stack) {
        return ScreenConfiguration.STORAGE_CONTROL_RFPERTICK.get();
    }

    @Override
    protected boolean hasGoldMessage(ItemStack stack) {
        return !hasTarget(stack);
    }

    @Override
    protected String getInfoString(ItemStack stack) {
        return getTargetString(stack);
    }

    public StorageControlModuleItem() {
        super(new Properties().maxStackSize(1).defaultMaxDamage(1).group(RFToolsUtility.setup.getTab()));
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        ItemStack stack = context.getItem();
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
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
                Logging.message(player, "Storage module is set to block '" + name + "'");
            }
        } else {
            ModuleTools.clearPositionInModule(stack);
            if (world.isRemote) {
                Logging.message(player, "Storage module is cleared");
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public Class<StorageControlScreenModule> getServerScreenModule() {
        return StorageControlScreenModule.class;
    }

    @Override
    public Class<StorageControlClientScreenModule> getClientScreenModule() {
        return StorageControlClientScreenModule.class;
    }

    @Override
    public String getModuleName() {
        return "Stor";
    }

    @Override
    public void createGui(IModuleGuiBuilder guiBuilder) {
        guiBuilder
                .ghostStack("stack0").ghostStack("stack1").ghostStack("stack2").nl()
                .ghostStack("stack3").ghostStack("stack4").ghostStack("stack5").nl()
                .ghostStack("stack6").ghostStack("stack7").ghostStack("stack8").nl()
                .toggle("starred", "Starred", "If enabled only count items", "in 'starred' inventories", "(mark inventories in storage scanner)")
                .toggle("oredict", "Ore Dict", "If enabled use ore dictionary", "to match items").nl()
                .block("monitor").nl();
    }

    // @todo 1.14 implement!
    @Override
    public Collection<String> getTagsToPreserve() {
        return null;
    }
}