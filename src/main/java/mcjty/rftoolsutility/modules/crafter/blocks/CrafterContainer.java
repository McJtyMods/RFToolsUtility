package mcjty.rftoolsutility.modules.crafter.blocks;

import mcjty.lib.container.*;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.ItemStackList;
import mcjty.rftoolsbase.modules.filter.items.FilterModuleItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static mcjty.lib.container.ContainerFactory.CONTAINER_CONTAINER;
import static mcjty.lib.container.SlotDefinition.*;
import static mcjty.rftoolsutility.modules.crafter.CrafterModule.CONTAINER_CRAFTER;

public class CrafterContainer extends GenericContainer {

    public static final int SLOT_CRAFTINPUT = 0;
    public static final int SLOT_CRAFTOUTPUT = 9;
    public static final int SLOT_BUFFER = 10;
    public static final int BUFFER_SIZE = (13*2);
    public static final int SLOT_BUFFEROUT = SLOT_BUFFER + BUFFER_SIZE;
    public static final int BUFFEROUT_SIZE = 4;
    public static final int SLOT_FILTER_MODULE = SLOT_BUFFEROUT + BUFFEROUT_SIZE;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(10 +BUFFER_SIZE + BUFFEROUT_SIZE + 1)
            .box(ghost(), SLOT_CRAFTINPUT, 193, 7, 3, 3)
            .slot(ghostOut(), SLOT_CRAFTOUTPUT, 193, 65)
            .box(generic().in(), SLOT_BUFFER, 13, 97, 13, 2)
            .box(generic().out(), SLOT_BUFFEROUT, 31, 142, 2, 2)
            .slot(specific(stack -> stack.getItem() instanceof FilterModuleItem), SLOT_FILTER_MODULE, 157, 43)
            .playerSlots(85, 142));


    public CrafterContainer(int id, ContainerFactory factory, BlockPos pos, @Nullable GenericTileEntity te) {
        super(CONTAINER_CRAFTER.get(), id, factory, pos, te);
//        generateSlots();
    }

    @Override
    protected Slot createSlot(SlotFactory slotFactory, PlayerEntity playerEntity, IItemHandler inventory, int index, int x, int y, SlotType slotType) {
        CrafterBaseTE c = (CrafterBaseTE) te;
        if (index >= SLOT_BUFFER && index < SLOT_BUFFEROUT) {
            return new BaseSlot(inventory, te, index, x, y) {
                @Override
                public boolean mayPlace(@Nonnull ItemStack stack) {
                    if (!c.isItemValidForSlot(getSlotIndex(), stack)) {
                        return false;
                    }
                    return super.mayPlace(stack);
                }

                @Override
                public void setChanged() {
                    c.noRecipesWork = false;
                    super.setChanged();
                }
            };
        } else if (index >= SLOT_BUFFEROUT && index < SLOT_FILTER_MODULE) {
            return new BaseSlot(inventory, te, index, x, y) {
                @Override
                public boolean mayPlace(@Nonnull ItemStack stack) {
                    if (!c.isItemValidForSlot(getSlotIndex(), stack)) {
                        return false;
                    }
                    return super.mayPlace(stack);
                }

                @Override
                public void setChanged() {
                    c.noRecipesWork = false;
                    super.setChanged();
                }
            };
        }
        return super.createSlot(slotFactory, playerEntity, inventory, index, x, y, slotType);
    }

    @Nonnull
    @Override
    public ItemStack clicked(int index, int button, @Nonnull ClickType mode, @Nonnull PlayerEntity player) {
        // Allow replacing input slot ghost items by shift-clicking.
        if (mode == ClickType.QUICK_MOVE &&
            index >= CrafterContainer.SLOT_BUFFER &&
            index < CrafterContainer.SLOT_BUFFEROUT) {

            CrafterBaseTE c = (CrafterBaseTE) te;

            int offset = index - CrafterContainer.SLOT_BUFFER;
            ItemStackList ghostSlots = c.getGhostSlots();
            ItemStack ghostSlot = ghostSlots.get(offset);
            ItemStack clickedWith = player.inventory.getCarried();
            if (!ghostSlot.isEmpty() && !ghostSlot.sameItem(clickedWith)) {
                ItemStack copy = clickedWith.copy();
                copy.setCount(1);
                ghostSlots.set(offset, copy);
                broadcastChanges();
                return ItemStack.EMPTY;
            }
        }

        return super.clicked(index, button, mode, player);
    }
}
