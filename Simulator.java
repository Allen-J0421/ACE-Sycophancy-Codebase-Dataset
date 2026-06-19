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

	private static final int TIME_CYCLE_LENGTH = 4;


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
		for (int i = 0; i < numSteps && gridView.isViable(field); i++) {
			simulateOneStep();
			delay(60);
		}
	}


	public void simulateOneStep() {
		step++;
		climate.updateClimate(step);
		updatePlants();
		List<Animal> newAnimals = new ArrayList<>();
		updateAnimals(newAnimals);
		animals.addAll(newAnimals);
		updateTimeCycle();
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
				populatePlant(rand, location);
				populateAnimal(rand, location);
			}
		}
	}

	private void populatePlant(Random rand, Location location) {
		if (rand.nextDouble() <= FLOWER_CREATION_PROBABILITY) {
			plants.add(new Flower(field, location));
		} else {
			plants.add(new Grass(field, location));
		}
	}

	private void populateAnimal(Random rand, Location location) {
		if (rand.nextDouble() <= BIRD_CREATION_PROBABILITY) {
			addAnimal(new Bird(true, field, location));
		} else if (rand.nextDouble() <= MOUSE_CREATION_PROBABILITY) {
			addAnimal(new Mouse(true, field, location));
		} else if (rand.nextDouble() <= DUCK_CREATION_PROBABILITY) {
			addAnimal(new Duck(true, field, location));
		} else if (rand.nextDouble() <= WOLF_CREATION_PROBABILITY) {
			addAnimal(new Wolf(true, field, location));
		} else if (rand.nextDouble() <= BEAR_CREATION_PROBABILITY) {
			addAnimal(new Bear(true, field, location));
		}
	}

	private void addAnimal(Animal animal) {
		animals.add(animal);
		graphView.setColor(animal.getClass(), animal.getObjectColor(climate));
	}

	private void updatePlants() {
		for (Plant plant : plants) {
			plant.increaseStage(climate);
		}
	}

	private void updateAnimals(List<Animal> newAnimals) {
		for (Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
			Animal animal = it.next();
			animal.act(newAnimals, currentTimeCycle);
			if (!animal.isAlive()) {
				it.remove();
			}
		}
	}

	private void updateTimeCycle() {
		if (step % TIME_CYCLE_LENGTH == 0) {
			currentTimeCycle = currentTimeCycle.toggle();
		}
	}

	private int calculateSickPercentage() {
		if (animals.isEmpty()) {
			return 0;
		}
		int count = 0;
		for (Animal animal : animals) {
			if (animal.isSick()) {
				count++;
			}
		}
		return (count * 100) / animals.size();
	}


	private void delay(int millisec) {
		try {
			Thread.sleep(millisec);
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
		}
	}
}
