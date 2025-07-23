package lack.fluidlib.test;

import net.minecraft.fluid.Fluid;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModFluidTags {
    public static final TagKey<Fluid> SWIMMABLE = of("swimmable");
    public static final TagKey<Fluid> NONSWIMMABLE = of("nonswimmable");
    public static final TagKey<Fluid> FLUIDS = of("fluids");
    public static final TagKey<Fluid> ACID = of("acid");

    private static TagKey<Fluid> of(String id) {
        return TagKey.of(RegistryKeys.FLUID, Identifier.ofVanilla(id));
    }


}
