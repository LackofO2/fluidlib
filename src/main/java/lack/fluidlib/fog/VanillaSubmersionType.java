package lack.fluidlib.fog;

import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.tag.TagKey;

public record VanillaSubmersionType(CameraSubmersionType cameraSubmersionType) implements ModSubmersionType {

    @Override
    public TagKey<Fluid> getFluidTag() {
        return null;
    }
}
