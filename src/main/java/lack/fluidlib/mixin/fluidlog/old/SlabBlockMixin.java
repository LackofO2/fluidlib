package lack.fluidlib.mixin.fluidlog.old;

import com.llamalad7.mixinextras.sugar.Local;
import lack.fluidlib.registry.fluidlog.FluidLogProperties;
import lack.fluidlib.registry.fluidlog.FluidProperty;
import lack.fluidlib.registry.fluidlog.FluidloggableFluid;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.Optional;

@Mixin(SlabBlock.class)
public abstract class SlabBlockMixin extends Block implements Waterloggable {
    @Shadow
    @Final
    public static BooleanProperty WATERLOGGED;
    @Shadow
    @Final
    public static EnumProperty<SlabType> TYPE;
    @Unique
    private static final FluidProperty FLUIDLOGGED = FluidLogProperties.FLUIDLOGGED;

    public SlabBlockMixin(Settings settings) {
        super(settings);
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;with(Lnet/minecraft/state/property/Property;Ljava/lang/Comparable;)Ljava/lang/Object;"))
    private <T extends Comparable<T>> Object redirectWaterlogged1(BlockState self, Property<T> property, T value) {
        if (property == WATERLOGGED) {
            return self.with(FLUIDLOGGED, FluidloggableFluid.empty());
        }
        return self.with(property, value);
    }

    @Redirect(method = "getPlacementState", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;with(Lnet/minecraft/state/property/Property;Ljava/lang/Comparable;)Ljava/lang/Object;"))
    private <T extends Comparable<T>> Object redirectWaterlogged2(BlockState self, Property<T> property, T value, @Local(argsOnly = true) ItemPlacementContext context, @Local BlockPos pos) {
        if (property == WATERLOGGED) {
            FluidState fluidState = context.getWorld().getFluidState(pos);
            Optional<FluidloggableFluid> fluidOptional = FLUIDLOGGED.parse(fluidState.getFluid());
            FluidloggableFluid fluid = fluidOptional.orElse(FluidloggableFluid.empty());
            return self.with(FLUIDLOGGED, fluid);
        }
        return self.with(property, value);
    }

    @Redirect(method = "getStateForNeighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;get(Lnet/minecraft/state/property/Property;)Ljava/lang/Comparable;"))
    private <T extends Comparable<T>> Comparable redirectWaterlogged3(BlockState instance, Property<T> property) {
        if (property == WATERLOGGED) {
            return false;
        }
        return instance.get(property);
    }

    @Inject(method = "getStateForNeighborUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;get(Lnet/minecraft/state/property/Property;)Ljava/lang/Comparable;"))
    private void redirectWaterlogged4(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random, CallbackInfoReturnable<BlockState> cir) {
        if (!state.get(FLUIDLOGGED).isEmpty()) {
            tickView.scheduleFluidTick(pos, state.get(FLUIDLOGGED).fluid(), state.get(FLUIDLOGGED).fluid().getTickRate(world));
        }
    }

    @Redirect(method = "appendProperties", at = @At(value = "INVOKE", target = "Lnet/minecraft/state/StateManager$Builder;add([Lnet/minecraft/state/property/Property;)Lnet/minecraft/state/StateManager$Builder;"))
    private StateManager.Builder<?, ?> redirectWaterlogged5(StateManager.Builder<?, ?> instance, Property<?>[] properties) {
        Property<?>[] modifiedProperties = new Property<?>[properties.length];

        for (int i = 0; i < properties.length; i++) {
            if (properties[i] == WATERLOGGED) {
                modifiedProperties[i] = FLUIDLOGGED;
            } else modifiedProperties[i] = properties[i];
        }

        return instance.add(modifiedProperties);
    }

    /**
     * @author a
     * @reason a
     */

    @Overwrite
    public FluidState getFluidState(BlockState state) {
        FluidloggableFluid fluidloggableFluid = state.get(FLUIDLOGGED);
        return !Objects.equals(fluidloggableFluid, FluidloggableFluid.empty()) ?
            fluidloggableFluid != null && fluidloggableFluid.fluid() != null ? fluidloggableFluid.fluid().getStill(false) :
                Fluids.WATER.getStill(false) : super.getFluidState(state);
    }


    /**
     * @author lack
     * @reason a
     */
    @Overwrite
    public boolean canFillWithFluid(@Nullable LivingEntity filler, BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
        return state.get(TYPE) != SlabType.DOUBLE ;
    }

    /**
     * @author lack
     * @reason a
     */
    @Overwrite
    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        if (state.get(TYPE) == SlabType.DOUBLE) {
            return false;
        }
        Fluid fluid = fluidState.getFluid();

        if (state.get(FluidLogProperties.FLUIDLOGGED).isEmpty()) {
            if (!world.isClient()) {
                Optional<FluidloggableFluid> flowableFluid = FluidLogProperties.FLUIDLOGGED.parse(fluid);
                if (flowableFluid.isPresent()) {
                    world.setBlockState(pos, state.with(FluidLogProperties.FLUIDLOGGED, flowableFluid.get()), Block.NOTIFY_ALL);
                    world.scheduleFluidTick(pos, fluidState.getFluid(), fluidState.getFluid().getTickRate(world));
                }

            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public ItemStack tryDrainFluid(@Nullable LivingEntity drainer, WorldAccess world, BlockPos pos, BlockState state) {
        FluidloggableFluid fluidloggableFluid = state.get(FluidLogProperties.FLUIDLOGGED);
        if (!Objects.equals(fluidloggableFluid, FluidloggableFluid.empty())) {
            world.setBlockState(pos, state.with(FluidLogProperties.FLUIDLOGGED, FluidloggableFluid.empty()), Block.NOTIFY_ALL);
            if (!state.canPlaceAt(world, pos)) {
                world.breakBlock(pos, true);
            }

            return new ItemStack(fluidloggableFluid.bucket());
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public Optional<SoundEvent> getBucketFillSound() {
        //todo
        return Optional.empty();
    }

}
