package mcjty.rftoolsutility.modules.environmental.items;

import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.environmental.EnvModuleProvider;
import mcjty.rftoolsutility.modules.environmental.EnvironmentalConfiguration;
import mcjty.rftoolsutility.modules.environmental.modules.EnvironmentModule;
import mcjty.rftoolsutility.modules.environmental.modules.PeacefulEModule;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;

public class PeacefulEModuleItem extends Item implements EnvModuleProvider {

    public PeacefulEModuleItem() {
        super(new Item.Properties().tab(RFToolsUtility.setup.getTab()).stacksTo(16));
//        super("peaceful_module");
    }

    @Override
    public void appendHoverText(ItemStack itemStack, World player, List<ITextComponent> list, ITooltipFlag flag) {
        super.appendHoverText(itemStack, player, list, flag);
        list.add(new StringTextComponent("This module prevents hostile mob spawning"));
        list.add(new StringTextComponent("when used in the environmental controller."));
        list.add(new StringTextComponent(TextFormatting.GREEN + "Uses " + EnvironmentalConfiguration.REGENERATION_RFPERTICK.get() + " RF/tick (per cubic block)"));
    }

    @Override
    public Class<? extends EnvironmentModule> getServerEnvironmentModule() {
        return PeacefulEModule.class;
    }

    @Override
    public String getName() {
        return "Peace";
    }
}