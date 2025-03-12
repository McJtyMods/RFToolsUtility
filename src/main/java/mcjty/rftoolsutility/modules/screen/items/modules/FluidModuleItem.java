package mcjty.rftoolsutility.modules.screen.items.modules;

import com.mojang.serialization.Codec;
import mcjty.lib.crafting.IComponentsToPreserve;
import mcjty.lib.varia.*;
import mcjty.rftoolsbase.api.screens.IClientScreenModule;
import mcjty.rftoolsbase.api.screens.IModuleGuiBuilder;
import mcjty.rftoolsbase.api.screens.IScreenModule;
import mcjty.rftoolsbase.api.screens.TextAlign;
import mcjty.rftoolsbase.tools.GenericModuleItem;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import mcjty.rftoolsutility.modules.screen.ScreenModule;
import mcjty.rftoolsutility.modules.screen.modules.FluidBarScreenModule;
import mcjty.rftoolsutility.modules.screen.modulesclient.FluidBarClientScreenModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentType;
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
import java.util.function.Function;

public class FluidModuleItem extends GenericModuleItem implements IComponentsToPreserve {

    public FluidModuleItem() {
        super(RFToolsUtility.setup.defaultProperties().stacksTo(1).durability(1));
    }

    @Override
    public @Nullable Codec<? extends IScreenModule<?, ?>> codec() {
        return FluidBarScreenModule.CODEC;
    }

    @Override
    public @Nullable StreamCodec<RegistryFriendlyByteBuf, ? extends IScreenModule<?, ?>> streamCodec() {
        return FluidBarScreenModule.STREAM_CODEC;
    }

    @Override
    public @Nullable DataComponentType<? extends IScreenModule<?, ?>> componentType() {
        return ScreenModule.MODULE_FLUIDBAR_DATA.get();
    }

    @Override
    public IScreenModule<?, ?> createServerScreenModule() {
        return FluidBarScreenModule.DEFAULT;
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
        return data(stack).getPos().pos() == BlockPosTools.INVALID;
    }

    @Override
    protected String getInfoString(ItemStack stack) {
        FluidBarScreenModule data = data(stack);
        return ModuleTools.getTargetString(data.getMonitor(), data.getPos());
    }


//    @Override
//    public int getMaxItemUseDuration(ItemStack stack) {
//        return 1;
//    }

    @Override
    public String getModuleName() {
        return "Fluid";
    }

    public static FluidBarScreenModule data(ItemStack stack) {
        FluidBarScreenModule data = stack.get(ScreenModule.MODULE_FLUIDBAR_DATA);
        if (data == null) {
            data = FluidBarScreenModule.DEFAULT;
        }
        return data;
    }

    public static void data(ItemStack stack, Function<FluidBarScreenModule, FluidBarScreenModule> setter) {
        FluidBarScreenModule data = data(stack);
        data = setter.apply(data);
        stack.set(ScreenModule.MODULE_FLUIDBAR_DATA, data);
    }

    @Override
    public void createGui(IModuleGuiBuilder guiBuilder) {
        guiBuilder
                .label("Label:")
                .text((stack, s) -> data(stack).withLine(s), stack -> data(stack).getLine(), "Label text")
                .color((stack, c) -> data(stack).withColor(c), stack -> data(stack).getColor(), "Color for the label")
                .nl()

                .label("mb+:")
                .color((stack, c) -> data(stack).withPosColor(c), stack -> data(stack).getPosColor(), "Color for the mb text")
                .label("mb-:")
                .color((stack, c) -> data(stack).withNegColor(c), stack -> data(stack).getNegColor(), "Color for the negative", "mb/tick ratio")
                .nl()

                .toggleNegative((stack, b) -> data(stack).withHideBar(b), stack -> data(stack).isHideBar(), "Bar", "Toggle visibility of the", "fluid bar")
                .mode((stack, m) -> data(stack).withBarMode(m), stack -> data(stack).getBarMode(), "mb")
                .format((stack, f) -> data(stack).withFormat(f), stack -> data(stack).getFormat())
                .nl()

                .choices((stack, c) -> data(stack).withAlign(TextAlign.get(c)), stack -> data(stack).getAlign().name(), "Label alignment", "Left", "Center", "Right")
                .nl()

                .label("Block:")
                .block(stack -> data(stack).getPos(), stack -> data(stack).getMonitor())
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
        FluidBarScreenModule data = data(stack);
        if (CapabilityTools.getFluidCapabilitySafe(te) != null) {
            data = data.withPos(GlobalPos.of(world.dimension(), pos));
            String name = "<invalid>";
            if (!world.getBlockState(pos).isAir()) {
                name = Tools.getReadableName(world, pos);
            }
            data = data.withMonitor(name);
            if (world.isClientSide) {
                Logging.message(player, "Fluid module is set to block '" + name + "'");
            }
        } else {
            data = data.withPos(GlobalPos.of(Level.OVERWORLD, BlockPosTools.INVALID));
            data = data.withMonitor("");
            if (world.isClientSide) {
                Logging.message(player, "Fluid module is cleared");
            }
        }
        stack.set(ScreenModule.MODULE_FLUIDBAR_DATA, data);
        return InteractionResult.SUCCESS;
    }

    @Override
    public Collection<DataComponentType<?>> getComponentsToPreserve() {
        return List.of(ScreenModule.MODULE_FLUIDBAR_DATA.get());
    }
}