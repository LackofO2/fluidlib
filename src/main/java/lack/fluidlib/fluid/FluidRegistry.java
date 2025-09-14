package lack.fluidlib.fluid;

import net.minecraft.fluid.Fluid;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class FluidRegistry {

    private static final Map<TagKey<Fluid>, FluidProperties> FLUIDS = new ConcurrentHashMap<>();

    static {
        FLUIDS.put(FluidTags.WATER, new WaterFluidProperties());
        FLUIDS.put(FluidTags.LAVA, new LavaFluidProperties());
    }

    static void addToFluids(TagKey<Fluid> fluidTagKey, FluidProperties fluidProperties) {
        FLUIDS.put(fluidTagKey, fluidProperties);
    }

    public static Map<TagKey<Fluid>, FluidProperties> getFluids() {
        return Collections.unmodifiableMap(FLUIDS);
    }


    private static final Predicate<Map.Entry<TagKey<Fluid>, FluidProperties>> IS_SWIMMABLE = entry -> entry.getValue().canSwim();

    public static Map<TagKey<Fluid>, FluidProperties> getSwimmableMap() {
        return applyPredicateMap(IS_SWIMMABLE);
    }

    public static Set<TagKey<Fluid>> getSwimmableSet() {
        return getSwimmableMap().keySet();
    }

    public static Map<TagKey<Fluid>, FluidProperties> getNonswimmableMap() {
        return applyPredicateMap(IS_SWIMMABLE.negate());
    }

    public static Set<TagKey<Fluid>> getNonswimmableSet() {
        return getNonswimmableMap().keySet();
    }


    public static Map<TagKey<Fluid>, FluidProperties> applyPredicateMap(Predicate<Map.Entry<TagKey<Fluid>, FluidProperties>> predicate) {
        return applyPredicateStream(predicate).collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static Stream<Map.Entry<TagKey<Fluid>, FluidProperties>> applyPredicateStream(Predicate<Map.Entry<TagKey<Fluid>, FluidProperties>> predicate) {
        return getFluids().entrySet().stream().filter(predicate);
    }


}
