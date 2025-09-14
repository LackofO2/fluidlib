package lack.fluidlib.fluid;

import net.minecraft.block.Block;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.Item;

public record FluidExport(Block fluidBlock, FlowableFluid fluidStill, FlowableFluid fluidFlowing, Item bucket, Block cauldron) {

}
