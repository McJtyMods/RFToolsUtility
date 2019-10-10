package mcjty.rftoolsutility.setup;


import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.client.ModSounds;
import mcjty.rftoolsutility.modules.crafter.CrafterSetup;
import mcjty.rftoolsutility.modules.tank.TankSetup;
import mcjty.rftoolsutility.modules.teleporter.TeleporterSetup;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RFToolsUtility.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Registration {

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        CrafterSetup.registerBlocks(event);
        TeleporterSetup.registerBlocks(event);
        TankSetup.registerBlocks(event);
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        CrafterSetup.registerItems(event);
        TeleporterSetup.registerItems(event);
        TankSetup.registerItems(event);
    }

    @SubscribeEvent
    public static void registerTiles(final RegistryEvent.Register<TileEntityType<?>> event) {
        CrafterSetup.registerTiles(event);
        TeleporterSetup.registerTiles(event);
        TankSetup.registerTiles(event);
    }

    @SubscribeEvent
    public static void registerContainers(final RegistryEvent.Register<ContainerType<?>> event) {
        CrafterSetup.registerContainers(event);
        TeleporterSetup.registerContainers(event);
        TankSetup.registerContainers(event);
    }

    @SubscribeEvent
    public static void registerSounds(final RegistryEvent.Register<SoundEvent> event) {
        ModSounds.init(event.getRegistry());
    }
}
