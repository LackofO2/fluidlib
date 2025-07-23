package lack.fluidlib.mixin;

import lack.fluidlib.mixinaccessor.EntityAccessor;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin implements EntityAccessor {

}
