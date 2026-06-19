import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * The engine of the predator-prey simulation. It owns the field, the live
 * animals and plants, and drives the world forward one step at a time while
 * keeping the grid and graph views in sync.
 */
public class Simulator {


	private static final int DEFAULT_WIDTH = 320;

	private static final int DEFAULT_DEPTH = 200;

	private static final double FLOWER_CREATION_PROBABILITY = 0.07;

	private static final TimeCycle DEFAULT_TIMECYCLE = TimeCycle.DAY;

	private static final Weather DEFAULT_WEATHER = Weather.SUN;

	private static final int TIMECYCLE_LENGTH = 4;


	/** A factory that creates a freshly spawned animal at a location. */
	@FunctionalInterface
	private interface AnimalFactory {
		Animal create(Field field, Location location);
	}

	/** The chance of spawning a given animal in an empty cell, and how to build it. */
	private record SpawnRule(double probability, AnimalFactory factory) {
	}

	/**
	 * Animal spawn rules, evaluated in order: the first rule that wins its dice
	 * roll claims the cell. Earlier rules therefore have priority.
	 */
	private static final List<SpawnRule> ANIMAL_SPAWN_RULES = List.of(
			new SpawnRule(0.07, (field, location) -> new Bird(true, field, location)),
			new SpawnRule(0.07, (field, location) -> new Mouse(true, field, location)),
			new SpawnRule(0.07, (field, location) -> new Duck(true, field, location)),
			new SpawnRule(0.03, (field, location) -> new Wolf(true, field, location)),
			new SpawnRule(0.03, (field, location) -> new Bear(true, field, location)));


	private final List<Animal> animals;

	private final List<Plant> plants;

	private final Field field;

	private int step;

	private final SimulatorView gridView;

	private final GraphView graphView;

	private TimeCycle currentTimeCycle;

	private final Climate climate;

	private int sickPercentage;


	public Simulator() {
		this(DEFAULT_DEPTH, DEFAULT_WIDTH);
	}


	public Simulator(int depth, int width) {
		if (width <= 0 || depth <= 0) {
			System.out.println("The dimensions must be greater than zero.");
			System.out.println("Using default values.");
			depth = DEFAULT_DEPTH;
			width = DEFAULT_WIDTH;
		}

		animals = new ArrayList<>();
		plants = new ArrayList<>();
		field = new Field(depth, width);
		climate = new Climate(DEFAULT_WEATHER);
		currentTimeCycle = DEFAULT_TIMECYCLE;

		gridView = new SimulatorView(depth, width);
		graphView = new GraphView(1000, 500, 500);

		reset();
	}


	public void runLongSimulation() {
		simulate(4000);
	}


	public void simulate(int numSteps) {
		for (int s = 1; s <= numSteps && gridView.isViable(field); s++) {
			simulateOneStep();
			delay(60);
		}
	}


	public void simulateOneStep() {
		step++;
		climate.updateClimate(step);

		for (Plant plant : plants) {
			plant.increaseStage(climate);
		}

		List<Animal> newAnimals = new ArrayList<>();
		for (Animal animal : animals) {
			animal.act(newAnimals, currentTimeCycle);
		}
		animals.removeIf(animal -> !animal.isAlive());
		animals.addAll(newAnimals);

		if (step % TIMECYCLE_LENGTH == 0) {
			currentTimeCycle = currentTimeCycle.next();
		}

		sickPercentage = computeSickPercentage();
		gridView.showStatus(step, currentTimeCycle, field, climate, sickPercentage);
		graphView.showStatus(step, field);
	}


	private int computeSickPercentage() {
		if (animals.isEmpty()) {
			return 0;
		}
		int sick = 0;
		for (Animal animal : animals) {
			if (animal.isSick()) {
				sick++;
			}
		}
		return (sick * 100) / animals.size();
	}


	public void reset() {
		step = 0;
		animals.clear();
		populate();
		currentTimeCycle = TimeCycle.DAY;
		climate.setCurrentWeather(Weather.SUN);

		sickPercentage = 0;
		graphView.reset();
		gridView.showStatus(step, currentTimeCycle, field, climate, sickPercentage);
		graphView.showStatus(step, field);
	}


	private void populate() {
		Random rand = Randomizer.getRandom();
		field.clear();

		for (int row = 0; row < field.getDepth(); row++) {
			for (int col = 0; col < field.getWidth(); col++) {
				Location location = new Location(row, col);
				spawnPlant(rand, location);
				spawnAnimal(rand, location);
			}
		}
	}


	private void spawnPlant(Random rand, Location location) {
		Plant plant = rand.nextDouble() <= FLOWER_CREATION_PROBABILITY
				? new Flower(field, location)
				: new Grass(field, location);
		plants.add(plant);
	}


	private void spawnAnimal(Random rand, Location location) {
		for (SpawnRule rule : ANIMAL_SPAWN_RULES) {
			if (rand.nextDouble() <= rule.probability()) {
				Animal animal = rule.factory().create(field, location);
				animals.add(animal);
				graphView.setColor(animal.getClass(), animal.getObjectColor(climate));
				return;
			}
		}
	}


	private void delay(int millisec) {
		try {
			Thread.sleep(millisec);
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
		}
	}
}
