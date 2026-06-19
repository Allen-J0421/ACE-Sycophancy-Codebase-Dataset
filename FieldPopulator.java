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

	private final GraphView graphView;

	private final Climate climate;

	private final List<AnimalSpawnRule> animalSpawnRules;


	public FieldPopulator(GraphView graphView, Climate climate) {
		this.random = RandomService.shared();
		this.graphView = graphView;
		this.climate = climate;
		this.animalSpawnRules = new ArrayList<>();
		registerAnimalSpawnRules();
	}


	public void populate(FieldEnvironment field, List<Animal> animals, List<Plant> plants) {
		field.clear();
		animals.clear();
		plants.clear();

		field.streamLocations().forEach(location -> populateLocation(field, location, animals, plants));
	}


	private Plant createPlant(FieldEnvironment field, Location location) {
		if (random.chance(FLOWER_CREATION_PROBABILITY)) {
			return new Flower(field, location);
		}
		return new Grass(field, location);
	}


	private Animal createAnimal(FieldEnvironment field, Location location) {
		return animalSpawnRules.stream()
				.filter(rule -> rule.shouldSpawn(random))
				.findFirst()
				.map(rule -> rule.createAnimal(field, location))
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


	private void populateLocation(FieldEnvironment field, Location location, List<Animal> animals, List<Plant> plants) {
		plants.add(createPlant(field, location));

		Animal animal = createAnimal(field, location);
		if (animal != null) {
			animals.add(animal);
			graphView.setColor(animal.getClass(), animal.getObjectColor(climate));
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


		private Animal createAnimal(FieldEnvironment field, Location location) {
			return animalFactory.create(true, field, location);
		}
	}
}
