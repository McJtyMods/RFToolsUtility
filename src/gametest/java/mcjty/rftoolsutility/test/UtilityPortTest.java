package mcjty.rftoolsutility.test;

import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.client.AbstractDynamicBakedModel;
import mcjty.lib.container.MenuTools;
import mcjty.lib.varia.CapabilityTools;
import mcjty.lib.varia.ModuleTools;
import mcjty.rftoolsutility.modules.crafter.CrafterModule;
import mcjty.rftoolsutility.modules.logic.LogicBlockModule;
import mcjty.rftoolsutility.modules.screen.ScreenModule;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenContainer;
import mcjty.rftoolsutility.modules.spawner.SpawnerModule;
import mcjty.rftoolsutility.modules.tank.TankModule;
import mcjty.rftoolsutility.modules.tank.client.GuiTank;
import mcjty.rftoolsutility.modules.teleporter.TeleporterModule;
import mcjty.rftoolsutility.modules.teleporter.client.GuiAdvancedPorter;
import mcjty.rftoolsutility.modules.teleporter.data.ChargedPorterData;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinations;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluids;
import team.reborn.energy.api.EnergyStorage;

import java.util.ArrayList;

/** Checks native transfers, module installation, recipes, a synchronized menu and advanced porter rendering. */
public class UtilityPortTest implements FabricClientGameTest {

    private static final BlockPos TANK = new BlockPos(0, -59, 0);
    private static final BlockPos SCREEN = new BlockPos(2, -59, 0);

    @Override
    public void runTest(ClientGameTestContext context) {
        try (var world = context.worldBuilder().create()) {
            context.waitFor(client -> client.player != null && client.level != null);
            world.getServer().runOnServer(server -> {
                var level = server.overworld();
                var player = server.getPlayerList().getPlayers().getFirst();
                level.setBlock(TANK, TankModule.TANK.block().get().defaultBlockState(), Block.UPDATE_ALL);
                level.setBlock(SCREEN, ScreenModule.CREATIVE_SCREEN.block().get().defaultBlockState(), Block.UPDATE_ALL);
                var tank = FluidStorage.SIDED.find(level, TANK, Direction.UP);
                check(tank != null, "Tank exposes native fluid storage");
                var water = FluidVariant.of(Fluids.WATER);
                try (var transaction = Transaction.openOuter()) {
                    check(tank.insert(water, 81_000, transaction) == 81_000, "Tank accepts a bucket in Fabric droplets");
                }
                check(tank.iterator().next().getAmount() == 0, "Aborted tank insertion rolls back");
                try (var transaction = Transaction.openOuter()) {
                    check(tank.insert(water, 81_000, transaction) == 81_000, "Tank commits fluid insertion");
                    transaction.commit();
                }
                check(tank.iterator().next().getAmount() == 81_000, "Tank retains committed fluid");
                var porter = new ItemStack(TeleporterModule.CHARGED_PORTER.get());
                var battery = EnergyStorage.ITEM.find(porter, CapabilityTools.itemContext(porter));
                check(battery != null, "Charged porter exposes native item energy");
                try (var transaction = Transaction.openOuter()) {
                    check(battery.insert(100, transaction) == 100, "Porter accepts energy");
                }
                check(porter.get(TeleporterModule.ITEM_CHARGEDPORTER_DATA.get()) == null, "Aborted porter charge leaves components unchanged");
                try (var transaction = Transaction.openOuter()) {
                    check(battery.insert(100, transaction) == 100, "Porter commits energy insertion");
                    transaction.commit();
                }
                check(porter.get(TeleporterModule.ITEM_CHARGEDPORTER_DATA.get()).energy() == 100, "Porter component stores committed energy");
                var module = new ItemStack(ScreenModule.TEXT_MODULE.get(), 2);
                player.setItemInHand(InteractionHand.MAIN_HAND, module);
                check(ModuleTools.installModule(player, module, InteractionHand.MAIN_HAND, SCREEN, 0, ScreenContainer.SCREEN_MODULES - 1), "Screen accepts held module through native storage");
                check(module.getCount() == 1, "Installing consumes one module");
                var inventory = ItemStorage.SIDED.find(level, SCREEN, null);
                check(inventory != null && inventory.iterator().next().getResource().getItem() == ScreenModule.TEXT_MODULE.get(), "Installed module is retained");
                check(server.getRecipeManager().getRecipes().stream().anyMatch(recipe -> recipe.id().identifier().getNamespace().equals("rftoolsutility")), "Utility recipes load");
            });
            context.waitFor(client -> client.level.getBlockEntity(TANK) != null);
            world.getServer().runOnServer(server -> {
                var level = server.overworld();
                var player = server.getPlayerList().getPlayers().getFirst();
                var provider = CapabilityContainerProvider.CONTAINER_PROVIDER_CAPABILITY.find(level, TANK, null);
                check(provider != null, "Tank exposes its menu");
                player.teleportTo(TANK.getX() + .5, TANK.getY() + 1, TANK.getZ() + 2);
                MenuTools.open(player, provider, data -> {
                    data.writeBlockPos(TANK);
                    if (provider instanceof DefaultContainerProvider<?> defaults) {
                        defaults.writeExtraData(data, level.getBlockEntity(TANK));
                    }
                });
            });
            context.waitForScreen(GuiTank.class);
            context.runOnClient(client -> {
                var state = client.level.getBlockState(TANK);
                var model = client.getModelManager().getBlockStateModelSet().get(state);
                var parts = new ArrayList<BlockStateModelPart>();
                check(model instanceof AbstractDynamicBakedModel, "Tank uses its dynamic Fabric model");
                ((AbstractDynamicBakedModel) model).collectParts(client.level, TANK, state, RandomSource.create(1), parts);
                check(!parts.isEmpty() && parts.getFirst().getQuads(null).size() >= 6, "Tank model has geometry");
                for (var part : parts) {
                    for (var quad : part.getQuads(null)) {
                        check(!quad.materialInfo().sprite().contents().name().getPath().equals("missingno"), "Tank textures are available");
                    }
                }
                check(CrafterModule.CONTAINER_CRAFTER.get() != null && LogicBlockModule.CONTAINER_REDSTONE_INFORMATION.get() != null
                        && SpawnerModule.SPAWNER.block().get() != null, "Machine registrations complete");
                client.player.closeContainer();
            });
            world.getServer().runOnServer(server -> {
                var player = server.getPlayerList().getPlayers().getFirst();
                var destinations = TeleportDestinations.get(player.level());
                var pos = GlobalPos.of(player.level().dimension(), new BlockPos(8, -59, 0));
                destinations.addDestination(pos);
                int id = destinations.getNewId(pos);
                destinations.setDestination(pos, destinations.getDestination(pos).withName("Porter regression destination"));
                var porter = new ItemStack(TeleporterModule.ADVANCED_CHARGED_PORTER.get());
                porter.set(TeleporterModule.ITEM_CHARGEDPORTER_DATA.get(),
                        ChargedPorterData.createDefault().withTarget(0, id).withCurrentTarget(id));
                player.setItemInHand(InteractionHand.MAIN_HAND, porter);
            });
            context.waitFor(client -> client.player.getMainHandItem().is(TeleporterModule.ADVANCED_CHARGED_PORTER.get()));
            context.getInput().lookAt(0, -90);
            context.getInput().holdKey(options -> options.keyShift);
            context.waitTicks(2);
            try {
                context.getInput().pressKey(options -> options.keyUse);
                context.waitForScreen(GuiAdvancedPorter.class);
            } finally {
                context.getInput().releaseKey(options -> options.keyShift);
            }
            // Opening succeeds before the first frame; render several frames to catch duplicate background blur.
            context.waitTicks(5);
            context.setScreen(() -> null);
            context.waitTicks(2);
        }
    }

    private static void check(boolean success, String message) {
        if (!success) {
            throw new AssertionError(message);
        }
    }
}
