import java.util.List;
import java.util.Random;


public final class WorldPopulator {

	private static final double FLOWER_CREATION_PROBABILITY = 0.07;

	private static final double MOUSE_CREATION_PROBABILITY = 0.07;

	private static final double DUCK_CREATION_PROBABILITY = 0.07;

	private static final double BIRD_CREATION_PROBABILITY = 0.07;

	private static final double WOLF_CREATION_PROBABILITY = 0.03;

	private static final double BEAR_CREATION_PROBABILITY = 0.03;

	private static final AnimalPopulation[] ANIMAL_POPULATIONS = {
			new AnimalPopulation(BIRD_CREATION_PROBABILITY, Bird.class, new AnimalFactory() {
				public Animal create(Field field, Location location) {
					return new Bird(true, field, location);
				}
			}),
			new AnimalPopulation(MOUSE_CREATION_PROBABILITY, Mouse.class, new AnimalFactory() {
				public Animal create(Field field, Location location) {
					return new Mouse(true, field, location);
				}
			}),
			new AnimalPopulation(DUCK_CREATION_PROBABILITY, Duck.class, new AnimalFactory() {
				public Animal create(Field field, Location location) {
					return new Duck(true, field, location);
				}
			}),
			new AnimalPopulation(WOLF_CREATION_PROBABILITY, Wolf.class, new AnimalFactory() {
				public Animal create(Field field, Location location) {
					return new Wolf(true, field, location);
				}
			}),
			new AnimalPopulation(BEAR_CREATION_PROBABILITY, Bear.class, new AnimalFactory() {
				public Animal create(Field field, Location location) {
					return new Bear(true, field, location);
				}
			})
	};


	private WorldPopulator() {
	}


	public static void populate(Field field,
								List<Animal> animals,
								List<Plant> plants,
								Climate climate,
								GraphView graphView) {
		Random rand = Randomizer.getRandom();
		field.clear();

		for (int row = 0; row < field.getDepth(); row++) {
			for (int col = 0; col < field.getWidth(); col++) {
				Location location = new Location(row, col);
				plants.add(createPlant(rand, field, location));
				populateAnimal(rand, field, location, animals, climate, graphView);
			}
		}
	}


	private static Plant createPlant(Random rand, Field field, Location location) {
		if (rand.nextDouble() <= FLOWER_CREATION_PROBABILITY) {
			return new Flower(field, location);
		}
		return new Grass(field, location);
	}


	private static void populateAnimal(Random rand,
										Field field,
										Location location,
										List<Animal> animals,
										Climate climate,
										GraphView graphView) {
		for (AnimalPopulation population : ANIMAL_POPULATIONS) {
			if (population.shouldCreate(rand)) {
				Animal animal = population.create(field, location);
				animals.add(animal);
				graphView.setColor(population.getAnimalClass(), animal.getObjectColor(climate));
				return;
			}
		}
	}
}
