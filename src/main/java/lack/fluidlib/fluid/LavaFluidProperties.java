package lack.fluidlib.fluid;

import net.minecraft.entity.Entity;

public class LavaFluidProperties extends FluidProperties {
    @Override
    public int luminance() {
        return 15;
    }

    @Override
    public boolean canSwim() {
        return false;
    }

    @Override
    public boolean suffocates(Entity entity) {
        return false;
    }

    @Override
    public float flyingEntitySpeed(Entity entity) {
        return ModFluidConstants.LAVA_FLYING_SPEED;
    }

    @Override
    public double entityMovementSpeed(Entity entity) {
        return entity.getWorld().getDimension().ultrawarm() ? ModFluidConstants.LAVA_NETHER_ENTITY_SPEED : ModFluidConstants.LAVA_ENTITY_SPEED;
    }

    @Override
    public float entityDrag(Entity entity) {
        return ModFluidConstants.LAVA_DRAG;
    }

    @Override
    public float fallDamageMultiplier(Entity entity) {
        return 0.5f;
    }

    @Override
    public boolean boatsFloatIn() {
        return false;
    }

    @Override
    public float vehicleSpeedModifier() {
        return 0.0f;
    }

    @Override
    public SpeedModifier speedModifier() {
        return new LavaSpeedModifier();
    }

}
