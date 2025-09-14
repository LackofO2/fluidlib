package lack.fluidlib.mixin.entity;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import lack.fluidlib.fluid.FluidProperties;
import lack.fluidlib.fluid.FluidRegistry;
import lack.fluidlib.fluid.LavaFluidProperties;
import lack.fluidlib.fluid.WaterFluidProperties;
import lack.fluidlib.mixinaccessor.EntityAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Debug(export = true)
@Mixin(Entity.class)
public abstract class EntityMixin implements EntityAccessor {
    @Unique
    protected boolean submergedInSwimmable;
    @Unique
    protected boolean touchingSwimmable;
    @Unique
    protected boolean submergedInNonswimmable;
    @Unique
    protected boolean touchingNonswimmable;

    @Shadow
    protected boolean firstUpdate;

    @Shadow
    protected Object2DoubleMap<TagKey<Fluid>> fluidHeight;

    @Shadow
    public abstract boolean updateMovementInFluid(TagKey<Fluid> tag, double speed);

    @Shadow
    public double fallDistance;

    @Shadow
    public abstract World getWorld();

    @Shadow
    public abstract boolean isSubmergedIn(TagKey<Fluid> fluidTag);

    @Shadow
    public abstract @Nullable Entity getVehicle();

    @Shadow
    protected abstract void onSwimmingStart();

    @Shadow
    public abstract void onLanding();

    @Shadow
    protected boolean touchingWater;

    @Override
    public boolean fluidlib$isInFluid(TagKey<Fluid> fluid) {
        return !this.firstUpdate && this.fluidHeight.getDouble(fluid) > 0.0;
    }

    @Unique
    public boolean fluidlib$isSubmergedInSwimmable() {
        return this.submergedInSwimmable && this.fluidlib$isTouchingSwimmable();
    }

    @Unique
    public boolean fluidlib$isTouchingSwimmable() {
        return this.touchingSwimmable;
    }

    @Unique
    public boolean fluidlib$isSubmergedInNonswimmable() {
        return this.submergedInNonswimmable && this.fluidlib$isTouchingNonswimmable();
    }

    @Unique
    public boolean fluidlib$isTouchingNonswimmable() {
        return this.touchingNonswimmable;
    }

    @Inject(method = "updateSubmergedInWaterState", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isSubmergedIn(Lnet/minecraft/registry/tag/TagKey;)Z"))
    public void submerged(CallbackInfo ci) {
        this.submergedInSwimmable = FluidRegistry.getSwimmableSet().stream().anyMatch(this::isSubmergedIn);
        this.submergedInNonswimmable = FluidRegistry.getNonswimmableSet().stream().anyMatch(this::isSubmergedIn);
    }

    @Redirect(method = "updateWaterState", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;updateMovementInFluid(Lnet/minecraft/registry/tag/TagKey;D)Z"))
    public boolean updateNonSwimmableState(Entity instance, TagKey<Fluid> tag, double a) {
        boolean isTouchingNonSwimmable = false;
        for (TagKey<Fluid> nonswimmable : FluidRegistry.getNonswimmableSet()) {
            double speed = FluidRegistry.getFluids().getOrDefault(nonswimmable, new LavaFluidProperties()).entityMovementSpeed(instance);
            if (this.updateMovementInFluid(nonswimmable, speed) && !isTouchingNonSwimmable) {
                isTouchingNonSwimmable = true;
            }
        }

        this.touchingNonswimmable = isTouchingNonSwimmable;
        return isTouchingNonSwimmable;
    }

    /**
     * @author me
     * @reason easier to overwrite instead
     */
    @Overwrite
    public void checkWaterState() {
        this.touchingWater = this.updateMovementInFluid(FluidTags.WATER, 0.014);
        checkSwimmableState();
    }

    @Unique
    void checkSwimmableState() {
        Entity entity = (Entity) (Object) this;
        if (this.getVehicle() instanceof AbstractBoatEntity abstractBoatEntity && !((EntityAccessor) abstractBoatEntity).fluidlib$isSubmergedInSwimmable()) {
            this.touchingSwimmable = false;
        } else {
            Optional<Float> lowestFallDamageMultiplier = Optional.empty();

            Map<TagKey<Fluid>, FluidProperties> fluids = FluidRegistry.getFluids();

            for (TagKey<Fluid> swimmableFluid : FluidRegistry.getSwimmableSet()) {
                FluidProperties fluidProperties = fluids.get(swimmableFluid);

                double movementSpeedInFluid = new WaterFluidProperties().entityMovementSpeed(entity);
                float fallDamageMultiplier = 1.0f;

                if (fluidProperties != null) {
                    movementSpeedInFluid = fluidProperties.entityMovementSpeed(entity);
                    fallDamageMultiplier = fluidProperties.fallDamageMultiplier(entity);
                }

                if (this.updateMovementInFluid(swimmableFluid, movementSpeedInFluid)) {
                    lowestFallDamageMultiplier = Optional.of(lowestFallDamageMultiplier.isPresent() ?
                        Math.min(fallDamageMultiplier, lowestFallDamageMultiplier.get()) :
                        fallDamageMultiplier);
                }
            }

            if (lowestFallDamageMultiplier.isPresent()) {
                if (!this.touchingSwimmable && !this.firstUpdate) {
                    this.onSwimmingStart();
                }
                //Disables fall damage if 0
                if (lowestFallDamageMultiplier.get() == 0f) {
                    this.onLanding();
                }

                this.touchingSwimmable = true;
            } else {
                this.touchingSwimmable = false;
            }
        }
    }


    @Redirect(method = "isInFluid", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isTouchingWater()Z"))
    public boolean redirectIsInWaterToSwimmableForIsInFluid(Entity instance) {
        return ((EntityAccessor) instance).fluidlib$isTouchingSwimmable();
    }

    @Redirect(method = "isInFluid", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isInLava()Z"))
    public boolean redirectIsInLavaToNonswimmableForIsInFluid(Entity instance) {
        return ((EntityAccessor) instance).fluidlib$isTouchingNonswimmable();
    }

    @Redirect(method = "isCrawling", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isTouchingWater()Z"))
    public boolean crawlFix(Entity instance) {
        return ((EntityAccessor) instance).fluidlib$isTouchingSwimmable();
    }

    @Inject(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isInLava()Z"))
    public void fallDamageMultiplier(CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;

        this.fallDistance *= FluidRegistry.applyPredicateStream(entry -> fluidlib$isInFluid(entry.getKey())).map(entry -> entry.getValue().fallDamageMultiplier(entity)).findFirst().orElse(1.0f);
    }

    @Redirect(method = "applyMoveEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isTouchingWater()Z"))
    public boolean updateMove(Entity instance) {
        return ((EntityAccessor) instance).fluidlib$isTouchingSwimmable();
    }

    @Redirect(method = "updateSwimming", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isTouchingWater()Z"))
    public boolean updateSwim(Entity instance) {
        return ((EntityAccessor) instance).fluidlib$isTouchingSwimmable();
    }

    @Redirect(method = "updateSwimming", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isSubmergedInWater()Z"))
    public boolean updateSwim2(Entity instance) {
        return ((EntityAccessor) instance).fluidlib$isSubmergedInSwimmable();
    }

    @Redirect(method = "updateSwimming", at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"))
    public boolean updateSwim4(FluidState instance, TagKey<Fluid> tag) {
        return FluidRegistry.getSwimmableSet().stream().anyMatch(instance::isIn);
    }


}

