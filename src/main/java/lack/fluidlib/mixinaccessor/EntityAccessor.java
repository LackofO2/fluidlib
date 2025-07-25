package lack.fluidlib.mixinaccessor;

import net.minecraft.fluid.Fluid;
import net.minecraft.registry.tag.TagKey;

import java.util.List;

public interface EntityAccessor {
    boolean fluidlib$isInFluid(TagKey<Fluid> fluid);

    boolean fluidlib$isSubmergedInSwimmable();

    boolean fluidlib$isTouchingSwimmable();

    boolean fluidlib$isSubmergedInNonswimmable();

    boolean fluidlib$isTouchingNonswimmable();

    boolean isInAnyFluid();

    List<TagKey<Fluid>> getAllFluidsTags();

//    void travelInFluid2(Vec3d movementInput);
}
