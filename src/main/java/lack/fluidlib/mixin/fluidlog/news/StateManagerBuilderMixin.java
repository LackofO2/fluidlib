package lack.fluidlib.mixin.fluidlog.news;

import lack.fluidlib.registry.fluidlog.FluidLogProperties;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Debug(export = true)
@Mixin(StateManager.Builder.class)
public abstract class StateManagerBuilderMixin {

    @ModifyVariable(method = "add", at = @At("HEAD"), index = 1, argsOnly = true)
    public Property<?>[] init(Property<?>[] properties) {

        for (Property<?> property : properties) {
            if (property == Properties.WATERLOGGED) {
                Property<?>[] modifiedProperties = new Property<?>[properties.length + 1];
                System.arraycopy(properties, 0, modifiedProperties, 0, properties.length);
                modifiedProperties[properties.length] = FluidLogProperties.FLUIDLOGGED;
                return modifiedProperties;
            }
        }
        return properties;
    }
}
