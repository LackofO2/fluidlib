package lack.fluidlib.fluid;

import net.minecraft.util.math.Vec3d;

public class ModFluidConstants {
    public static final float WATER_FLYING_SPEED = 0.2f;
    public static final float LAVA_FLYING_SPEED = 0.2f;
    public static final float WATER_ENTITY_SPEED = 0.014f;
    public static final float LAVA_ENTITY_SPEED = 0.007f;
    public static final float LAVA_NETHER_ENTITY_SPEED = 0.0023333333333333335f;
    public static final Vec3d WATER_SPEED_MODIFIER = new Vec3d(0.5, 0.8F, 0.5);
    public static final Vec3d LAVA_SPEED_MODIFIER = new Vec3d(0.5, 0.8F, 0.5);
    public static final float WATER_DRAG = 0.8F;
    public static final float LAVA_DRAG = 0.5F;
}
