public class FieldEntityController implements EntityController {

	private final FieldEnvironment fieldEnvironment;


	public FieldEntityController(FieldEnvironment fieldEnvironment) {
		this.fieldEnvironment = fieldEnvironment;
	}


	@Override
	public FieldEnvironment getFieldEnvironment() {
		return fieldEnvironment;
	}


	@Override
	public void place(Entity entity, Location location) {
		entity.updateLocation(location);
		placeInField(entity, location);
	}


	@Override
	public void move(Entity entity, Location location) {
		Location currentLocation = entity.getLocation();
		if (currentLocation != null) {
			clearFromField(entity, currentLocation);
		}
		entity.updateLocation(location);
		placeInField(entity, location);
	}


	@Override
	public void remove(Entity entity) {
		Location currentLocation = entity.getLocation();
		if (currentLocation != null) {
			clearFromField(entity, currentLocation);
			entity.updateLocation(null);
		}
	}


	private void placeInField(Entity entity, Location location) {
		if (entity instanceof Animal) {
			fieldEnvironment.placeAnimal((Animal) entity, location);
		} else if (entity instanceof Plant) {
			fieldEnvironment.placePlant((Plant) entity, location);
		}
	}


	private void clearFromField(Entity entity, Location location) {
		if (entity instanceof Animal) {
			fieldEnvironment.clearAnimalAt(location);
		} else if (entity instanceof Plant) {
			fieldEnvironment.clearPlantAt(location);
		}
	}
}
