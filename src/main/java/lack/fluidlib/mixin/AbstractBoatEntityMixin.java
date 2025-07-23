package lack.fluidlib.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import lack.fluidlib.fluid.ModFluid;
import lack.fluidlib.mixinaccessor.EntityAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Debug(export = true)
@Mixin(AbstractBoatEntity.class)
public abstract class AbstractBoatEntityMixin extends Entity {

    @Shadow
    private double fallVelocity;

    public AbstractBoatEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Unique
    private List<TagKey<Fluid>> what(FluidState instance) {
        List<ModFluid> fluidTags = ((EntityAccessor) this).getAllModFluids();
        return fluidTags.stream().filter(ModFluid::doBoatsFloat).map(ModFluid::getFluidTag).filter(instance::isIn).toList();
    }

    @Redirect(method = "getWaterHeightBelow", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"))
    public boolean allFluids(FluidState instance, TagKey<Fluid> tag) {
        return !what(instance).isEmpty() || instance.isIn(FluidTags.WATER);
    }

    @Redirect(method = "checkBoatInWater", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"))
    public boolean check(FluidState instance, TagKey<Fluid> tag) {
        return !what(instance).isEmpty() || instance.isIn(FluidTags.WATER);
    }

    @Redirect(method = "getUnderWaterLocation", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"))
    public boolean underwater(FluidState instance, TagKey<Fluid> tag) {
        return !what(instance).isEmpty() || instance.isIn(FluidTags.WATER);
    }


    @Redirect(method = "updatePassengerForDismount", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/world/World;isWater(Lnet/minecraft/util/math/BlockPos;)Z"))
    public boolean passenger(World instance, BlockPos blockPos) {
        List<ModFluid> fluidTags = ((EntityAccessor) this).getAllModFluids();
        List<TagKey<Fluid>> validTags = fluidTags.stream().filter(ModFluid::doBoatsFloat).map(ModFluid::getFluidTag)
            .filter(instance.getFluidState(blockPos)::isIn).toList();
        return !validTags.isEmpty() || instance.getFluidState(blockPos).isIn(FluidTags.WATER);
    }

    @Redirect(method = "fall", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"))
    public boolean fall(FluidState instance, TagKey<Fluid> tag) {
        return !what(instance).isEmpty() || instance.isIn(FluidTags.WATER);
    }

    @Redirect(method = "canAddPassenger", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/entity/vehicle/AbstractBoatEntity;isSubmergedIn(Lnet/minecraft/registry/tag/TagKey;)Z"))
    public boolean canadd(AbstractBoatEntity instance, TagKey tagKey) {
        List<ModFluid> fluidTags = ((EntityAccessor) this).getAllModFluids();
        List<TagKey<Fluid>> validTags = fluidTags.stream().filter(ModFluid::doBoatsFloat)
            .map(ModFluid::getFluidTag).filter(instance::isSubmergedIn).toList();
        return !validTags.isEmpty() || instance.isSubmergedIn(FluidTags.WATER);
    }

    @Inject(method = "updateVelocity", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/entity/vehicle/AbstractBoatEntity;getVelocity()Lnet/minecraft/util/math/Vec3d;",
        ordinal = 1))
    public void updateVelocity(CallbackInfo ci, @Local LocalFloatRef localFloatRef) {
        localFloatRef.set(localFloatRef.get() * getSpeedPercentage());
    }

    @Inject(method = "updatePaddles", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/entity/vehicle/AbstractBoatEntity;getVelocity()Lnet/minecraft/util/math/Vec3d;",
        ordinal = 0))
    public void updatePaddles(CallbackInfo ci, @Local LocalFloatRef localFloatRef) {
        localFloatRef.set(localFloatRef.get() * getSpeedPercentage());
    }


    @Unique
    private float getSpeedPercentage() {
        List<ModFluid> fluids = ((EntityAccessor) this).getAllModFluids();

        Box box = this.getBoundingBox();

        int minX = MathHelper.floor(box.minX);
        int maxX = MathHelper.ceil(box.maxX);
        int minY = MathHelper.floor(box.minY);
        int maxY = MathHelper.ceil(box.maxY);
        int minZ = MathHelper.floor(box.minZ);
        int maxZ = MathHelper.ceil(box.maxZ);

        float sum = 0;
        float count = 0;

        BlockPos.Mutable pos = new BlockPos.Mutable();

        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                for (int z = minZ; z < maxZ; z++) {
                    pos.set(x,y,z);
                    FluidState fluidState = this.getWorld().getFluidState(pos);
                    if (fluidState.isEmpty()){
                        continue;
                    }
                    if (fluidState.isIn(FluidTags.WATER)) {
                        sum += 1f;
                        count++;
                    } else {
                        Optional<ModFluid> optionalModFluid = fluids.stream()
                            .filter(fluid -> fluidState.isIn(fluid.getFluidTag())).findAny();
                        if (optionalModFluid.isPresent()) {
                            sum += optionalModFluid.get().getBoatSpeedModifier();
                            count++;
                        }
                    }
                }
            }
        }

        return count == 0 ? 1.0f : sum / count;
    }
}
