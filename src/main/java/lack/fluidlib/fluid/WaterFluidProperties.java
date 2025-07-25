package lack.fluidlib.fluid;

import net.minecraft.entity.Entity;

public class WaterFluidProperties extends FluidProperties {

    @Override
    public float fallDamageMultiplier(Entity entity) {
        return 0.0f;
    }
}
