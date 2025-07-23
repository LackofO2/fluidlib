package lack.fluidlib.registry;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;

@FunctionalInterface
public interface SubmersionPredicate {
    boolean test(BlockView blockView, Vec3d vec3d, BlockPos pos, FluidState fluidState);
}
