package lack.fluidlib.mixin.fluidlog;

import com.llamalad7.mixinextras.sugar.Local;
import lack.fluidlib.registry.fluidlog.FluidLogProperties;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidFillable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Debug(export = true)
@Mixin(BucketItem.class)
public abstract class BucketItemMixin {
    @Shadow
    @Final
    private Fluid fluid;

    @Shadow
    protected abstract void playEmptyingSound(@Nullable LivingEntity user, WorldAccess world, BlockPos pos);


    /**
     * @param pos blockPos3
     */
    @ModifyVariable(method = "use", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/item/BucketItem;placeFluid(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/hit/BlockHitResult;)Z"),
        index = 10)
    public BlockPos replace(BlockPos pos, @Local BlockState blockState, @Local(ordinal = 0) BlockPos blockPos) {
        return redirectBlockPosForUse(blockState, blockPos, pos);
    }

    /**
     * @param pos blockPos3
     */
    @ModifyArg(method = "use", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/item/BucketItem;placeFluid(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/hit/BlockHitResult;)Z"),
        index = 2)
    public BlockPos replace2(BlockPos pos, @Local BlockState blockState, @Local(ordinal = 0) BlockPos blockPos) {
        return redirectBlockPosForUse(blockState, blockPos, pos);
    }

    @Unique
    private BlockPos redirectBlockPosForUse(BlockState blockState, BlockPos originalPos, BlockPos newPos) {
        return isBlockFitForFluidlogging(blockState) ? originalPos : newPos;
    }

    @Inject(method = "placeFluid", at = @At(value = "FIELD", target = "Lnet/minecraft/fluid/Fluids;WATER:Lnet/minecraft/fluid/FlowableFluid;"), cancellable = true)
    public void replace(LivingEntity user, World world, BlockPos pos, BlockHitResult hitResult, CallbackInfoReturnable<Boolean> cir, @Local BlockState blockState, @Local FlowableFluid flowableFluid) {
        if (isBlockFitForFluidlogging(blockState)) {
            if (blockState.get(FluidLogProperties.FLUIDLOGGED).isEmpty()) {
                ((FluidFillable) blockState.getBlock()).tryFillWithFluid(world, pos, blockState, flowableFluid.getStill(false));
                this.playEmptyingSound(user, world, pos);
                cir.setReturnValue(true);
            } else {
                cir.setReturnValue(false);
            }
        }
    }

    @Unique
    private boolean isBlockFitForFluidlogging(BlockState blockState) {
        return blockState.getBlock() instanceof FluidFillable &&
            blockState.contains(FluidLogProperties.FLUIDLOGGED) &&
            FluidLogProperties.FLUIDLOGGED.getValues().stream()
                .anyMatch(fluid1 -> fluid1.fluid() == this.fluid);
    }

}
