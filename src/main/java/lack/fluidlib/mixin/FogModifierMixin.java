package lack.fluidlib.mixin;

import lack.fluidlib.fog.ModFogModifier;
import lack.fluidlib.fog.ModSubmersionType;
import lack.fluidlib.fog.VanillaSubmersionType;
import lack.fluidlib.mixinaccessor.FogModifierAccessor;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.fog.FogModifier;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Debug(export = true)
@Mixin(FogModifier.class)
public abstract class FogModifierMixin implements FogModifierAccessor {

    @Shadow
    public abstract boolean shouldApply(@Nullable CameraSubmersionType submersionType, Entity cameraEntity);

    @Unique
    public boolean fluidlib$shouldApply(@Nullable ModSubmersionType modSubmersionType, Entity cameraEntity) {
        if (modSubmersionType instanceof VanillaSubmersionType vanillaSubmersionType) {
            return shouldApply(vanillaSubmersionType.cameraSubmersionType(), cameraEntity);
        } else {
            if (((Object) this) instanceof ModFogModifier modFogModifier) {
                return modFogModifier.shouldApply(modSubmersionType, cameraEntity);
            }
            return false;
        }
    }


}
