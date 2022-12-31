package mcjty.rftoolsutility.modules.screen.items.modules;

import mcjty.lib.varia.EnergyTools;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.ModuleTools;
import mcjty.lib.varia.Tools;
import mcjty.rftoolsbase.api.screens.IModuleGuiBuilder;
import mcjty.rftoolsbase.tools.GenericModuleItem;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import mcjty.rftoolsutility.modules.screen.modules.EnergyPlusBarScreenModule;
import mcjty.rftoolsutility.modules.screen.modulesclient.EnergyPlusBarClientScreenModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;

public class EnergyPlusModuleItem extends GenericModuleItem {

    public EnergyPlusModuleItem() {
        super(RFToolsUtility.setup.defaultProperties().stacksTo(1).defaultDurability(1));
    }

    @Override
    protected int getUses(ItemStack stack) {
        return ScreenConfiguration.ENERGYPLUS_RFPERTICK.get();
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
    public Class<EnergyPlusBarScreenModule> getServerScreenModule() {
        return EnergyPlusBarScreenModule.class;
    }

    @Override
    public Class<EnergyPlusBarClientScreenModule> getClientScreenModule() {
        return EnergyPlusBarClientScreenModule.class;
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

    @Nonnull
    @Override
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
        if (EnergyTools.isEnergyTE(te, facing)) {
            tagCompound.putString("monitordim", world.dimension().location().toString());
            tagCompound.putInt("monitorx", pos.getX());
            tagCompound.putInt("monitory", pos.getY());
            tagCompound.putInt("monitorz", pos.getZ());
            String name = "<invalid>";
            if (!world.getBlockState(pos).isAir()) {
                name = Tools.getReadableName(world, pos);
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
            tagCompound.remove("monitorname");
            if (world.isClientSide) {
                Logging.message(player, "Energy module is cleared");
            }
        }
        stack.setTag(tagCompound);
        return InteractionResult.SUCCESS;
    }
}