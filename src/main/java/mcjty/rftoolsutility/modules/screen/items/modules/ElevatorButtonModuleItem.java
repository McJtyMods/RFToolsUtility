package mcjty.rftoolsutility.modules.screen.items.modules;

import mcjty.rftoolsbase.api.screens.IModuleGuiBuilder;
import mcjty.rftoolsbase.tools.GenericModuleItem;
import mcjty.lib.varia.ModuleTools;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import mcjty.rftoolsutility.modules.screen.modules.ElevatorButtonScreenModule;
import mcjty.rftoolsutility.modules.screen.modulesclient.ElevatorButtonClientScreenModule;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import net.minecraft.world.item.Item.Properties;

import javax.annotation.Nonnull;

public class ElevatorButtonModuleItem extends GenericModuleItem {

    public ElevatorButtonModuleItem() {
        super(new Properties().defaultDurability(1).tab(RFToolsUtility.setup.getTab()));
    }

    @Override
    protected int getUses(ItemStack stack) {
        return ScreenConfiguration.ELEVATOR_BUTTON_RFPERTICK.get();
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
    public Class<ElevatorButtonScreenModule> getServerScreenModule() {
        return ElevatorButtonScreenModule.class;
    }

    @Override
    public Class<ElevatorButtonClientScreenModule> getClientScreenModule() {
        return ElevatorButtonClientScreenModule.class;
    }

    @Override
    public String getModuleName() {
        return "EButton";
    }

    @Override
    public void createGui(IModuleGuiBuilder guiBuilder) {
        guiBuilder
                .color("buttonColor", "Button color").color("curColor", "Current level button color").nl()
                .toggle("vertical", "Vertical", "Order the buttons vertically").toggle("large", "Large", "Larger buttons").nl()
                .toggle("lights", "Lights", "Use buttons resembling lights").toggle("start1", "Start 1", "start numbering at 1 instead of 0").nl()
                .text("l0", "Level 0 name").text("l1", "Level 1 name").text("l2", "Level 2 name").text("l3", "Level 3 name").nl()
                .text("l4", "Level 4 name").text("l5", "Level 5 name").text("l6", "Level 6 name").text("l7", "Level 7 name").nl()
                .label("Block:").block("monitor").nl();
    }

    @Nonnull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        Level world = context.getLevel();
        InteractionHand hand = context.getHand();
        ItemStack stack = player.getItemInHand(hand);
        BlockEntity te = world.getBlockEntity(pos);
        CompoundTag tagCompound = stack.getTag();
        if (tagCompound == null) {
            tagCompound = new CompoundTag();
        }
        // @todo 1.14
//        if (te instanceof ElevatorTileEntity) {
//            tagCompound.putInt("monitordim", world.getDimension().getType().getId());
//            tagCompound.putInt("monitorx", pos.getX());
//            tagCompound.putInt("monitory", pos.getY());
//            tagCompound.putInt("monitorz", pos.getZ());
//            BlockState state = player.getEntityWorld().getBlockState(pos);
//            Block block = state.getBlock();
//            String name = "<invalid>";
//            if (block != null && !block.isAir(state, world, pos)) {
//                name = BlockTools.getReadableName(world, pos);
//            }
//            tagCompound.putString("monitorname", name);
//            if (world.isRemote) {
//                Logging.message(player, "Elevator module is set to block '" + name + "'");
//            }
//        } else {
//            tagCompound.remove("monitordim");
//            tagCompound.remove("monitorx");
//            tagCompound.remove("monitory");
//            tagCompound.remove("monitorz");
//            tagCompound.remove("monitorname");
//            if (world.isRemote) {
//                Logging.message(player, "Elevator module is cleared");
//            }
//        }
        stack.setTag(tagCompound);
        return InteractionResult.SUCCESS;
    }
}