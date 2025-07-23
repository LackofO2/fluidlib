//package lack.fluidlib.modifiers;
//
//import lack.fluidlib.fluid.ModFluid;
//import lack.fluidlib.mixinaccessor.EntityAccessor;
//import net.minecraft.entity.LivingEntity;
//import net.minecraft.registry.tag.FluidTags;
//import net.minecraft.util.math.Vec3d;
//
//public class LavaSpeedModifier implements ModFluid.Properties.SpeedModifier {
//    @Override
//    public boolean accept(LivingEntity livingEntity, boolean falling, double entityY, double effectiveGravity) {li
//
//        return (((EntityAccessor) livingEntity)).fluidlib$isInFluid(FluidTags.LAVA) && livingEntity.getFluidHeight(FluidTags.LAVA) <= livingEntity.getSwimHeight();
//    }
//
//    @Override
//    public Vec3d applyVelocityModifierAfter(LivingEntity livingEntity, boolean falling, double entityY, double effectiveGravity, Vec3d velocity, Vec3d movementInput) {
//        if (effectiveGravity != 0.0) {
//            return (livingEntity.getVelocity().add(0.0, -effectiveGravity / 4.0, 0.0));
//        }
//        return ModFluid.Properties.SpeedModifier.super.applyVelocityModifierAfter(livingEntity, falling, entityY, effectiveGravity, velocity, movementInput);
//    }
//
//    @Override
//    public float applySpeedModifier(LivingEntity livingEntity, boolean falling, double entityY, double effectiveGravity, float speedModifier) {
//        return 0.5f;
//    }
//
//}
