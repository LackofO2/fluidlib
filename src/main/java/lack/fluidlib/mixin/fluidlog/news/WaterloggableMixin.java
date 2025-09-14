package lack.fluidlib.mixin.fluidlog.news;

import lack.fluidlib.registry.fluidlog.FluidLogProperties;
import lack.fluidlib.registry.fluidlog.FluidloggableFluid;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Objects;
import java.util.Optional;

@Mixin(Waterloggable.class)
public interface WaterloggableMixin {
    /**
     * @author lack
     * @reason .
     */
    @Overwrite
    default boolean canFillWithFluid(@Nullable LivingEntity filler, BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
        return true;
    }

    /**
     * @author lack
     * @reason .
     */
    @Overwrite
    default boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        Fluid fluid = fluidState.getFluid();
        if (Objects.equals(state.get(FluidLogProperties.FLUIDLOGGED), FluidloggableFluid.empty())) {
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


    /**
     * @author lack
     * @reason .
     */
    @Overwrite
    default ItemStack tryDrainFluid(@Nullable LivingEntity drainer, WorldAccess world, BlockPos pos, BlockState state) {
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

    /**
     * @author lack
     * @reason .
     */
    @Overwrite
    default Optional<SoundEvent> getBucketFillSound() {
        return Optional.empty();
    }


}
