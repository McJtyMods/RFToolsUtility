package mcjty.rftoolsutility.setup;

import mcjty.lib.api.smartwrench.SmartWrench;
import mcjty.rftoolsutility.RFToolsUtility;
import mcjty.rftoolsutility.commands.ModCommands;
import mcjty.rftoolsutility.modules.environmental.NoTeleportAreaManager;
import mcjty.rftoolsutility.modules.environmental.PeacefulAreaManager;
import mcjty.rftoolsutility.modules.screen.ScreenModule;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenBlock;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenHitBlock;
import mcjty.rftoolsutility.modules.teleporter.TeleportationTools;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestination;
import mcjty.rftoolsutility.playerprops.PlayerBuff;
import mcjty.rftoolsutility.playerprops.PlayerExtendedProperties;
import mcjty.rftoolsutility.playerprops.PropertiesDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ForgeMod;
import net.neoforged.neoforge.event.AttachCapabilitiesEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.eventbus.api.SubscribeEvent;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ForgeEventHandlers {

    // Workaround for the charged porter so that the teleport can be done outside
    // of the entity tick loop
    private static final List<Pair<TeleportDestination,Player>> playersToTeleportHere = new ArrayList<>();

    public static void addPlayerToTeleportHere(TeleportDestination destination, Player player) {
        playersToTeleportHere.add(Pair.of(destination, player));
    }

    private static void performDelayedTeleports() {
        if (!playersToTeleportHere.isEmpty()) {
            var copy = new ArrayList<>(playersToTeleportHere);
            playersToTeleportHere.clear();
            // Teleport players here
            for (Pair<TeleportDestination, Player> pair : copy) {
                TeleportationTools.performTeleport(pair.getRight(), pair.getLeft(), 0, 10, false);
            }
        }
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.START && (!event.level.isClientSide) && event.level.dimension().equals(Level.OVERWORLD)) {
            performDelayedTeleports();
        }
    }


    @SubscribeEvent
    public void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START && !event.player.getCommandSenderWorld().isClientSide) {
            PlayerExtendedProperties.getBuffProperties(event.player).ifPresent(h -> {
                h.tickBuffs((ServerPlayer) event.player);
            });
        }
    }

    @SubscribeEvent
    public void onEntityConstructing(AttachCapabilitiesEvent<Entity> event){
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(PlayerExtendedProperties.BUFF_CAPABILITY).isPresent()) {
                event.addCapability(new ResourceLocation(RFToolsUtility.MODID, "properties"), new PropertiesDispatcher());
            }
        }
    }


    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Level world = event.getLevel();
        if (world.isClientSide) {
            return;
        }
        Player player = event.getEntity();
        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.isEmpty() || !(heldItem.getItem() instanceof SmartWrench)) {
            double blockReachDistance = player.getAttribute(ForgeMod.BLOCK_REACH.get()).getValue();
            BlockHitResult rayTrace = rayTraceEyes(player, blockReachDistance + 1);
            if (rayTrace.getType() == HitResult.Type.BLOCK) {
                Block block = world.getBlockState(rayTrace.getBlockPos()).getBlock();
                if (block instanceof ScreenBlock) {
                    event.setCanceled(true);
                } else if (block instanceof ScreenHitBlock) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @Nonnull
    public static BlockHitResult rayTraceEyes(LivingEntity entity, double length) {
        Vec3 startPos = new Vec3(entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ());
        Vec3 endPos = startPos.add(new Vec3(entity.getLookAngle().x * length, entity.getLookAngle().y * length, entity.getLookAngle().z * length));
        ClipContext context = new ClipContext(startPos, endPos, ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE, entity);
        return entity.level().clip(context);
    }


    @SubscribeEvent
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getEntity();

        if (event instanceof PlayerInteractEvent.LeftClickBlock) {
            checkCreativeClick(event);
        } else if (event instanceof PlayerInteractEvent.RightClickBlock) {
            if (player.isShiftKeyDown()) {
                ItemStack heldItem = player.getMainHandItem();
                if (heldItem.isEmpty() || !(heldItem.getItem() instanceof SmartWrench)) {
                    Level world = event.getLevel();
                    BlockState state = world.getBlockState(event.getPos());
                    Block block = state.getBlock();
                    // @todo 1.14
//                    if (block instanceof ScreenBlock) {
//                        Vector3d vec = ((PlayerInteractEvent.RightClickBlock) event).getHitVec();
//                        ((ScreenBlock) block).activate(world, event.getPos(), state, player, event.getHand(), event.getFace(), (float) vec.x, (float) vec.y, (float) vec.z);
//                        ((PlayerInteractEvent.RightClickBlock) event).setUseItem(Event.Result.DENY);
//                        return;
//                    } else if (block instanceof ScreenHitBlock) {
//                        Vector3d vec = ((PlayerInteractEvent.RightClickBlock) event).getHitVec();
//                        ((ScreenHitBlock) block).activate(world, event.getPos(), state, player, event.getHand(), event.getFace(), (float) vec.x, (float) vec.y, (float) vec.z);
//                        ((PlayerInteractEvent.RightClickBlock) event).setUseItem(Event.Result.DENY);
//                        return;
//                    }
                }
            }
        }

        ItemStack heldItem = player.getItemInHand(event.getHand());
        if (heldItem.isEmpty()) {
            return;
        } else {
            heldItem.getItem();
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
//            World world = event.getLevel();
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
        if (event.getEntity().isCreative()) {
            // In creative we don't want our screens to be destroyed by left click unless he/she is sneaking
            BlockState state = event.getLevel().getBlockState(event.getPos());
            Block block = state.getBlock();
            if (block == ScreenModule.SCREEN.get() || block == ScreenModule.CREATIVE_SCREEN.get() || block == ScreenModule.SCREEN_HIT.get()) {
                if (!event.getEntity().isShiftKeyDown()) {
                    // If not sneaking while we hit a screen we cancel the destroy. Otherwise we go through.

                    if (event.getLevel().isClientSide) {
                        // simulate click because it isn't called in creativemode or when we cancel the event
                        block.attack(state, event.getLevel(), event.getPos(), event.getEntity());
                    }

                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingFall(LivingFallEvent event) {
        if (event.getEntity() instanceof Player player) {
            PlayerExtendedProperties.getBuffProperties(player).ifPresent(h -> {
                if (h.hasBuff(PlayerBuff.BUFF_FEATHERFALLING)) {
                    event.setDamageMultiplier(event.getDamageMultiplier() / 2);
                }
                if (h.hasBuff(PlayerBuff.BUFF_FEATHERFALLINGPLUS)) {
                    event.setCanceled(true);
                }
            });
        }
    }

    @SubscribeEvent
    public void onPearlTeleport(EntityTeleportEvent.EnderPearl event) {
        checkTeleport(event);
    }

    @SubscribeEvent
    public void onEntityTeleport(EntityTeleportEvent.EnderEntity event) {
        checkTeleport(event);
    }

    private void checkTeleport(EntityTeleportEvent event) {
        Level world = event.getEntity().getCommandSenderWorld();
        ResourceKey<Level> id = world.dimension();

        Entity entity = event.getEntity();
        BlockPos coordinate = new BlockPos((int) entity.getX(), (int) entity.getY(), (int) entity.getZ());
        if (NoTeleportAreaManager.isTeleportPrevented(entity, GlobalPos.of(id, coordinate))) {
            event.setCanceled(true);
        } else {
            coordinate = new BlockPos((int) event.getTargetX(), (int) event.getTargetY(), (int) event.getTargetZ());
            if (NoTeleportAreaManager.isTeleportPrevented(entity, GlobalPos.of(id, coordinate))) {
                event.setCanceled(true);
            }
        }
    }


    public static boolean onEntitySpawnEvent(Entity entity) {
        Level world = entity.level();
        ResourceKey<Level> id = world.dimension();

        if (entity instanceof Enemy) {
            BlockPos coordinate = new BlockPos((int) entity.getX(), (int) entity.getY(), (int) entity.getZ());
            if (PeacefulAreaManager.isPeaceful(GlobalPos.of(id, coordinate))) {
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public void onPlayerCloned(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            // We need to copyFrom the capabilities
            event.getOriginal().getCapability(PlayerExtendedProperties.FAVORITE_DESTINATIONS_CAPABILITY).ifPresent(oldFavorites -> {
                PlayerExtendedProperties.getFavoriteDestinations(event.getEntity()).ifPresent(h -> {
                    h.copyFrom(oldFavorites);
                });
            });
        }
    }

    @SubscribeEvent
    public void serverLoad(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }
}
