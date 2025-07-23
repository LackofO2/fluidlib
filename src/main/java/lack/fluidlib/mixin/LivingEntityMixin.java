package lack.fluidlib.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import lack.fluidlib.fluid.ModFluid;
import lack.fluidlib.mixinaccessor.EntityAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Debug(export = true)
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements EntityAccessor {


    @Shadow
    public abstract boolean shouldSwimInFluids();

    @Shadow
    public abstract boolean canWalkOnFluid(FluidState state);

    @Shadow
    public abstract Vec3d applyFluidMovingSpeed(double gravity, boolean falling, Vec3d motion);

    @Shadow
    protected abstract double getEffectiveGravity();

    @Shadow
    protected abstract void travelInFluid(Vec3d movementInput);

    protected LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    /**
     * @reason Emulates vanilla fluid mechanics for custom fluids
     */
    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    public void travel(Vec3d movementInput, CallbackInfo ci) {
        FluidState fluidState = this.getWorld().getFluidState(this.getBlockPos());
        if (isInSwimmableFluid() || isInAnyFluid() && this.shouldSwimInFluids() && !this.canWalkOnFluid(fluidState)) {
            this.travelInFluid(movementInput);
            ci.cancel();
        }
    }

    /**
     * @reason Emulates vanilla fluid mechanics for custom fluids
     */
    @Inject(method = "travelFlying(Lnet/minecraft/util/math/Vec3d;FFF)V", at = @At("HEAD"), cancellable = true)
    protected void travelFlying(Vec3d movementInput, float inWaterSpeed, float inLavaSpeed, float regularSpeed, CallbackInfo ci) {

        List<ModFluid> modFluids = getAllModFluids();

        if (modFluids != null && !modFluids.isEmpty()) {
            ModFluid fluid = modFluids.getFirst();
            this.updateVelocity(fluid.getFlyingEntitySpeed(), movementInput);
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(fluid.getEntityDrag()));
            ci.cancel();
        }
    }



    /**
     * @param instance The Living Entity
     * @return true if the entity is in a fluid
     * @reason Emulates vanilla fluid jump mechanics for custom fluids. Part 1 of 2.
     */
    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isInLava()Z", ordinal = 1))
    public boolean acidLogic(LivingEntity instance) {
        return isInAnyFluid() || instance.isInLava();
    }

    /**
     * @param fluid The fluid tag
     * @return first valid fluid tag
     * @reason Emulates vanilla fluid jump mechanics for custom fluids. Part 2 of 2.
     */
    @ModifyArg(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;swimUpward(Lnet/minecraft/registry/tag/TagKey;)V", ordinal = 1))
    public TagKey<Fluid> acidLogic2(TagKey<Fluid> fluid) {
        return getFirstFluid().orElse(fluid);
    }

    /**
     * @param fluidTagKey the fluid tag
     * @reason fixes fluid height for custom fluids
     */
    @ModifyArg(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getFluidHeight(Lnet/minecraft/registry/tag/TagKey;)D", ordinal = 1))
    public TagKey<Fluid> replaceFluidTag(TagKey<Fluid> fluidTagKey) {
        Optional<TagKey<Fluid>> firstFluid = getFirstFluid();
        return firstFluid.orElse(fluidTagKey);
    }

//    @Inject(method = "travelInFluid", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isTouchingWater()Z"), cancellable = true)
//    private void travelF(Vec3d movementInput, CallbackInfo ci, @Local boolean falling, @Local(ordinal = 0) double entityY, @Local(ordinal = 1) double effectiveGravity) {
//        List<ModFluid> fluids = getSwimmableFluids();
//        Optional<ModFluid> firstFluid = fluids.isEmpty() ? Optional.empty() : Optional.of(fluids.getFirst());
//        if (!isTouchingWater() && firstFluid.isPresent()) {
//            ModFluid.Properties.SpeedModifier speedModifier = firstFluid.get().getSpeedModifier();
//            if (speedModifier.accept(((LivingEntity) (Object) this), falling, entityY, effectiveGravity)) {
//                speedModifier.apply(((LivingEntity) (Object) this), falling, entityY, effectiveGravity, movementInput);
//            }
//
//
//            ci.cancel();
//        }
//    }

    @Redirect(method = "travelInFluid", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isTouchingWater()Z"))
    private boolean travelF(LivingEntity instance) {
        List<ModFluid> fluids = getSwimmableFluids();
        Optional<TagKey<Fluid>> firstFluid = fluids.isEmpty() ? Optional.empty() : Optional.of(fluids.getFirst().getFluidTag());

        return firstFluid.map(fluidTagKey -> ((EntityAccessor) instance).fluidlib$isInFluid(fluidTagKey) || instance.isTouchingWater()).orElseGet(instance::isTouchingWater);
    }

    @Redirect(method = "travelInFluid", at = @At(value = "FIELD", target = "Lnet/minecraft/registry/tag/FluidTags;LAVA:Lnet/minecraft/registry/tag/TagKey;"))
    private TagKey<Fluid> travelF() {
        List<ModFluid> fluids = getSwimmableFluids();
        Optional<TagKey<Fluid>> firstFluid = fluids.isEmpty() ? Optional.empty() : Optional.of(fluids.getFirst().getFluidTag());

        return firstFluid.orElse(FluidTags.LAVA);
    }

}
