package mcjty.rftoolsutility.modules.screen.items.modules;

import mcjty.lib.crafting.INBTPreservingIngredient;
import mcjty.lib.varia.*;
import mcjty.rftoolsbase.api.screens.IModuleGuiBuilder;
import mcjty.rftoolsbase.tools.GenericModuleItem;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import mcjty.rftoolsutility.modules.screen.modules.EnergyBarScreenModule;
import mcjty.rftoolsutility.modules.screen.modulesclient.EnergyBarClientScreenModule;
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
import net.minecraft.world.World;

import java.util.Collection;

import net.minecraft.item.Item.Properties;

public class EnergyModuleItem extends GenericModuleItem implements INBTPreservingIngredient {

    public EnergyModuleItem() {
        super(new Properties().stacksTo(1).defaultDurability(1).tab(RFToolsUtility.setup.getTab()));
    }

    @Override
    protected int getUses(ItemStack stack) {
        return ScreenConfiguration.ENERGY_RFPERTICK.get();
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
    public Class<EnergyBarScreenModule> getServerScreenModule() {
        return EnergyBarScreenModule.class;
    }

    @Override
    public Class<EnergyBarClientScreenModule> getClientScreenModule() {
        return EnergyBarClientScreenModule.class;
    }

    @Override
    public String getModuleName() {
        return "RF";
    }

    @Override
    public void createGui(IModuleGuiBuilder guiBuilder) {
        guiBuilder
                .label("Label:").text("text", "Label text").color("color", "Color for the label").nl()
                .label("RF+:").color("rfcolor", "Color for the RF text").label("RF-:").color("rfcolor_neg", "Color for the negative", "RF/tick ratio").nl()
                .toggleNegative("hidebar", "Bar", "Toggle visibility of the", "energy bar").mode("RF").format("format").nl()
                .choices("align", "Label alignment", "Left", "Center", "Right").nl()
                .label("Block:").block("monitor").nl();
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        ItemStack stack = context.getItemInHand();
        World world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction facing = context.getClickedFace();
        PlayerEntity player = context.getPlayer();
        TileEntity te = world.getBlockEntity(pos);
        CompoundNBT tagCompound = stack.getTag();
        if (tagCompound == null) {
            tagCompound = new CompoundNBT();
        }
        if (EnergyTools.isEnergyTE(te, facing)) {
            tagCompound.putString("monitordim", world.dimension().location().toString());
            tagCompound.putInt("monitorx", pos.getX());
            tagCompound.putInt("monitory", pos.getY());
            tagCompound.putInt("monitorz", pos.getZ());
            tagCompound.putInt("monitorside", facing.get3DDataValue());
            BlockState state = player.getCommandSenderWorld().getBlockState(pos);
            Block block = state.getBlock();
            String name = "<invalid>";
            if (block != null && !block.isAir(state, world, pos)) {
                name = BlockTools.getReadableName(world, pos);
            }
            tagCompound.putString("monitorname", name);
            if (world.isClientSide) {
                Logging.message(player, "Energy module is set to block '" + name + "'");
            }
        } else {
            tagCompound.remove("monitordim");
            tagCompound.remove("monitorx");
            tagCompound.remove("monitory");
            tagCompound.remove("monitorz");
            tagCompound.remove("monitorside");
            tagCompound.remove("monitorname");
            if (world.isClientSide) {
                Logging.message(player, "Energy module is cleared");
            }
        }
        stack.setTag(tagCompound);
        return ActionResultType.SUCCESS;
    }

    // @todo 1.14 implement
    @Override
    public Collection<String> getTagsToPreserve() {
        return null;
    }
}
