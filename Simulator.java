import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


public class Simulator {


	private static final int DEFAULT_WIDTH = 320;

	private static final int DEFAULT_DEPTH = 200;

	private static final double FLOWER_CREATION_PROBABILITY = 0.07;

	private static final double MOUSE_CREATION_PROBABILITY = 0.07;

	private static final double DUCK_CREATION_PROBABILITY = 0.07;

	private static final double BIRD_CREATION_PROBABILITY = 0.07;

	private static final double WOLF_CREATION_PROBABILITY = 0.03;

	private static final double BEAR_CREATION_PROBABILITY = 0.03;

	private static final TimeCycle DEFAULT_TIMECYCLE = TimeCycle.DAY;

	private static final Weather DEFAULT_WEATHER = Weather.SUN;

	private static final int TIMECYCLE_LENGTH = 4;

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


	private List<Animal> animals;

	private List<Plant> plants;

	private Field field;

	private int step;

	private SimulatorView gridView;

	private GraphView graphView;

	private TimeCycle currentTimeCycle;

	private Climate climate;

	private int sickPercentage;


	public Simulator() {
		this(DEFAULT_DEPTH, DEFAULT_WIDTH);
		currentTimeCycle = DEFAULT_TIMECYCLE;
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


		gridView = new SimulatorView(depth, width);


		graphView = new GraphView(1000, 500, 500);


		reset();
	}


	public void runLongSimulation() {
		simulate(4000);
	}


	public void simulate(int numSteps) {
		for (int step = 1; step <= numSteps && gridView.isViable(field); step++) {
			simulateOneStep();
			delay(60);
		}
	}


	public void simulateOneStep() {
		step++;
		climate.updateClimate(step);

		updatePlants();
		List<Animal> newAnimals = new ArrayList<>();

		for (Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
			Animal animal = it.next();
			animal.act(newAnimals, currentTimeCycle);
			if (!animal.isAlive()) {
				it.remove();
			}
		}


		animals.addAll(newAnimals);

		if (step % TIMECYCLE_LENGTH == 0) {
			currentTimeCycle = currentTimeCycle.toggle();
		}

		sickPercentage = calculateSickPercentage();
		gridView.showStatus(step, currentTimeCycle, field, climate, sickPercentage);
		graphView.showStatus(step, field);
	}


	public void reset() {
		step = 0;
		animals.clear();
		plants.clear();
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


				if (rand.nextDouble() <= FLOWER_CREATION_PROBABILITY) {
					Flower flower = new Flower(field, location);
					plants.add(flower);
				} else {
					Grass grass = new Grass(field, location);
					plants.add(grass);
				}

				populateAnimal(rand, location);
			}
		}
	}


	private void populateAnimal(Random rand, Location location) {
		for (AnimalPopulation population : ANIMAL_POPULATIONS) {
			if (rand.nextDouble() <= population.probability) {
				Animal animal = population.factory.create(field, location);
				animals.add(animal);
				graphView.setColor(population.animalClass, animal.getObjectColor(climate));
				return;
			}
		}
	}


	private void updatePlants() {
		for (Plant plant : plants) {
			plant.increaseStage(climate);
		}
	}


	private int calculateSickPercentage() {
		if (animals.isEmpty()) {
			return 0;
		}

		int sickCount = 0;
		for (Animal animal : animals) {
			if (animal.isSick()) {
				sickCount++;
			}
		}
		return (sickCount * 100) / animals.size();
	}


	private void delay(int millisec) {
		try {
			Thread.sleep(millisec);
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
		}
	}


	private interface AnimalFactory {
		Animal create(Field field, Location location);
	}


	private static class AnimalPopulation {
		private final double probability;
		private final Class<? extends Animal> animalClass;
		private final AnimalFactory factory;


		private AnimalPopulation(double probability, Class<? extends Animal> animalClass, AnimalFactory factory) {
			this.probability = probability;
			this.animalClass = animalClass;
			this.factory = factory;
		}
	}
}
