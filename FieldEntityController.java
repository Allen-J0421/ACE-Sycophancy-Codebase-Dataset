public class FieldEntityController implements EntityController {

	private final FieldEnvironment fieldEnvironment;

	private final EntityHandlerRegistry handlerRegistry;


	public FieldEntityController(FieldEnvironment fieldEnvironment) {
		this(fieldEnvironment, EntityHandlerRegistry.shared());
	}


	public FieldEntityController(FieldEnvironment fieldEnvironment, EntityHandlerRegistry handlerRegistry) {
		this.fieldEnvironment = fieldEnvironment;
		this.handlerRegistry = handlerRegistry;
	}


	@Override
	public FieldEnvironment getFieldEnvironment() {
		return fieldEnvironment;
	}


	@Override
	public void place(Entity entity, Location location) {
		entity.updateLocation(location);
		handlerRegistry.resolve(entity).place(fieldEnvironment, entity, location);
	}


	@Override
	public void move(Entity entity, Location location) {
		Location currentLocation = entity.getLocation();
		if (currentLocation != null) {
			handlerRegistry.resolve(entity).clear(fieldEnvironment, currentLocation);
		}
		entity.updateLocation(location);
		handlerRegistry.resolve(entity).place(fieldEnvironment, entity, location);
	}


	@Override
	public void remove(Entity entity) {
		Location currentLocation = entity.getLocation();
		if (currentLocation != null) {
			handlerRegistry.resolve(entity).clear(fieldEnvironment, currentLocation);
			entity.updateLocation(null);
		}
	}
}
