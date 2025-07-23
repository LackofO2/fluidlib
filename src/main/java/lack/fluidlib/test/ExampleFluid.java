package lack.fluidlib.test;

import lack.fluidlib.cauldron.CauldronBehaviorBuilder;
import lack.fluidlib.fluid.FluidBuilder;
import lack.fluidlib.fluid.ModFluid;
import lack.fluidlib.fog.ModFogCreator;
import lack.fluidlib.registry.SubmersionTypeRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import static lack.fluidlib.FluidLib.MOD_ID;

public class ExampleFluid {
    public static void init() {

    }

    public static final FlowableFluid ACID_STILL;
    public static final FlowableFluid ACID_FLOWING;
    public static final Block ACID;
    public static final Block ACID_CAULDRON;
    public static final Item ACID_BUCKET;


    static {

        ModFluid acidFluid = ModFluid.create(new AcidFluid.Still(), Identifier.of(MOD_ID, "acid"), ModFluidTags.ACID)
            .setBoatSpeedModifier(0.7f);

        ModFluid acidFluidFlowing = ModFluid.create(new AcidFluid.Flowing(), Identifier.of(MOD_ID, "acid_flowing"), ModFluidTags.ACID)
            .setBoatSpeedModifier(0.7f);

        FluidBuilder acidBuilder = new FluidBuilder().fluidStill(acidFluid).fluidFlowing(acidFluidFlowing);
        ACID_STILL = acidBuilder.exportToStill();
        ACID_FLOWING = acidBuilder.exportToFlowing();

        ACID = acidBuilder.toBlock(AbstractBlock.Settings.create()
            .mapColor(MapColor.GREEN).replaceable().noCollision()
            .ticksRandomly().strength(100.0F).luminance(state -> 3)
            .pistonBehavior(PistonBehavior.DESTROY).dropsNothing()
            .liquid().sounds(BlockSoundGroup.INTENTIONALLY_EMPTY));
        ACID_BUCKET = acidBuilder.toBucket(Identifier.of(MOD_ID, "acid_bucket"), new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1));
        CauldronBehaviorBuilder behaviorBuilder = CauldronBehaviorBuilder.create("acid", ACID_BUCKET);
        ACID_CAULDRON = acidBuilder.toCauldron(Identifier.of(MOD_ID, "acid_cauldron"), behaviorBuilder);

        ModFogCreator.create(new AcidFogModifier());
        SubmersionTypeRegistry.register(Test.ACID, (blockView, vec3d, blockPos, fluidState) -> {
            if (fluidState.isIn(Test.ACID.getFluidTag())) {
                return vec3d.y <= fluidState.getHeight(blockView, blockPos) + blockPos.getY();
            }
            return false;
        });

    }
}
