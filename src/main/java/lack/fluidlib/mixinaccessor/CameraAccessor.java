package lack.fluidlib.mixinaccessor;

import lack.fluidlib.fog.ModSubmersionType;

public interface CameraAccessor {
    <T extends ModSubmersionType> T fluidlib$getSubmersionType();
}
