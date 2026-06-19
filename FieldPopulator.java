import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class FieldPopulator {

	private static final double FLOWER_CREATION_PROBABILITY = 0.07;

	private static final double MOUSE_CREATION_PROBABILITY = 0.07;

	private static final double DUCK_CREATION_PROBABILITY = 0.07;

	private static final double BIRD_CREATION_PROBABILITY = 0.07;

	private static final double WOLF_CREATION_PROBABILITY = 0.03;

	private static final double BEAR_CREATION_PROBABILITY = 0.03;

	private final Random random;

	private final GraphView graphView;

	private final Climate climate;

	private final List<AnimalSpawnRule> animalSpawnRules;


	public FieldPopulator(GraphView graphView, Climate climate) {
		this.random = Randomizer.getRandom();
		this.graphView = graphView;
		this.climate = climate;
		this.animalSpawnRules = new ArrayList<>();
		registerAnimalSpawnRules();
	}


	public void populate(Field field, List<Animal> animals, List<Plant> plants) {
		field.clear();
		animals.clear();
		plants.clear();

		for (int row = 0; row < field.getDepth(); row++) {
			for (int col = 0; col < field.getWidth(); col++) {
				Location location = new Location(row, col);
				plants.add(createPlant(field, location));

				Animal animal = createAnimal(field, location);
				if (animal != null) {
					animals.add(animal);
					graphView.setColor(animal.getClass(), animal.getObjectColor(climate));
				}
			}
		}
	}


	private Plant createPlant(Field field, Location location) {
		if (random.nextDouble() <= FLOWER_CREATION_PROBABILITY) {
			return new Flower(field, location);
		}
		return new Grass(field, location);
	}


	private Animal createAnimal(Field field, Location location) {
		for (AnimalSpawnRule rule : animalSpawnRules) {
			if (rule.shouldSpawn(random)) {
				return rule.createAnimal(field, location);
			}
		}
		return null;
	}


	private void registerAnimalSpawnRules() {
		animalSpawnRules.add(new AnimalSpawnRule(BIRD_CREATION_PROBABILITY) {
			@Override
			Animal createAnimal(Field field, Location location) {
				return new Bird(true, field, location);
			}
		});
		animalSpawnRules.add(new AnimalSpawnRule(MOUSE_CREATION_PROBABILITY) {
			@Override
			Animal createAnimal(Field field, Location location) {
				return new Mouse(true, field, location);
			}
		});
		animalSpawnRules.add(new AnimalSpawnRule(DUCK_CREATION_PROBABILITY) {
			@Override
			Animal createAnimal(Field field, Location location) {
				return new Duck(true, field, location);
			}
		});
		animalSpawnRules.add(new AnimalSpawnRule(WOLF_CREATION_PROBABILITY) {
			@Override
			Animal createAnimal(Field field, Location location) {
				return new Wolf(true, field, location);
			}
		});
		animalSpawnRules.add(new AnimalSpawnRule(BEAR_CREATION_PROBABILITY) {
			@Override
			Animal createAnimal(Field field, Location location) {
				return new Bear(true, field, location);
			}
		});
	}


	private abstract static class AnimalSpawnRule {
		private final double spawnProbability;


		private AnimalSpawnRule(double spawnProbability) {
			this.spawnProbability = spawnProbability;
		}


		private boolean shouldSpawn(Random random) {
			return random.nextDouble() <= spawnProbability;
		}


		abstract Animal createAnimal(Field field, Location location);
	}
}
