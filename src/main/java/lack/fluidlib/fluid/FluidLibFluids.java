package lack.fluidlib.fluid;

import lack.fluidlib.FluidLib;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class FluidLibFluids {

    public static FlowableFluid EMPTY = register("empty", new EmptyFlowableFluid());

    private static <T extends Fluid> T register(String id, T value) {
        return Registry.register(Registries.FLUID, Identifier.of(FluidLib.MOD_ID, id), value);
    }

}
