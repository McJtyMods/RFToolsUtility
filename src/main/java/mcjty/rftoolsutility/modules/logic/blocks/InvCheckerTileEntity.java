package mcjty.rftoolsutility.modules.logic.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.bindings.GuiValue;
import mcjty.lib.blockcommands.Command;
import mcjty.lib.blockcommands.ServerCommand;
import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.GenericItemHandler;
import mcjty.lib.gui.widgets.TagSelector;
import mcjty.lib.tileentity.*;
import mcjty.lib.varia.CapabilityTools;
import mcjty.lib.varia.InventoryTools;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import net.minecraft.block.BlockState;
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
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

import static mcjty.lib.api.container.DefaultContainerProvider.container;
import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;
import static mcjty.lib.container.GenericItemHandler.no;
import static mcjty.lib.container.SlotDefinition.ghost;
import static mcjty.rftoolsutility.modules.logic.blocks.InvCheckerDamageMode.DMG_IGNORE;
import static mcjty.rftoolsutility.modules.logic.blocks.InvCheckerDamageMode.DMG_MATCH;

public class InvCheckerTileEntity extends TickingTileEntity {

    private final LogicSupport support = new LogicSupport();

    public static final int SLOT_ITEMMATCH = 0;
    
    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(1)
            .slot(ghost(), SLOT_ITEMMATCH, 154, 24)
            .playerSlots(10, 70));

    @Cap(type = CapType.ITEMS_AUTOMATION)
    private final GenericItemHandler items = GenericItemHandler.create(this, CONTAINER_FACTORY).itemValid(no()).build();

    @Cap(type = CapType.CONTAINER)
    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Inventory Checker")
            .containerSupplier(container(LogicBlockModule.CONTAINER_INVCHECKER, CONTAINER_FACTORY,this))
            .itemHandler(() -> items)
            .setupSync(this));

    @GuiValue
    private int amount = 1;
    @GuiValue
    private int slot = 0;

    @GuiValue(name = "damage")
    private InvCheckerDamageMode useDamage = DMG_IGNORE;

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

    @Override
    public void checkRedstone(World world, BlockPos pos) {
        support.checkRedstone(this, world, pos);
    }

    @Override
    public int getRedstoneOutput(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
        return support.getRedstoneOutput(state, side);
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

    public InvCheckerDamageMode isUseDamage() {
        return useDamage;
    }

    public void setUseDamage(InvCheckerDamageMode useDamage) {
        this.useDamage = useDamage;
        setChanged();
    }

    @Override
    protected void tickServer() {
        checkCounter--;
        if (checkCounter > 0) {
            return;
        }
        checkCounter = 10;

        support.setRedstoneState(this, checkOutput() ? 15 : 0);
    }

    public boolean checkOutput() {
        Direction inputSide = LogicSupport.getFacing(level.getBlockState(getBlockPos())).getInputSide();
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
            if (useDamage == DMG_MATCH) {
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
    public void load(CompoundNBT tagCompound) {
        super.load(tagCompound);
        support.setPowerOutput(tagCompound.getBoolean("rs") ? 15 : 0);
    }

    @Override
    public void loadInfo(CompoundNBT tagCompound) {
        super.loadInfo(tagCompound);
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
            useDamage = info.getBoolean("useDamage") ? DMG_MATCH : DMG_IGNORE;
        }
    }

    @Override
    public void saveAdditional(@Nonnull CompoundNBT tagCompound) {
        super.saveAdditional(tagCompound);
        tagCompound.putBoolean("rs", support.getPowerOutput() > 0);
    }

    @Override
    public void saveInfo(CompoundNBT tagCompound) {
        super.saveInfo(tagCompound);
        CompoundNBT info = getOrCreateInfo(tagCompound);
        info.putInt("amount", amount);
        info.putInt("slot", slot);
        if (tag != null) {
            info.putString("tag", tag.getName().toString());
        }
        info.putBoolean("useDamage", useDamage == DMG_MATCH);
    }

    @Override
    public void saveClientDataToNBT(CompoundNBT tagCompound) {
        CompoundNBT info = getOrCreateInfo(tagCompound);
        if (tag != null) {
            info.putString("tag", tag.getName().toString());
        }
    }

    @Override
    public void loadClientDataFromNBT(CompoundNBT tagCompound) {
        CompoundNBT info = tagCompound.getCompound("Info");
        if (info.contains("tag")) {
            String tagString = info.getString("tag");
            if (!tagString.isEmpty()) {
                tag = getiNamedTag(tagString);
            }
        }
    }

    @ServerCommand
    public static final Command<?> CMD_SETTAG = Command.<InvCheckerTileEntity>create("inv.setTag",
            (te, player, params) -> te.setTagByName(params.get(TagSelector.PARAM_TAG)));

}