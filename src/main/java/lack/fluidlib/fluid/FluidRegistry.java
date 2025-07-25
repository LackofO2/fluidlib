package lack.fluidlib.fluid;

import net.minecraft.fluid.Fluid;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;

import java.util.*;

public final class FluidRegistry {
    static final Map<TagKey<Fluid>, FluidProperties> FLUIDS = new LinkedHashMap<>();
    static final Set<TagKey<Fluid>> SWIMMABLE = new LinkedHashSet<>(Collections.singleton(FluidTags.WATER));
    static final Set<TagKey<Fluid>> NONSWIMMABLE = new LinkedHashSet<>(Collections.singleton(FluidTags.LAVA));

    public static Map<TagKey<Fluid>, FluidProperties> getFluids() {
        return Collections.unmodifiableMap(FLUIDS);
    }

    public static Set<TagKey<Fluid>> getSwimmable() {
        return Collections.unmodifiableSet(SWIMMABLE);
    }

    public static Set<TagKey<Fluid>> getNonswimmable() {
        return Collections.unmodifiableSet(NONSWIMMABLE);
    }

    public static Set<TagKey<Fluid>> getAll() {
        Set<TagKey<Fluid>> fluids = new HashSet<>(SWIMMABLE);
        fluids.addAll(NONSWIMMABLE);
        return Collections.unmodifiableSet(fluids);
    }
}
