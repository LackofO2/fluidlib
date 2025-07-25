package lack.fluidlib.example;

import lack.fluidlib.fluid.WaterFluidProperties;
import net.minecraft.entity.Entity;

public class AcidProperties extends WaterFluidProperties {

    @Override
    public float fallDamageMultiplier(Entity entity) {
        return 0.33f;
    }

    @Override
    public float boatSpeedModifier() {
        return 0.7f;
    }
}
