package lack.fluidlib.example;

import lack.fluidlib.cauldron.CauldronBehaviorBuilder;
import lack.fluidlib.fluid.FluidBuilder;
import lack.fluidlib.fog.ModFogCreator;
import lack.fluidlib.registry.SubmersionTypeRegistry;
import net.minecraft.block.*;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.FluidModificationItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static lack.fluidlib.FluidLib.MOD_ID;

public class FluidExample {

    public static void init() {
    }

    public static final TagKey<Fluid> ACID_TAG;
    public static final FlowableFluid ACID_STILL;
    public static final FlowableFluid ACID_FLOWING;
    public static final Block ACID;
    public static final Block ACID_CAULDRON;
    public static final Item ACID_BUCKET;


    static {
        ACID_TAG = TagKey.of(RegistryKeys.FLUID, Identifier.ofVanilla("acid"));
        FluidBuilder acidBuilder = new FluidBuilder(new AcidFluid.Still(),
            new AcidFluid.Flowing(), new AcidProperties(), ACID_TAG);
        ACID_STILL = acidBuilder.createStillFluid(Identifier.of(MOD_ID, "acid"));
        ACID_FLOWING = acidBuilder.createFlowingFluid(Identifier.of(MOD_ID, "acid_flowing"));

        ACID = acidBuilder.createBlock(Identifier.of(MOD_ID, "acid"), AbstractBlock.Settings.create()
            .mapColor(MapColor.GREEN).replaceable().noCollision()
            .ticksRandomly().strength(100.0F).luminance(state -> 3)
            .pistonBehavior(PistonBehavior.DESTROY).dropsNothing()
            .liquid().sounds(BlockSoundGroup.INTENTIONALLY_EMPTY));
        ACID_BUCKET = acidBuilder.createBucket(Identifier.of(MOD_ID, "acid_bucket"), new Item.Settings()
            .recipeRemainder(Items.BUCKET).maxCount(1));
        CauldronBehaviorBuilder behaviorBuilder = CauldronBehaviorBuilder.create("acid", ACID_BUCKET);
        ACID_CAULDRON = acidBuilder.createCauldron(Identifier.of(MOD_ID, "acid_cauldron"), AbstractBlock.Settings
            .copyShallow(Blocks.CAULDRON), behaviorBuilder);

        ModFogCreator.create(new AcidFogModifier());

        SubmersionTypeRegistry.register(ModCameraSubmersionTypes.ACID, (blockView, vec3d, blockPos, fluidState) -> {
            if (fluidState.isIn(ModCameraSubmersionTypes.ACID.getFluidTag())) {
                return vec3d.y <= fluidState.getHeight(blockView, blockPos) + blockPos.getY();
            }
            return false;
        });
        DispenserBehavior acidBucketBehavior = new ItemDispenserBehavior() {
            private final ItemDispenserBehavior fallback = new ItemDispenserBehavior();

            @Override
            public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                FluidModificationItem fluidModificationItem = (FluidModificationItem) stack.getItem();
                BlockPos blockPos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING));
                World world = pointer.world();
                if (fluidModificationItem.placeFluid(null, world, blockPos, null)) {
                    fluidModificationItem.onEmptied(null, world, stack, blockPos);
                    return this.decrementStackWithRemainder(pointer, stack, new ItemStack(Items.BUCKET));
                } else {
                    return this.fallback.dispense(pointer, stack);
                }
            }
        };
        DispenserBlock.registerBehavior(ACID_BUCKET, acidBucketBehavior);

    }
}
