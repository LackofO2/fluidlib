package lack.fluidlib.mixin.entity;

import lack.fluidlib.fluid.FluidProperties;
import lack.fluidlib.fluid.FluidRegistry;
import lack.fluidlib.mixinaccessor.EntityAccessor;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.DefaultMinecartController;
import net.minecraft.entity.vehicle.ExperimentalMinecartController;
import net.minecraft.entity.vehicle.MinecartController;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;
import java.util.Optional;

public class MinecartControllersMixin {
    @Debug(export = true)
    @Mixin(DefaultMinecartController.class)
    public abstract static class DefaultMinecartControllerMixin extends MinecartController {
        protected DefaultMinecartControllerMixin(AbstractMinecartEntity minecart) {
            super(minecart);
        }

        @Redirect(method = "moveOnRail", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;isTouchingWater()Z"))
        public boolean water(AbstractMinecartEntity instance) {
            return ((EntityAccessor) instance).fluidlib$isTouchingNonswimmable() || ((EntityAccessor) instance).fluidlib$isTouchingSwimmable();
        }

        @ModifyConstant(method = "moveOnRail", constant = @Constant(doubleValue = 0.2))
        public double modify2(double constant) {
            Optional<FluidProperties> fluidProperties = FluidRegistry.applyPredicateStream(entry -> ((EntityAccessor) minecart).fluidlib$isInFluid(entry.getKey())).map(Map.Entry::getValue).findFirst();
            if (fluidProperties.isPresent()) {
                constant *= fluidProperties.get().vehicleSpeedModifier();
            }
            return constant;
        }

        @Redirect(method = "getMaxSpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;isTouchingWater()Z"))
        public boolean water2(AbstractMinecartEntity instance) {
            return ((EntityAccessor) instance).fluidlib$isTouchingNonswimmable() || ((EntityAccessor) instance).fluidlib$isTouchingSwimmable();
        }

        @ModifyConstant(method = "getMaxSpeed", constant = @Constant(doubleValue = 0.2))
        public double modify(double constant) {
            Optional<FluidProperties> fluidProperties = FluidRegistry.applyPredicateStream(entry -> ((EntityAccessor) minecart).fluidlib$isInFluid(entry.getKey())).map(Map.Entry::getValue).findFirst();
            if (fluidProperties.isPresent()) {
                constant *= fluidProperties.get().vehicleSpeedModifier();
            }
            return constant;
        }
    }

    @Mixin(ExperimentalMinecartController.class)
    public abstract static class ExperimentalMinecartControllerMixin extends MinecartController {
        protected ExperimentalMinecartControllerMixin(AbstractMinecartEntity minecart) {
            super(minecart);
        }

        @Redirect(method = "applySlopeVelocity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;isTouchingWater()Z"))
        public boolean water(AbstractMinecartEntity instance) {
            return ((EntityAccessor) instance).fluidlib$isTouchingNonswimmable() || ((EntityAccessor) instance).fluidlib$isTouchingSwimmable();
        }

        @ModifyConstant(method = "applySlopeVelocity", constant = @Constant(doubleValue = 0.2))
        public double modify(double constant) {
            Optional<FluidProperties> fluidProperties = FluidRegistry.applyPredicateStream(entry -> ((EntityAccessor) minecart).fluidlib$isInFluid(entry.getKey())).map(Map.Entry::getValue).findFirst();
            if (fluidProperties.isPresent()) {
                constant *= fluidProperties.get().vehicleSpeedModifier();
            }
            return constant;
        }

        @Redirect(method = "getMaxSpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;isTouchingWater()Z"))
        public boolean water2(AbstractMinecartEntity instance) {
            return ((EntityAccessor) instance).fluidlib$isTouchingNonswimmable() || ((EntityAccessor) instance).fluidlib$isTouchingSwimmable();
        }

        @ModifyConstant(method = "getMaxSpeed", constant = @Constant(doubleValue = 0.5))
        public double modify2(double constant) {
            Optional<FluidProperties> fluidProperties = FluidRegistry.applyPredicateStream(entry -> ((EntityAccessor) minecart).fluidlib$isInFluid(entry.getKey())).map(Map.Entry::getValue).findFirst();
            if (fluidProperties.isPresent()) {
                constant *= fluidProperties.get().vehicleSpeedModifier();
            }
            return constant;
        }

    }
}
