package mcjty.rftoolsutility.modules.logic.blocks;

import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.bindings.GuiValue;
import mcjty.lib.bindings.Value;
import mcjty.lib.blockcommands.Command;
import mcjty.lib.blockcommands.ServerCommand;
import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.GenericItemHandler;
import mcjty.lib.gui.widgets.TagSelector;
import mcjty.lib.setup.Registration;
import mcjty.lib.tileentity.Cap;
import mcjty.lib.tileentity.CapType;
import mcjty.lib.tileentity.LogicSupport;
import mcjty.lib.tileentity.TickingTileEntity;
import mcjty.lib.typed.Type;
import mcjty.lib.varia.CapabilityTools;
import mcjty.lib.varia.InventoryTools;
import mcjty.lib.varia.TagTools;
import mcjty.rftoolsbase.tools.ManualHelper;
import mcjty.rftoolsutility.compat.RFToolsUtilityTOPDriver;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.logic.data.IncCheckerData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.function.Function;

import static mcjty.lib.api.container.DefaultContainerProvider.container;
import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;
import static mcjty.lib.container.GenericItemHandler.no;
import static mcjty.lib.container.SlotDefinition.ghost;
import static mcjty.rftoolsutility.modules.logic.blocks.InvCheckerDamageMode.DMG_MATCH;

public class InvCheckerTileEntity extends TickingTileEntity {

    private final LogicSupport support = new LogicSupport();

    public static final int SLOT_ITEMMATCH = 0;
    
    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(1)
            .slot(ghost(), SLOT_ITEMMATCH, 154, 24)
            .playerSlots(10, 70));

    private final GenericItemHandler items = GenericItemHandler.create(this, CONTAINER_FACTORY).itemValid(no()).build();
    @Cap(type = CapType.ITEMS_AUTOMATION)
    private static final Function<InvCheckerTileEntity, GenericItemHandler> ITEM_CAP = tile -> tile.items;

    @Cap(type = CapType.CONTAINER)
    private static final Function<InvCheckerTileEntity, MenuProvider> SCREEN_CAP = be -> new DefaultContainerProvider<GenericContainer>("Inventory Checker")
            .containerSupplier(container(LogicBlockModule.CONTAINER_INVCHECKER, CONTAINER_FACTORY,be))
            .itemHandler(() -> be.items)
            .setupSync(be);

    @GuiValue
    public static final Value<InvCheckerTileEntity, Integer> VALUE_AMOUNT = Value.create("amount", Type.INTEGER, InvCheckerTileEntity::getAmount, InvCheckerTileEntity::setAmount);
    @GuiValue
    public static final Value<InvCheckerTileEntity, Integer> VALUE_SLOT = Value.create("slot", Type.INTEGER, InvCheckerTileEntity::getSlot, InvCheckerTileEntity::setSlot);

    @GuiValue(name = "damage")
    public static final Value<InvCheckerTileEntity, String> VALUE_DAMAGE_MODE = Value.createEnum("damage", InvCheckerDamageMode.values(), InvCheckerTileEntity::getDamageMode, InvCheckerTileEntity::setDamageMode);

    private int checkCounter = 0;

    public InvCheckerTileEntity(BlockPos pos, BlockState state) {
        super(LogicBlockModule.INVCHECKER.be().get(), pos, state);
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
    public void checkRedstone(Level world, BlockPos pos) {
        support.checkRedstone(this, world, pos);
    }

    @Override
    public int getRedstoneOutput(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        return support.getRedstoneOutput(state, side);
    }

    public int getAmount() {
        return getData(LogicBlockModule.INVCHECKER_DATA).amount();
    }

    public void setAmount(int amount) {
        IncCheckerData data = getData(LogicBlockModule.INVCHECKER_DATA);
        setData(LogicBlockModule.INVCHECKER_DATA, data.withAmount(amount));
    }

    public int getSlot() {
        return getData(LogicBlockModule.INVCHECKER_DATA).slot();
    }

    public void setSlot(int slot) {
        IncCheckerData data = getData(LogicBlockModule.INVCHECKER_DATA);
        setData(LogicBlockModule.INVCHECKER_DATA, data.withSlot(slot));
    }

    public InvCheckerDamageMode getDamageMode() {
        return getData(LogicBlockModule.INVCHECKER_DATA).useDamage();
    }

    public void setDamageMode(InvCheckerDamageMode mode) {
        IncCheckerData data = getData(LogicBlockModule.INVCHECKER_DATA);
        setData(LogicBlockModule.INVCHECKER_DATA, data.withUseDamage(mode));
    }

    public TagKey<Item> getTag() {
        IncCheckerData data = getData(LogicBlockModule.INVCHECKER_DATA);
        return data.tag();
    }

    public void setTag(TagKey<Item> tag) {
        IncCheckerData data = getData(LogicBlockModule.INVCHECKER_DATA);
        setData(LogicBlockModule.INVCHECKER_DATA, data.withTag(tag));
    }

    public void setTagByName(String tagName) {
        IncCheckerData data = getData(LogicBlockModule.INVCHECKER_DATA);
        if (tagName == null) {
            setData(LogicBlockModule.INVCHECKER_DATA, data.withTag(null));
        } else {
            setData(LogicBlockModule.INVCHECKER_DATA, data.withTag(getiNamedTag(tagName)));
        }
        markDirtyClient();
    }

    private TagKey<Item> getiNamedTag(String tagName) {
        return TagTools.createItemTagKey(ResourceLocation.parse(tagName));
    }

    public String getTagName() {
        IncCheckerData data = getData(LogicBlockModule.INVCHECKER_DATA);
        return data.tag() == null ? null : data.tag().location().toString();
    }

    public InvCheckerDamageMode isUseDamage() {
        IncCheckerData data = getData(LogicBlockModule.INVCHECKER_DATA);
        return data.useDamage();
    }

    public void setUseDamage(InvCheckerDamageMode useDamage) {
        IncCheckerData data = getData(LogicBlockModule.INVCHECKER_DATA);
        setData(LogicBlockModule.INVCHECKER_DATA, data.withUseDamage(useDamage));
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
        BlockEntity te = level.getBlockEntity(inputPos);
        if (InventoryTools.isInventory(te)) {
            IItemHandler capability = CapabilityTools.getItemCapabilitySafe(te);
            if (capability != null) {
                IncCheckerData data = getData(LogicBlockModule.INVCHECKER_DATA);
                if (data.slot() >= 0 && data.slot() < capability.getSlots()) {
                    ItemStack stack = capability.getStackInSlot(data.slot());
                    if (!stack.isEmpty()) {
                        int nr = isItemMatching(stack);
                        if (nr >= data.amount()) {
                            if (data.tag() != null) {
                                return stack.getItem().builtInRegistryHolder().is(data.tag());
                            } else {
                                return true;
                            }
                        }
                    }
                }
                return false;
            }
        }
        return false;
    }

    private int isItemMatching(ItemStack stack) {
        int nr = 0;
        ItemStack matcher = items.getStackInSlot(0);
        if (!matcher.isEmpty()) {
            IncCheckerData data = getData(LogicBlockModule.INVCHECKER_DATA);
            if (data.useDamage() == DMG_MATCH) {
                if (ItemStack.isSameItem(matcher, stack)) {
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
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        support.setPowerOutput(tag.getBoolean("rs") ? 15 : 0);
        items.load(tag, "items", provider);
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putBoolean("rs", support.getPowerOutput() > 0);
        items.save(tag, "items", provider);
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput input) {
        super.applyImplicitComponents(input);
        var data = input.get(LogicBlockModule.ITEM_INVCHECKER_DATA);
        if (data != null) {
            setData(LogicBlockModule.INVCHECKER_DATA, data);
        }
        items.applyImplicitComponents(input.get(Registration.ITEM_INVENTORY));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(LogicBlockModule.ITEM_INVCHECKER_DATA, getData(LogicBlockModule.INVCHECKER_DATA));
        items.collectImplicitComponents(builder);
    }

    @Override
    public void saveClientDataToNBT(CompoundTag tag, HolderLookup.Provider provider) {
        if (getTag() != null) {
            tag.putString("tag", this.getTag().location().toString());
        }
    }

    @Override
    public void loadClientDataFromNBT(CompoundTag tag, HolderLookup.Provider provider) {
        String tagString = tag.getString("tag");
        if (!tagString.isEmpty()) {
            setTag(getiNamedTag(tagString));
        } else {
            setTag(null);
        }
    }

    @ServerCommand
    public static final Command<?> CMD_SETTAG = Command.<InvCheckerTileEntity>create("inv.setTag",
            (te, player, params) -> te.setTagByName(params.get(TagSelector.PARAM_TAG)));

}