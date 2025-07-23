package lack.fluidlib.cauldron;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;

import static net.minecraft.block.cauldron.CauldronBehavior.*;

public class CauldronBehaviorBuilder {
    public static final HashMap<Item, CauldronBehavior.CauldronBehaviorMap> CAULDRON_BEHAVIOR_MAP = new HashMap<>();


    private CauldronBehaviorBuilder() {
    }

    private Item bucket;

    private CauldronBehavior.CauldronBehaviorMap cauldronBehaviorMap;

    public static CauldronBehaviorBuilder create(String name, Item bucket) {
        CauldronBehaviorBuilder builder = new CauldronBehaviorBuilder();
        builder.cauldronBehaviorMap = createMap(name);
        builder.bucket = bucket;
        registerBucketBehavior(builder.cauldronBehaviorMap.map());
        CAULDRON_BEHAVIOR_MAP.put(bucket, builder.cauldronBehaviorMap);
        return builder;
    }


    public CauldronBehaviorBuilder addToMap(Item item, CauldronBehavior cauldronBehavior) {
        cauldronBehaviorMap.map().put(item, cauldronBehavior);
        return this;
    }

    public CauldronBehavior.CauldronBehaviorMap export() {
        createDefault();
        return cauldronBehaviorMap;
    }


    public void createBucket(Block cauldronBlock) {
        EMPTY_CAULDRON_BEHAVIOR.map().put(bucket, (state, world, pos, player, hand, stack) -> tryFillWithFluid(state, world, pos, player, hand, stack, cauldronBlock));
        LAVA_CAULDRON_BEHAVIOR.map().put(bucket, (state, world, pos, player, hand, stack) -> tryFillWithFluid(state, world, pos, player, hand, stack, cauldronBlock));
        WATER_CAULDRON_BEHAVIOR.map().put(bucket, (state, world, pos, player, hand, stack) -> tryFillWithFluid(state, world, pos, player, hand, stack, cauldronBlock));
        POWDER_SNOW_CAULDRON_BEHAVIOR.map().put(bucket, (state, world, pos, player, hand, stack) -> tryFillWithFluid(state, world, pos, player, hand, stack, cauldronBlock));
        CAULDRON_BEHAVIOR_MAP.forEach((bucketItem, cauldronBehaviorMap1) -> cauldronBehaviorMap1.map().put(bucket, (state, world, pos, player, hand, stack) -> tryFillWithFluid(state, world, pos, player, hand, stack, cauldronBlock)));
    }

    private static ActionResult tryFillWithFluid(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, Block cauldronBlock) {
        return isUnderwater(world, pos)
            ? ActionResult.CONSUME
            : fillCauldron(world, pos, player, hand, stack,
            cauldronBlock.getDefaultState(), SoundEvents.ITEM_BUCKET_EMPTY_LAVA);
    }

    private void createDefault() {
        addToMap(Items.BUCKET, (state, world, pos, player, hand, stack) ->
            emptyCauldron(state, world, pos, player, hand, stack, new ItemStack(bucket),
                statex -> true, SoundEvents.ITEM_BUCKET_FILL_LAVA)

        );
    }
}
