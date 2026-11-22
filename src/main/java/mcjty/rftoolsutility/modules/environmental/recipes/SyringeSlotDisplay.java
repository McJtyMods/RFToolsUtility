package mcjty.rftoolsutility.modules.environmental.recipes;

import mcjty.rftoolsutility.modules.spawner.SpawnerConfiguration;
import mcjty.rftoolsutility.modules.spawner.SpawnerModule;
import mcjty.rftoolsutility.modules.spawner.data.SyringeData;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.DisplayContentsFactory;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.stream.Stream;

public record SyringeSlotDisplay(Identifier mob) implements SlotDisplay {

    public static final Type<SyringeSlotDisplay> TYPE = new Type<>(
            Identifier.CODEC.fieldOf("mob").xmap(SyringeSlotDisplay::new, SyringeSlotDisplay::mob),
            Identifier.STREAM_CODEC.map(SyringeSlotDisplay::new, SyringeSlotDisplay::mob).cast());

    @Override
    public Type<SyringeSlotDisplay> type() {
        return TYPE;
    }

    @Override
    public <T> Stream<T> resolve(ContextMap context, DisplayContentsFactory<T> factory) {
        if (factory instanceof DisplayContentsFactory.ForStacks<T> stacks) {
            // Recipe displays are built before a world's spawner configuration is loaded.
            ItemStack syringe = new ItemStack(SpawnerModule.SYRINGE.get());
            syringe.set(SpawnerModule.ITEM_SYRINGE_DATA, new SyringeData(mob, SpawnerConfiguration.maxMobInjections.get()));
            return Stream.of(stacks.forStack(syringe));
        }
        return Stream.empty();
    }
}
