package mcjty.rftoolsutility.modules.screen.items.modules;

import mcjty.lib.varia.BlockTools;
import mcjty.lib.varia.DimensionId;
import mcjty.lib.varia.Logging;
import mcjty.rftoolsbase.api.machineinfo.CapabilityMachineInformation;
import mcjty.rftoolsbase.api.screens.IModuleGuiBuilder;
import mcjty.rftoolsbase.api.screens.IModuleProvider;
import mcjty.rftoolsbase.tools.GenericModuleItem;
import mcjty.lib.varia.ModuleTools;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import mcjty.rftoolsutility.modules.screen.modules.MachineInformationScreenModule;
import mcjty.rftoolsutility.modules.screen.modulesclient.MachineInformationClientScreenModule;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraft.item.Item.Properties;

public class MachineInformationModuleItem extends GenericModuleItem implements IModuleProvider {

    public MachineInformationModuleItem() {
        super(new Properties().stacksTo(1).defaultDurability(1).tab(RFToolsUtility.setup.getTab()));
    }

    @Override
    protected int getUses(ItemStack stack) {
        return ScreenConfiguration.MACHINEINFO_RFPERTICK.get();
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
    public Class<MachineInformationScreenModule> getServerScreenModule() {
        return MachineInformationScreenModule.class;
    }

    @Override
    public Class<MachineInformationClientScreenModule> getClientScreenModule() {
        return MachineInformationClientScreenModule.class;
    }

    @Override
    public String getModuleName() {
        return "Info";
    }

    private static final IModuleGuiBuilder.Choice[] EMPTY_CHOICES = new IModuleGuiBuilder.Choice[0];

    @Override
    public void createGui(IModuleGuiBuilder guiBuilder) {
        World world = guiBuilder.getWorld();
        CompoundNBT currentData = guiBuilder.getCurrentData();
        IModuleGuiBuilder.Choice[] choices = EMPTY_CHOICES;
        if(currentData.getString("monitordim").equals(world.dimension().location().toString())) {
	        TileEntity tileEntity = world.getBlockEntity(new BlockPos(currentData.getInt("monitorx"), currentData.getInt("monitory"), currentData.getInt("monitorz")));
	        if (tileEntity != null) {
	            choices = tileEntity.getCapability(CapabilityMachineInformation.MACHINE_INFORMATION_CAPABILITY).map(h -> {
                    int count = h.getTagCount();
                    IModuleGuiBuilder.Choice[] cs = new IModuleGuiBuilder.Choice[count];
                    for (int i = 0; i < count; ++i) {
                        cs[i] = new IModuleGuiBuilder.Choice(h.getTagName(i), h.getTagDescription(i));
                    }
                    return cs;
                }).orElse(EMPTY_CHOICES);
	        }
        }

        guiBuilder
                .label("L:").color("color", "Color for the label").label("Txt:").color("txtcolor", "Color for the text").nl()
                .choices("monitorTag", choices).nl()
                .block("monitor").nl();
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        ItemStack stack = context.getItemInHand();
        World world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction facing = context.getClickedFace();
        PlayerEntity player = context.getPlayer();
        TileEntity te = world.getBlockEntity(pos);
        CompoundNBT tagCompound = stack.getOrCreateTag();
        if (te != null && te.getCapability(CapabilityMachineInformation.MACHINE_INFORMATION_CAPABILITY).isPresent()) {
            tagCompound.putString("monitordim", world.dimension().location().toString());
            tagCompound.putInt("monitorx", pos.getX());
            tagCompound.putInt("monitory", pos.getY());
            tagCompound.putInt("monitorz", pos.getZ());
            BlockState state = player.getCommandSenderWorld().getBlockState(pos);
            Block block = state.getBlock();
            String name = "<invalid>";
            if (block != null && !block.isAir(state, world, pos)) {
                name = BlockTools.getReadableName(world, pos);
            }
            tagCompound.putString("monitorname", name);
            if (world.isClientSide) {
                Logging.message(player, "Machine Information module is set to block '" + name + "'");
            }
        } else {
            tagCompound.remove("monitordim");
            tagCompound.remove("monitorx");
            tagCompound.remove("monitory");
            tagCompound.remove("monitorz");
            tagCompound.remove("monitorname");
            if (world.isClientSide) {
                Logging.message(player, "Machine Information module is cleared");
            }
        }
        stack.setTag(tagCompound);
        return ActionResultType.SUCCESS;
    }
}