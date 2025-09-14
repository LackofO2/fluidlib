package lack.fluidlib.registry.fluidlog;

import net.minecraft.fluid.Fluid;
import net.minecraft.state.property.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class FluidProperty extends Property<FluidloggableFluid> {
    private static final List<FluidloggableFluid> VALUES = createValues();

    private static List<FluidloggableFluid> createValues() {
        List<FluidloggableFluid> fluids = new ArrayList<>();

        fluids.add(FluidloggableFluid.empty());
        fluids.add(FluidloggableFluid.water());
        fluids.add(FluidloggableFluid.lava());
        fluids.addAll(getCustomFluids());

        return Collections.unmodifiableList(fluids);
    }


    /**
     * do @ModifyReturnValue to add your modded custom fluid so it may fluidlog.
     *
     * @return A list of modded fluids.
     */
    public static List<FluidloggableFluid> getCustomFluids() {
        return new ArrayList<>();
    }

    protected FluidProperty(String name) {
        super(name, FluidloggableFluid.class);

    }

    public static FluidProperty of(String name) {
        return new FluidProperty(name);
    }

    @Override
    public List<FluidloggableFluid> getValues() {
        return VALUES;
    }

    @Override
    public String name(FluidloggableFluid value) {
        return value.asString();
    }

    @Override
    public Optional<FluidloggableFluid> parse(String name) {
        return VALUES.stream().filter(fluid -> fluid != null && fluid.fluid() != null && fluid.asString().equals(name)).findFirst();
    }

    public Optional<FluidloggableFluid> parse(Fluid fluid) {
        return VALUES.stream().filter(fluid1 -> fluid1 != null && fluid1.fluid() != null && fluid1.fluid().equals(fluid)).findFirst();
    }

    @Override
    public int ordinal(FluidloggableFluid value) {
        return VALUES.contains(value) ? VALUES.indexOf(value) : -1;
    }

}
