package lack.fluidlib.mixin.entity;

import lack.fluidlib.fluid.FluidRegistry;
import lack.fluidlib.mixinaccessor.EntityAccessor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public class PlayerMixin {

    @Redirect(method = "getBlockBreakingSpeed", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/entity/player/PlayerEntity;isSubmergedIn(Lnet/minecraft/registry/tag/TagKey;)Z", ordinal = 0))
    protected boolean blockBreak(PlayerEntity instance, TagKey tagKey) {
        return FluidRegistry.getSwimmableSet().stream().anyMatch(instance::isSubmergedIn);
    }

    @Redirect(method = "checkGliding", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/entity/player/PlayerEntity;isTouchingWater()Z"))
    protected boolean gliding(PlayerEntity instance) {
        return ((EntityAccessor) instance).fluidlib$isTouchingSwimmable();

    }

    @Redirect(method = "playStepSound", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/entity/player/PlayerEntity;isTouchingWater()Z"))
    protected boolean step(PlayerEntity instance) {
        return ((EntityAccessor) instance).fluidlib$isTouchingSwimmable();
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/entity/player/PlayerEntity;isSubmergedIn(Lnet/minecraft/registry/tag/TagKey;)Z"))
    protected boolean turtle(PlayerEntity instance, TagKey tagKey) {
        return FluidRegistry.applyPredicateStream(entry -> instance.isSubmergedIn(entry.getKey())).anyMatch(entry -> entry.getValue().suffocates(instance));

    }
}
