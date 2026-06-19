import java.util.Random;


public final class AnimalPopulation {

	private final double probability;
	private final Class<? extends Animal> animalClass;
	private final AnimalFactory factory;


	public AnimalPopulation(double probability, Class<? extends Animal> animalClass, AnimalFactory factory) {
		this.probability = probability;
		this.animalClass = animalClass;
		this.factory = factory;
	}


	public boolean shouldCreate(Random rand) {
		return rand.nextDouble() <= probability;
	}


	public Animal create(Field field, Location location) {
		return factory.create(field, location);
	}


	public Class<? extends Animal> getAnimalClass() {
		return animalClass;
	}
}
