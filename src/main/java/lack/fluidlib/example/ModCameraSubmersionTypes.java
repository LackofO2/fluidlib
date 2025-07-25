package lack.fluidlib.example;

import lack.fluidlib.fog.ModSubmersionType;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.tag.TagKey;

public enum ModCameraSubmersionTypes implements ModSubmersionType {
    ACID(FluidExample.ACID_TAG);

    private final TagKey<Fluid> fluidTag;

    ModCameraSubmersionTypes(TagKey<Fluid> fluidTag) {
        this.fluidTag = fluidTag;
    }

    @Override
    public TagKey<Fluid> getFluidTag() {
        return fluidTag;
    }
}
