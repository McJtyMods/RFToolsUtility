package mcjty.rftoolsutility.modules.screen.items;

import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.crafting.INBTPreservingIngredient;
import mcjty.lib.tooltips.ITooltipSettings;
import mcjty.lib.varia.BlockPosTools;
import mcjty.lib.varia.BlockTools;
import mcjty.lib.varia.Logging;
import mcjty.rftoolsbase.api.screens.IModuleGuiBuilder;
import mcjty.rftoolsbase.api.screens.IModuleProvider;
import mcjty.rftoolsbase.api.storage.IStorageScanner;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.RFToolsTools;
import mcjty.rftoolsutility.modules.screen.modules.StorageControlScreenModule;
import mcjty.rftoolsutility.modules.screen.modulesclient.StorageControlClientScreenModule;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.List;

import static mcjty.lib.builder.TooltipBuilder.*;

public class StorageControlModuleItem extends Item implements IModuleProvider, INBTPreservingIngredient, ITooltipSettings {

    private final TooltipBuilder tooltipBuilder = new TooltipBuilder()
            .info(key("message.rftoolsutility.shiftmessage"))
            .infoShift(header(),
                    gold(stack -> !hasTarget(stack)),
                    parameter("target", StorageControlModuleItem::getTarget));

    private static boolean hasTarget(ItemStack stack) {
        return RFToolsTools.hasModuleTarget(stack);
    }

    private static String getTarget(ItemStack stack) {
        BlockPos pos = RFToolsTools.getPositionFromModule(stack);
        String monitorname = stack.getTag().getString("monitorname");
        return monitorname + " (at " + BlockPosTools.toString(pos) + ")";

    }

    public StorageControlModuleItem() {
        super(new Properties().maxStackSize(1).defaultMaxDamage(1).group(RFToolsUtility.setup.getTab()));
    }

    @Override
    public void addInformation(ItemStack itemStack, World world, List<ITextComponent> list, ITooltipFlag flag) {
        super.addInformation(itemStack, world, list, flag);
        tooltipBuilder.makeTooltip(getRegistryName(), itemStack, list, flag);
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
            RFToolsTools.setPositionInModule(stack, world.getDimension().getType(), pos, name);
            if (world.isRemote) {
                Logging.message(player, "Storage module is set to block '" + name + "'");
            }
        } else {
            RFToolsTools.clearPositionInModule(stack);
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