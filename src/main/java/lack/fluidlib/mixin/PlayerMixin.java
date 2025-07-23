package lack.fluidlib.mixin;

import lack.fluidlib.mixinaccessor.EntityAccessor;
import lack.fluidlib.test.ModFluidTags;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public class PlayerMixin {

    @Redirect(method = "updateWaterSubmersionState", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isSubmergedIn(Lnet/minecraft/registry/tag/TagKey;)Z"))
    protected boolean updateWaterSubmersionState(PlayerEntity instance, TagKey tagKey) {
        return instance.isSubmergedIn(ModFluidTags.SWIMMABLE);

    }
}
