package lack.fluidlib.mixin.fluidlog.news;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import lack.fluidlib.registry.fluidlog.FluidLogProperties;
import lack.fluidlib.registry.fluidlog.FluidloggableFluid;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Objects;
import java.util.Optional;

@Mixin(Block.class)
public abstract class WaterloggableBlocksMixin extends AbstractBlock {
    @Shadow
    public abstract BlockState getDefaultState();


    @Shadow
    @Final
    protected StateManager<Block, BlockState> stateManager;

    public WaterloggableBlocksMixin(AbstractBlock.Settings settings) {
        super(settings);
    }

    @ModifyVariable(method = "setDefaultState", at = @At("HEAD"), index = 1, argsOnly = true)
    public BlockState modify(BlockState value) {
        if (value.contains(Properties.WATERLOGGED)) {
//            value = value.with(FluidProperties.FLUIDLOGGED, FluidloggableFluid.empty());
            BlockState state = stateManager.getDefaultState();
            for (Property<?> property : value.getProperties()) {
                if (property == Properties.WATERLOGGED) {
                    state = state.with(Properties.WATERLOGGED, false);
                    state = state.with(FluidLogProperties.FLUIDLOGGED, FluidloggableFluid.empty());
                } else {
                    state = applyProperty(state, value, property);
                }
            }
            return state;
        }
        return value;
    }

    @ModifyReturnValue(method = "getPlacementState", at = @At("RETURN"))
    public BlockState modify2(BlockState value, @Local(argsOnly = true) ItemPlacementContext context) {
        if (value.contains(Properties.WATERLOGGED)) {
            BlockState state = getDefaultState();
            for (Property<?> property : value.getProperties()) {
                if (property == Properties.WATERLOGGED) {
                    state = state.with(Properties.WATERLOGGED, false);

                    FluidState fluidState = context.getWorld().getFluidState(context.getBlockPos());
                    Optional<FluidloggableFluid> fluidOptional = FluidLogProperties.FLUIDLOGGED.parse(fluidState.getFluid());
                    FluidloggableFluid fluid = fluidOptional.orElse(FluidloggableFluid.empty());
                    state = state.with(FluidLogProperties.FLUIDLOGGED, fluid);
                } else {
                    state = applyProperty(state, value, property);
                }

            }
            return state;
        }
        return value;
    }

    @Mixin(State.class)
    public static abstract class StateMixin<S> {
//        @Shadow
//        public abstract <T extends Comparable<T>, V extends T> S with(Property<T> property, V value);
//
//        @Shadow
//        public abstract <T extends Comparable<T>> T get(Property<T> property);
//
//        @Inject(method = "get(Lnet/minecraft/state/property/Property;)Ljava/lang/Comparable;", at = @At("HEAD"), cancellable = true)
//        public <T extends Comparable<T>> void init(Property<T> property, CallbackInfoReturnable<Boolean> cir) {
//            if (property == Properties.WATERLOGGED) {
//                cir.setReturnValue(!Objects.equals(get(FluidProperties.FLUIDLOGGED), FluidloggableFluid.empty()));
//            }
//        }
//
    }

    @Unique
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static BlockState applyProperty(BlockState target, BlockState source, Property<?> property) {

        return source.get(property) == null ? target : target.with((Property) property, source.get(property));
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        if (state.contains(FluidLogProperties.FLUIDLOGGED)) {

            FluidloggableFluid fluidloggableFluid = state.get(FluidLogProperties.FLUIDLOGGED);
            if (!Objects.equals(fluidloggableFluid, FluidloggableFluid.empty())) {
                if (fluidloggableFluid != null && fluidloggableFluid.fluid() != null) {
                    return fluidloggableFluid.fluid().getStill(false);
                } else return Fluids.WATER.getStill(false);
            } else return super.getFluidState(state);
        }
        return super.getFluidState(state);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (state.contains(FluidLogProperties.FLUIDLOGGED) && !state.get(FluidLogProperties.FLUIDLOGGED).isEmpty()) {
            tickView.scheduleFluidTick(pos, state.get(FluidLogProperties.FLUIDLOGGED).fluid(), state.get(FluidLogProperties.FLUIDLOGGED).fluid().getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    //

}
