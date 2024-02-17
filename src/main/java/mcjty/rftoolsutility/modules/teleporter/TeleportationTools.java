package mcjty.rftoolsutility.modules.teleporter;

import mcjty.lib.api.infusable.CapabilityInfusable;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.varia.*;
import mcjty.rftoolsutility.modules.teleporter.blocks.DialingDeviceTileEntity;
import mcjty.rftoolsutility.modules.teleporter.blocks.MatterReceiverTileEntity;
import mcjty.rftoolsutility.modules.teleporter.blocks.MatterTransmitterTileEntity;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestination;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestinations;
import mcjty.rftoolsutility.setup.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import java.util.UUID;

public class TeleportationTools {
    public static final int STATUS_OK = 0;
    public static final int STATUS_WARN = 1;
    public static final int STATUS_UNKNOWN = 2;

    public static MobEffect confusion;
    public static MobEffect harm;
    public static MobEffect wither;

    public static void getPotions() {
        if (confusion == null) {
            confusion = Tools.getEffect(new ResourceLocation("nausea"));
            harm = Tools.getEffect(new ResourceLocation("instant_damage"));
            wither = Tools.getEffect(new ResourceLocation("wither"));
        }
    }

    public static void applyEffectForSeverity(Player player, int severity, boolean boostNeeded) {
        getPotions();

        switch (severity) {
            case 1:
                if (boostNeeded) {
                    // @todo 1.14
//                    player.addPotionEffect(new Effect(confusion, 100));
//                    player.addPotionEffect(new Effect(harm, 5));
                }
                break;
            case 2:
                // @todo 1.14
//                player.addPotionEffect(new Effect(harm, 100));
                break;
            case 3:
                // @todo 1.14
//                player.addPotionEffect(new Effect(harm, 100));
                player.hurt(DamageTools.getGenericDamageSource(player), 0.5f);
                break;
            case 4:
                // @todo 1.14
//                player.addPotionEffect(new Effect(harm, 200));
                player.hurt(DamageTools.getGenericDamageSource(player), 0.5f);
                break;
            case 5:
                // @todo 1.14
//                player.addPotionEffect(new Effect(harm, 200));
                player.hurt(DamageTools.getGenericDamageSource(player), 1.0f);
                break;
            case 6:
                // @todo 1.14
//                player.addPotionEffect(new Effect(harm, 300));
                player.hurt(DamageTools.getGenericDamageSource(player), 1.0f);
                break;
            case 7:
                // @todo 1.14
//                player.addPotionEffect(new Effect(harm, 300));
//                player.addPotionEffect(new Effect(wither, 200));
                player.hurt(DamageTools.getGenericDamageSource(player), 2.0f);
                break;
            case 8:
                // @todo 1.14
//                player.addPotionEffect(new Effect(harm, 400));
//                player.addPotionEffect(new Effect(wither, 300));
                player.hurt(DamageTools.getGenericDamageSource(player), 2.0f);
                break;
            case 9:
                // @todo 1.14
//                player.addPotionEffect(new Effect(harm, 400));
//                player.addPotionEffect(new Effect(wither, 400));
                player.hurt(DamageTools.getGenericDamageSource(player), 3.0f);
                break;
            case 10:
                // @todo 1.14
//                player.addPotionEffect(new Effect(harm, 500));
//                player.addPotionEffect(new Effect(wither, 500));
                player.hurt(DamageTools.getGenericDamageSource(player), 3.0f);
                break;
        }
    }

    /**
     * Calculate the cost of doing a dial between a transmitter and a destination.
     * @param c1 the start coordinate
     */
    public static int calculateRFCost(Level world, BlockPos c1, TeleportDestination teleportDestination) {
        if (!world.dimension().equals(teleportDestination.getDimension())) {
            return TeleportConfiguration.rfStartTeleportBaseDim.get();
        } else {
            BlockPos c2 = teleportDestination.getCoordinate();
            double dist = new Vec3(c1.getX(), c1.getY(), c1.getZ()).distanceTo(new Vec3(c2.getX(), c2.getY(), c2.getZ()));
            int rf = TeleportConfiguration.rfStartTeleportBaseLocal.get() + (int)(TeleportConfiguration.rfStartTeleportDist.get() * dist);
            if (rf > TeleportConfiguration.rfStartTeleportBaseDim.get()) {
                rf = TeleportConfiguration.rfStartTeleportBaseDim.get();
            }
            return rf;
        }
    }

    /**
     * Calculate the time in ticks of doing a dial between a transmitter and a destination.
     * @param c1 the start coordinate
     */
    public static int calculateTime(Level world, BlockPos c1, TeleportDestination teleportDestination) {
        if (!world.dimension().equals(teleportDestination.getDimension())) {
            return TeleportConfiguration.timeTeleportBaseDim.get();
        } else {
            BlockPos c2 = teleportDestination.getCoordinate();
            double dist = new Vec3(c1.getX(), c1.getY(), c1.getZ()).distanceTo(new Vec3(c2.getX(), c2.getY(), c2.getZ()));
            int time = TeleportConfiguration.timeTeleportBaseLocal.get() + (int)(TeleportConfiguration.timeTeleportDist.get() * dist / 1000);
            if (time > TeleportConfiguration.timeTeleportBaseDim.get()) {
                time = TeleportConfiguration.timeTeleportBaseDim.get();
            }
            return time;
        }
    }

    // Return true if we needed a boost.
    public static boolean performTeleport(Player player, TeleportDestination dest, int bad, int good, boolean boosted) {
        BlockPos c = dest.getCoordinate();

        BlockPos old = new BlockPos((int)player.getX(), (int)player.getY(), (int)player.getZ());
        ResourceKey<Level> oldId = player.getCommandSenderWorld().dimension();

        if (!TeleportationTools.allowTeleport(player, oldId, old, dest.getDimension(), dest.getCoordinate())) {
            return false;
        }

        if (!oldId.equals(dest.getDimension())) {
            mcjty.lib.varia.TeleportationTools.teleportToDimension(player, dest.getDimension(), c.getX() + 0.5, c.getY() + 1.5, c.getZ() + 0.5);
        } else {
            player.teleportTo(c.getX()+0.5, c.getY()+1, c.getZ()+0.5);
        }

        if (TeleportConfiguration.whooshMessage.get()) {
            Logging.message(player, "Whoosh!");
        }
        // @todo achievements
//        Achievements.trigger(player, Achievements.firstTeleport);

        boolean boostNeeded = false;
        int severity = consumeReceiverEnergy(player, dest.getCoordinate(), dest.getDimension());
        if (severity > 0 && boosted) {
            boostNeeded = true;
            severity = 1;
        }

        severity = applyBadEffectIfNeeded(player, severity, bad, good, boostNeeded);
        if (severity <= 0) {
            if (TeleportConfiguration.teleportVolume.get() >= 0.01) {
                SoundTools.playSound(player.getCommandSenderWorld(), ModSounds.WHOOSH.get(), player.getX(), player.getY(), player.getZ(), TeleportConfiguration.teleportVolume.get(), 1.0f);
            }
        }
        if (TeleportConfiguration.logTeleportUsages.get()) {
            Logging.log("Teleport: Player " + player.getName() + " from " + old + " (dim " + oldId + ") to " + dest.getCoordinate() + " (dim " + dest.getDimension() + ") with severity " + severity);
        }
        return boostNeeded;
    }

    // Server side only
    public static int dial(Level worldObj, DialingDeviceTileEntity dialingDeviceTileEntity, UUID player, BlockPos transmitter, ResourceKey<Level> transDim, BlockPos coordinate, ResourceKey<Level> dimension, boolean once) {
        Level transWorld = LevelTools.getLevel(transDim);
        if (transWorld == null) {
            return DialingDeviceTileEntity.DIAL_INVALID_SOURCE_MASK;
        }
        MatterTransmitterTileEntity transmitterTileEntity = (MatterTransmitterTileEntity) transWorld.getBlockEntity(transmitter);
        if (transmitterTileEntity == null) {
            return DialingDeviceTileEntity.DIAL_INVALID_TRANSMITTER;
        }

        if (player != null && !transmitterTileEntity.checkAccess(player)) {
            return DialingDeviceTileEntity.DIAL_TRANSMITTER_NOACCESS;
        }

        if (coordinate == null) {
            transmitterTileEntity.setTeleportDestination(null, false);
            return DialingDeviceTileEntity.DIAL_INTERRUPTED;
        }

        TeleportDestination teleportDestination = findDestination(worldObj, coordinate, dimension);
        if (teleportDestination == null) {
            return DialingDeviceTileEntity.DIAL_INVALID_DESTINATION_MASK;
        }

        BlockPos c = teleportDestination.getCoordinate();
        Level recWorld = LevelTools.getLevel(teleportDestination.getDimension());
        if (recWorld == null) {
            recWorld = LevelTools.getLevel(worldObj, teleportDestination.getDimension());
            if (recWorld == null) {
                return DialingDeviceTileEntity.DIAL_INVALID_DESTINATION_MASK;
            }
        }

        // Only do this if not an rftools dimension.
        BlockEntity tileEntity = recWorld.getBlockEntity(c);
        if (!(tileEntity instanceof MatterReceiverTileEntity receiver)) {
            return DialingDeviceTileEntity.DIAL_INVALID_DESTINATION_MASK;
        }
        TeleportDestination destination = receiver.updateDestination();// Make sure destination is ok.
        if (player != null && !destination.checkAccess(recWorld, player)) {
            return DialingDeviceTileEntity.DIAL_RECEIVER_NOACCESS;
        }

        if (!checkBeam(transmitter, transWorld, 1, 4, 2)) {
            return DialingDeviceTileEntity.DIAL_TRANSMITTER_BLOCKED_MASK;
        }

        if (dialingDeviceTileEntity != null) {
            if (!dialingDeviceTileEntity.getCapability(ForgeCapabilities.ENERGY).map(h -> {
                int defaultCost = TeleportConfiguration.rfPerDial.get();
                int cost = dialingDeviceTileEntity.getCapability(CapabilityInfusable.INFUSABLE_CAPABILITY).map(inf ->
                            (int) (defaultCost * (2.0f - inf.getInfusedFactor()) / 2.0f)).orElse(defaultCost);

                if (h.getEnergyStored() < cost) {
                    return false;
                }
                ((GenericEnergyStorage)h).consumeEnergy(cost);
                return true;
            }).orElse(false)) {
                return DialingDeviceTileEntity.DIAL_DIALER_POWER_LOW_MASK;
            }
        }

        transmitterTileEntity.setTeleportDestination(teleportDestination, once);

        return DialingDeviceTileEntity.DIAL_OK;
    }


    /**
     * Consume energy on the receiving side and return a number indicating how good this went.
     * @return 0 in case of success. 10 in case of severe failure
     */
    private static int consumeReceiverEnergy(Player player, BlockPos c, ResourceKey<Level> dimension) {
        Level world = LevelTools.getLevel(player.level(), dimension);
        if (world == null) {
            Logging.warn(player, "Something went wrong with the destination!");
            return 0;
        }
        BlockEntity te = world.getBlockEntity(c);
        if (!(te instanceof MatterReceiverTileEntity receiver)) {
            Logging.warn(player, "Something went wrong with the destination!");
            return 0;
        }

        return receiver.getCapability(ForgeCapabilities.ENERGY).map(h -> {
            int defaultCost = TeleportConfiguration.rfPerTeleportReceiver.get();
            int rf = receiver.getCapability(CapabilityInfusable.INFUSABLE_CAPABILITY).map(inf ->
                    (int) (defaultCost * (2.0f - inf.getInfusedFactor()) / 2.0f)).orElse(defaultCost);

            if (rf <= 0) {
                return 0;
            }
            int extracted = Math.min(rf, h.getEnergyStored());
            ((GenericEnergyStorage)h).consumeEnergy(rf);

            long remainingRf = ((GenericEnergyStorage) h).getEnergy();
            if (remainingRf <= 1) {
                Logging.warn(player, "The matter receiver has run out of power!");
            } else if (remainingRf < (TeleportConfiguration.RECEIVER_MAXENERGY.get() / 10)) {
                Logging.warn(player, "The matter receiver is getting very low on power!");
            } else if (remainingRf < (TeleportConfiguration.RECEIVER_MAXENERGY.get() / 5)) {
                Logging.warn(player, "The matter receiver is getting low on power!");
            }

            return 10 - (extracted * 10 / rf);
        }).orElse(0);
    }

    /**
     * Return a number between 0 and 10 indicating the severity of the teleportation.
     */
    public static int calculateSeverity(int bad, int total) {
        if (total == 0) {
            total = 1;
        }
        int severity = bad * 10 / total;
        if (mustInterrupt(bad, total)) {
            // If an interrupt was done then severity is worse.
            severity += 2;
        }
        if (severity > 10) {
            severity = 10;
        }
        return severity;
    }

    public static int applyBadEffectIfNeeded(Player player, int severity, int bad, int total, boolean boostNeeded) {
        if (player == null) {
            return 0;
        }
        severity += calculateSeverity(bad, total);
        if (severity > 10) {
            severity = 10;
        }
        if (severity <= 0) {
            return 0;
        }

        if (TeleportConfiguration.teleportErrorVolume.get() >= 0.01) {
            SoundTools.playSound(player.getCommandSenderWorld(), ModSounds.ERROR.get(), player.getX(), player.getY(), player.getZ(), TeleportConfiguration.teleportErrorVolume.get(), 1.0f);
        }

        applyEffectForSeverity(player, severity, boostNeeded);
        return severity;
    }

    public static boolean mustInterrupt(int bad, int total) {
        return bad > (total / 2);
    }

    public static boolean allowTeleport(Entity entity, ResourceKey<Level> sourceDim, BlockPos source, ResourceKey<Level> destDim, BlockPos dest) {
        // @todo 1.14 once env controller has been ported
//        if (NoTeleportAreaManager.isTeleportPrevented(entity, new GlobalCoordinate(source, sourceDim))) {
//            return false;
//        }
//        if (NoTeleportAreaManager.isTeleportPrevented(entity, new GlobalCoordinate(dest, destDim))) {
//            return false;
//        }
        return true;
    }

    public static TeleportDestination findDestination(Level worldObj, BlockPos coordinate, ResourceKey<Level> dimension) {
        TeleportDestinations destinations = TeleportDestinations.get(worldObj);
        return destinations.getDestination(coordinate, dimension);
    }

    // Check if there is room for a beam.
    public static boolean checkBeam(BlockPos c, Level world, int dy1, int dy2, int errory) {
        for (int dy = dy1 ; dy <= dy2 ; dy++) {
            BlockPos pos = new BlockPos(c.getX(), c.getY() + dy, c.getZ());
            if (!world.getBlockState(pos).isAir()) {
                if (dy <= errory) {
                    // Everything below errory must be free.
                    return false;
                } else {
                    // Everything higher then errory doesn't have to be free.
                    break;
                }
            }
        }
        return true;
    }

    public static boolean checkValidTeleport(Player player, ResourceKey<Level> srcId, ResourceKey<Level> dstId) {
        if (TeleportConfiguration.preventInterdimensionalTeleports.get()) {
            if (srcId.equals(dstId)) {
                Logging.warn(player, "Teleportation in the same dimension is not allowed!");
                return false;
            }
        }
        if (TeleportConfiguration.getBlacklistedTeleportationDestinations().contains(dstId)) {
            Logging.warn(player, "Teleportation to that dimension is not allowed!");
            return false;
        }
        if (TeleportConfiguration.getBlacklistedTeleportationSources().contains(srcId)) {
            Logging.warn(player, "Teleportation from this dimension is not allowed!");
            return false;
        }
        return true;
    }
}
