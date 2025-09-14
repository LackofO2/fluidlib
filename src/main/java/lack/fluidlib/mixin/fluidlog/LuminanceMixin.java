package lack.fluidlib.mixin.fluidlog;

import lack.fluidlib.registry.fluidlog.FluidLogProperties;
import lack.fluidlib.registry.fluidlog.FluidloggableFluid;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Optional;
import java.util.function.ToIntFunction;

@Mixin(Block.class)
public abstract class LuminanceMixin {

    @ModifyVariable(method = "<init>", at = @At("HEAD"), index = 1, argsOnly = true)
    private static AbstractBlock.Settings changeLuminanceToMatchFluidForFluidloggedBlocks(AbstractBlock.Settings value) {

        ToIntFunction<BlockState> currentLuminance = ((LuminanceAccessor) value).getLuminance();
        value.luminance(state -> {
            Optional<FluidloggableFluid> optionalFluidloggableFluid = state.getOrEmpty(FluidLogProperties.FLUIDLOGGED);
            if (optionalFluidloggableFluid.isPresent()) {
                FluidloggableFluid fluidloggableFluid = optionalFluidloggableFluid.get();
                if (!fluidloggableFluid.isEmpty() && fluidloggableFluid.fluid() != null) {
                    int fluidLuminance = fluidloggableFluid.fluidProperties.luminance();
                    return Math.max(fluidLuminance, currentLuminance.applyAsInt(state));

                }
            }
            return currentLuminance.applyAsInt(state);
        });
        return value;
    }
}
