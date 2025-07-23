package lack.fluidlib.fluid;

import lack.fluidlib.cauldron.CauldronBehaviorBuilder;
import lack.fluidlib.cauldron.ModdedCauldronBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class FluidBuilder {
    public static final Map<Identifier, ModFluid> FLUID_MAP = new HashMap<>();


    private boolean canSwimIn = false;
    private ModFluid fluidStill = null;
    private ModFluid fluidFlowing = null;

    public static ModFluid getFluidFromIdentifier(Identifier id) {
        return FLUID_MAP.get(id);
    }

    public FlowableFluid exportToStill() {
        return exportToFluid(fluidStill);
    }

    public FlowableFluid exportToFlowing() {
        return exportToFluid(fluidFlowing);
    }

    private FlowableFluid exportToFluid(ModFluid fluid) {
        FlowableFluid registered = Registry.register(Registries.FLUID, fluid.getFluidId(), fluid.getFluid());
        FLUID_MAP.put(fluid.getFluidId(), fluid);
        return registered;
    }

    public FluidBuilder fluidStill(ModFluid fluid) {
        this.fluidStill = fluid;
        return this;
    }

    public FluidBuilder fluidFlowing(ModFluid fluid) {
        this.fluidFlowing = fluid;
        return this;
    }

    public FluidBuilder canSwimIn(boolean canSwimIn) {
        this.canSwimIn = canSwimIn;
        return this;
    }

    public Block toBlock(AbstractBlock.Settings settings) {
        return toBlock(fluidStill.getFluidId(), settings);
    }

    public Block toBlock(Identifier id, AbstractBlock.Settings settings) {
        return registerBlock(id, settings1 -> new FluidBlock(fluidStill.getFluid(), settings), settings);
    }

    public Item toBucket(Identifier bucketId, Item.Settings settings) {
        return registerBucket(bucketId, settings1 -> new BucketItem(fluidStill.getFluid(), settings1), settings);
    }

    public static Block registerBlock(Identifier id, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, id);
        Block block = factory.apply(settings.registryKey(key));
        return Registry.register(Registries.BLOCK, key, block);
    }

    public static Item registerBucket(Identifier id, Function<Item.Settings, Item> factory, Item.Settings settings) {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
        Item item = (Item) factory.apply(settings.registryKey(key));
        if (item instanceof BlockItem blockItem) {
            blockItem.appendBlocks(Item.BLOCK_ITEMS, item);
        }
        return Registry.register(Registries.ITEM, key, item);
    }

    public Block toCauldron(Identifier id, CauldronBehaviorBuilder cauldronBehaviorBuilder) {
        AbstractBlock.Settings settings = AbstractBlock.Settings.copyShallow(Blocks.CAULDRON);
        return toCauldron(id, settings, cauldronBehaviorBuilder);
    }

    public Block toCauldron(Identifier id, AbstractBlock.Settings settings, CauldronBehaviorBuilder cauldronBehaviorBuilder) {
        return toCauldron(id, s -> new ModdedCauldronBlock(s, cauldronBehaviorBuilder.export()), settings, cauldronBehaviorBuilder);
    }

    public Block toCauldron(Identifier id, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings, CauldronBehaviorBuilder cauldronBehaviorBuilder) {
        Block output = registerBlock(id, blockFactory, settings);
        cauldronBehaviorBuilder.createBucket(output);
        return output;
    }


}
