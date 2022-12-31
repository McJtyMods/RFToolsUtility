package mcjty.rftoolsutility.modules.screen.items.modules;

import mcjty.lib.varia.ComponentFactory;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.ModuleTools;
import mcjty.lib.varia.Tools;
import mcjty.rftoolsbase.api.screens.IModuleGuiBuilder;
import mcjty.rftoolsbase.tools.GenericModuleItem;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.blocks.CounterTileEntity;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import mcjty.rftoolsutility.modules.screen.modules.CounterPlusScreenModule;
import mcjty.rftoolsutility.modules.screen.modulesclient.CounterPlusClientScreenModule;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.List;

public class CounterPlusModuleItem extends GenericModuleItem {

    public CounterPlusModuleItem() {
        super(RFToolsUtility.setup.defaultProperties().stacksTo(1).defaultDurability(1));
    }

    @Override
    protected int getUses(ItemStack stack) {
        return ScreenConfiguration.COUNTERPLUS_RFPERTICK.get();
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
    public Class<CounterPlusScreenModule> getServerScreenModule() {
        return CounterPlusScreenModule.class;
    }

    @Override
    public Class<CounterPlusClientScreenModule> getClientScreenModule() {
        return CounterPlusClientScreenModule.class;
    }

    @Override
    public String getModuleName() {
        return "Count";
    }

    @Override
    public void createGui(IModuleGuiBuilder guiBuilder) {
        guiBuilder
                .label("Label:").text("text", "Label text").nl()
                .label("L:").color("color", "Color for the label").label("C:").color("cntcolor", "Color for the counter").nl()
                .format("format")
                .choices("align", "Label alignment", "Left", "Center", "Right").nl()
                .label("Block:").block("monitor").nl();
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack itemStack, Level world, @Nonnull List<Component> list, @Nonnull TooltipFlag flag) {
        super.appendHoverText(itemStack, world, list, flag);
        list.add(ComponentFactory.literal(ChatFormatting.GREEN + "Uses " + ScreenConfiguration.COUNTERPLUS_RFPERTICK.get() + " RF/tick"));
        boolean hasTarget = false;
        CompoundTag tagCompound = itemStack.getTag();
        if (tagCompound != null) {
            list.add(ComponentFactory.literal(ChatFormatting.YELLOW + "Label: " + tagCompound.getString("text")));
            if (tagCompound.contains("monitorx")) {
                int monitorx = tagCompound.getInt("monitorx");
                int monitory = tagCompound.getInt("monitory");
                int monitorz = tagCompound.getInt("monitorz");
                String monitorname = tagCompound.getString("monitorname");
                list.add(ComponentFactory.literal(ChatFormatting.YELLOW + "Monitoring: " + monitorname + " (at " + monitorx + "," + monitory + "," + monitorz + ")"));
                hasTarget = true;
            }
        }
        if (!hasTarget) {
            list.add(ComponentFactory.literal(ChatFormatting.YELLOW + "Sneak right-click on a counter to set the"));
            list.add(ComponentFactory.literal(ChatFormatting.YELLOW + "target for this counter module"));
        }
    }

    @Override
    @Nonnull
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction facing = context.getClickedFace();
        Player player = context.getPlayer();
        BlockEntity te = world.getBlockEntity(pos);
        CompoundTag tagCompound = stack.getTag();
        if (tagCompound == null) {
            tagCompound = new CompoundTag();
        }
        if (te instanceof CounterTileEntity) {
            tagCompound.putString("monitordim", world.dimension().location().toString());
            tagCompound.putInt("monitorx", pos.getX());
            tagCompound.putInt("monitory", pos.getY());
            tagCompound.putInt("monitorz", pos.getZ());
            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            String name = "<invalid>";
            if (block != null && !state.isAir()) {
                name = Tools.getReadableName(world, pos);
            }
            tagCompound.putString("monitorname", name);
            if (world.isClientSide) {
                Logging.message(player, "Counter module is set to block '" + name + "'");
            }
        } else {
            tagCompound.remove("monitordim");
            tagCompound.remove("monitorx");
            tagCompound.remove("monitory");
            tagCompound.remove("monitorz");
            tagCompound.remove("monitorname");
            if (world.isClientSide) {
                Logging.message(player, "Counter module is cleared");
            }
        }
        stack.setTag(tagCompound);
        return InteractionResult.SUCCESS;
    }
}