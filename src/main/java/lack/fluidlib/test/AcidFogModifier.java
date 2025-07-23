package lack.fluidlib.test;

import lack.fluidlib.fog.ModFogModifier;
import lack.fluidlib.fog.ModSubmersionType;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.fog.FogData;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class AcidFogModifier extends ModFogModifier {
    private static final int COLOR = -6743808;

    @Override
    public int getFogColor(ClientWorld world, Camera camera, int viewDistance, float skyDarkness) {
        return COLOR;
    }

    @Override
    public void applyStartEndModifier(FogData data, Entity cameraEntity, BlockPos cameraPos, ClientWorld world, float viewDistance, RenderTickCounter tickCounter) {
        if (cameraEntity.isSpectator()) {
            data.environmentalStart = -8.0F;
            data.environmentalEnd = viewDistance * 0.5F;
        } else if (cameraEntity instanceof LivingEntity livingEntity && livingEntity.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
            data.environmentalStart = 0.0F;
            data.environmentalEnd = 5.0F;
        } else {
            data.environmentalStart = 0.25F;
            data.environmentalEnd = 1.0F;
        }

        data.skyEnd = data.environmentalEnd;
        data.cloudEnd = data.environmentalEnd;
    }


    @Override
    public boolean shouldApply(@Nullable ModSubmersionType submersionType, Entity cameraEntity) {
        return submersionType == Test.ACID;
    }
}
