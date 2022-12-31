package mcjty.rftoolsutility.modules.environmental.items;

import mcjty.lib.builder.InfoLine;
import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.tooltips.ITooltipSettings;
import mcjty.lib.varia.Tools;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.environmental.EnvModuleProvider;
import mcjty.rftoolsutility.modules.environmental.EnvironmentalConfiguration;
import mcjty.rftoolsutility.modules.environmental.modules.*;
import mcjty.rftoolsutility.playerprops.PlayerBuff;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.util.Lazy;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Supplier;

import static mcjty.lib.builder.TooltipBuilder.*;

public abstract class EnvironmentalControllerItem extends Item implements EnvModuleProvider, ITooltipSettings {

    private final Lazy<TooltipBuilder> tooltipBuilder;

    private EnvironmentalControllerItem(Lazy<TooltipBuilder> tooltipBuilder) {
        super(RFToolsUtility.setup.defaultProperties().stacksTo(16));
        this.tooltipBuilder = tooltipBuilder;
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack itemStack, Level world, @Nonnull List<Component> list, @Nonnull TooltipFlag flag) {
        super.appendHoverText(itemStack, world, list, flag);
        tooltipBuilder.get().makeTooltip(Tools.getId(this), itemStack, list, flag);
    }

    private static InfoLine[] createInfoLines(InfoLine[] inner, ForgeConfigSpec.DoubleValue rfPerTick) {
        InfoLine[] lines = new InfoLine[1 + inner.length + 1];
        lines[0] = header();
        System.arraycopy(inner, 0, lines, 1, inner.length);
        lines[lines.length-1] = parameter("power", stack -> rfPerTick.get() + " RF/tick (per cubic block)");
        return lines;
    }

    public static EnvironmentalControllerItem create(String name, Supplier<? extends EnvironmentModule> supplier, ForgeConfigSpec.DoubleValue rfPerTick, InfoLine... tooltips) {
        return new EnvironmentalControllerItem(() -> new TooltipBuilder()
                .info(key("message.rftoolsutility.shiftmessage"))
                .infoShift(createInfoLines(tooltips, rfPerTick))) {
            @Override
            public Supplier<? extends EnvironmentModule> getServerEnvironmentModule() {
                return supplier;
            }

            @Override
            public String getName() {
                return name;
            }
        };
    }

    public static EnvironmentalControllerItem createBlindnessModule() {
        return create("Blindness",
                () -> PotionEffectModule.create("blindness", 0, PlayerBuff.BUFF_BLINDNESS, EnvironmentalConfiguration.BLINDNESS_RFPERTICK.get(),
                        () -> EnvironmentalConfiguration.blindnessAvailable.get()),
                EnvironmentalConfiguration.BLINDNESS_RFPERTICK,
                warning(stack -> !EnvironmentalConfiguration.blindnessAvailable.get()));
    }

    public static EnvironmentalControllerItem createFeatherfallingModule() {
        return create("Feather",
                () -> BuffEModule.create(PlayerBuff.BUFF_FEATHERFALLING, EnvironmentalConfiguration.FEATHERFALLING_RFPERTICK.get()),
                EnvironmentalConfiguration.FEATHERFALLING_RFPERTICK,
                gold());
    }

    public static EnvironmentalControllerItem createFeatherfallingPlusModule() {
        return create("Feather+",
                () -> BuffEModule.create(PlayerBuff.BUFF_FEATHERFALLINGPLUS, EnvironmentalConfiguration.FEATHERFALLINGPLUS_RFPERTICK.get()),
                EnvironmentalConfiguration.FEATHERFALLINGPLUS_RFPERTICK,
                gold());
    }

    public static EnvironmentalControllerItem createFlightModule() {
        return create("Flight",
                () -> BuffEModule.create(PlayerBuff.BUFF_FLIGHT, EnvironmentalConfiguration.FLIGHT_RFPERTICK.get()),
                EnvironmentalConfiguration.FLIGHT_RFPERTICK);
    }

    public static EnvironmentalControllerItem createGlowingModule() {
        return create("Glowing",
                () -> PotionEffectModule.create("glowing", 0, PlayerBuff.BUFF_GLOWING, EnvironmentalConfiguration.GLOWING_RFPERTICK.get()),
                EnvironmentalConfiguration.GLOWING_RFPERTICK);
    }

    public static EnvironmentalControllerItem createHasteModule() {
        return create("Haste",
                () -> PotionEffectModule.create("haste", 0, PlayerBuff.BUFF_HASTE, EnvironmentalConfiguration.HASTE_RFPERTICK.get()),
                EnvironmentalConfiguration.HASTE_RFPERTICK);
    }

    public static EnvironmentalControllerItem createHastePlusModule() {
        return create("Haste+",
                () -> PotionEffectModule.create("haste", 2, PlayerBuff.BUFF_HASTEPLUS, EnvironmentalConfiguration.HASTEPLUS_RFPERTICK.get()),
                EnvironmentalConfiguration.HASTEPLUS_RFPERTICK);
    }

    public static EnvironmentalControllerItem createLuckModule() {
        return create("Luck",
                () -> PotionEffectModule.create("luck", 0, PlayerBuff.BUFF_LUCK, EnvironmentalConfiguration.LUCK_RFPERTICK.get()),
                EnvironmentalConfiguration.LUCK_RFPERTICK);
    }

    public static EnvironmentalControllerItem createNightvisionModule() {
        return create("Vision",
                () -> PotionEffectModule.create("night_vision", 0, PlayerBuff.BUFF_NIGHTVISION, EnvironmentalConfiguration.NIGHTVISION_RFPERTICK.get()),
                EnvironmentalConfiguration.NIGHTVISION_RFPERTICK);
    }

    public static EnvironmentalControllerItem createNoteleportModule() {
        return create("NoTP", NoTeleportEModule::new,
                EnvironmentalConfiguration.NOTELEPORT_RFPERTICK);
    }

    public static EnvironmentalControllerItem createPeacefulModule() {
        return create("Peace", PeacefulEModule::new,
                EnvironmentalConfiguration.PEACEFUL_RFPERTICK);
    }

    public static EnvironmentalControllerItem createPoisonModule() {
        return create("Poison",
                () -> PotionEffectModule.create("poison", 1, PlayerBuff.BUFF_POISON, EnvironmentalConfiguration.POISON_RFPERTICK.get(),
                        () -> EnvironmentalConfiguration.poisonAvailable.get()),
                EnvironmentalConfiguration.POISON_RFPERTICK,
                warning(stack -> !EnvironmentalConfiguration.poisonAvailable.get()));
    }

    public static EnvironmentalControllerItem createRegenerationModule() {
        return create("Regen",
                () -> PotionEffectModule.create("regeneration", 0, PlayerBuff.BUFF_REGENERATION, EnvironmentalConfiguration.REGENERATION_RFPERTICK.get()),
                EnvironmentalConfiguration.REGENERATION_RFPERTICK);
    }

    public static EnvironmentalControllerItem createRegenerationPlusModule() {
        return create("Regen+",
                () -> PotionEffectModule.create("regeneration", 2, PlayerBuff.BUFF_REGENERATIONPLUS, EnvironmentalConfiguration.REGENERATIONPLUS_RFPERTICK.get()),
                EnvironmentalConfiguration.REGENERATIONPLUS_RFPERTICK);
    }

    public static EnvironmentalControllerItem createSaturationModule() {
        return create("Saturation",
                () -> PotionEffectModule.create("saturation", 0, PlayerBuff.BUFF_SATURATION, EnvironmentalConfiguration.SATURATION_RFPERTICK.get()),
                EnvironmentalConfiguration.SATURATION_RFPERTICK);
    }

    public static EnvironmentalControllerItem createSaturationPlusModule() {
        return create("Saturation+",
                () -> PotionEffectModule.create("saturation", 2, PlayerBuff.BUFF_SATURATIONPLUS, EnvironmentalConfiguration.SATURATIONPLUS_RFPERTICK.get()),
                EnvironmentalConfiguration.SATURATIONPLUS_RFPERTICK);
    }

    public static EnvironmentalControllerItem createSlownessModule() {
        return create("Slowness",
                () -> PotionEffectModule.create("slowness", 3, PlayerBuff.BUFF_SLOWNESS, EnvironmentalConfiguration.SLOWNESS_RFPERTICK.get(),
                        () -> EnvironmentalConfiguration.slownessAvailable.get()),
                EnvironmentalConfiguration.SLOWNESS_RFPERTICK,
                warning(stack -> !EnvironmentalConfiguration.slownessAvailable.get()));
    }

    public static EnvironmentalControllerItem createSpeedModule() {
        return create("Speed",
                () -> PotionEffectModule.create("speed", 0, PlayerBuff.BUFF_SPEED, EnvironmentalConfiguration.SPEED_RFPERTICK.get()),
                EnvironmentalConfiguration.SPEED_RFPERTICK);
    }

    public static EnvironmentalControllerItem createSpeedPlusModule() {
        return create("Speed+",
                () -> PotionEffectModule.create("speed", 2, PlayerBuff.BUFF_SPEEDPLUS, EnvironmentalConfiguration.SPEEDPLUS_RFPERTICK.get()),
                EnvironmentalConfiguration.SPEEDPLUS_RFPERTICK);
    }

    public static EnvironmentalControllerItem createWaterbreathingModule() {
        return create("Water",
                () -> PotionEffectModule.create("water_breathing", 0, PlayerBuff.BUFF_WATERBREATHING, EnvironmentalConfiguration.WATERBREATHING_RFPERTICK.get()),
                EnvironmentalConfiguration.WATERBREATHING_RFPERTICK);
    }

    public static EnvironmentalControllerItem createWeaknessModule() {
        return create("Weakness",
                () -> PotionEffectModule.create("weakness", 1, PlayerBuff.BUFF_WEAKNESS, EnvironmentalConfiguration.WEAKNESS_RFPERTICK.get(),
                        () -> EnvironmentalConfiguration.weaknessAvailable.get()),
                EnvironmentalConfiguration.WEAKNESS_RFPERTICK,
                warning(stack -> !EnvironmentalConfiguration.weaknessAvailable.get()));
    }

}
