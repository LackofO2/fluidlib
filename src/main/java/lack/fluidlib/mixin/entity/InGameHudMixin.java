package lack.fluidlib.mixin.entity;

import lack.fluidlib.fluid.FluidRegistry;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Redirect(method = "renderAirBubbles", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isSubmergedIn(Lnet/minecraft/registry/tag/TagKey;)Z"))
    public boolean redirToSuffocation(PlayerEntity instance, TagKey tagKey) {

        return FluidRegistry.applyPredicateStream(entry ->
            instance.isSubmergedIn(entry.getKey())).anyMatch(entry -> entry.getValue().suffocates(instance));
    }
}
