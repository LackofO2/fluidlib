package lack.fluidlib.fluid;

import lack.fluidlib.test.WaterSpeedModifier;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class ModFluid {

    private FlowableFluid fluid;
    private Identifier fluidId;
    private TagKey<Fluid> fluidTag;

    private Properties properties;


    private ModFluid() {

    }

    public static ModFluid create(FlowableFluid fluid, Identifier fluidId, TagKey<Fluid> fluidTag) {
        ModFluid modFluid = new ModFluid();
        modFluid.fluid = fluid;
        modFluid.fluidId = fluidId;
        modFluid.fluidTag = fluidTag;
        modFluid.properties = new Properties();
        return modFluid;
    }

    public FlowableFluid getFluid() {
        return fluid;
    }

    public TagKey<Fluid> getFluidTag() {
        return fluidTag;
    }

    public boolean canSwimIn() {
        return properties.canSwim;
    }

    public void setCanSwim(boolean canswim) {
        properties.canSwim = canswim;
    }

    public Identifier getFluidId() {
        return fluidId;
    }

    public int getViscosity() {
        return properties.viscosity;
    }

    public ModFluid setViscosity(int viscosity) {
        properties.viscosity = viscosity;
        return this;
    }

    public float getFlyingEntitySpeed() {
        return properties.flyingEntitySpeed;
    }

    public ModFluid setFlyingEntitySpeed(float viscosity) {
        properties.flyingEntitySpeed = viscosity;
        return this;
    }

    public float getEntitySpeed() {
        return properties.entityMovementSpeed;
    }

    public ModFluid setEntitySpeed(float viscosity) {
        properties.entityMovementSpeed = viscosity;
        return this;
    }

    public float getEntityDrag() {
        return properties.entityDrag;
    }

    public ModFluid setEntityDrag(float viscosity) {
        properties.entityDrag = viscosity;
        return this;
    }

    public float getFallDamageMultiplier() {
        return properties.fallDamageMultiplier;
    }

    public ModFluid setFallDamageMultiplier(float fallDamageMultiplier) {
        this.properties.fallDamageMultiplier = fallDamageMultiplier;
        return this;
    }

    public ModFluid setBoatsFloat(boolean boatsFloat) {
        this.properties.boatsFloat = boatsFloat;
        return this;
    }

    public boolean doBoatsFloat() {
        return properties.boatsFloat;
    }

    public ModFluid setBoatSpeedModifier(float boatSpeedModifier) {
        this.properties.boatSpeedModifier = boatSpeedModifier;
        return this;
    }

    public float getBoatSpeedModifier() {
        return properties.boatSpeedModifier;
    }
    public Properties.SpeedModifier getSpeedModifier() {
        return properties.speedModifier;
    }

    public void setSpeedModifier(Properties.SpeedModifier speedModifier) {
        this.properties.speedModifier = speedModifier;
    }
    public static class Properties {


        private boolean canSwim = true;
        private int viscosity = FluidConstants.WATER_VISCOSITY;

        private float flyingEntitySpeed = ModFluidConstants.WATER_FLYING_SPEED;
        private float entityMovementSpeed = ModFluidConstants.WATER_ENTITY_SPEED;
        private float entityDrag = ModFluidConstants.WATER_DRAG;

        private float fallDamageMultiplier = 1.0f;
        private boolean boatsFloat = false;
        private float boatSpeedModifier = 1.0f;

        private SpeedModifier speedModifier = new WaterSpeedModifier();


        public interface SpeedModifier {
            boolean accept(LivingEntity livingEntity, boolean falling, double entityY, double effectiveGravity);

            void apply(LivingEntity livingEntity, boolean falling, double entityY, double effectiveGravity, Vec3d movementInput);

        }
    }
}
