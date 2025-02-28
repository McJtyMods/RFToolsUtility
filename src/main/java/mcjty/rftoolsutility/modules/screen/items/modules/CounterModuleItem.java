package mcjty.rftoolsutility.modules.screen.items.modules;

import com.mojang.serialization.Codec;
import mcjty.lib.crafting.IComponentsToPreserve;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.ModuleTools;
import mcjty.lib.varia.Tools;
import mcjty.rftoolsbase.api.screens.IClientScreenModule;
import mcjty.rftoolsbase.api.screens.IModuleGuiBuilder;
import mcjty.rftoolsbase.api.screens.IScreenModule;
import mcjty.rftoolsbase.api.screens.TextAlign;
import mcjty.rftoolsbase.tools.GenericModuleItem;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.blocks.CounterTileEntity;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import mcjty.rftoolsutility.modules.screen.ScreenModule;
import mcjty.rftoolsutility.modules.screen.modules.CounterScreenModule;
import mcjty.rftoolsutility.modules.screen.modulesclient.CounterClientScreenModule;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class CounterModuleItem extends GenericModuleItem implements IComponentsToPreserve {

    public CounterModuleItem() {
        super(RFToolsUtility.setup.defaultProperties().stacksTo(1).durability(1));
    }

    @Override
    public @Nullable Codec<? extends IScreenModule<?>> codec() {
        return CounterScreenModule.CODEC;
    }

    @Override
    public @Nullable StreamCodec<RegistryFriendlyByteBuf, ? extends IScreenModule<?>> streamCodec() {
        return CounterScreenModule.STREAM_CODEC;
    }

    @Override
    public @Nullable DataComponentType<? extends IScreenModule<?>> componentType() {
        return ScreenModule.MODULE_COUNTER_DATA.get();
    }

    @Override
    public IScreenModule<?> createServerScreenModule() {
        return new CounterScreenModule();
    }

    @Override
    public IClientScreenModule<?> createClientScreenModule() {
        return new CounterClientScreenModule();
    }

    @Override
    protected int getUses(ItemStack stack) {
        return ScreenConfiguration.COUNTER_RFPERTICK.get();
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
        return "Count";
    }

    public static CounterScreenModule data(ItemStack stack) {
        CounterScreenModule data = stack.get(ScreenModule.MODULE_COUNTER_DATA);
        if (data == null) {
            data = new CounterScreenModule();
        }
        return data;
    }

    public static void data(ItemStack stack, Consumer<CounterScreenModule> setter) {
        CounterScreenModule data = data(stack);
        setter.accept(data);
        stack.set(ScreenModule.MODULE_COUNTER_DATA, data);
    }

    @Override
    public void createGui(IModuleGuiBuilder guiBuilder) {
        guiBuilder
                .label("Label:")
                .text((stack, s) -> data(stack, d -> d.setLine(s)), stack -> data(stack).getLine() ,"Label text")
                .nl()

                .label("L:")
                .color((stack, c) -> data(stack, d -> d.setColor(c)), stack -> data(stack).getColor(), "Color for the label")
                .label("C:")
                .color((stack, c) -> data(stack, d -> d.setCntcolor(c)), stack -> data(stack).getCntcolor(), "Color for the counter")
                .nl()

                .format((stack, f) -> data(stack).setFormat(f), stack -> data(stack).getFormat())
                .choices((stack, c) -> data(stack, d -> d.setAlign(TextAlign.get(c))), stack -> data(stack).getAlign().name(), "Label alignment", "Left", "Center", "Right")
                .nl()

                .label("Block:")
                .block(stack -> data(stack).getPos())
                .nl();
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
        // @todo 1.21 data
        CompoundTag tagCompound = new CompoundTag();//stack.getTag();
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