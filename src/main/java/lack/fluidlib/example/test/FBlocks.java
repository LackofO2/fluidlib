package lack.fluidlib.example.test;

import lack.fluidlib.cauldron.CauldronBehaviorBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import static lack.fluidlib.FluidLib.MOD_ID;
import static lack.fluidlib.example.FluidExample.ACID_BUILDER;

public class FBlocks {
    public static void init() {

    }

    public static final Block ACID = ACID_BUILDER.createBlock(Identifier.of(MOD_ID, "acid"), AbstractBlock.Settings.create()
        .mapColor(MapColor.GREEN).replaceable().noCollision()
        .ticksRandomly().strength(100.0F).luminance(state -> 3)
        .pistonBehavior(PistonBehavior.DESTROY).dropsNothing()
        .liquid().sounds(BlockSoundGroup.INTENTIONALLY_EMPTY));
    static CauldronBehaviorBuilder behaviorBuilder = CauldronBehaviorBuilder.create("acid", Fitems.ACID_BUCKET);
    public static final Block ACID_CAULDRON = ACID_BUILDER.createCauldron(Identifier.of(MOD_ID, "acid_cauldron"), AbstractBlock.Settings
        .copyShallow(Blocks.CAULDRON), behaviorBuilder);
}
