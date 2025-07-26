package lack.fluidlib.example.test;

import net.minecraft.fluid.FlowableFluid;
import net.minecraft.util.Identifier;

import static lack.fluidlib.FluidLib.MOD_ID;
import static lack.fluidlib.example.FluidExample.ACID_BUILDER;

public class FFluids {
    public static void init() {

    }


    public static final FlowableFluid ACID_STILL = ACID_BUILDER.createStillFluid(Identifier.of(MOD_ID, "acid"));
    public static final FlowableFluid ACID_FLOWING = ACID_BUILDER.createFlowingFluid(Identifier.of(MOD_ID, "acid_flowing"));

}
