package lack.fluidlib.test;

import lack.fluidlib.FluidLib;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.util.Identifier;

public class FluidTestClient {
    public static void init() {

    }

    static {
        FluidRenderHandlerRegistry.INSTANCE.register(ExampleFluid.ACID_STILL, ExampleFluid.ACID_FLOWING, new SimpleFluidRenderHandler(
            Identifier.of(FluidLib.MOD_ID, "block/acid_still"),
            Identifier.of(FluidLib.MOD_ID, "block/acid_flow")
        ));
        BlockRenderLayerMap.putFluids(BlockRenderLayer.TRANSLUCENT, ExampleFluid.ACID_STILL, ExampleFluid.ACID_FLOWING);

    }
}
