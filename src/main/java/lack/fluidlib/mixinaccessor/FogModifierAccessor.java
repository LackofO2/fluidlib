package lack.fluidlib.mixinaccessor;

import lack.fluidlib.fog.ModSubmersionType;
import lack.fluidlib.fog.ModSubmersionTypes;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;

public interface FogModifierAccessor {
    boolean fluidlib$shouldApply(@Nullable ModSubmersionType submersionType, Entity cameraEntity);

}
