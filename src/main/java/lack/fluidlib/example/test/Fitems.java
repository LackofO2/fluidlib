package lack.fluidlib.example.test;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import static lack.fluidlib.FluidLib.MOD_ID;
import static lack.fluidlib.example.FluidExample.ACID_BUILDER;

public class Fitems {
    public static void init() {

    }
    public static final Item ACID_BUCKET = ACID_BUILDER.createBucket(Identifier.of(MOD_ID, "acid_bucket"), new Item.Settings()
        .recipeRemainder(Items.BUCKET).maxCount(1));

}
