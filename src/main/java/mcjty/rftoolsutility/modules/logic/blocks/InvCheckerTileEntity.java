package mcjty.rftoolsutility.modules.logic.blocks;

import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.AutomationFilterItemHander;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.NoDirectionItemHander;
import mcjty.lib.gui.widgets.ChoiceLabel;
import mcjty.lib.gui.widgets.TagSelector;
import mcjty.lib.gui.widgets.TextField;
import mcjty.lib.tileentity.LogicTileEntity;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.CapabilityTools;
import mcjty.lib.varia.InventoryTools;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import mcjty.rftoolsutility.modules.logic.LogicBlockSetup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;
import static mcjty.lib.container.ContainerFactory.CONTAINER_CONTAINER;
import static mcjty.lib.container.SlotDefinition.ghost;
import static mcjty.rftoolsutility.modules.logic.client.GuiInvChecker.DMG_MATCH;

public class InvCheckerTileEntity extends LogicTileEntity implements ITickableTileEntity {

    public static final String CMD_SETAMOUNT = "inv.setCounter";
    public static final String CMD_SETSLOT = "inv.setSlot";
    public static final String CMD_SETDAMAGE = "inv.setUseDamage";
    public static final String CMD_SETTAG = "inv.setTag";

    public static final String CONTAINER_INVENTORY = "container";
    public static final int SLOT_ITEMMATCH = 0;
    
    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(1)
            .slot(ghost(), CONTAINER_CONTAINER, SLOT_ITEMMATCH, 154, 24)
            .playerSlots(10, 70));

    private final NoDirectionItemHander items = createItemHandler();
    private final LazyOptional<NoDirectionItemHander> itemHandler = LazyOptional.of(() -> items);
    private final LazyOptional<AutomationFilterItemHander> automationItemHandler = LazyOptional.of(() -> new AutomationFilterItemHander(items));

    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Inventory Checker")
            .containerSupplier((windowId, player) -> new GenericContainer(LogicBlockSetup.CONTAINER_INVCHECKER.get(), windowId, CONTAINER_FACTORY.get(), getPos(), InvCheckerTileEntity.this))
            .itemHandler(itemHandler));

    private int amount = 1;
    private int slot = 0;
    private boolean useDamage = false;
    private Tag<Item> tag = null;

    private int checkCounter = 0;

    public InvCheckerTileEntity() {
        super(LogicBlockSetup.TYPE_INVCHECKER.get());
    }

    public static LogicSlabBlock createBlock() {
        return new LogicSlabBlock(new BlockBuilder()
                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .info(key("message.rftoolsutility.shiftmessage"))
                .infoShift(header())
                .tileEntitySupplier(InvCheckerTileEntity::new));
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
        markDirtyClient();
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
        markDirtyClient();
    }

    public Tag<Item> getTag() {
        return tag;
    }

    public void setTag(Tag<Item> tag) {
        this.tag = tag;
        markDirtyClient();
    }

    public void setTagByName(String tagName) {
        if (tagName == null) {
            tag = null;
        } else {
            tag = ItemTags.getCollection().get(new ResourceLocation(tagName));
        }
        markDirtyClient();
    }

    public String getTagName() {
        return tag == null ? null : tag.getId().toString();
    }

    public boolean isUseDamage() {
        return useDamage;
    }

    public void setUseDamage(boolean useDamage) {
        this.useDamage = useDamage;
        markDirtyClient();
    }

    @Override
    public void tick() {
        if (world.isRemote) {
            return;
        }

        checkCounter--;
        if (checkCounter > 0) {
            return;
        }
        checkCounter = 10;

        setRedstoneState(checkOutput() ? 15 : 0);
    }

    public boolean checkOutput() {
        Direction inputSide = getFacing(world.getBlockState(getPos())).getInputSide();
        BlockPos inputPos = getPos().offset(inputSide);
        TileEntity te = world.getTileEntity(inputPos);
        if (InventoryTools.isInventory(te)) {
            return CapabilityTools.getItemCapabilitySafe(te).map(capability -> {
                if (slot >= 0 && slot < capability.getSlots()) {
                    ItemStack stack = capability.getStackInSlot(slot);
                    if (!stack.isEmpty()) {
                        int nr = isItemMatching(stack);
                        if (nr >= amount) {
                            if (tag != null) {
                                return tag.contains(stack.getItem());
                            } else {
                                return true;
                            }
                        }
                    }
                }
                return false;
            }).orElse(false);
        }
        return false;
    }

    private int isItemMatching(ItemStack stack) {
        int nr = 0;
        ItemStack matcher = items.getStackInSlot(0);
        if (!matcher.isEmpty()) {
            if (useDamage) {
                if (matcher.isItemEqual(stack)) {
                    nr = stack.getCount();
                }
            } else {
                if (matcher.getItem() == stack.getItem()) {
                    nr = stack.getCount();
                }
            }
        } else {
            nr = stack.getCount();
        }
        return nr;
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);
        powerOutput = tagCompound.getBoolean("rs") ? 15 : 0;
    }

    @Override
    public void readInfo(CompoundNBT tagCompound) {
        super.readInfo(tagCompound);
        CompoundNBT info = tagCompound.getCompound("Info");
        amount = info.getInt("amount");
        slot = info.getInt("slot");
        String tagString = info.getString("tag");
        if (!tagString.isEmpty()) {
            tag = ItemTags.getCollection().get(new ResourceLocation(tagString));
        }
        useDamage = info.getBoolean("useDamage");
    }

    @Override
    public CompoundNBT write(CompoundNBT tagCompound) {
        super.write(tagCompound);
        tagCompound.putBoolean("rs", powerOutput > 0);
        return tagCompound;
    }

    @Override
    public void writeInfo(CompoundNBT tagCompound) {
        super.writeInfo(tagCompound);
        CompoundNBT info = getOrCreateInfo(tagCompound);
        info.putInt("amount", amount);
        info.putInt("slot", slot);
        if (tag != null) {
            info.putString("tag", tag.getId().toString());
        }
        info.putBoolean("useDamage", useDamage);
    }

    @Override
    public boolean execute(PlayerEntity playerMP, String command, TypedMap params) {
        boolean rc = super.execute(playerMP, command, params);
        if (rc) {
            return true;
        }
        if (CMD_SETDAMAGE.equals(command)) {
            setUseDamage(DMG_MATCH.equals(params.get(ChoiceLabel.PARAM_CHOICE)));
            return true;
        } else if (CMD_SETTAG.equals(command)) {
            String tag = params.get(TagSelector.PARAM_TAG);
            setTagByName(tag);
            return true;
        } else if (CMD_SETSLOT.equals(command)) {
            int slot;
            try {
                slot = Integer.parseInt(params.get(TextField.PARAM_TEXT));
            } catch (NumberFormatException e) {
                slot = 0;
            }
            setSlot(slot);
            return true;
        } else if (CMD_SETAMOUNT.equals(command)) {
            int amount;
            try {
                amount = Integer.parseInt(params.get(TextField.PARAM_TEXT));
            } catch (NumberFormatException e) {
                amount = 1;
            }
            setAmount(amount);
            return true;
        }
        return false;
    }

    private NoDirectionItemHander createItemHandler() {
        return new NoDirectionItemHander(this, CONTAINER_FACTORY.get()) {

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return false;
            }

            @Override
            public boolean isItemInsertable(int slot, @Nonnull ItemStack stack) {
                return isItemValid(slot, stack);
            }
        };
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction facing) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return automationItemHandler.cast();
        }
        if (cap == CapabilityContainerProvider.CONTAINER_PROVIDER_CAPABILITY) {
            return screenHandler.cast();
        }
        return super.getCapability(cap, facing);
    }
}