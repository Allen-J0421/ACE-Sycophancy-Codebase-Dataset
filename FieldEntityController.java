import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class FieldEntityController implements EntityController {

	private final FieldEnvironment fieldEnvironment;

	private final Map<Class<? extends Entity>, EntityHandler<? extends Entity>> handlers;


	public FieldEntityController(FieldEnvironment fieldEnvironment) {
		this(fieldEnvironment, List.of(new AnimalEntityHandler(), new PlantEntityHandler()));
	}


	public FieldEntityController(FieldEnvironment fieldEnvironment, List<EntityHandler<? extends Entity>> handlers) {
		this.fieldEnvironment = fieldEnvironment;
		this.handlers = new LinkedHashMap<>();
		handlers.forEach(handler -> this.handlers.put(handler.getEntityType(), handler));
	}


	@Override
	public FieldEnvironment getFieldEnvironment() {
		return fieldEnvironment;
	}


	@Override
	public void place(Entity entity, Location location) {
		entity.updateLocation(location);
		resolveHandler(entity).place(fieldEnvironment, entity, location);
	}


	@Override
	public void move(Entity entity, Location location) {
		Location currentLocation = entity.getLocation();
		if (currentLocation != null) {
			resolveHandler(entity).clear(fieldEnvironment, currentLocation);
		}
		entity.updateLocation(location);
		resolveHandler(entity).place(fieldEnvironment, entity, location);
	}


	@Override
	public void remove(Entity entity) {
		Location currentLocation = entity.getLocation();
		if (currentLocation != null) {
			resolveHandler(entity).clear(fieldEnvironment, currentLocation);
			entity.updateLocation(null);
		}
	}


	@SuppressWarnings("unchecked")
	private <T extends Entity> EntityHandler<T> resolveHandler(T entity) {
		return handlers.values().stream()
				.filter(handler -> handler.getEntityType().isAssignableFrom(entity.getClass()))
				.map(handler -> (EntityHandler<T>) handler)
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No handler registered for " + entity.getClass().getName()));
	}
}
