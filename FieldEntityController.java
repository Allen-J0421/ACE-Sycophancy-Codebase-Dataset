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
		entity.accept(new PlacementVisitor(location));
	}


	@Override
	public void move(Entity entity, Location location) {
		Location currentLocation = entity.getLocation();
		if (currentLocation != null) {
			entity.accept(new ClearingVisitor(currentLocation));
		}
		entity.updateLocation(location);
		entity.accept(new PlacementVisitor(location));
	}


	@Override
	public void remove(Entity entity) {
		Location currentLocation = entity.getLocation();
		if (currentLocation != null) {
			entity.accept(new ClearingVisitor(currentLocation));
			entity.updateLocation(null);
		}
	}


	private class PlacementVisitor implements EntityVisitor {
		private final Location location;


		private PlacementVisitor(Location location) {
			this.location = location;
		}


		@Override
		public void visitAnimal(Animal animal) {
			fieldEnvironment.placeAnimal(animal, location);
		}


		@Override
		public void visitPlant(Plant plant) {
			fieldEnvironment.placePlant(plant, location);
		}
	}


	private class ClearingVisitor implements EntityVisitor {
		private final Location location;


		private ClearingVisitor(Location location) {
			this.location = location;
		}


		@Override
		public void visitAnimal(Animal animal) {
			fieldEnvironment.clearAnimalAt(location);
		}


		@Override
		public void visitPlant(Plant plant) {
			fieldEnvironment.clearPlantAt(location);
		}
	}
}
