package lack.fluidlib.registry;

import lack.fluidlib.fog.ModSubmersionType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SubmersionTypeRegistry {
    private static final Map<ModSubmersionType, SubmersionPredicate> REGISTRY = new ConcurrentHashMap<>();

    public static void register(ModSubmersionType submersionType, SubmersionPredicate predicate) {
        if (REGISTRY.containsKey(submersionType)) {
            throw new IllegalStateException("Submersion type already registered: " + submersionType);
        }
        REGISTRY.put(submersionType, predicate);
    }


    public static Map<ModSubmersionType, SubmersionPredicate> getAll() {
        return Map.copyOf(REGISTRY);
    }
}
