package lack.fluidlib.mixin.fog;

import com.llamalad7.mixinextras.sugar.Local;
import lack.fluidlib.enumInjector.CST;
import lack.fluidlib.fog.ModSubmersionType;
import lack.fluidlib.fog.VanillaSubmersionType;
import lack.fluidlib.mixinaccessor.CameraAccessor;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Unique
    private ModSubmersionType getCameraSubmersionType(Camera camera) {
        return ((CameraAccessor) camera).fluidlib$getSubmersionType();
    }

    @Redirect(
        method = "getFov",
        at = @At(value = "FIELD",
            target = "Lnet/minecraft/block/enums/CameraSubmersionType;LAVA:Lnet/minecraft/block/enums/CameraSubmersionType;"))
    public CameraSubmersionType lavaChange(@Local(argsOnly = true) Camera camera) {
        ModSubmersionType modSubmersionType = this.getCameraSubmersionType(camera);
        return modSubmersionType instanceof VanillaSubmersionType vanillaSubmersionType && vanillaSubmersionType.cameraSubmersionType() == CameraSubmersionType.LAVA ? CameraSubmersionType.LAVA : CST.NULL_TYPE;
    }
    @Redirect(
        method = "getFov",
        at = @At(value = "FIELD",
            target = "Lnet/minecraft/block/enums/CameraSubmersionType;WATER:Lnet/minecraft/block/enums/CameraSubmersionType;"))
    public CameraSubmersionType waterChange(@Local(argsOnly = true) Camera camera) {
        ModSubmersionType modSubmersionType = this.getCameraSubmersionType(camera);
        return modSubmersionType instanceof VanillaSubmersionType vanillaSubmersionType && vanillaSubmersionType.cameraSubmersionType() == CameraSubmersionType.WATER ? CameraSubmersionType.WATER : CST.NULL_TYPE;
    }
}
