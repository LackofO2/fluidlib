package lack.fluidlib.modifiers;

//
//public class AcidSpeedModifier implements ModFluid.Properties.SpeedModifier {
//    @Override
//    public boolean accept(LivingEntity livingEntity, boolean falling, double entityY, double effectiveGravity) {
//        return ((EntityAccessor) livingEntity).fluidlib$isInFluid(ModFluidTags.ACID);
//    }
//
//    @Override
//    public float applySpeedModifier(LivingEntity livingEntity, boolean falling, double entityY, double effectiveGravity, float speedModifier) {
//        return livingEntity.isSprinting() ? 0.9f : livingEntity.getBaseWaterMovementSpeedMultiplier();
//
//    }
//    @Override
//    public Vec3d applyVelocityModifier(LivingEntity livingEntity, boolean falling, double entityY, double effectiveGravity, Vec3d velocity, Vec3d movementInput) {
//        Vec3d newVelocity = velocity;
//        if (livingEntity.horizontalCollision && livingEntity.isClimbing()) {
//            newVelocity = new Vec3d(newVelocity.x, 0.2, newVelocity.z);
//        }
//
//        return newVelocity;
//    }
//}
