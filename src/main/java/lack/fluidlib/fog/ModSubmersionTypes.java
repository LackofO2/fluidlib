package lack.fluidlib.fog;

import net.minecraft.block.enums.CameraSubmersionType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModSubmersionTypes {

    static List<ModSubmersionType> submersionTypes = Collections.synchronizedList(new ArrayList<>());

    static {
        for (CameraSubmersionType cameraSubmersionType : CameraSubmersionType.values()) {
            submersionTypes.add(new VanillaSubmersionType(cameraSubmersionType));
        }
    }

    public ModSubmersionTypes() {
    }

    public static void register(ModSubmersionType type) {
        submersionTypes.add(type);
    }


    public static VanillaSubmersionType getFromSubmersionType(CameraSubmersionType cameraSubmersionType) {
        return submersionTypes.stream()
            .filter(submersionType -> submersionType instanceof VanillaSubmersionType vanilla &&
                vanilla.cameraSubmersionType() == cameraSubmersionType)
            .map(submersionType -> (VanillaSubmersionType) submersionType)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No matching VanillaSubmersionType for " + cameraSubmersionType));

    }

    public static <T extends ModSubmersionType> T toModSubmersionType(CameraSubmersionType cameraSubmersionType) {
        return (T) ModSubmersionTypes.getFromSubmersionType(cameraSubmersionType);
    }

}
