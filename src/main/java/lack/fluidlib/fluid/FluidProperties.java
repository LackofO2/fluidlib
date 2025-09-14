package lack.fluidlib.fluid;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

public abstract class FluidProperties {

    public int luminance() {
        return 0;
    }

    public boolean canSwim() {
        return true;
    }

    public boolean suffocates(Entity entity) {
        return true;
    }

    public boolean fluidLoggable() {
        return true;
    }

    public float suffocationSpeed(Entity entity) {
        return 1.0f;
    }

    public float flyingEntitySpeed(Entity entity) {
        return ModFluidConstants.WATER_FLYING_SPEED;
    }

    public double entityMovementSpeed(Entity entity) {
        return ModFluidConstants.WATER_ENTITY_SPEED;
    }

    public float entityDrag(Entity entity) {
        return ModFluidConstants.WATER_DRAG;
    }

    public float fallDamageMultiplier(Entity entity) {
        return 1.0f;
    }

    public boolean boatsFloatIn() {
        return true;
    }

    public float vehicleSpeedModifier() {
        return 1.0f;
    }

    public SpeedModifier speedModifier() {
        return new WaterSpeedModifier();
    }

    public interface SpeedModifier {
        boolean accept(LivingEntity livingEntity, boolean falling, double entityY, double effectiveGravity);

        void apply(LivingEntity livingEntity, boolean falling, double entityY, double effectiveGravity, Vec3d movementInput);
    }
}
