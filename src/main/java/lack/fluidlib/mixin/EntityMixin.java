package lack.fluidlib.mixin;

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
        this.submergedInSwimmable = FluidRegistry.getSwimmable().stream().anyMatch(this::isSubmergedIn);
        this.submergedInNonswimmable = FluidRegistry.getNonswimmable().stream().anyMatch(this::isSubmergedIn);
    }

    @Redirect(method = "updateWaterState", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;updateMovementInFluid(Lnet/minecraft/registry/tag/TagKey;D)Z"))
    public boolean update(Entity instance, TagKey<Fluid> tag, double a) {
        boolean touching = false;
        for (TagKey<Fluid> nonswimmable : FluidRegistry.getNonswimmable()) {
            double speed = FluidRegistry.getFluids().getOrDefault(nonswimmable, new LavaFluidProperties()).entityMovementSpeed(instance);
            if (this.updateMovementInFluid(nonswimmable, speed) && !touching) {
                touching = true;
            }
            //should not break
        }

        this.touchingNonswimmable = touching;
        return touching;
    }


    @Unique
    void checkSwimmableState() {
        Entity entity = (Entity) (Object) this;
        if (this.getVehicle() instanceof AbstractBoatEntity abstractBoatEntity && !((EntityAccessor) abstractBoatEntity).fluidlib$isSubmergedInSwimmable()) {
            this.touchingSwimmable = false;
        } else {
            Optional<Float> happened = Optional.empty();
            Map<TagKey<Fluid>, FluidProperties> fluids = FluidRegistry.getFluids();
            for (TagKey<Fluid> swimmableFluid : FluidRegistry.getSwimmable()) {

                FluidProperties fluidProperties = fluids.get(swimmableFluid);
                double speed = fluidProperties != null ? fluidProperties.entityMovementSpeed(entity) : new WaterFluidProperties().entityMovementSpeed(entity);

                float fallDamageMultiplier = fluidProperties != null ? fluidProperties.fallDamageMultiplier(entity) : 1.0f;

                if (this.updateMovementInFluid(swimmableFluid, speed) && happened.isEmpty()) {
                    happened = Optional.of(fallDamageMultiplier);
                }
            }
            if (happened.isPresent()) {
                if (!this.touchingSwimmable && !this.firstUpdate) {
                    this.onSwimmingStart();
                }
                //Removes fall damage
                if (happened.get() == 0f) {
                    this.onLanding();
                }

                this.touchingSwimmable = true;
            } else {
                this.touchingSwimmable = false;
            }
        }
    }

    /**
     * @author me
     * @reason lazy
     */
    @Overwrite
    public void checkWaterState() {
        this.touchingWater = this.updateMovementInFluid(FluidTags.WATER, 0.014);
        checkSwimmableState();
    }

    @Redirect(method = "isInFluid", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isTouchingWater()Z"))
    public boolean inFluid(Entity instance) {
        return ((EntityAccessor) instance).fluidlib$isTouchingSwimmable();
    }

    @Redirect(method = "isInFluid", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isInLava()Z"))
    public boolean inFluid2(Entity instance) {
        return ((EntityAccessor) instance).fluidlib$isTouchingNonswimmable();
    }

    @Redirect(method = "isCrawling", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isTouchingWater()Z"))
    public boolean crawlFix(Entity instance) {
        return ((EntityAccessor) instance).fluidlib$isTouchingSwimmable();
    }

    @Inject(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isInLava()Z"))
    public void fallDamageMultiplier(CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        List<TagKey<Fluid>> allFluids = new ArrayList<>(FluidRegistry.getAll());

        for (TagKey<Fluid> fluid : allFluids) {
            if (fluidlib$isInFluid(fluid) && FluidRegistry.getFluids().containsKey(fluid)) {
                this.fallDistance *= FluidRegistry.getFluids().get(fluid).fallDamageMultiplier(entity);
                break;
            }
        }
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
        return FluidRegistry.getSwimmable().stream().anyMatch(instance::isIn);
    }


}

