package lack.fluidlib.mixin.entity;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import lack.fluidlib.fluid.FluidProperties;
import lack.fluidlib.fluid.FluidRegistry;
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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Debug(export = true)
@Mixin(AbstractBoatEntity.class)
public abstract class AbstractBoatEntityMixin extends Entity {

    protected AbstractBoatEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Unique
    private Map<TagKey<Fluid>, FluidProperties> getValid() {
        return FluidRegistry.applyPredicateMap(entry -> ((EntityAccessor) this).fluidlib$isInFluid(entry.getKey()));
    }


    @Unique
    private Map<TagKey<Fluid>, FluidProperties> getFluidsSubmergedIn(FluidState fluidState) {
        return getValid().entrySet().stream()
            .filter(entry -> fluidState.isIn(entry.getKey()) && entry.getValue().boatsFloatIn())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Unique
    private Map<TagKey<Fluid>, FluidProperties> getFluidsSubmergedIn(AbstractBoatEntity boatEntity) {
        return getValid().entrySet().stream()
            .filter(entry -> boatEntity.isSubmergedIn(entry.getKey()) && entry.getValue().boatsFloatIn())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }


    @Redirect(method = "getWaterHeightBelow", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"))
    public boolean allFluids(FluidState instance, TagKey<Fluid> tag) {
        return !getFluidsSubmergedIn(instance).isEmpty();
    }

    @Redirect(method = "checkBoatInWater", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"))
    public boolean check(FluidState instance, TagKey<Fluid> tag) {
        return !getFluidsSubmergedIn(instance).isEmpty();
    }

    @Redirect(method = "getUnderWaterLocation", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"))
    public boolean underwater(FluidState instance, TagKey<Fluid> tag) {
        return !getFluidsSubmergedIn(instance).isEmpty();
    }


    @Redirect(method = "updatePassengerForDismount", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/world/World;isWater(Lnet/minecraft/util/math/BlockPos;)Z"))
    public boolean passenger(World instance, BlockPos blockPos) {
        return !getFluidsSubmergedIn(instance.getFluidState(blockPos)).isEmpty();
    }

    @Redirect(method = "fall", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"))
    public boolean fall(FluidState instance, TagKey<Fluid> tag) {
        return !getFluidsSubmergedIn(instance).isEmpty();
    }

    @Redirect(method = "canAddPassenger", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/entity/vehicle/AbstractBoatEntity;isSubmergedIn(Lnet/minecraft/registry/tag/TagKey;)Z"))
    public boolean canadd(AbstractBoatEntity instance, TagKey<Fluid> tagKey) {
        return !getFluidsSubmergedIn(instance).isEmpty();
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
        Map<TagKey<Fluid>, FluidProperties> validFluids = getValid();

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
                    pos.set(x, y, z);
                    FluidState fluidState = this.getWorld().getFluidState(pos);
                    if (fluidState.isEmpty()) {
                        continue;
                    }
                    if (fluidState.isIn(FluidTags.WATER)) {
                        sum += 1f;
                        count++;
                    } else {
                        Optional<Float> optionalBoatSpeed = validFluids.entrySet().stream()
                            .filter(entry -> fluidState.isIn(entry.getKey()))
                            .map(entry -> entry.getValue().vehicleSpeedModifier())
                            .findAny();
                        if (optionalBoatSpeed.isPresent()) {
                            sum += optionalBoatSpeed.get();
                            count++;
                        }
                    }
                }
            }
        }

        return count == 0 ? 1.0f : sum / count;
    }
}
