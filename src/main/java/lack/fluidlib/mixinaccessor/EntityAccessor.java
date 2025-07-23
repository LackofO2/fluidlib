package lack.fluidlib.mixinaccessor;

import lack.fluidlib.fluid.ModFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Optional;

public interface EntityAccessor {
    boolean fluidlib$isInFluid(TagKey<Fluid> fluid);

    boolean isInAnyFluid();
    boolean isInSwimmableFluid();
    boolean isSubmergedInSwimmableFluid();

    Optional<TagKey<Fluid>> getFirstFluid();

    List<TagKey<Fluid>> getAllFluidsTags();
    List<ModFluid> getAllModFluids();
    List<ModFluid> getSwimmableFluids();
//    void travelInFluid2(Vec3d movementInput);
}
