package lack.fluidlib.cauldron;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ModdedCauldronBlock extends AbstractCauldronBlock {
    public static final MapCodec<ModdedCauldronBlock> CODEC =
        RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    createSettingsCodec(),
                    CauldronBehavior.CODEC.fieldOf("interactions").forGetter(block -> block.behaviorMap)
                )
                .apply(instance, ModdedCauldronBlock::new)
        );
    private static final VoxelShape FLUID_SHAPE = Block.createColumnShape(12.0, 4.0, 15.0);
    private static final VoxelShape INSIDE_COLLISION_SHAPE = VoxelShapes.union(AbstractCauldronBlock.OUTLINE_SHAPE, FLUID_SHAPE);

    public ModdedCauldronBlock(Settings settings, CauldronBehavior.CauldronBehaviorMap behaviorMap) {
        super(settings, behaviorMap);
    }

    @Override
    protected MapCodec<? extends AbstractCauldronBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected double getFluidHeight(BlockState state) {
        return 0.9375;
    }

    @Override
    public boolean isFull(BlockState state) {
        return true;
    }

    @Override
    protected VoxelShape getInsideCollisionShape(BlockState state, BlockView world, BlockPos pos, Entity entity) {
        return INSIDE_COLLISION_SHAPE;
    }

    @Override
    protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return 3;
    }

}
