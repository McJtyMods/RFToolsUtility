package mcjty.rftoolsutility.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mcjty.rftoolsutility.setup.ForgeEventHandlers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import net.minecraft.world.level.portal.TeleportTransition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ThrownEnderpearl.class)
public class EnderPearlMixin {

    @WrapOperation(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;teleport(Lnet/minecraft/world/level/portal/TeleportTransition;)Lnet/minecraft/server/level/ServerPlayer;"))
    private ServerPlayer rftoolsutility$playerTeleport(ServerPlayer player, TeleportTransition transition, Operation<ServerPlayer> original) {
        var pos = transition.position();
        return ForgeEventHandlers.canTeleport(player, pos.x, pos.y, pos.z) ? original.call(player, transition) : null;
    }

    @WrapOperation(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;teleport(Lnet/minecraft/world/level/portal/TeleportTransition;)Lnet/minecraft/world/entity/Entity;"))
    private Entity rftoolsutility$entityTeleport(Entity entity, TeleportTransition transition, Operation<Entity> original) {
        var pos = transition.position();
        return ForgeEventHandlers.canTeleport(entity, pos.x, pos.y, pos.z) ? original.call(entity, transition) : null;
    }
}
