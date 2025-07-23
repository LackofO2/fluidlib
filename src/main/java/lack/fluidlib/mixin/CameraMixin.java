package lack.fluidlib.mixin;

import lack.fluidlib.fog.ModSubmersionType;
import lack.fluidlib.fog.ModSubmersionTypes;
import lack.fluidlib.mixinaccessor.CameraAccessor;
import lack.fluidlib.registry.SubmersionPredicate;
import lack.fluidlib.registry.SubmersionTypeRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.Camera;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static lack.fluidlib.fog.ModSubmersionTypes.toModSubmersionType;

@Mixin(Camera.class)
public abstract class CameraMixin implements CameraAccessor {

    @Unique
    public <T extends ModSubmersionType> T fluidlib$getSubmersionType() {
        if (!this.ready) {
            return (toModSubmersionType(CameraSubmersionType.NONE));
        } else {
            FluidState fluidState = this.area.getFluidState(this.blockPos);
            if (fluidState.isIn(FluidTags.WATER) && this.pos.y < this.blockPos.getY() + fluidState.getHeight(this.area, this.blockPos)) {
                return toModSubmersionType(CameraSubmersionType.WATER);
            } else {
                Camera.Projection projection = this.getProjection();

                for (Vec3d vec3d : Arrays.asList(projection.center, projection.getBottomRight(), projection.getTopRight(), projection.getBottomLeft(), projection.getTopLeft())) {
                    Vec3d vec3d2 = this.pos.add(vec3d);
                    BlockPos blockPos = BlockPos.ofFloored(vec3d2);
                    FluidState fluidState2 = this.area.getFluidState(blockPos);
                    if (fluidState2.isIn(FluidTags.LAVA)) {
                        if (vec3d2.y <= fluidState2.getHeight(this.area, blockPos) + blockPos.getY()) {
                            return toModSubmersionType(CameraSubmersionType.LAVA);
                        }
                    } else {
                        Optional<Map.Entry<ModSubmersionType, SubmersionPredicate>> a = SubmersionTypeRegistry.getAll().entrySet().stream().filter(submersionPredicate -> submersionPredicate.getValue().test(this.area, vec3d2, blockPos, fluidState)).findFirst();
                        if (a.isPresent()) {
                            return (T) a.get().getKey();

                        } else {
                            BlockState blockState = this.area.getBlockState(blockPos);
                            if (blockState.isOf(Blocks.POWDER_SNOW)) {
                                return toModSubmersionType(CameraSubmersionType.POWDER_SNOW);
                            }
                        }
                    }
                }

                return toModSubmersionType(CameraSubmersionType.NONE);
            }
        }
    }



    @Shadow
    private BlockView area;

    @Shadow
    private boolean ready;

    @Shadow
    @Final
    private BlockPos.Mutable blockPos;

    @Shadow
    private Vec3d pos;

    @Shadow
    public abstract Camera.Projection getProjection();
}
