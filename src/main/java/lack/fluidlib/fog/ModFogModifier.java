package lack.fluidlib.fog;

import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.fog.FogModifier;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;

public abstract class ModFogModifier extends FogModifier {


    @Override
    public boolean shouldApply(@Nullable CameraSubmersionType submersionType, Entity cameraEntity) {
        return false;
    }

    public abstract boolean shouldApply(@Nullable ModSubmersionType submersionType, Entity cameraEntity);

}
