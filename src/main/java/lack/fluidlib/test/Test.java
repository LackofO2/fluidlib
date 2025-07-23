package lack.fluidlib.test;

import lack.fluidlib.fog.ModSubmersionType;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.tag.TagKey;

public enum Test implements ModSubmersionType {
    ACID(ModFluidTags.ACID);

    private final TagKey<Fluid> fluidTag;

    Test(TagKey<Fluid> fluidTag) {
        this.fluidTag = fluidTag;
    }

    @Override
    public TagKey<Fluid> getFluidTag() {
        return fluidTag;
    }
}
