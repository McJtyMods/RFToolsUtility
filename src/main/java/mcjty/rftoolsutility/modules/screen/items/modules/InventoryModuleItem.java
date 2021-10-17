package mcjty.rftoolsutility.modules.screen.items.modules;

import mcjty.lib.crafting.INBTPreservingIngredient;
import mcjty.lib.varia.*;
import mcjty.rftoolsbase.api.screens.IModuleGuiBuilder;
import mcjty.rftoolsbase.tools.GenericModuleItem;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import mcjty.rftoolsutility.modules.screen.modules.ItemStackScreenModule;
import mcjty.rftoolsutility.modules.screen.modulesclient.ItemStackClientScreenModule;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.Collection;

public class InventoryModuleItem extends GenericModuleItem implements INBTPreservingIngredient {

    public InventoryModuleItem() {
        super(new Properties().stacksTo(1).defaultDurability(1).tab(RFToolsUtility.setup.getTab()));
    }

    @Override
    protected int getUses(ItemStack stack) {
        return ScreenConfiguration.ITEMSTACK_RFPERTICK.get();
    }

    @Override
    protected boolean hasGoldMessage(ItemStack stack) {
        return !ModuleTools.hasModuleTarget(stack);
    }

    @Override
    protected String getInfoString(ItemStack stack) {
        return ModuleTools.getTargetString(stack);
    }


    @Override
    public ActionResultType useOn(ItemUseContext context) {
        ItemStack stack = context.getItemInHand();
        World world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction facing = context.getClickedFace();
        PlayerEntity player = context.getPlayer();
        TileEntity te = world.getBlockEntity(pos);
        if (te == null) {
            if (world.isClientSide) {
                Logging.message(player, TextFormatting.RED + "This is not a valid inventory!");
            }
            return ActionResultType.SUCCESS;
        }
        CompoundNBT tagCompound = stack.getTag();
        if (tagCompound == null) {
            tagCompound = new CompoundNBT();
        }
        if (CapabilityTools.getItemCapabilitySafe(te).isPresent()) {
            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            String name = "<invalid>";
            if (block != null && !block.isAir(state, world, pos)) {
                name = Tools.getReadableName(world, pos);
            }
            ModuleTools.setPositionInModule(stack, world.dimension(), pos, name);
            if (world.isClientSide) {
                Logging.message(player, "Inventory module is set to block '" + name + "'");
            }
        } else {
            ModuleTools.clearPositionInModule(stack);
            if (world.isClientSide) {
                Logging.message(player, "Inventory module is cleared");
            }
        }
        stack.setTag(tagCompound);
        return ActionResultType.SUCCESS;
    }

    @Override
    public Class<ItemStackScreenModule> getServerScreenModule() {
        return ItemStackScreenModule.class;
    }

    @Override
    public Class<ItemStackClientScreenModule> getClientScreenModule() {
        return ItemStackClientScreenModule.class;
    }

    @Override
    public String getModuleName() {
        return "Inv";
    }

    @Override
    public void createGui(IModuleGuiBuilder guiBuilder) {
        guiBuilder.
                label("Slot 1:").integer("slot1", "Slot index to show").nl().
                label("Slot 2:").integer("slot2", "Slot index to show").nl().
                label("Slot 3:").integer("slot3", "Slot index to show").nl().
                label("Slot 4:").integer("slot4", "Slot index to show").nl().
                block("monitor").nl();
    }

    // @todo 1.14 implement
    @Override
    public Collection<String> getTagsToPreserve() {
        return null;
    }
}