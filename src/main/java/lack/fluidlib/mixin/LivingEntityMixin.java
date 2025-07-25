package lack.fluidlib.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import lack.fluidlib.fluid.FluidProperties;
import lack.fluidlib.fluid.FluidRegistry;
import lack.fluidlib.mixinaccessor.EntityAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Debug(export = true)
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements EntityAccessor {

    @Shadow
    protected abstract void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition);

    protected LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    /**
     * @reason Emulates vanilla fluid mechanics for custom fluids
     */
    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isTouchingWater()Z"))
    public boolean travel1(LivingEntity instance) {
        return ((EntityAccessor) instance).fluidlib$isTouchingSwimmable();
    }

    /**
     * @reason Emulates vanilla fluid mechanics for custom fluids
     */
    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isInLava()Z"))
    public boolean travel2(LivingEntity instance) {
        return ((EntityAccessor) instance).fluidlib$isTouchingNonswimmable();
    }

    @Inject(method = "travelFlying(Lnet/minecraft/util/math/Vec3d;FFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;updateVelocity(FLnet/minecraft/util/math/Vec3d;)V", ordinal = 2), cancellable = true)
    protected void travelFlying(Vec3d movementInput, float inWaterSpeed, float inLavaSpeed, float regularSpeed, CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        List<TagKey<Fluid>> allFluids = new ArrayList<>(FluidRegistry.getAll());

        Optional<FluidProperties> fluidProperties = allFluids.stream().filter(this::fluidlib$isInFluid).filter(FluidRegistry.getFluids()::containsKey).map(FluidRegistry.getFluids()::get).findAny();

        if (fluidProperties.isPresent()) {
            this.updateVelocity(fluidProperties.get().flyingEntitySpeed(livingEntity), movementInput);
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(fluidProperties.get().entityDrag(livingEntity)));
            ci.cancel();
        }
    }

    /**
     * @reason Changes the fluid height  from lava to non-swimmable. Subject to change.
     * Part 2 of 2.
     */
    @Redirect(method = "tickMovement", at = @At(value = "FIELD",
        target = "Lnet/minecraft/registry/tag/FluidTags;WATER:Lnet/minecraft/registry/tag/TagKey;", ordinal = 0))
    public TagKey<Fluid> fluidHeightNonSwimmable2() {
        List<TagKey<Fluid>> allFluids = new ArrayList<>(FluidRegistry.getAll());

        for (TagKey<Fluid> swimmable : allFluids) {
            if (fluidlib$isInFluid(swimmable)) {
                return swimmable;
            }
        }

        return FluidTags.WATER;
    }


    /**
     * @reason Changes the jump check from water to swimmable. Subject to change.
     */
    @Redirect(method = "tickMovement", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/entity/LivingEntity;isTouchingWater()Z", ordinal = 0))
    public boolean jumpCheckSwimmable(LivingEntity instance) {
        return ((EntityAccessor) instance).fluidlib$isTouchingSwimmable();
    }

    /**
     * @reason Changes the jump check from lava to non-swimmable. Subject to change.
     */
    @Redirect(method = "tickMovement", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/entity/LivingEntity;isInLava()Z", ordinal = 1))
    public boolean jumpCheckNonSwimmable(LivingEntity instance) {
        return ((EntityAccessor) instance).fluidlib$isTouchingNonswimmable();
    }

    @Inject(method = "travelInFluid", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/entity/LivingEntity;updateVelocity(FLnet/minecraft/util/math/Vec3d;)V", ordinal = 1), cancellable = true)
    private void waterToSwimmableFluid2(Vec3d movementInput, CallbackInfo ci, @Local boolean falling, @Local(ordinal = 0) double entityY, @Local(ordinal = 1) double effectiveGravity) {
        LivingEntity entity = (LivingEntity) (Object) this;

        List<TagKey<Fluid>> allFluids = new ArrayList<>(FluidRegistry.getAll());

        boolean shouldCancel = false;
        for (TagKey<Fluid> fluid : allFluids) {
            if (fluidlib$isInFluid(fluid) && FluidRegistry.getFluids().containsKey(fluid)) {
                FluidProperties.SpeedModifier speedModifier = FluidRegistry.getFluids().get(fluid).speedModifier();
                if (speedModifier.accept(entity, falling, entityY, effectiveGravity)) {
                    shouldCancel = true;
                    speedModifier.apply(entity, falling, entityY, effectiveGravity, movementInput);
                }
            }
        }
        if (shouldCancel) {
            Vec3d vec3d2 = this.getVelocity();
            if (this.horizontalCollision && this.doesNotCollide(vec3d2.x, vec3d2.y + 0.6F - this.getY() + entityY, vec3d2.z)) {
                this.setVelocity(vec3d2.x, 0.3F, vec3d2.z);
            }
            ci.cancel();
        }

    }

    @Redirect(method = "baseTick", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/entity/LivingEntity;isSubmergedIn(Lnet/minecraft/registry/tag/TagKey;)Z", ordinal = 0))
    public boolean suffocation1(LivingEntity instance, TagKey tagKey) {
        Map<TagKey<Fluid>, FluidProperties> fluids = FluidRegistry.getFluids();
        return FluidRegistry.getAll().stream().filter(instance::isSubmergedIn).filter(fluids::containsKey)
            .anyMatch(fluidTagKey -> fluids.get(fluidTagKey).suffocates(instance));
    }


}
