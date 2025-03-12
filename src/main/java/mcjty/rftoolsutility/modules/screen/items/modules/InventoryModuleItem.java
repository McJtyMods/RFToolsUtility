package mcjty.rftoolsutility.modules.screen.items.modules;

import com.mojang.serialization.Codec;
import mcjty.lib.crafting.IComponentsToPreserve;
import mcjty.lib.varia.*;
import mcjty.rftoolsbase.api.screens.IClientScreenModule;
import mcjty.rftoolsbase.api.screens.IModuleGuiBuilder;
import mcjty.rftoolsbase.api.screens.IScreenModule;
import mcjty.rftoolsbase.tools.GenericModuleItem;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import mcjty.rftoolsutility.modules.screen.ScreenModule;
import mcjty.rftoolsutility.modules.screen.modules.InventoryScreenModule;
import mcjty.rftoolsutility.modules.screen.modulesclient.InventoryClientScreenModule;
import net.minecraft.ChatFormatting;
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

public class InventoryModuleItem extends GenericModuleItem implements IComponentsToPreserve {

    public InventoryModuleItem() {
        super(RFToolsUtility.setup.defaultProperties().stacksTo(1).durability(1));
    }

    @Override
    public @Nullable Codec<? extends IScreenModule<?, ?>> codec() {
        return InventoryScreenModule.CODEC;
    }

    @Override
    public @Nullable StreamCodec<RegistryFriendlyByteBuf, ? extends IScreenModule<?, ?>> streamCodec() {
        return InventoryScreenModule.STREAM_CODEC;
    }

    @Override
    public @Nullable DataComponentType<? extends IScreenModule<?, ?>> componentType() {
        return ScreenModule.MODULE_INVENTORY_DATA.get();
    }

    @Override
    public IScreenModule<?, ?> createServerScreenModule() {
        return InventoryScreenModule.DEFAULT;
    }

    @Override
    public IClientScreenModule<?> createClientScreenModule() {
        return new InventoryClientScreenModule();
    }

    @Override
    protected int getUses(ItemStack stack) {
        return ScreenConfiguration.ITEMSTACK_RFPERTICK.get();
    }

    @Override
    protected boolean hasGoldMessage(ItemStack stack) {
        return data(stack).getPos().pos() == BlockPosTools.INVALID;
    }

    @Override
    protected String getInfoString(ItemStack stack) {
        InventoryScreenModule data = data(stack);
        return ModuleTools.getTargetString(data.getMonitor(), data.getPos());
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
        if (te == null) {
            if (world.isClientSide) {
                Logging.message(player, ChatFormatting.RED + "This is not a valid inventory!");
            }
            return InteractionResult.SUCCESS;
        }
        InventoryScreenModule data = data(stack);
        if (CapabilityTools.getItemCapabilitySafe(te) != null) {
            data = data.withPos(GlobalPos.of(world.dimension(), pos));
            String name = "<invalid>";
            if (!world.getBlockState(pos).isAir()) {
                name = Tools.getReadableName(world, pos);
            }
            data = data.withMonitor(name);
            if (world.isClientSide) {
                Logging.message(player, "Inventory module is set to block '" + name + "'");
            }
        } else {
            data = data.withPos(GlobalPos.of(Level.OVERWORLD, BlockPosTools.INVALID));
            data = data.withMonitor("");
            if (world.isClientSide) {
                Logging.message(player, "Inventory module is cleared");
            }
        }
        stack.set(ScreenModule.MODULE_INVENTORY_DATA, data);
        return InteractionResult.SUCCESS;
    }

    @Override
    public String getModuleName() {
        return "Inv";
    }

    public static InventoryScreenModule data(ItemStack stack) {
        InventoryScreenModule data = stack.get(ScreenModule.MODULE_INVENTORY_DATA);
        if (data == null) {
            data = InventoryScreenModule.DEFAULT;
        }
        return data;
    }

    public static void data(ItemStack stack, Function<InventoryScreenModule, InventoryScreenModule> setter) {
        InventoryScreenModule data = data(stack);
        data = setter.apply(data);
        stack.set(ScreenModule.MODULE_INVENTORY_DATA, data);
    }

    @Override
    public void createGui(IModuleGuiBuilder guiBuilder) {
        guiBuilder
                .label("Slot 1:")
                .integer((stack, index) -> data(stack).withSlot1(index), stack -> data(stack).getSlot1(), "Slot index to show")
                .nl()

                .label("Slot 2:")
                .integer((stack, index) -> data(stack).withSlot2(index), stack -> data(stack).getSlot2(), "Slot index to show")
                .nl()

                .label("Slot 3:")
                .integer((stack, index) -> data(stack).withSlot3(index), stack -> data(stack).getSlot3(), "Slot index to show")
                .nl()

                .label("Slot 4:")
                .integer((stack, index) -> data(stack).withSlot4(index), stack -> data(stack).getSlot4(), "Slot index to show")
                .nl()

                .block(stack -> data(stack).getPos(), stack -> data(stack).getMonitor())
                .nl();
    }

    @Override
    public Collection<DataComponentType<?>> getComponentsToPreserve() {
        return List.of(ScreenModule.MODULE_INVENTORY_DATA.get());
    }
}