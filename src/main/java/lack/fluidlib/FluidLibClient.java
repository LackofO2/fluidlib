package lack.fluidlib;

import lack.fluidlib.test.FluidTestClient;
import net.fabricmc.api.ClientModInitializer;

public class FluidLibClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FluidTestClient.init();
    }
}
