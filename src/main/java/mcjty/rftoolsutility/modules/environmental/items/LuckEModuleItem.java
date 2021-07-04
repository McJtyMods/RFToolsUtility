package mcjty.rftoolsutility.modules.environmental.items;

import mcjty.rftoolsutility.modules.environmental.EnvModuleProvider;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class LuckEModuleItem extends Item implements EnvModuleProvider {

    public LuckEModuleItem() {
        super(new Properties().stacksTo(16));
//        super("luck_module");
    }

    @Override
    public void appendHoverText(ItemStack itemStack, World player, List<ITextComponent> list, ITooltipFlag flag) {
        super.appendHoverText(itemStack, player, list, flag);
        // @todo 1.16
        list.add("This module gives luck bonus when");
        list.add("used in the environmental controller.");
        list.add(TextFormatting.GREEN + "Uses " + EnvironmentalConfiguration.LUCK_RFPERTICK.get() + " RF/tick (per cubic block)");
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 1;
    }

    @Override
    public Class<? extends EnvironmentModule> getServerEnvironmentModule() {
        return LuckEModule.class;
    }

    @Override
    public String getName() {
        return "Luck";
    }
}