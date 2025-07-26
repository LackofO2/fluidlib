package lack.fluidlib.example;


import lack.fluidlib.cauldron.CauldronBehaviorBuilder;
import lack.fluidlib.example.test.FBlocks;
import lack.fluidlib.example.test.FFluids;
import lack.fluidlib.example.test.Fitems;
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
        FFluids.init();
        FBlocks.init();
        Fitems.init();

    }

    public static final TagKey<Fluid> ACID_TAG = TagKey.of(RegistryKeys.FLUID, Identifier.ofVanilla("acid"));
    public static final FluidBuilder ACID_BUILDER = new FluidBuilder(new AcidFluid.Still(),
        new AcidFluid.Flowing(), new AcidProperties(), ACID_TAG);




    static {


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
        DispenserBlock.registerBehavior(Fitems.ACID_BUCKET, acidBucketBehavior);

    }
}
