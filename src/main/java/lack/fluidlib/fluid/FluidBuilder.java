package lack.fluidlib.fluid;

import lack.fluidlib.cauldron.CauldronBehaviorBuilder;
import lack.fluidlib.cauldron.ModdedCauldronBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.function.BiFunction;
import java.util.function.Function;

public class FluidBuilder {
    public final FluidProperties fluidProperties;
    private final TagKey<Fluid> fluidTagKey;

    public FluidBuilder(FluidProperties fluidProperties, TagKey<Fluid> fluidTagKey) {
        this.fluidProperties = fluidProperties;
        this.fluidTagKey = fluidTagKey;
    }

    /**
     * Must be registered first.
     * Used to register still fluids & adds to the fluid registry.
     */
    public FlowableFluid createStillFluid(FlowableFluid still, Identifier identifier) {
        addToFluidRegistry();
//        addToFluidloggableProperty(still);
        return registerFluid(still, identifier);
    }

    /**
     * Must be registered second.
     * Used to register flowing fluids.
     */
    public FlowableFluid createFlowingFluid(FlowableFluid flowing, Identifier identifier) {
        return registerFluid(flowing, identifier);
    }

    private FlowableFluid registerFluid(FlowableFluid fluid, Identifier identifier) {
        return Registry.register(Registries.FLUID, identifier, fluid);
    }

    private void addToFluidRegistry() {
//        if (fluidProperties.canSwim()) {
//            FluidRegistry.SWIMMABLE.add(fluidTagKey);
//        } else {
//            FluidRegistry.NONSWIMMABLE.add(fluidTagKey);
//        }

        FluidRegistry.addToFluids(fluidTagKey, fluidProperties);
    }
//
//    private void addToFluidloggableProperty(FlowableFluid still) {
//        if (fluidProperties.fluidLoggable()) {
//            FluidProperty.addToValues(FluidloggableFluid.of(still, fluidProperties));
//        }
//    }

    /**
     * Must be registered third.
     * Used to register block for fluids.
     */
    public Block createBlock(Identifier id, FlowableFluid fluid, AbstractBlock.Settings settings) {
        return registerBlock(id, settings1 -> new FluidBlock(fluid, settings), settings);
    }

    /**
     * Must be registered fourth.
     * Used to register fluid buckets.
     */
    public Item createBucket(Identifier bucketId, FlowableFluid fluid, Item.Settings settings) {
        return registerBucket(bucketId, settings1 -> new BucketItem(fluid, settings), settings);
    }

    public static Block registerBlock(Identifier id, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, id);
        Block block = factory.apply(settings.registryKey(key));
        return Registry.register(Registries.BLOCK, key, block);
    }

    public static Item registerBucket(Identifier id, Function<Item.Settings, Item> factory, Item.Settings settings) {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
        Item item = factory.apply(settings.registryKey(key));
        if (item instanceof BlockItem blockItem) {
            blockItem.appendBlocks(Item.BLOCK_ITEMS, item);
        }
        return Registry.register(Registries.ITEM, key, item);
    }

    /**
     * Must be registered fifth.
     * Used to register block for fluids.
     */
    public Block createCauldron(Identifier id, AbstractBlock.Settings settings, CauldronBehaviorBuilder cauldronBehaviorBuilder, BiFunction<AbstractBlock.Settings, CauldronBehavior.CauldronBehaviorMap, Block> cauldronFactory) {
        return registerCauldron(id, settings1 -> cauldronFactory.apply(settings, cauldronBehaviorBuilder.export()), settings, cauldronBehaviorBuilder);
    }

    public Block createCauldron(Identifier id, AbstractBlock.Settings settings, CauldronBehaviorBuilder cauldronBehaviorBuilder) {
        return createCauldron(id, settings, cauldronBehaviorBuilder, ModdedCauldronBlock::new);
    }

    public Block registerCauldron(Identifier id, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings, CauldronBehaviorBuilder cauldronBehaviorBuilder) {
        Block output = registerBlock(id, blockFactory, settings);
        cauldronBehaviorBuilder.createBucket(output);
        return output;
    }


}
