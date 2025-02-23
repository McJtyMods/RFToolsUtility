package mcjty.rftoolsutility.modules.screen.items.modules;

import com.mojang.serialization.Codec;
import mcjty.rftoolsbase.api.screens.IClientScreenModule;
import mcjty.rftoolsbase.api.screens.IModuleGuiBuilder;
import mcjty.rftoolsbase.api.screens.IScreenModule;
import mcjty.rftoolsbase.tools.GenericModuleItem;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.ScreenConfiguration;
import mcjty.rftoolsutility.modules.screen.ScreenModule;
import mcjty.rftoolsutility.modules.screen.modules.ClockScreenModule;
import mcjty.rftoolsutility.modules.screen.modulesclient.ClockClientScreenModule;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public class ClockModuleItem extends GenericModuleItem {

    public ClockModuleItem() {
        super(RFToolsUtility.setup.defaultProperties()
                .stacksTo(16)
                .durability(1));
    }

    @Override
    public Codec<? extends IScreenModule<?>> codec() {
        return null;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ? extends IScreenModule<?>> streamCodec() {
        return null;
    }

    @Override
    public DataComponentType<? extends IScreenModule<?>> componentType() {
        return null;
    }

    @Override
    public IScreenModule<?> createServerScreenModule() {
        return new ClockScreenModule();
    }

    @Override
    public Codec<? extends IClientScreenModule<?>> clientCodec() {
        return ClockClientScreenModule.CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ? extends IClientScreenModule<?>> clientStreamCodec() {
        return ClockClientScreenModule.STREAM_CODEC;
    }

    @Override
    public IClientScreenModule<?> createClientScreenModule() {
        return new ClockClientScreenModule();
    }

    @Override
    public DataComponentType<? extends IClientScreenModule<?>> clientComponentType() {
        return ScreenModule.CLIENTMODULE_CLOCK_DATA.get();
    }

    @Override
    protected int getUses(ItemStack stack) {
        return ScreenConfiguration.CLOCK_RFPERTICK.get();
    }

//    @Override
//    public int getMaxItemUseDuration(ItemStack stack) {
//        return 1;
//    }

    @Override
    public String getModuleName() {
        return "Clock";
    }

    @Override
    public void createGui(IModuleGuiBuilder guiBuilder) {
        guiBuilder.
                label("Label:").text("text", "Label text").color("color", "Label color").nl().
                toggle("large", "Large", "Large or small font").nl();
    }
}