package mcjty.rftoolsutility.modules.environmental.modules;

import mcjty.rftoolsutility.modules.environmental.blocks.EnvironmentalControllerTileEntity;
import mcjty.rftoolsutility.playerprops.BuffProperties;
import mcjty.rftoolsutility.playerprops.PlayerBuff;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class PotionEffectModule implements EnvironmentModule {
    public static final int MAXTICKS = 180;

    private final MobEffect potion;
    private final int amplifier;

    private boolean active = false;
    private int ticks = MAXTICKS;

    public PotionEffectModule(String potionname, int amplifier) {
        this.potion = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(potionname));
        this.amplifier = amplifier;
    }

    protected abstract PlayerBuff getBuff();

    protected boolean allowedForPlayers() {
        return true;
    }

    @Override
    public void tick(Level world, BlockPos pos, int radius, int miny, int maxy, EnvironmentalControllerTileEntity controllerTileEntity) {
        if (!active) {
            return;
        }

        ticks--;
        if (ticks > 0) {
            return;
        }
        ticks = MAXTICKS;

        EnvironmentalControllerTileEntity.EnvironmentalMode mode = controllerTileEntity.getMode();
        switch (mode) {
            case MODE_BLACKLIST:
            case MODE_WHITELIST:
                if (allowedForPlayers()) {
                    processPlayers(world, pos, radius, miny, maxy, controllerTileEntity);
                }
                break;
            case MODE_HOSTILE:
            case MODE_PASSIVE:
            case MODE_MOBS:
            case MODE_ALL:
                processEntities(world, pos, radius, miny, maxy, controllerTileEntity);
                break;
        }
    }

    private void processPlayers(Level world, BlockPos pos, int radius, int miny, int maxy, EnvironmentalControllerTileEntity controllerTileEntity) {
        double maxsqdist = radius * radius;
        List<Player> players = new ArrayList<>(world.players());
        for (Player player : players) {
            double py = player.getY();
            if (py >= miny && py <= maxy) {
                double px = player.getX();
                double pz = player.getZ();
                double sqdist = (px-pos.getX()) * (px-pos.getX()) + (pz-pos.getZ()) * (pz-pos.getZ());
                if (sqdist < maxsqdist) {
                    if (controllerTileEntity.isPlayerAffected(player)) {
                        player.addEffect(new MobEffectInstance(potion, MAXTICKS * 3, amplifier, true, false));
                        PlayerBuff buff = getBuff();
                        if (buff != null) {
                            BuffProperties.addBuffToPlayer(player, buff, MAXTICKS);
                        }
                    }
                }
            }
        }
    }

    private void processEntities(Level world, BlockPos pos, int radius, int miny, int maxy, EnvironmentalControllerTileEntity controllerTileEntity) {
        double maxsqdist = radius * radius;
        List<LivingEntity> entities = world.getEntities((EntityType)null, new AABB(
                pos.getX() - radius, pos.getY() - radius, pos.getZ() - radius,
                pos.getX() + radius, pos.getY() + radius, pos.getZ() + radius), e -> e instanceof LivingEntity);
        for (LivingEntity entity : entities) {
            double py = entity.getY();
            if (py >= miny && py <= maxy) {
                double px = entity.getX();
                double pz = entity.getZ();
                double sqdist = (px-pos.getX()) * (px-pos.getX()) + (pz-pos.getZ()) * (pz-pos.getZ());
                if (sqdist < maxsqdist) {
                    if (controllerTileEntity.isEntityAffected(entity)) {
                        if (!(entity instanceof Player) || allowedForPlayers()) {
                            entity.addEffect(new MobEffectInstance(potion, MAXTICKS * 3, amplifier, true, false));
                            PlayerBuff buff = getBuff();
                            if (buff != null) {
                                if (entity instanceof Player) {
                                    BuffProperties.addBuffToPlayer((Player) entity, buff, MAXTICKS);
                                }
                            }
                        }
                    } else if (entity instanceof Player) {
                        PlayerBuff buff = getBuff();
                        if (buff != null) {
                            BuffProperties.addBuffToPlayer((Player) entity, buff, MAXTICKS);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean apply(Level world, BlockPos pos, LivingEntity entity, int duration) {
        entity.addEffect(new MobEffectInstance(potion, duration, amplifier, true, false));
        return true;
    }

    @Override
    public void activate(boolean a) {
        if (active == a) {
            return;
        }
        active = a;
        ticks = 1;
    }

    public static PotionEffectModule create(String name, int amplifier, PlayerBuff buff, double rfPerTick) {
        return new PotionEffectModule(name, amplifier) {
            @Override
            protected PlayerBuff getBuff() {
                return buff;
            }

            @Override
            public float getRfPerTick() {
                return (float)rfPerTick;
            }
        };
    }

    public static PotionEffectModule create(String name, int amplifier, PlayerBuff buff, double rfPerTick, Supplier<Boolean> isAllowed) {
        return new PotionEffectModule(name, amplifier) {
            @Override
            protected PlayerBuff getBuff() {
                return buff;
            }

            @Override
            public float getRfPerTick() {
                return (float) rfPerTick;
            }

            @Override
            protected boolean allowedForPlayers() {
                return isAllowed.get();
            }
        };
    }
}
