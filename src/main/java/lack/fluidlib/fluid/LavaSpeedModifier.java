package lack.fluidlib.fluid;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.Vec3d;

public class LavaSpeedModifier implements FluidProperties.SpeedModifier {
    @Override
    public boolean accept(LivingEntity livingEntity, boolean falling, double entityY, double effectiveGravity) {
        return true;
    }

    @Override
    public void apply(LivingEntity livingEntity, boolean falling, double entityY, double effectiveGravity, Vec3d movementInput) {
        livingEntity.updateVelocity(0.02F, movementInput);
        livingEntity.move(MovementType.SELF, livingEntity.getVelocity());
        if (livingEntity.getFluidHeight(FluidTags.LAVA) <= livingEntity.getSwimHeight()) {
            livingEntity.setVelocity(livingEntity.getVelocity().multiply(0.5, 0.8F, 0.5));
            Vec3d vec3d2 = livingEntity.applyFluidMovingSpeed(effectiveGravity, falling, livingEntity.getVelocity());
            livingEntity.setVelocity(vec3d2);
        } else {
            livingEntity.setVelocity(livingEntity.getVelocity().multiply(0.5));
        }

        if (effectiveGravity != 0.0) {
            livingEntity.setVelocity(livingEntity.getVelocity().add(0.0, -effectiveGravity / 4.0, 0.0));
        }
    }
}
