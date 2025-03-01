package mcjty.rftoolsutility.modules.screen.items.modules;

import com.mojang.serialization.Codec;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.ModuleTools;
import mcjty.lib.varia.Tools;
import mcjty.rftoolsbase.api.machineinfo.CapabilityMachineInformation;
import mcjty.rftoolsbase.api.machineinfo.IMachineInformation;
import mcjty.rftoolsbase.api.screens.IClientScreenModule;
import mcjty.rftoolsbase.api.screens.IModuleGuiBuilder;
import mcjty.rftoolsbase.api.screens.IModuleProvider;
import mcjty.rftoolsbase.api.screens.IScreenModule;
import mcjty.rftoolsbase.tools.GenericModuleItem;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import mcjty.rftoolsutility.modules.screen.ScreenModule;
import mcjty.rftoolsutility.modules.screen.modules.MachineInformationScreenModule;
import mcjty.rftoolsutility.modules.screen.modulesclient.MachineInformationClientScreenModule;
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
import java.util.function.Consumer;

public class MachineInformationModuleItem extends GenericModuleItem implements IModuleProvider {

    public MachineInformationModuleItem() {
        super(RFToolsUtility.setup.defaultProperties().stacksTo(1).durability(1));
    }

    @Override
    public @Nullable Codec<? extends IScreenModule<?>> codec() {
        return MachineInformationScreenModule.CODEC;
    }

    @Override
    public @Nullable StreamCodec<RegistryFriendlyByteBuf, ? extends IScreenModule<?>> streamCodec() {
        return MachineInformationScreenModule.STREAM_CODEC;
    }

    @Override
    public @Nullable DataComponentType<? extends IScreenModule<?>> componentType() {
        return ScreenModule.MODULE_MACHINEINFO_DATA.get();
    }

    @Override
    public IScreenModule<?> createServerScreenModule() {
        return new MachineInformationScreenModule();
    }

    @Override
    public IClientScreenModule<?> createClientScreenModule() {
        return new MachineInformationClientScreenModule();
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
    public String getModuleName() {
        return "Info";
    }

    private static final IModuleGuiBuilder.Choice[] EMPTY_CHOICES = new IModuleGuiBuilder.Choice[0];

    public static MachineInformationScreenModule data(ItemStack stack) {
        MachineInformationScreenModule data = stack.get(ScreenModule.MODULE_MACHINEINFO_DATA);
        if (data == null) {
            data = new MachineInformationScreenModule();
        }
        return data;
    }

    public static void data(ItemStack stack, Consumer<MachineInformationScreenModule> setter) {
        MachineInformationScreenModule data = data(stack);
        setter.accept(data);
        stack.set(ScreenModule.MODULE_MACHINEINFO_DATA, data);
    }

    @Override
    public void createGui(IModuleGuiBuilder guiBuilder) {
        Level world = guiBuilder.getWorld();
        MachineInformationScreenModule currentData = data(guiBuilder.getCurrentModule());
        IModuleGuiBuilder.Choice[] choices = EMPTY_CHOICES;
        if (currentData.getPos().dimension().equals(world.dimension())) {
	        BlockEntity tileEntity = world.getBlockEntity(currentData.getPos().pos());
	        if (tileEntity != null) {
                IMachineInformation capability = world.getCapability(CapabilityMachineInformation.MACHINE_INFORMATION_CAPABILITY, currentData.getPos().pos(), null);
                if (capability != null) {
                    int count = capability.getTagCount();
                    IModuleGuiBuilder.Choice[] cs = new IModuleGuiBuilder.Choice[count];
                    for (int i = 0; i < count; ++i) {
                        cs[i] = new IModuleGuiBuilder.Choice(capability.getTagName(i), capability.getTagDescription(i));
                    }
                    choices = cs;
                }
	        }
        }

        guiBuilder
                .label("L:")
                .color((stack, c) -> data(stack).setLabcolor(c), stack -> data(stack).getLabcolor(), "Color for the label")
                .label("Txt:")
                .color((stack, c) -> data(stack).setTxtcolor(c), stack -> data(stack).getTxtcolor(), "Color for the text")
                .nl()

                .choices((stack, s) -> data(stack, d -> d.setTag(s)), stack -> data(stack).getTag(), choices)
                .nl()

                .block(stack -> data(stack).getPos(), stack -> data(stack).getMonitor())
                .nl();
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
        MachineInformationScreenModule data = data(stack);
        IMachineInformation capability = world.getCapability(CapabilityMachineInformation.MACHINE_INFORMATION_CAPABILITY, pos, null);
        if (te != null && capability != null) {
            data.setPos(GlobalPos.of(world.dimension(), pos));
            String name = "<invalid>";
            if (!world.getBlockState(pos).isAir()) {
                name = Tools.getReadableName(world, pos);
            }
            data.setMonitor(name);
            if (world.isClientSide) {
                Logging.message(player, "Machine Information module is set to block '" + name + "'");
            }
        } else {
            data.setPos(null);
            data.setMonitor("");
            if (world.isClientSide) {
                Logging.message(player, "Machine Information module is cleared");
            }
        }
        stack.set(ScreenModule.MODULE_MACHINEINFO_DATA, data);
        return InteractionResult.SUCCESS;
    }
}