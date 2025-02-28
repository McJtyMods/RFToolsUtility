package mcjty.rftoolsutility.modules.screen.items.modules;

import com.mojang.serialization.Codec;
import mcjty.lib.crafting.IComponentsToPreserve;
import mcjty.lib.varia.CapabilityTools;
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
import mcjty.rftoolsutility.modules.screen.modules.FluidBarScreenModule;
import mcjty.rftoolsutility.modules.screen.modulesclient.FluidBarClientScreenModule;
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
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class FluidModuleItem extends GenericModuleItem implements IComponentsToPreserve {

    public FluidModuleItem() {
        super(RFToolsUtility.setup.defaultProperties().stacksTo(1).durability(1));
    }

    @Override
    public @Nullable Codec<? extends IScreenModule<?>> codec() {
        return FluidBarScreenModule.CODEC;
    }

    @Override
    public @Nullable StreamCodec<RegistryFriendlyByteBuf, ? extends IScreenModule<?>> streamCodec() {
        return FluidBarScreenModule.STREAM_CODEC;
    }

    @Override
    public @Nullable DataComponentType<? extends IScreenModule<?>> componentType() {
        return ScreenModule.MODULE_FLUIDBAR_DATA.get();
    }

    @Override
    public IScreenModule<?> createServerScreenModule() {
        return new FluidBarScreenModule();
    }

    @Override
    public @Nullable Codec<? extends IClientScreenModule<?>> clientCodec() {
        return FluidBarClientScreenModule.CODEC;
    }

    @Override
    public @Nullable StreamCodec<RegistryFriendlyByteBuf, ? extends IClientScreenModule<?>> clientStreamCodec() {
        return FluidBarClientScreenModule.STREAM_CODEC;
    }

    @Override
    public @Nullable DataComponentType<? extends IClientScreenModule<?>> clientComponentType() {
        return ScreenModule.CLIENTMODULE_FLUIDBAR_DATA.get();
    }

    @Override
    public IClientScreenModule<?> createClientScreenModule() {
        return new FluidBarClientScreenModule();
    }

    @Override
    protected int getUses(ItemStack stack) {
        return ScreenConfiguration.FLUID_RFPERTICK.get();
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
    public String getModuleName() {
        return "Fluid";
    }

    private FluidBarClientScreenModule cd(ItemStack stack) {
        FluidBarClientScreenModule data = stack.get(ScreenModule.CLIENTMODULE_FLUIDBAR_DATA);
        if (data == null) {
            data = new FluidBarClientScreenModule();
        }
        return data;
    }

    private void cd(ItemStack stack, Consumer<FluidBarClientScreenModule> setter) {
        FluidBarClientScreenModule data = cd(stack);
        setter.accept(data);
        stack.set(ScreenModule.CLIENTMODULE_FLUIDBAR_DATA, data);
    }

    @Override
    public void createGui(IModuleGuiBuilder guiBuilder) {
        guiBuilder
                .label("Label:")
                .text((stack, s) -> cd(stack).setLine(s), stack -> cd(stack).getLine(), "Label text")
                .color((stack, c) -> cd(stack).setColor(c), stack -> cd(stack).getColor(), "Color for the label")
                .nl()

                .label("mb+:")
                .color((stack, c) -> cd(stack).setPosColor(c), stack -> cd(stack).getPosColor(), "Color for the mb text")
                .label("mb-:")
                .color((stack, c) -> cd(stack).setNegColor(c), stack -> cd(stack).getNegColor(), "Color for the negative", "mb/tick ratio")
                .nl()

                .toggleNegative((stack, b) -> cd(stack).setHideBar(b), stack -> cd(stack).isHideBar(), "Bar", "Toggle visibility of the", "fluid bar")
                .mode("mb")
                .format((stack, f) -> cd(stack).setFormat(f), stack -> cd(stack).getFormat())
                .nl()

                .choices((stack, c) -> cd(stack).setAlign(c), stack -> cd(stack).getAlign(), "Label alignment", "Left", "Center", "Right")
                .nl()

                .label("Block:")
                .block(stack -> cd(stack).getPos())
                .nl();
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
        CompoundTag tagCompound = new CompoundTag();//stack.getTag();
        if (tagCompound == null) {
            tagCompound = new CompoundTag();
        }
        if (CapabilityTools.getFluidCapabilitySafe(te) != null) {
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
                Logging.message(player, "Fluid module is set to block '" + name + "'");
            }
        } else {
            tagCompound.remove("monitordim");
            tagCompound.remove("monitorx");
            tagCompound.remove("monitory");
            tagCompound.remove("monitorz");
            tagCompound.remove("monitorname");
            if (world.isClientSide) {
                Logging.message(player, "Fluid module is cleared");
            }
        }
        // @todo 1.21 data
//        stack.setTag(tagCompound);
        return InteractionResult.SUCCESS;
    }

    @Override
    public Collection<DataComponentType<?>> getComponentsToPreserve() {
        // @todo 1.21 implement?
        return List.of();
    }
}