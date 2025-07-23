package lack.fluidlib.registry;

import lack.fluidlib.fog.ModSubmersionType;
import lack.fluidlib.fog.ModSubmersionTypes;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SubmersionTypeRegistry {
    private static final Map<ModSubmersionType, SubmersionPredicate> REGISTRY = new ConcurrentHashMap<>();

    public static void register(ModSubmersionType submersionType, SubmersionPredicate predicate) {
        if (REGISTRY.containsKey(submersionType)) {
            throw new IllegalStateException("Submersion type already registered: " + submersionType);
        }
        REGISTRY.put(submersionType, predicate);
    }

    public static SubmersionPredicate get(ModSubmersionType key) {
        return REGISTRY.get(key);
    }

    public static boolean test(ModSubmersionType key, BlockView blockView, Vec3d vec3d, BlockPos pos, FluidState fluidState) {
        SubmersionPredicate predicate = REGISTRY.get(key);
        return predicate != null && predicate.test(blockView, vec3d, pos, fluidState);
    }

    public static Map<ModSubmersionType, SubmersionPredicate> getAll() {
        return Map.copyOf(REGISTRY); // immutable copy
    }
    private <T extends ModSubmersionType> T getFromSubmersionType(CameraSubmersionType cameraSubmersionType){
        return ModSubmersionTypes.toModSubmersionType(cameraSubmersionType);
    }

}
