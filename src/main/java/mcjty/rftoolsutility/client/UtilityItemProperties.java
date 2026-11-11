package mcjty.rftoolsutility.client;

import com.mojang.serialization.MapCodec;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.spawner.SpawnerConfiguration;
import mcjty.rftoolsutility.modules.spawner.SpawnerModule;
import mcjty.rftoolsutility.modules.teleporter.TeleporterModule;
import mcjty.rftoolsutility.modules.teleporter.items.porter.ChargedPorterItem;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent;

public record UtilityItemProperties(boolean syringe) implements RangeSelectItemModelProperty {

    private static final MapCodec<UtilityItemProperties> SYRINGE = MapCodec.unit(new UtilityItemProperties(true));
    private static final MapCodec<UtilityItemProperties> PORTER = MapCodec.unit(new UtilityItemProperties(false));

    public static void register(RegisterRangeSelectItemModelPropertyEvent event) {
        event.register(Identifier.fromNamespaceAndPath(RFToolsUtility.MODID, "level"), SYRINGE);
        event.register(Identifier.fromNamespaceAndPath(RFToolsUtility.MODID, "charge"), PORTER);
    }

    @Override
    public float get(ItemStack stack, ClientLevel level, ItemOwner owner, int seed) {
        if (syringe) {
            var data = stack.get(SpawnerModule.ITEM_SYRINGE_DATA);
            return data == null ? 0 : data.level() * 5 / SpawnerConfiguration.maxMobInjections.get();
        }
        if (stack.getItem() instanceof ChargedPorterItem porter) {
            var data = stack.get(TeleporterModule.ITEM_CHARGEDPORTER_DATA);
            long energy = data == null ? 0 : data.energy();
            return 9 - Math.clamp(9 * energy / porter.getMaxEnergyStoredL(stack), 0, 8);
        }
        return 9;
    }

    @Override
    public MapCodec<UtilityItemProperties> type() {
        return syringe ? SYRINGE : PORTER;
    }
}
