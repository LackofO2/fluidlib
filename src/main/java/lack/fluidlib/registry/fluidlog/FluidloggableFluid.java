package lack.fluidlib.registry.fluidlog;

import lack.fluidlib.fluid.FluidLibFluids;
import lack.fluidlib.fluid.FluidProperties;
import lack.fluidlib.fluid.LavaFluidProperties;
import lack.fluidlib.fluid.WaterFluidProperties;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public class FluidloggableFluid implements StringIdentifiable, Comparable<FluidloggableFluid> {
    public final FluidProperties fluidProperties;
    private static final FluidloggableFluid EMPTY = new FluidloggableFluid(FluidLibFluids.EMPTY, new FluidProperties() {
    });
    private static final FluidloggableFluid WATER = new FluidloggableFluid(Fluids.WATER, new WaterFluidProperties());
    private static final FluidloggableFluid LAVA = new FluidloggableFluid(Fluids.LAVA, new LavaFluidProperties());

    private final FlowableFluid fluid;

    private FluidloggableFluid(FlowableFluid fluid) {
        this(fluid, new WaterFluidProperties());
    }

    private FluidloggableFluid(FlowableFluid fluid, FluidProperties fluidProperties) {

        this.fluid = fluid;
        this.fluidProperties = fluidProperties;
    }

    public static FluidloggableFluid of(FlowableFluid fluid) {
        return of(fluid, new WaterFluidProperties());
    }

    public static FluidloggableFluid of(FlowableFluid fluid, FluidProperties fluidProperties) {
        if (fluid == null) return EMPTY;
        return new FluidloggableFluid(fluid, fluidProperties);
    }

    public static FluidloggableFluid empty() {
        return EMPTY;
    }

    public static FluidloggableFluid water() {
        return WATER;
    }

    public static FluidloggableFluid lava() {
        return LAVA;
    }

    public boolean isEmpty() {
        return Objects.equals(this, EMPTY);
    }

    public FlowableFluid fluid() {
        return fluid;
    }


    public Fluid still() {
        return fluid.getStill();
    }

    public Fluid flowing() {
        return fluid.getFlowing();
    }

    public Item bucket() {
        return fluid != null ? fluid.getBucketItem() : null;
    }

    @Override
    public String asString() {
        if (fluid == null) return "empty";
        Identifier id = Registries.FLUID.getId(fluid);
        return id.getPath();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FluidloggableFluid other)) return false;
        return Objects.equals(this.fluid, other.fluid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fluid);
    }

    @Override
    public String toString() {
        return "FluidloggableFluid[" + asString() + "]";
    }

    @Override
    public int compareTo(@NotNull FluidloggableFluid o) {
        return this.asString().compareTo(o.asString());
    }
}
