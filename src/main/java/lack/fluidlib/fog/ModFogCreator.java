package lack.fluidlib.fog;

import net.minecraft.client.render.fog.FogModifier;
import net.minecraft.client.render.fog.FogRenderer;

public class ModFogCreator {

    public static void create(FogModifier fogModifier) {
        FogRenderer.FOG_MODIFIERS.add(fogModifier);
    }
}
