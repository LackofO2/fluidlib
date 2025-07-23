//package lack.fluidlib.modifiers;
//
//import net.minecraft.entity.LivingEntity;
//import net.minecraft.entity.attribute.EntityAttributes;
//
//public class WaterMovementModifier extends EntityAttributeSpeedModifier {
//    public WaterMovementModifier() {
//        super(EntityAttributes.WATER_MOVEMENT_EFFICIENCY);
//    }
//
//    @Override
//    public float applySpeedModifier(LivingEntity livingEntity, boolean falling, double entityY, double effectiveGravity, float speedModifier) {
//        float waterEfficiency = (float) livingEntity.getAttributeValue(entityAttribute);
//        if (!livingEntity.isOnGround()) {
//            waterEfficiency *= 0.5F;
//        }
//        return waterEfficiency > 0.0f ? speedModifier + (0.54600006F - speedModifier) * waterEfficiency : speedModifier;
//    }
//
//    @Override
//    public float applyBaseSpeedModifier(LivingEntity livingEntity, boolean falling, double entityY, double effectiveGravity, float baseSpeed) {
//        float waterEfficiency = (float) livingEntity.getAttributeValue(entityAttribute);
//        if (!livingEntity.isOnGround()) {
//            waterEfficiency *= 0.5F;
//        }
//        return waterEfficiency > 0.0f ? baseSpeed + (livingEntity.getMovementSpeed() - baseSpeed) * waterEfficiency : baseSpeed;
//
//    }
//}
