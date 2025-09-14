package lack.fluidlib.mixin.fog;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import lack.fluidlib.enumInjector.CST;
import lack.fluidlib.fog.ModSubmersionType;
import lack.fluidlib.fog.ModSubmersionTypes;
import lack.fluidlib.fog.VanillaSubmersionType;
import lack.fluidlib.mixinaccessor.CameraAccessor;
import lack.fluidlib.mixinaccessor.FogModifierAccessor;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.fog.FogModifier;
import net.minecraft.client.render.fog.FogRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Debug(export = true)
@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {

    @Unique
    private ModSubmersionType getCameraSubmersionType(Camera camera, boolean thick) {
        ModSubmersionType cameraSubmersionType = ((CameraAccessor) camera).fluidlib$getSubmersionType();
        if (cameraSubmersionType instanceof VanillaSubmersionType vanillaSubmersionType) {
            if (vanillaSubmersionType.cameraSubmersionType() == CameraSubmersionType.NONE) {
                return thick ? ModSubmersionTypes.toModSubmersionType(CameraSubmersionType.DIMENSION_OR_BOSS) : ModSubmersionTypes.toModSubmersionType(CameraSubmersionType.ATMOSPHERIC);
            }
        }
        return cameraSubmersionType;
    }

    @Redirect(
        method = "applyFog(Lnet/minecraft/client/render/Camera;IZLnet/minecraft/client/render/RenderTickCounter;FLnet/minecraft/client/world/ClientWorld;)Lorg/joml/Vector4f;",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/fog/FogModifier;shouldApply(Lnet/minecraft/block/enums/CameraSubmersionType;Lnet/minecraft/entity/Entity;)Z"))
    public boolean apply(FogModifier instance, CameraSubmersionType cameraSubmersionType, Entity entity, @Local(argsOnly = true) Camera camera, @Local(argsOnly = true) boolean thick) {
        ModSubmersionType modSubmersionType = this.getCameraSubmersionType(camera, thick);
        return ((FogModifierAccessor) instance).fluidlib$shouldApply(modSubmersionType, entity);

    }

    @Inject(
        method = "getFogColor",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/fog/FogModifier;onSkipped()V"))
    public void fogColor23(Camera camera, float tickProgress, ClientWorld world, int viewDistance, float skyDarkness, boolean thick, CallbackInfoReturnable<Vector4f> cir, @Local(ordinal = 2) FogModifier fogModifier3, @Local(ordinal = 0) LocalRef<FogModifier> fogModifier, @Local(ordinal = 0) Entity entity) {
        ModSubmersionType modSubmersionType = this.getCameraSubmersionType(camera, thick);
        if (((FogModifierAccessor) fogModifier3).fluidlib$shouldApply(modSubmersionType, entity)) {
            fogModifier.set(fogModifier3);
        }
    }

    @Redirect(
        method = "getFogColor",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/fog/FogModifier;shouldApply(Lnet/minecraft/block/enums/CameraSubmersionType;Lnet/minecraft/entity/Entity;)Z"))
    public boolean fogColor(FogModifier instance, CameraSubmersionType cameraSubmersionType, Entity entity, @Local(argsOnly = true) Camera camera, @Local(argsOnly = true) boolean thick) {
        ModSubmersionType modSubmersionType = this.getCameraSubmersionType(camera, thick);
        return ((FogModifierAccessor) instance).fluidlib$shouldApply(modSubmersionType, entity);

    }

    @Redirect(
        method = "getFogColor",
        at = @At(value = "FIELD",
            target = "Lnet/minecraft/block/enums/CameraSubmersionType;LAVA:Lnet/minecraft/block/enums/CameraSubmersionType;"))
    public CameraSubmersionType lavaChange(@Local(argsOnly = true) Camera camera, @Local(argsOnly = true) boolean thick) {
        ModSubmersionType modSubmersionType = this.getCameraSubmersionType(camera, thick);
        return modSubmersionType instanceof VanillaSubmersionType vanillaSubmersionType && vanillaSubmersionType.cameraSubmersionType() == CameraSubmersionType.LAVA ? CameraSubmersionType.LAVA : CST.NULL_TYPE;
    }

    @Redirect(
        method = "getFogColor",
        at = @At(value = "FIELD",
            target = "Lnet/minecraft/block/enums/CameraSubmersionType;WATER:Lnet/minecraft/block/enums/CameraSubmersionType;"))
    public CameraSubmersionType waterChange(@Local(argsOnly = true) Camera camera, @Local(argsOnly = true) boolean thick) {
        ModSubmersionType modSubmersionType = this.getCameraSubmersionType(camera, thick);
        return modSubmersionType instanceof VanillaSubmersionType vanillaSubmersionType && vanillaSubmersionType.cameraSubmersionType() == CameraSubmersionType.WATER ? CameraSubmersionType.WATER : CST.NULL_TYPE;
    }

    @Redirect(
        method = "getFogColor",
        at = @At(value = "FIELD",
            target = "Lnet/minecraft/block/enums/CameraSubmersionType;POWDER_SNOW:Lnet/minecraft/block/enums/CameraSubmersionType;"))
    public CameraSubmersionType snowChange(@Local(argsOnly = true) Camera camera, @Local(argsOnly = true) boolean thick) {
        ModSubmersionType modSubmersionType = this.getCameraSubmersionType(camera, thick);
        return modSubmersionType instanceof VanillaSubmersionType vanillaSubmersionType && vanillaSubmersionType.cameraSubmersionType() == CameraSubmersionType.POWDER_SNOW ? CameraSubmersionType.POWDER_SNOW : CST.NULL_TYPE;
    }
}
