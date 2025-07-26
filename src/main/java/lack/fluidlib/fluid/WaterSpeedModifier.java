package lack.fluidlib.fluid;

import lack.fluidlib.mixinaccessor.EntityAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.Vec3d;

public class WaterSpeedModifier implements FluidProperties.SpeedModifier {
    @Override
    public boolean accept(LivingEntity livingEntity, boolean falling, double entityY, double effectiveGravity) {
        return ((EntityAccessor) livingEntity).fluidlib$isInFluid(FluidTags.WATER);
    }

    @Override
    public void apply(LivingEntity livingEntity, boolean falling, double entityY, double effectiveGravity, Vec3d movementInput) {
        float speed = livingEntity.isSprinting() ? 0.9F : livingEntity.getBaseWaterMovementSpeedMultiplier();
        float base = 0.02F;
        float waterMovement = (float) livingEntity.getAttributeValue(EntityAttributes.WATER_MOVEMENT_EFFICIENCY);
        if (!livingEntity.isOnGround()) {
            waterMovement *= 0.5F;
        }

        if (waterMovement > 0.0F) {
            speed += (0.54600006F - speed) * waterMovement;
            base += (livingEntity.getMovementSpeed() - base) * waterMovement;
        }

        if (livingEntity.hasStatusEffect(StatusEffects.DOLPHINS_GRACE)) {
            speed = 0.96F;
        }

        livingEntity.updateVelocity(base, movementInput);
        livingEntity.move(MovementType.SELF, livingEntity.getVelocity());
        Vec3d vec3d = livingEntity.getVelocity();
        if (livingEntity.horizontalCollision && livingEntity.isClimbing()) {
            vec3d = new Vec3d(vec3d.x, 0.2, vec3d.z);
        }

        vec3d = vec3d.multiply(speed, 0.8F, speed);
        livingEntity.setVelocity(livingEntity.applyFluidMovingSpeed(effectiveGravity, falling, vec3d));

        Vec3d vec3d2 = livingEntity.getVelocity();
        if (livingEntity.horizontalCollision && livingEntity.doesNotCollide(vec3d2.x, vec3d2.y + 0.6F - livingEntity.getY() + entityY, vec3d2.z)) {
            livingEntity.setVelocity(vec3d2.x, 0.3F, vec3d2.z);
        }
    }

}
