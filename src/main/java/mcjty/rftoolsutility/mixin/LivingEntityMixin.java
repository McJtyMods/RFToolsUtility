package mcjty.rftoolsutility.mixin;

import mcjty.rftoolsutility.setup.ForgeEventHandlers;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @ModifyVariable(method = "causeFallDamage", at = @At("HEAD"), argsOnly = true)
    private float rftoolsutility$fallMultiplier(float multiplier) {
        return ForgeEventHandlers.fallDamageMultiplier((LivingEntity) (Object) this, multiplier);
    }

    @Inject(method = "randomTeleport", at = @At("HEAD"), cancellable = true)
    private void rftoolsutility$preventTeleport(double x, double y, double z, boolean particles, CallbackInfoReturnable<Boolean> cir) {
        if (!ForgeEventHandlers.canTeleport((LivingEntity) (Object) this, x, y, z)) {
            cir.setReturnValue(false);
        }
    }
}
