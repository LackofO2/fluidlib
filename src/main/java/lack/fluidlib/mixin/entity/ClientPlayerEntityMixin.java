package lack.fluidlib.mixin.entity;

import lack.fluidlib.mixinaccessor.EntityAccessor;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Debug(export = true)
@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Redirect(method = "shouldStopSprinting", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/network/ClientPlayerEntity;isTouchingWater()Z"))
    public boolean shouldStop(ClientPlayerEntity instance) {
        return ((EntityAccessor) instance).fluidlib$isTouchingSwimmable();
    }

    @Redirect(method = "shouldStopSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSubmergedInWater()Z"))
    public boolean shouldStop2(ClientPlayerEntity instance) {
        return ((EntityAccessor) instance).fluidlib$isSubmergedInSwimmable();
    }

    @Redirect(method = "shouldStopSwimSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isTouchingWater()Z"))
    public boolean shouldStop3(ClientPlayerEntity instance) {
        return ((EntityAccessor) instance).fluidlib$isTouchingSwimmable();
    }

    @Redirect(method = "canStartSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isTouchingWater()Z"))
    public boolean canStart(ClientPlayerEntity instance) {
        return ((EntityAccessor) instance).fluidlib$isTouchingSwimmable();
    }

    @Redirect(method = "canStartSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSubmergedInWater()Z"))
    public boolean canStart2(ClientPlayerEntity instance) {
        return ((EntityAccessor) instance).fluidlib$isSubmergedInSwimmable();
    }
}
