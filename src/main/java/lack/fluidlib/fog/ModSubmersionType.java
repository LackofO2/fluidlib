package lack.fluidlib.fog;

import net.minecraft.fluid.Fluid;
import net.minecraft.registry.tag.TagKey;

public interface ModSubmersionType {
    TagKey<Fluid> getFluidTag();
}
