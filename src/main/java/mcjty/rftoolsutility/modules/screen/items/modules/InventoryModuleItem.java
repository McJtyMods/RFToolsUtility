package mcjty.rftoolsutility.modules.screen.items.modules;

import mcjty.lib.crafting.INBTPreservingIngredient;
import mcjty.lib.varia.CapabilityTools;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.ModuleTools;
import mcjty.lib.varia.Tools;
import mcjty.rftoolsbase.api.screens.IModuleGuiBuilder;
import mcjty.rftoolsbase.tools.GenericModuleItem;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import mcjty.rftoolsutility.modules.screen.modules.ItemStackScreenModule;
import mcjty.rftoolsutility.modules.screen.modulesclient.ItemStackClientScreenModule;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;

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


    @Nonnull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction facing = context.getClickedFace();
        Player player = context.getPlayer();
        BlockEntity te = world.getBlockEntity(pos);
        if (te == null) {
            if (world.isClientSide) {
                Logging.message(player, ChatFormatting.RED + "This is not a valid inventory!");
            }
            return InteractionResult.SUCCESS;
        }
        CompoundTag tagCompound = stack.getTag();
        if (tagCompound == null) {
            tagCompound = new CompoundTag();
        }
        if (CapabilityTools.getItemCapabilitySafe(te).isPresent()) {
            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            String name = "<invalid>";
            if (!world.getBlockState(pos).isAir()) {
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
        return InteractionResult.SUCCESS;
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
        return Collections.emptyList();
    }
}