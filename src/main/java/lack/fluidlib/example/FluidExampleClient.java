package lack.fluidlib.example;

import lack.fluidlib.FluidLib;
import lack.fluidlib.example.test.FFluids;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.util.Identifier;

public class FluidExampleClient {
    public static void init() {

    }

    static {
        FluidRenderHandlerRegistry.INSTANCE.register(FFluids.ACID_STILL, FFluids.ACID_FLOWING, new SimpleFluidRenderHandler(
            Identifier.of(FluidLib.MOD_ID, "block/acid_still"),
            Identifier.of(FluidLib.MOD_ID, "block/acid_flow")
        ));
        BlockRenderLayerMap.putFluids(BlockRenderLayer.TRANSLUCENT, FFluids.ACID_STILL, FFluids.ACID_FLOWING);

    }
}
