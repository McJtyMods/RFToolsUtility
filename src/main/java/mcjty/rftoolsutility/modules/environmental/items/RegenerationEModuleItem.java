package mcjty.rftoolsutility.modules.environmental.items;

import mcjty.rftools.blocks.environmental.EnvModuleProvider;
import mcjty.rftools.blocks.environmental.EnvironmentalConfiguration;
import mcjty.rftools.blocks.environmental.modules.EnvironmentModule;
import mcjty.rftools.blocks.environmental.modules.RegenerationEModule;
import mcjty.rftools.items.GenericRFToolsItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class RegenerationEModuleItem extends GenericRFToolsItem implements EnvModuleProvider {

    public RegenerationEModuleItem() {
        super("regeneration_module");
        setMaxStackSize(16);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void appendHoverText(ItemStack itemStack, World player, List<String> list, ITooltipFlag whatIsThis) {
        super.appendHoverText(itemStack, player, list, whatIsThis);
        list.add("This module gives regeneration bonus when");
        list.add("used in the environmental controller.");
        list.add(TextFormatting.GREEN + "Uses " + EnvironmentalConfiguration.REGENERATION_RFPERTICK.get() + " RF/tick (per cubic block)");
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 1;
    }

    @Override
    public Class<? extends EnvironmentModule> getServerEnvironmentModule() {
        return RegenerationEModule.class;
    }

    @Override
    public String getName() {
        return "Regen";
    }
}