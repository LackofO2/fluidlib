package lack.fluidlib.mixin;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import lack.fluidlib.fluid.FluidBuilder;
import lack.fluidlib.fluid.ModFluid;
import lack.fluidlib.mixinaccessor.EntityAccessor;
import lack.fluidlib.test.ModFluidTags;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Debug(export = true)
@Mixin(Entity.class)
public abstract class EntityMixin implements EntityAccessor {

    public boolean isInSwimmableFluid() {
        List<ModFluid> fluids = getSwimmableFluids();
        return !fluids.isEmpty();

    }

    public boolean isSubmergedInSwimmableFluid() {
        List<ModFluid> fluids = getSwimmableFluids();
        return fluids.stream().anyMatch(fluid -> isSubmergedIn(fluid.getFluidTag()));

    }

    public List<ModFluid> getSwimmableFluids() {
        List<ModFluid> fluids = getAllModFluids();
        return fluids.stream().filter(ModFluid::canSwimIn).toList();
    }

    @Override
    public boolean isInAnyFluid() {
        EntityAccessor accessor = (EntityAccessor) this;
        return FluidBuilder.FLUID_MAP.values().stream().filter(modFluid -> modFluid != null && modFluid.getFluidTag() != null).map(ModFluid::getFluidTag).anyMatch(accessor::fluidlib$isInFluid);
    }

    @Override
    public Optional<TagKey<Fluid>> getFirstFluid() {
        List<TagKey<Fluid>> fluidsTags = this.getAllFluidsTags();
        return fluidsTags.isEmpty() ? Optional.empty() : Optional.of(fluidsTags.getFirst());
    }

    @Override
    public List<TagKey<Fluid>> getAllFluidsTags() {
        List<ModFluid> fluids = getAllModFluids();
        return fluids.stream().map(ModFluid::getFluidTag).toList();
    }

    @Override
    public List<ModFluid> getAllModFluids() {
        return FluidBuilder.FLUID_MAP.values().stream()
            .filter(modFluid -> modFluid != null && modFluid.getFluid() != null &&
                modFluid.getFluidTag() != null).filter(modFluid -> ((EntityAccessor) this)
                .fluidlib$isInFluid(modFluid.getFluidTag())).toList();
    }

    @Shadow
    protected boolean firstUpdate;

    @Shadow
    protected Object2DoubleMap<TagKey<Fluid>> fluidHeight;

    @Shadow
    public abstract boolean updateMovementInFluid(TagKey<Fluid> tag, double speed);

    @Shadow
    public double fallDistance;

    @Shadow
    public abstract boolean isSubmergedIn(TagKey<Fluid> fluidTag);

    @Shadow
    public abstract World getWorld();

    @Override
    public boolean fluidlib$isInFluid(TagKey<Fluid> fluid) {
        return !this.firstUpdate && this.fluidHeight.getDouble(fluid) > 0.0;
    }

    @Inject(method = "updateWaterState", at = @At("RETURN"), cancellable = true)
    public void update(CallbackInfoReturnable<Boolean> cir) {

        Map<Double, TagKey<Fluid>> tagKeyMap = new HashMap<>();

        FluidBuilder.FLUID_MAP.values().forEach(modFluid -> tagKeyMap.put((double) modFluid.getEntitySpeed(), modFluid.getFluidTag()));

        List<ModFluid> fluids = FluidBuilder.FLUID_MAP.values().stream().filter(modFluid -> modFluid != null && modFluid.getFluidTag() != null).toList();

        if (!fluids.isEmpty()) {
            fluids = fluids.stream().collect(Collectors.toMap(ModFluid::getFluidTag, Function.identity(), (f1, f2) -> f1)).values().stream().toList();
            boolean anyMatch = false;
            for (ModFluid fluid : fluids) {
                if (this.updateMovementInFluid(fluid.getFluidTag(), 0.014)) {
                    anyMatch = true;
                }
            }

            if (anyMatch) {
                cir.setReturnValue(true);
            }
        }

    }

    @Inject(method = "isInFluid", at = @At("HEAD"), cancellable = true)
    public void inFluid(CallbackInfoReturnable<Boolean> cir) {
        if (isInAnyFluid()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isInLava()Z"))
    public void fallDamageMultiplier(CallbackInfo ci) {
        List<ModFluid> fluids = getAllModFluids();

        if (!fluids.isEmpty() && fluids.getFirst() != null) {
            this.fallDistance *= fluids.getFirst().getFallDamageMultiplier();
        }
    }

    @Redirect(method = "updateSwimming", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isTouchingWater()Z"))
    public boolean updateSwim(Entity instance) {
        return ((EntityAccessor) instance).fluidlib$isInFluid(ModFluidTags.SWIMMABLE);
    }

    @Redirect(method = "updateSwimming", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isSubmergedInWater()Z"))
    public boolean updateSwim2(Entity instance) {
        return instance.isSubmergedIn(ModFluidTags.SWIMMABLE);
    }

    @Redirect(method = "updateSwimming", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"))
    public boolean updateSwim4(FluidState instance, TagKey<Fluid> tag) {
        return instance.isIn(ModFluidTags.SWIMMABLE);
    }


}

