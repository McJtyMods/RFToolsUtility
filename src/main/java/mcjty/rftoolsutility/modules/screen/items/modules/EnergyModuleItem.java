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
import mcjty.rftoolsutility.modules.screen.modules.EnergyBarScreenModule;
import mcjty.rftoolsutility.modules.screen.modulesclient.EnergyBarClientScreenModule;
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

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class EnergyModuleItem extends GenericModuleItem implements IComponentsToPreserve {

    public EnergyModuleItem() {
        super(RFToolsUtility.setup.defaultProperties().stacksTo(1).durability(1));
    }

    @Override
    public Codec<? extends IScreenModule<?, ?>> codec() {
        return EnergyBarScreenModule.CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ? extends IScreenModule<?, ?>> streamCodec() {
        return EnergyBarScreenModule.STREAM_CODEC;
    }

    @Override
    public DataComponentType<? extends IScreenModule<?, ?>> componentType() {
        return ScreenModule.MODULE_ENERGY_BAR_DATA.get();
    }

    @Override
    protected int getUses(ItemStack stack) {
        return ScreenConfiguration.ENERGY_RFPERTICK.get();
    }

    @Override
    protected boolean hasGoldMessage(ItemStack stack) {
        return !BlockPosTools.isValid(data(stack).getPos().pos());
    }

    @Override
    protected String getInfoString(ItemStack stack) {
        EnergyBarScreenModule data = data(stack);
        return ModuleTools.getTargetString(data.getMonitor(), data.getPos());
    }


    //    @Override
//    public int getMaxItemUseDuration(ItemStack stack) {
//        return 1;
//    }


    @Override
    public IScreenModule<?, ?> createServerScreenModule() {
        return EnergyBarScreenModule.DEFAULT;
    }

    @Override
    public IClientScreenModule<?> createClientScreenModule() {
        return new EnergyBarClientScreenModule();
    }

    @Override
    public String getModuleName() {
        return "RF";
    }

    public static EnergyBarScreenModule data(ItemStack stack) {
        EnergyBarScreenModule data = stack.get(ScreenModule.MODULE_ENERGY_BAR_DATA);
        if (data == null) {
            data = EnergyBarScreenModule.DEFAULT;
        }
        return data;
    }

    public static void data(ItemStack stack, Function<EnergyBarScreenModule, EnergyBarScreenModule> setter) {
        EnergyBarScreenModule data = data(stack);
        data = setter.apply(data);
        stack.set(ScreenModule.MODULE_ENERGY_BAR_DATA, data);
    }

    @Override
    public void createGui(IModuleGuiBuilder guiBuilder) {
        guiBuilder
                .label("Label:")
                .text((stack, s) -> data(stack, d -> d.withLine(s)), stack -> data(stack).getLine(), "Label text")
                .color((stack, c) -> data(stack, d -> d.withColor(c)), stack -> data(stack).getColor(), "Color for the label")
                .nl()

                .label("RF+:")
                .color((stack, c) -> data(stack, d -> d.withPosColor(c)), stack -> data(stack).getPosColor(), "Color for the RF text")
                .label("RF-:")
                .color((stack, c) -> data(stack, d -> d.withNegColor(c)), stack -> data(stack).getNegColor(), "Color for the negative", "RF/tick ratio")
                .nl()

                .toggleNegative((stack, b) -> data(stack, d -> d.withHideBar(b)), stack -> data(stack).isHideBar(), "Bar", "Toggle visibility of the", "energy bar")
                .mode((stack, m) -> data(stack, d -> d.withBarMode(m)), stack -> data(stack).getBarMode(), "RF")
                .format((stack, f) -> data(stack, d -> d.withFormat(f)), stack -> data(stack).getFormat())
                .nl()

                .choices((stack, c) -> data(stack, d -> d.withAlign(TextAlign.get(c))), stack -> data(stack).getAlign().getSerializedName(), "Label alignment", "Left", "Center", "Right")
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
        EnergyBarScreenModule data = data(stack);
        if (EnergyTools.isEnergyTE(te, facing)) {
            data = data.withPos(GlobalPos.of(world.dimension(), pos));
            data = data.withSide(facing);
            String name = "<invalid>";
            if (!world.getBlockState(pos).isAir()) {
                name = Tools.getReadableName(world, pos);
            }
            data = data.withMonitor(name);
            if (world.isClientSide) {
                Logging.message(player, "Energy module is set to block '" + name + "'");
            }
        } else {
            data = data.withPos(GlobalPos.of(Level.OVERWORLD, BlockPosTools.INVALID));
            data = data.withMonitor("");
            if (world.isClientSide) {
                Logging.message(player, "Energy module is cleared");
            }
        }
        stack.set(ScreenModule.MODULE_ENERGY_BAR_DATA, data);
        return InteractionResult.SUCCESS;
    }

    @Override
    public Collection<DataComponentType<?>> getComponentsToPreserve() {
        return List.of(ScreenModule.MODULE_ENERGY_BAR_DATA.get());
    }
}
