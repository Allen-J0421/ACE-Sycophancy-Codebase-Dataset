import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class EntityHandlerRegistry {

	private static final EntityHandlerRegistry SHARED = createDefaultRegistry();

	private final Map<Class<? extends Entity>, EntityHandler<? extends Entity>> handlers;


	public EntityHandlerRegistry() {
		handlers = new ConcurrentHashMap<>();
	}


	public static EntityHandlerRegistry shared() {
		return SHARED;
	}


	public <T extends Entity> void register(EntityHandler<T> handler) {
		handlers.put(handler.getEntityType(), handler);
	}


	@SuppressWarnings("unchecked")
	public <T extends Entity> EntityHandler<T> resolve(T entity) {
		return handlers.values().stream()
				.filter(handler -> handler.getEntityType().isAssignableFrom(entity.getClass()))
				.map(handler -> (EntityHandler<T>) handler)
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No handler registered for " + entity.getClass().getName()));
	}


	private static EntityHandlerRegistry createDefaultRegistry() {
		EntityHandlerRegistry registry = new EntityHandlerRegistry();
		registry.register(new AnimalEntityHandler());
		registry.register(new PlantEntityHandler());
		return registry;
	}
}
