public class PlantEntityHandler implements EntityHandler<Plant> {

	@Override
	public Class<Plant> getEntityType() {
		return Plant.class;
	}


	@Override
	public void place(FieldEnvironment fieldEnvironment, Plant plant, Location location) {
		fieldEnvironment.placePlant(plant, location);
	}


	@Override
	public void clear(FieldEnvironment fieldEnvironment, Location location) {
		fieldEnvironment.clearPlantAt(location);
	}
}
