import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


public class FieldPopulator {

	private static final double FLOWER_CREATION_PROBABILITY = 0.07;

	private static final double MOUSE_CREATION_PROBABILITY = 0.07;

	private static final double DUCK_CREATION_PROBABILITY = 0.07;

	private static final double BIRD_CREATION_PROBABILITY = 0.07;

	private static final double WOLF_CREATION_PROBABILITY = 0.03;

	private static final double BEAR_CREATION_PROBABILITY = 0.03;

	private final RandomService random;

	private final List<AnimalSpawnRule> animalSpawnRules;


	public FieldPopulator() {
		this.random = RandomService.shared();
		this.animalSpawnRules = new ArrayList<>();
		registerAnimalSpawnRules();
	}


	public void populate(EntityController controller, List<Animal> animals, List<Plant> plants) {
		FieldEnvironment field = controller.getFieldEnvironment();
		field.clear();
		animals.clear();
		plants.clear();

		field.streamLocations().forEach(location -> populateLocation(controller, location, animals, plants));
	}


	private Plant createPlant(EntityController controller, Location location) {
		if (random.chance(FLOWER_CREATION_PROBABILITY)) {
			return new Flower(controller, location);
		}
		return new Grass(controller, location);
	}


	private Animal createAnimal(EntityController controller, Location location) {
		return animalSpawnRules.stream()
				.filter(rule -> rule.shouldSpawn(random))
				.findFirst()
				.map(rule -> rule.createAnimal(controller, location))
				.orElse(null);
	}


	private void registerAnimalSpawnRules() {
		Stream.of(
				new AnimalSpawnRule(BIRD_CREATION_PROBABILITY, Bird::new),
				new AnimalSpawnRule(MOUSE_CREATION_PROBABILITY, Mouse::new),
				new AnimalSpawnRule(DUCK_CREATION_PROBABILITY, Duck::new),
				new AnimalSpawnRule(WOLF_CREATION_PROBABILITY, Wolf::new),
				new AnimalSpawnRule(BEAR_CREATION_PROBABILITY, Bear::new))
				.forEach(animalSpawnRules::add);
	}


	private void populateLocation(EntityController controller, Location location, List<Animal> animals, List<Plant> plants) {
		plants.add(createPlant(controller, location));

		Animal animal = createAnimal(controller, location);
		if (animal != null) {
			animals.add(animal);
		}
	}


	private static class AnimalSpawnRule {
		private final double spawnProbability;

		private final AnimalFactory animalFactory;


		private AnimalSpawnRule(double spawnProbability, AnimalFactory animalFactory) {
			this.spawnProbability = spawnProbability;
			this.animalFactory = animalFactory;
		}


		private boolean shouldSpawn(RandomService random) {
			return random.chance(spawnProbability);
		}


		private Animal createAnimal(EntityController controller, Location location) {
			return animalFactory.create(true, controller, location);
		}
	}
}
