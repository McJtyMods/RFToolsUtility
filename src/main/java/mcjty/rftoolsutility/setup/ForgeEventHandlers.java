package mcjty.rftoolsutility.setup;

import mcjty.lib.api.smartwrench.SmartWrench;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.modules.screen.ScreenModule;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenBlock;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenHitBlock;
import mcjty.rftoolsutility.modules.teleporter.TeleportationTools;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestination;
import mcjty.rftoolsutility.playerprops.PlayerExtendedProperties;
import mcjty.rftoolsutility.playerprops.PropertiesDispatcher;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ForgeEventHandlers {

    // Workaround for the charged porter so that the teleport can be done outside
    // of the entity tick loop
    private static List<Pair<TeleportDestination,PlayerEntity>> playersToTeleportHere = new ArrayList<>();

    public static void addPlayerToTeleportHere(TeleportDestination destination, PlayerEntity player) {
        playersToTeleportHere.add(Pair.of(destination, player));
    }

    private static void performDelayedTeleports() {
        if (!playersToTeleportHere.isEmpty()) {
            // Teleport players here
            for (Pair<TeleportDestination, PlayerEntity> pair : playersToTeleportHere) {
                TeleportationTools.performTeleport(pair.getRight(), pair.getLeft(), 0, 10, false);
            }
            playersToTeleportHere.clear();
        }
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.START && event.world.getDimension().getType().getId() == 0) {
            performDelayedTeleports();
        }
    }


    @SubscribeEvent
    public void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START && !event.player.getEntityWorld().isRemote) {
            PlayerExtendedProperties.getBuffProperties(event.player).ifPresent(h -> {
                h.tickBuffs((ServerPlayerEntity) event.player);
            });
        }
    }

    @SubscribeEvent
    public void onEntityConstructing(AttachCapabilitiesEvent<Entity> event){
        if (event.getObject() instanceof PlayerEntity) {
            if (!event.getObject().getCapability(PlayerExtendedProperties.BUFF_CAPABILITY).isPresent()) {
                event.addCapability(new ResourceLocation(RFToolsUtility.MODID, "properties"), new PropertiesDispatcher());
            }
        }
    }


    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        World world = event.getWorld();
        if (world.isRemote) {
            return;
        }
        PlayerEntity player = event.getPlayer();
        ItemStack heldItem = player.getHeldItemMainhand();
        if (heldItem.isEmpty() || !(heldItem.getItem() instanceof SmartWrench)) {
            double blockReachDistance = player.getAttribute(PlayerEntity.REACH_DISTANCE).getValue();
            BlockRayTraceResult rayTrace = rayTraceEyes(player, blockReachDistance + 1);
            if (rayTrace.getType() == RayTraceResult.Type.BLOCK) {
                Block block = world.getBlockState(rayTrace.getPos()).getBlock();
                if (block instanceof ScreenBlock) {
                    event.setCanceled(true);
                    return;
                } else if (block instanceof ScreenHitBlock) {
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }

    @Nonnull
    public static BlockRayTraceResult rayTraceEyes(LivingEntity entity, double length) {
        Vec3d startPos = new Vec3d(entity.getPosX(), entity.getPosY() + entity.getEyeHeight(), entity.getPosZ());
        Vec3d endPos = startPos.add(new Vec3d(entity.getLookVec().x * length, entity.getLookVec().y * length, entity.getLookVec().z * length));
        RayTraceContext context = new RayTraceContext(startPos, endPos, RayTraceContext.BlockMode.COLLIDER,
                RayTraceContext.FluidMode.NONE, entity);
        return entity.world.rayTraceBlocks(context);
    }


    @SubscribeEvent
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        PlayerEntity player = event.getPlayer();

        if (event instanceof PlayerInteractEvent.LeftClickBlock) {
            checkCreativeClick(event);
        } else if (event instanceof PlayerInteractEvent.RightClickBlock) {
            if (player.isSneaking()) {
                ItemStack heldItem = player.getHeldItemMainhand();
                if (heldItem.isEmpty() || !(heldItem.getItem() instanceof SmartWrench)) {
                    World world = event.getWorld();
                    BlockState state = world.getBlockState(event.getPos());
                    Block block = state.getBlock();
                    // @todo 1.14
//                    if (block instanceof ScreenBlock) {
//                        Vec3d vec = ((PlayerInteractEvent.RightClickBlock) event).getHitVec();
//                        ((ScreenBlock) block).activate(world, event.getPos(), state, player, event.getHand(), event.getFace(), (float) vec.x, (float) vec.y, (float) vec.z);
//                        ((PlayerInteractEvent.RightClickBlock) event).setUseItem(Event.Result.DENY);
//                        return;
//                    } else if (block instanceof ScreenHitBlock) {
//                        Vec3d vec = ((PlayerInteractEvent.RightClickBlock) event).getHitVec();
//                        ((ScreenHitBlock) block).activate(world, event.getPos(), state, player, event.getHand(), event.getFace(), (float) vec.x, (float) vec.y, (float) vec.z);
//                        ((PlayerInteractEvent.RightClickBlock) event).setUseItem(Event.Result.DENY);
//                        return;
//                    }
                }
            }
        }

        ItemStack heldItem = player.getHeldItem(event.getHand());
        if (heldItem.isEmpty() || heldItem.getItem() == null) {
            return;
        }
        // @todo 1.14
//        if (BlockProtectorConfiguration.enabled.get() && player.isShiftKeyDown /*isSneaking*/() && WrenchChecker.isAWrench(heldItem.getItem())) {
//            // If the block is protected we prevent sneak-wrenching it.
//            if (heldItem.getItem() instanceof SmartWrenchItem) {
//                // But if it is a smart wrench in select mode we allow it
//                if (SmartWrenchItem.getCurrentMode(heldItem) == SmartWrenchMode.MODE_SELECT) {
//                    return;
//                }
//            }
//            World world = event.getWorld();
//            int x = event.getPos().getX();
//            int y = event.getPos().getY();
//            int z = event.getPos().getZ();
//            Collection<GlobalCoordinate> protectors = BlockProtectors.getProtectors(world, x, y, z);
//            if (BlockProtectors.checkHarvestProtection(x, y, z, world, protectors)) {
//                event.setCanceled(true);
//            }
//        }

    }

    private void checkCreativeClick(PlayerInteractEvent event) {
        if (event.getPlayer().isCreative()) {
            // In creative we don't want our screens to be destroyed by left click unless he/she is sneaking
            BlockState state = event.getWorld().getBlockState(event.getPos());
            Block block = state.getBlock();
            if (block == ScreenModule.SCREEN.get() || block == ScreenModule.CREATIVE_SCREEN.get() || block == ScreenModule.SCREEN_HIT.get()) {
                if (!event.getPlayer().isSneaking()) {
                    // If not sneaking while we hit a screen we cancel the destroy. Otherwise we go through.

                    if (event.getWorld().isRemote) {
                        // simulate click because it isn't called in creativemode or when we cancel the event
                        block.onBlockClicked(state, event.getWorld(), event.getPos(), event.getPlayer());
                    }

                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingFall(LivingFallEvent event) {
        if (event.getEntityLiving() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            PlayerExtendedProperties.getBuffProperties(player).ifPresent(h -> {
                // @todo 1.14
//                if (h.hasBuff(PlayerBuff.BUFF_FEATHERFALLING)) {
//                    event.setDamageMultiplier(event.getDamageMultiplier() / 2);
//                }
//                if (h.hasBuff(PlayerBuff.BUFF_FEATHERFALLINGPLUS)) {
//                    event.setCanceled(true);
//                }
            });
        }
    }

    @SubscribeEvent
    public void onEntityTeleport(EnderTeleportEvent event) {
        World world = event.getEntity().getEntityWorld();
        int id = world.getDimension().getType().getId();

        Entity entity = event.getEntity();
        BlockPos coordinate = new BlockPos((int) entity.getPosX(), (int) entity.getPosY(), (int) entity.getPosZ());
        // @todo 1.14
//        if (NoTeleportAreaManager.isTeleportPrevented(entity, new GlobalCoordinate(coordinate, id))) {
//            event.setCanceled(true);
//        } else {
//            coordinate = new BlockPos((int) event.getTargetX(), (int) event.getTargetY(), (int) event.getTargetZ());
//            if (NoTeleportAreaManager.isTeleportPrevented(entity, new GlobalCoordinate(coordinate, id))) {
//                event.setCanceled(true);
//            }
//        }
    }


    @SubscribeEvent
    public void onEntitySpawnEvent(LivingSpawnEvent.CheckSpawn event) {
        World world = event.getWorld().getWorld();
        int id = world.getDimension().getType().getId();

        Entity entity = event.getEntity();
        if (entity instanceof IMob) {
            BlockPos coordinate = new BlockPos((int) entity.getPosX(), (int) entity.getPosY(), (int) entity.getPosZ());
            // @todo 1.14
//            if (PeacefulAreaManager.isPeaceful(new GlobalCoordinate(coordinate, id))) {
//                event.setResult(Event.Result.DENY);
//            }
        }
    }

    @SubscribeEvent
    public void onPlayerCloned(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            // We need to copyFrom the capabilities
            event.getOriginal().getCapability(PlayerExtendedProperties.FAVORITE_DESTINATIONS_CAPABILITY).ifPresent(oldFavorites -> {
                PlayerExtendedProperties.getFavoriteDestinations(event.getPlayer()).ifPresent(h -> {
                    h.copyFrom(oldFavorites);
                });
            });
        }
    }
}
