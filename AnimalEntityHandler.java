public class AnimalEntityHandler implements EntityHandler<Animal> {

	@Override
	public Class<Animal> getEntityType() {
		return Animal.class;
	}


	@Override
	public void place(FieldEnvironment fieldEnvironment, Animal animal, Location location) {
		fieldEnvironment.placeAnimal(animal, location);
	}


	@Override
	public void clear(FieldEnvironment fieldEnvironment, Location location) {
		fieldEnvironment.clearAnimalAt(location);
	}
}
