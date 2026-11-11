package mcjty.rftoolsutility.setup;

import mcjty.lib.api.smartwrench.SmartWrench;
import mcjty.rftoolsutility.commands.ModCommands;
import mcjty.rftoolsutility.modules.environmental.NoTeleportAreaManager;
import mcjty.rftoolsutility.modules.environmental.PeacefulAreaManager;
import mcjty.rftoolsutility.modules.screen.blocks.IAttackableBlock;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenBlock;
import mcjty.rftoolsutility.modules.screen.blocks.ScreenHitBlock;
import mcjty.rftoolsutility.modules.screen.items.modules.ButtonModuleItem;
import mcjty.rftoolsutility.modules.spawner.items.SyringeItem;
import mcjty.rftoolsutility.modules.teleporter.TeleportationTools;
import mcjty.rftoolsutility.modules.teleporter.data.TeleportDestination;
import mcjty.rftoolsutility.playerprops.PlayerBuff;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class ForgeEventHandlers {

    private static final List<Pair<TeleportDestination, Player>> PLAYERS_TO_TELEPORT = new ArrayList<>();
    private static MinecraftServer server;

    public static MinecraftServer getServer() {
        return server;
    }

    public static void register() {
        ServerLifecycleEvents.SERVER_STARTING.register(current -> server = current);
        ServerLifecycleEvents.SERVER_STOPPED.register(current -> {
            server = null;
            PLAYERS_TO_TELEPORT.clear();
        });
        ServerTickEvents.START_SERVER_TICK.register(current -> {
            var teleports = new ArrayList<>(PLAYERS_TO_TELEPORT);
            PLAYERS_TO_TELEPORT.clear();
            for (var entry : teleports) {
                TeleportationTools.performTeleport(entry.getRight(), entry.getLeft(), 0, 10, false);
            }
            for (var player : current.getPlayerList().getPlayers()) {
                player.getAttachedOrCreate(Registration.ATTACHMENT_TYPE_BUFF_PROPERTIES).tickBuffs(player);
            }
        });
        CommandRegistrationCallback.EVENT.register((dispatcher, access, environment) -> ModCommands.register(dispatcher));
        UseItemCallback.EVENT.register((player, level, hand) -> {
            if (!(player.getMainHandItem().getItem() instanceof SmartWrench)) {
                BlockHitResult hit = rayTraceEyes(player, player.blockInteractionRange() + 1);
                if (hit.getType() == HitResult.Type.BLOCK) {
                    Block block = level.getBlockState(hit.getBlockPos()).getBlock();
                    if (block instanceof ScreenBlock || block instanceof ScreenHitBlock) {
                        return InteractionResult.FAIL;
                    }
                }
            }
            return InteractionResult.PASS;
        });
        AttackEntityCallback.EVENT.register((player, level, hand, entity, hit) -> {
            if (!level.isClientSide() && player.getItemInHand(hand).getItem() instanceof SyringeItem syringe) {
                syringe.onLeftClickEntity(player.getItemInHand(hand), player, entity);
            }
            return InteractionResult.PASS;
        });
        UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
            if (player.isShiftKeyDown() && player.getItemInHand(hand).getItem() instanceof ButtonModuleItem) {
                var state = level.getBlockState(hit.getBlockPos());
                if (state.getBlock() instanceof ScreenBlock block) {
                    return block.activate(level, hit.getBlockPos(), state, player, hand, hit);
                }
                if (state.getBlock() instanceof ScreenHitBlock block) {
                    return block.activate(level, hit.getBlockPos(), state, player, hand, hit);
                }
            }
            return InteractionResult.PASS;
        });
        AttackBlockCallback.EVENT.register((player, level, hand, pos, direction) -> {
            if (player.isCreative() && !player.isShiftKeyDown()
                    && level.getBlockState(pos).getBlock() instanceof IAttackableBlock block) {
                if (level.isClientSide()) {
                    block.doAttack(level, pos);
                }
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });
    }

    public static void addPlayerToTeleportHere(TeleportDestination destination, Player player) {
        PLAYERS_TO_TELEPORT.add(Pair.of(destination, player));
    }

    public static BlockHitResult rayTraceEyes(LivingEntity entity, double length) {
        var start = entity.getEyePosition();
        return entity.level().clip(new ClipContext(start, start.add(entity.getLookAngle().scale(length)),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
    }

    public static float fallDamageMultiplier(LivingEntity entity, float multiplier) {
        if (entity instanceof Player player) {
            var buffs = player.getAttachedOrCreate(Registration.ATTACHMENT_TYPE_BUFF_PROPERTIES);
            if (buffs.hasBuff(PlayerBuff.BUFF_FEATHERFALLINGPLUS)) {
                return 0;
            }
            if (buffs.hasBuff(PlayerBuff.BUFF_FEATHERFALLING)) {
                return multiplier / 2;
            }
        }
        return multiplier;
    }

    public static boolean canTeleport(Entity entity, double x, double y, double z) {
        var dimension = entity.level().dimension();
        return !NoTeleportAreaManager.isTeleportPrevented(entity, GlobalPos.of(dimension, entity.blockPosition()))
                && !NoTeleportAreaManager.isTeleportPrevented(entity, GlobalPos.of(dimension, BlockPos.containing(x, y, z)));
    }

    public static boolean onEntitySpawnEvent(Entity entity) {
        return entity instanceof Enemy
                && PeacefulAreaManager.isPeaceful(GlobalPos.of(entity.level().dimension(), entity.blockPosition()));
    }
}
