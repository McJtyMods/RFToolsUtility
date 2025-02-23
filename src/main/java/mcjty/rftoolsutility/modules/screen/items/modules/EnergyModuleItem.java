package mcjty.rftoolsutility.modules.screen.items.modules;

import com.mojang.serialization.Codec;
import mcjty.lib.crafting.IComponentsToPreserve;
import mcjty.lib.varia.EnergyTools;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.ModuleTools;
import mcjty.lib.varia.Tools;
import mcjty.rftoolsbase.api.screens.IClientScreenModule;
import mcjty.rftoolsbase.api.screens.IModuleGuiBuilder;
import mcjty.rftoolsbase.api.screens.IScreenModule;
import mcjty.rftoolsbase.tools.GenericModuleItem;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import mcjty.rftoolsutility.modules.screen.ScreenModule;
import mcjty.rftoolsutility.modules.screen.modules.EnergyBarScreenModule;
import mcjty.rftoolsutility.modules.screen.modulesclient.EnergyBarClientScreenModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

public class EnergyModuleItem extends GenericModuleItem implements IComponentsToPreserve {

    public EnergyModuleItem() {
        super(RFToolsUtility.setup.defaultProperties().stacksTo(1).durability(1));
    }

    @Override
    public Codec<? extends IScreenModule<?>> codec() {
        return EnergyBarScreenModule.CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ? extends IScreenModule<?>> streamCodec() {
        return EnergyBarScreenModule.STREAM_CODEC;
    }

    @Override
    public Codec<? extends IClientScreenModule<?>> clientCodec() {
        return EnergyBarClientScreenModule.CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ? extends IClientScreenModule<?>> clientStreamCodec() {
        return EnergyBarClientScreenModule.STREAM_CODEC;
    }

    @Override
    public DataComponentType<? extends IScreenModule<?>> componentType() {
        return ScreenModule.MODULE_ENERGY_BAR_DATA.get();
    }

    @Override
    public DataComponentType<? extends IClientScreenModule<?>> clientComponentType() {
        return ScreenModule.CLIENTMODULE_ENERGY_BAR_DATA.get();
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
    public IScreenModule<?> createServerScreenModule() {
        return new EnergyBarScreenModule();
    }

    @Override
    public IClientScreenModule<?> createClientScreenModule() {
        return new EnergyBarClientScreenModule();
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
        // @todo 1.21 data
        CompoundTag tagCompound = new CompoundTag();// stack.getTag();
        if (tagCompound == null) {
            tagCompound = new CompoundTag();
        }
        if (EnergyTools.isEnergyTE(te, facing)) {
            tagCompound.putString("monitordim", world.dimension().location().toString());
            tagCompound.putInt("monitorx", pos.getX());
            tagCompound.putInt("monitory", pos.getY());
            tagCompound.putInt("monitorz", pos.getZ());
            tagCompound.putInt("monitorside", facing.get3DDataValue());
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
            tagCompound.remove("monitorside");
            tagCompound.remove("monitorname");
            if (world.isClientSide) {
                Logging.message(player, "Energy module is cleared");
            }
        }
        // @todo 1.21 data
//        stack.setTag(tagCompound);
        return InteractionResult.SUCCESS;
    }

    @Override
    public Collection<DataComponentType<?>> getComponentsToPreserve() {
        // @todo 1.21 implement me?
        return List.of();
    }
}
