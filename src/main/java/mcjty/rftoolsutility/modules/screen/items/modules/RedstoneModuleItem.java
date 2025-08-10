package mcjty.rftoolsutility.modules.screen.items.modules;

import com.mojang.serialization.Codec;
import mcjty.lib.varia.BlockPosTools;
import mcjty.lib.varia.ComponentFactory;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.ModuleTools;
import mcjty.rftoolsbase.api.screens.IClientScreenModule;
import mcjty.rftoolsbase.api.screens.IModuleGuiBuilder;
import mcjty.rftoolsbase.api.screens.IScreenModule;
import mcjty.rftoolsbase.api.screens.TextAlign;
import mcjty.rftoolsbase.tools.GenericModuleItem;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.logic.blocks.RedstoneChannelTileEntity;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import mcjty.rftoolsutility.modules.screen.ScreenModule;
import mcjty.rftoolsutility.modules.screen.modules.RedstoneScreenModule;
import mcjty.rftoolsutility.modules.screen.modulesclient.RedstoneClientScreenModule;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class RedstoneModuleItem extends GenericModuleItem {

    public RedstoneModuleItem() {
        super(RFToolsUtility.setup.defaultProperties()
                .stacksTo(1)
                .durability(1));
    }

    @Override
    public @Nullable Codec<? extends IScreenModule<?, ?>> codec() {
        return RedstoneScreenModule.CODEC;
    }

    @Override
    public @Nullable StreamCodec<RegistryFriendlyByteBuf, ? extends IScreenModule<?, ?>> streamCodec() {
        return RedstoneScreenModule.STREAM_CODEC;
    }

    @Override
    public @Nullable DataComponentType<? extends IScreenModule<?, ?>> componentType() {
        return ScreenModule.MODULE_REDSTONE_DATA.get();
    }

    @Override
    public IScreenModule<?, ?> createServerScreenModule() {
        return RedstoneScreenModule.DEFAULT;
    }

    @Override
    public IClientScreenModule<?> createClientScreenModule() {
        return new RedstoneClientScreenModule();
    }

    @Override
    protected int getUses(ItemStack stack) {
        return ScreenConfiguration.REDSTONE_RFPERTICK.get();
    }

    @Override
    protected boolean hasGoldMessage(ItemStack stack) {
        return !BlockPosTools.isValid(data(stack).getPos().pos());
    }

    @Override
    protected String getInfoString(ItemStack stack) {
        RedstoneScreenModule data = data(stack);
        return ModuleTools.getTargetString(data.getMonitor(), data.getPos());
    }

//    @Override
//    public int getMaxItemUseDuration(ItemStack stack) {
//        return 1;
//    }


    @Override
    public void appendHoverText(@Nonnull ItemStack itemStack, @Nullable TooltipContext context, @Nonnull List<Component> list, @Nonnull TooltipFlag flag) {
        super.appendHoverText(itemStack, context, list, flag);
        RedstoneScreenModule data = data(itemStack);
        int channel = data.getChannel();
        list.add(ComponentFactory.literal(ChatFormatting.YELLOW + "Channel: " + channel));
    }

    @Override
    public String getModuleName() {
        return "Red";
    }

    public static RedstoneScreenModule data(ItemStack stack) {
        RedstoneScreenModule data = stack.get(ScreenModule.MODULE_REDSTONE_DATA);
        if (data == null) {
            data = RedstoneScreenModule.DEFAULT;
        }
        return data;
    }

    public static void data(ItemStack stack, Function<RedstoneScreenModule, RedstoneScreenModule> setter) {
        RedstoneScreenModule data = data(stack);
        data = setter.apply(data);
        stack.set(ScreenModule.MODULE_REDSTONE_DATA, data);
    }

    @Override
    public void createGui(IModuleGuiBuilder guiBuilder) {
        guiBuilder
                .label("Label:")
                .text((stack, s) -> data(stack).withLine(s), stack -> data(stack).getLine(), "Label text")
                .color((stack, c) -> data(stack).withColor(c), stack -> data(stack).getColor(), "Color for the label")
                .nl()

                .label("Yes:")
                .text((stack, s) -> data(stack).withYestext(s), stack -> data(stack).getYestext(), "Positive text")
                .color((stack, c) -> data(stack).withYescolor(c), stack -> data(stack).getYescolor(), "Color for the positive text")
                .nl()

                .label("No:")
                .text((stack, s) -> data(stack).withNotext(s), stack -> data(stack).getNotext(), "Negative text")
                .color((stack, c) -> data(stack).withNocolor(c), stack -> data(stack).getNocolor(), "Color for the negative text")
                .nl()

                .choices((stack, c) -> data(stack, d -> d.withAlign(TextAlign.get(c))), stack -> data(stack).getAlign().getSerializedName(), "Label alignment", "Left", "Center", "Right")
                .toggle((stack, b) -> data(stack).withAnalog(b), stack -> data(stack).isAnalog(), "Analog mode", "Whether to show the exact level")
                .nl()

                .label("Block:")
                .block(stack -> data(stack).getPos(), stack -> data(stack).getMonitor())
                .nl();
    }

    @Nonnull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        ItemStack stack = context.getItemInHand();
        BlockPos pos = context.getClickedPos();
        BlockEntity te = world.getBlockEntity(pos);
        Player player = context.getPlayer();
        Direction facing = context.getClickedFace();
        RedstoneScreenModule data = data(stack);
        int channel = -1;
        if (te instanceof RedstoneChannelTileEntity) {
            channel = ((RedstoneChannelTileEntity) te).getChannel(true);
        } else {
            // We selected a random block.
            data = data.withChannel(-1);
            data = data.withPos(GlobalPos.of(world.dimension(), pos));
            data = data.withSide(facing);
            Logging.message(player, "Redstone module is set to " + pos);
            stack.set(ScreenModule.MODULE_REDSTONE_DATA, data);

            return InteractionResult.SUCCESS;
        }

        ModuleTools.clearPositionInModule(stack);

        if (channel != -1) {
            data = data.withChannel(channel);
            Logging.message(player, "Redstone module is set to channel '" + channel + "'");
        } else {
            data = data.withChannel(-1);
            Logging.message(player, "Redstone module is cleared");
        }
        stack.set(ScreenModule.MODULE_REDSTONE_DATA, data);
        return InteractionResult.SUCCESS;
    }
}