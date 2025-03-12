package mcjty.rftoolsutility.modules.screen.modules;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mcjty.lib.network.NetworkTools;
import mcjty.lib.varia.BlockPosTools;
import mcjty.lib.varia.CapabilityTools;
import mcjty.lib.varia.LevelTools;
import mcjty.rftoolsbase.api.screens.IScreenDataHelper;
import mcjty.rftoolsbase.api.screens.IScreenModule;
import mcjty.rftoolsbase.api.screens.data.IModuleData;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.Objects;

public record InventoryScreenModule(int slot1, int slot2, int slot3, int slot4, GlobalPos pos, boolean active, String monitor) implements IScreenModule<InventoryScreenModule, InventoryScreenModule.ModuleDataStacks> {

    public static final InventoryScreenModule DEFAULT = new InventoryScreenModule(-1, -1, -1, -1, GlobalPos.of(Level.OVERWORLD, BlockPosTools.INVALID), false, "");

    public static final Codec<InventoryScreenModule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("slot1").forGetter(module -> module.slot1),
            Codec.INT.fieldOf("slot2").forGetter(module -> module.slot2),
            Codec.INT.fieldOf("slot3").forGetter(module -> module.slot3),
            Codec.INT.fieldOf("slot4").forGetter(module -> module.slot4),
            GlobalPos.CODEC.fieldOf("pos").forGetter(module -> module.pos),
            Codec.STRING.fieldOf("monitor").forGetter(module -> module.monitor)
    ).apply(instance, InventoryScreenModule::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, InventoryScreenModule> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, module -> module.slot1,
            ByteBufCodecs.INT, module -> module.slot2,
            ByteBufCodecs.INT, module -> module.slot3,
            ByteBufCodecs.INT, module -> module.slot4,
            GlobalPos.STREAM_CODEC, module -> module.pos,
            ByteBufCodecs.STRING_UTF8, module -> module.monitor,
            InventoryScreenModule::new);

    public InventoryScreenModule(int slot1, int slot2, int slot3, int slot4, GlobalPos pos, String monitor) {
        this(slot1, slot2, slot3, slot4, pos, false, monitor);
    }


    public int getSlot1() {
        return slot1;
    }

    public int getSlot2() {
        return slot2;
    }

    public int getSlot3() {
        return slot3;
    }

    public int getSlot4() {
        return slot4;
    }

    public GlobalPos getPos() {
        return pos;
    }

    public String getMonitor() {
        return monitor;
    }

    public InventoryScreenModule withSlot1(int slot1) {
        return new InventoryScreenModule(slot1, slot2, slot3, slot4, pos, active, monitor);
    }

    public InventoryScreenModule withSlot2(int slot2) {
        return new InventoryScreenModule(slot1, slot2, slot3, slot4, pos, active, monitor);
    }

    public InventoryScreenModule withSlot3(int slot3) {
        return new InventoryScreenModule(slot1, slot2, slot3, slot4, pos, active, monitor);
    }

    public InventoryScreenModule withSlot4(int slot4) {
        return new InventoryScreenModule(slot1, slot2, slot3, slot4, pos, active, monitor);
    }

    public InventoryScreenModule withPos(GlobalPos pos) {
        return new InventoryScreenModule(slot1, slot2, slot3, slot4, pos, active, monitor);
    }

    public InventoryScreenModule withMonitor(String monitor) {
        return new InventoryScreenModule(slot1, slot2, slot3, slot4, pos, active, monitor);
    }

    public InventoryScreenModule withActive(boolean active) {
        return new InventoryScreenModule(slot1, slot2, slot3, slot4, pos, active, monitor);
    }

    public static class ModuleDataStacks implements IModuleData {

        public static final String ID = RFToolsUtility.MODID + ":itemStacks";

        private final ItemStack[] stacks = new ItemStack[4];

        @Override
        public String getId() {
            return ID;
        }

        public ModuleDataStacks(ItemStack stack1, ItemStack stack2, ItemStack stack3, ItemStack stack4) {
            this.stacks[0] = stack1;
            this.stacks[1] = stack2;
            this.stacks[2] = stack3;
            this.stacks[3] = stack4;
        }

        public ModuleDataStacks(RegistryFriendlyByteBuf buf) {
            for (int i = 0 ; i < 4 ; i++) {
                stacks[i] = NetworkTools.readItemStack(buf);
            }
        }

        public ItemStack getStack(int idx) {
            return stacks[idx];
        }

        @Override
        public void writeToBuf(RegistryFriendlyByteBuf buf) {
            writeStack(buf, stacks[0]);
            writeStack(buf, stacks[1]);
            writeStack(buf, stacks[2]);
            writeStack(buf, stacks[3]);
        }

        private void writeStack(RegistryFriendlyByteBuf buf, ItemStack stack) {
            NetworkTools.writeItemStack(buf, stack);
        }
    }

    @Override
    public ModuleDataStacks getData(IScreenDataHelper helper, Level worldObj, long millis) {
        if (!active) {
            return null;
        }
        Level world = LevelTools.getLevel(worldObj, pos.dimension());
        if (world == null) {
            return null;
        }

        if (!LevelTools.isLoaded(world, pos.pos())) {
            return null;
        }

        BlockEntity te = world.getBlockEntity(pos.pos());
        if (te == null) {
            return null;
        }

        IItemHandler h = CapabilityTools.getItemCapabilitySafe(te);
        if (h != null) {
            ItemStack stack1 = getItemStack(h, slot1);
            ItemStack stack2 = getItemStack(h, slot2);
            ItemStack stack3 = getItemStack(h, slot3);
            ItemStack stack4 = getItemStack(h, slot4);
            return new ModuleDataStacks(stack1, stack2, stack3, stack4);
        }
        return null;
    }

    private ItemStack getItemStack(Container inventory, int slot) {
        if (slot == -1) {
            return ItemStack.EMPTY;
        }
        if (slot < inventory.getContainerSize()) {
//            if (RFTools.instance.mfr && MFRCompatibility.isExtendedStorage(inventory)) {
//                return MFRCompatibility.getContents(inventory);
//            } else if (RFTools.instance.jabba && MFRCompatibility.isExtendedStorage(inventory)) {
//                return MFRCompatibility.getContents(inventory);
//            }
            return inventory.getItem(slot);
        } else {
            return ItemStack.EMPTY;
        }
    }

    private ItemStack getItemStack(IItemHandler itemHandler, int slot) {
        if (slot == -1) {
            return ItemStack.EMPTY;
        }
        if (slot < itemHandler.getSlots()) {
//            if (RFTools.instance.mfr && MFRCompatibility.isExtendedStorage(inventory)) {
//                return MFRCompatibility.getContents(inventory);
//            } else if (RFTools.instance.jabba && MFRCompatibility.isExtendedStorage(inventory)) {
//                return MFRCompatibility.getContents(inventory);
//            }
            return itemHandler.getStackInSlot(slot);
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public InventoryScreenModule validate(Level world, BlockPos p, boolean isPlus) {
        if (isPlus) {
            return withActive(true);
        }
        // To check if this is active we need to check that the coordinate in this module is correct,
        // the dimension is equal and the coordinate is not too far from the given position (max 64 blocks)
        if (LevelTools.isLoaded(world, pos.pos())) {
            if (Objects.equals(pos.dimension(), world.dimension())) {
                int dx = Math.abs(pos.pos().getX() - p.getX());
                int dy = Math.abs(pos.pos().getY() - p.getY());
                int dz = Math.abs(pos.pos().getZ() - p.getZ());
                if (dx <= 64 && dy <= 64 && dz <= 64) {
                }
            }
        }
        return withActive(false);
    }

    @Override
    public int getRfPerTick() {
        return ScreenConfiguration.ITEMSTACK_RFPERTICK.get();
    }

    @Override
    public void mouseClick(Level world, int x, int y, boolean clicked, Player player) {

    }
}
