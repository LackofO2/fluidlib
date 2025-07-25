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
    private final FlowableFluid fluidStill;
    private final FlowableFluid fluidFlowing;
    private final FluidProperties fluidProperties;
    private final TagKey<Fluid> fluidTagKey;

    private final BiFunction<AbstractBlock.Settings, CauldronBehavior.CauldronBehaviorMap, Block> cauldronFactory;

    public FluidBuilder(FlowableFluid fluidStill, FlowableFluid fluidFlowing, FluidProperties properties, TagKey<Fluid> fluidTagKey) {
        this(fluidStill, fluidFlowing, properties, fluidTagKey, ModdedCauldronBlock::new);
    }

    public FluidBuilder(FlowableFluid fluidStill, FlowableFluid fluidFlowing, FluidProperties properties,
                        TagKey<Fluid> fluidTagKey, BiFunction<AbstractBlock.Settings, CauldronBehavior.CauldronBehaviorMap, Block> cauldronFactory) {
        this.fluidStill = fluidStill;
        this.fluidFlowing = fluidFlowing;
        this.fluidProperties = properties;
        this.fluidTagKey = fluidTagKey;
        this.cauldronFactory = cauldronFactory;
    }

    public FlowableFluid createStillFluid(Identifier identifier) {
        addToFluidRegistry();
        return registerFluid(fluidStill, identifier);
    }

    public FlowableFluid createFlowingFluid(Identifier identifier) {
        return registerFluid(fluidFlowing, identifier);
    }

    private FlowableFluid registerFluid(FlowableFluid fluid, Identifier identifier) {
        return Registry.register(Registries.FLUID, identifier, fluid);
    }

    private void addToFluidRegistry() {
        if (fluidProperties.canSwim()) {
            FluidRegistry.SWIMMABLE.add(fluidTagKey);
        } else {
            FluidRegistry.NONSWIMMABLE.add(fluidTagKey);
        }

        FluidRegistry.FLUIDS.put(fluidTagKey, fluidProperties);
    }

    public Block createBlock(Identifier id, AbstractBlock.Settings settings) {
        return registerBlock(id, settings1 -> new FluidBlock(fluidStill, settings), settings);
    }

    public Item createBucket(Identifier bucketId, Item.Settings settings) {
        return registerBucket(bucketId, settings1 -> new BucketItem(fluidStill, settings1), settings);
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


    public Block createCauldron(Identifier id, AbstractBlock.Settings settings, CauldronBehaviorBuilder cauldronBehaviorBuilder) {
        return registerCauldron(id, settings1 -> cauldronFactory.apply(settings, cauldronBehaviorBuilder.export()), settings, cauldronBehaviorBuilder);
    }

    public Block registerCauldron(Identifier id, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings, CauldronBehaviorBuilder cauldronBehaviorBuilder) {
        Block output = registerBlock(id, blockFactory, settings);
        cauldronBehaviorBuilder.createBucket(output);
        return output;
    }


}
