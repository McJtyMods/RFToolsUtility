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
import mcjty.lib.varia.Sync;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.Tags;
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
    private final LazyOptional<AutomationFilterItemHander> itemHandler = LazyOptional.of(() -> new AutomationFilterItemHander(items));

    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Inventory Checker")
            .shortListener(Sync.bool(this::isUseDamage, this::setUseDamage))
            .integerListener(Sync.integer(this::getAmount, this::setAmount))
            .shortListener(Sync.integer(this::getSlot, this::setSlot))
            .containerSupplier((windowId, player) -> new GenericContainer(LogicBlockModule.CONTAINER_INVCHECKER.get(), windowId, CONTAINER_FACTORY.get(), getBlockPos(), InvCheckerTileEntity.this))
            .itemHandler(() -> items));

    private int amount = 1;
    private int slot = 0;
    private boolean useDamage = false;
    private Tags.IOptionalNamedTag<Item> tag = null;
    private int checkCounter = 0;

    public InvCheckerTileEntity() {
        super(LogicBlockModule.TYPE_INVCHECKER.get());
    }

    public static LogicSlabBlock createBlock() {
        return new LogicSlabBlock(new BlockBuilder()
                .topDriver(RFToolsUtilityTOPDriver.DRIVER)
                .manualEntry(ManualHelper.create("rftoolsutility:logic/invchecker"))
                .info(key("message.rftoolsutility.shiftmessage"))
                .infoShift(header())
                .tileEntitySupplier(InvCheckerTileEntity::new));
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
        setChanged();
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
        setChanged();
    }

    public Tags.IOptionalNamedTag<Item> getTag() {
        return tag;
    }

    public void setTag(Tags.IOptionalNamedTag<Item> tag) {
        this.tag = tag;
        setChanged();
    }

    public void setTagByName(String tagName) {
        if (tagName == null) {
            tag = null;
        } else {
            tag = getiNamedTag(tagName);
        }
        markDirtyClient();
    }

    private Tags.IOptionalNamedTag<Item> getiNamedTag(String tagName) {
        return ItemTags.createOptional(new ResourceLocation(tagName));
    }

    public String getTagName() {
        return tag == null ? null : tag.getName().toString();
    }

    public boolean isUseDamage() {
        return useDamage;
    }

    public void setUseDamage(boolean useDamage) {
        this.useDamage = useDamage;
        setChanged();
    }

    @Override
    public void tick() {
        if (level.isClientSide) {
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
        Direction inputSide = getFacing(level.getBlockState(getBlockPos())).getInputSide();
        BlockPos inputPos = getBlockPos().relative(inputSide);
        TileEntity te = level.getBlockEntity(inputPos);
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
                if (matcher.sameItem(stack)) {
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
        if (info.contains("amount")) {
            amount = info.getInt("amount");
        }
        if (info.contains("slot")) {
            slot = info.getInt("slot");
        }
        if (info.contains("tag")) {
            String tagString = info.getString("tag");
            if (!tagString.isEmpty()) {
                tag = getiNamedTag(tagString);
            }
        }
        if (info.contains("useDamage")) {
            useDamage = info.getBoolean("useDamage");
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT tagCompound) {
        super.save(tagCompound);
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
            info.putString("tag", tag.getName().toString());
        }
        info.putBoolean("useDamage", useDamage);
    }

    @Override
    public void writeClientDataToNBT(CompoundNBT tagCompound) {
        CompoundNBT info = getOrCreateInfo(tagCompound);
        if (tag != null) {
            info.putString("tag", tag.getName().toString());
        }
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
            return itemHandler.cast();
        }
        if (cap == CapabilityContainerProvider.CONTAINER_PROVIDER_CAPABILITY) {
            return screenHandler.cast();
        }
        return super.getCapability(cap, facing);
    }
}