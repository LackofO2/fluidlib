package lack.fluidlib.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import lack.fluidlib.enumInjector.CST;
import lack.fluidlib.fog.ModSubmersionType;
import lack.fluidlib.fog.VanillaSubmersionType;
import lack.fluidlib.mixinaccessor.CameraAccessor;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Unique
    private ModSubmersionType getCameraSubmersionType(Camera camera) {
        return ((CameraAccessor) camera).fluidlib$getSubmersionType();
    }

    @Redirect(
        method = "renderSky",
        at = @At(value = "FIELD",
            target = "Lnet/minecraft/block/enums/CameraSubmersionType;LAVA:Lnet/minecraft/block/enums/CameraSubmersionType;"))
    public CameraSubmersionType lavaChange(@Local(argsOnly = true) Camera camera) {
        ModSubmersionType modSubmersionType = this.getCameraSubmersionType(camera);
        return modSubmersionType instanceof VanillaSubmersionType vanillaSubmersionType && vanillaSubmersionType.cameraSubmersionType() == CameraSubmersionType.LAVA ? CameraSubmersionType.LAVA : CST.NULL_TYPE;
    }
    @Redirect(
        method = "renderSky",
        at = @At(value = "FIELD",
            target = "Lnet/minecraft/block/enums/CameraSubmersionType;POWDER_SNOW:Lnet/minecraft/block/enums/CameraSubmersionType;"))
    public CameraSubmersionType snowChange(@Local(argsOnly = true) Camera camera) {
        ModSubmersionType modSubmersionType = this.getCameraSubmersionType(camera);
        return modSubmersionType instanceof VanillaSubmersionType vanillaSubmersionType && vanillaSubmersionType.cameraSubmersionType() == CameraSubmersionType.POWDER_SNOW ? CameraSubmersionType.POWDER_SNOW : CST.NULL_TYPE;
    }

}
