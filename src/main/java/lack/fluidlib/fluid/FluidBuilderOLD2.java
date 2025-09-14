//package lack.fluidlib.fluid;
//
//import lack.fluidlib.cauldron.CauldronBehaviorBuilder;
//import lack.fluidlib.cauldron.ModdedCauldronBlock;
//import net.minecraft.block.AbstractBlock;
//import net.minecraft.block.Block;
//import net.minecraft.block.FluidBlock;
//import net.minecraft.block.cauldron.CauldronBehavior;
//import net.minecraft.fluid.FlowableFluid;
//import net.minecraft.fluid.Fluid;
//import net.minecraft.item.BlockItem;
//import net.minecraft.item.BucketItem;
//import net.minecraft.item.Item;
//import net.minecraft.registry.Registries;
//import net.minecraft.registry.Registry;
//import net.minecraft.registry.RegistryKey;
//import net.minecraft.registry.RegistryKeys;
//import net.minecraft.registry.tag.TagKey;
//import net.minecraft.util.Identifier;
//
//import java.util.function.BiFunction;
//import java.util.function.Function;
//
//import static lack.fluidlib.FluidLib.MOD_ID;
//
//public class FluidBuilderOLD2 {
//
//    public static FluidExport fluidExport;
//    private static FlowableFluid fluidStill;
//    private static FlowableFluid fluidFlowing;
//    private static TagKey<Fluid> fluidTag;
//    private static FluidProperties fluidProperties;
//    private static BiFunction<AbstractBlock.Settings, CauldronBehavior.CauldronBehaviorMap, Block> cauldronFactory;
//
//    public FluidBuilderOLD2(FlowableFluid fluidStill, FlowableFluid fluidFlowing, FluidProperties properties, TagKey<Fluid> fluidTagKey) {
//        this(fluidStill, fluidFlowing, properties, fluidTagKey, ModdedCauldronBlock::new);
//    }
//
//    public FluidBuilderOLD2(FlowableFluid stillFluid, FlowableFluid flowingFluid, FluidProperties properties,
//                            TagKey<Fluid> fluidTagKey, BiFunction<AbstractBlock.Settings, CauldronBehavior.CauldronBehaviorMap, Block> cauldron) {
//        fluidStill = stillFluid;
//        fluidFlowing = flowingFluid;
//        fluidProperties = properties;
//        fluidTag = fluidTagKey;
//        cauldronFactory = cauldron;
//    }
//
//    private static Block createBlock(Identifier id, AbstractBlock.Settings settings) {
//        return registerBlock(id, settings1 -> new FluidBlock(fluidStill, settings), settings);
//    }
//
//    private static Block registerBlock(Identifier id, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
//        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, id);
//        Block block = factory.apply(settings.registryKey(key));
//        return Registry.register(Registries.BLOCK, key, block);
//    }
//
//    private static FlowableFluid createStillFluid(Identifier identifier) {
//        addToFluidRegistry();
//        return registerFluid(fluidStill, identifier);
//    }
//
//    public static FlowableFluid createFlowingFluid(Identifier identifier) {
//        return registerFluid(fluidFlowing, identifier);
//    }
//
//    private static FlowableFluid registerFluid(FlowableFluid fluid, Identifier identifier) {
//        return Registry.register(Registries.FLUID, identifier, fluid);
//    }
//
//    private static void addToFluidRegistry() {
//        if (fluidProperties.canSwim()) {
//            FluidRegistry.SWIMMABLE.add(fluidTag);
//        } else {
//            FluidRegistry.NONSWIMMABLE.add(fluidTag);
//        }
//
//        FluidRegistry.FLUIDS.put(fluidTag, fluidProperties);
//    }
//
//    public static Item createBucket(Identifier bucketId, Item.Settings settings) {
//        return registerBucket(bucketId, settings1 -> new BucketItem(fluidStill, settings1), settings);
//    }
//
//    public static Item registerBucket(Identifier id, Function<Item.Settings, Item> factory, Item.Settings settings) {
//        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
//        Item item = factory.apply(settings.registryKey(key));
//        if (item instanceof BlockItem blockItem) {
//            blockItem.appendBlocks(Item.BLOCK_ITEMS, item);
//        }
//        return Registry.register(Registries.ITEM, key, item);
//    }
//
//    public static Block createCauldron(Identifier id, AbstractBlock.Settings settings, CauldronBehaviorBuilder cauldronBehaviorBuilder) {
//        return registerCauldron(id, settings1 -> cauldronFactory.apply(settings, cauldronBehaviorBuilder.export()), settings, cauldronBehaviorBuilder);
//    }
//
//    public static Block registerCauldron(Identifier id, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings, CauldronBehaviorBuilder cauldronBehaviorBuilder) {
//        Block output = registerBlock(id, blockFactory, settings);
//        cauldronBehaviorBuilder.createBucket(output);
//        return output;
//    }
//
//
//    static {
//        Block fluidBlock = createBlock(Identifier.of(MOD_ID, "acid"), AbstractBlock.Settings.create());
//        FlowableFluid stillFluid = createStillFluid(Identifier.of(MOD_ID, "acid"));
//        FlowableFluid flowingFluid = createFlowingFluid(Identifier.of(MOD_ID, "acid_flowing"));
//        Item bucket = createBucket(Identifier.of(MOD_ID, "acid_bucket"), new Item.Settings());
//        Block cauldron = createCauldron(Identifier.of(MOD_ID, "acid_cauldron"), AbstractBlock.Settings.create(), CauldronBehaviorBuilder.create("acid", bucket));
//
//        fluidExport = new FluidExport(fluidBlock, stillFluid, flowingFluid, bucket, cauldron);
//    }
//}
