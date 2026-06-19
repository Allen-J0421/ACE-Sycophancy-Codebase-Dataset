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


		for (Iterator<Plant> it = plants.iterator(); it.hasNext(); ) {
			Plant plant = it.next();
			plant.increaseStage(climate);
		}


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
			currentTimeCycle = currentTimeCycle.toggleTimeCycle(currentTimeCycle);
		}


		int count = 0;
		for (Animal i : animals) {
			if (i.isSick()) {
				count++;
			}
		}

		sickPercentage = animals.isEmpty() ? 0 : (count * 100) / animals.size();
		gridView.showStatus(step, currentTimeCycle, field, climate, sickPercentage);
		graphView.showStatus(step, field);
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

		double mouseThreshold = BIRD_CREATION_PROBABILITY + MOUSE_CREATION_PROBABILITY;
		double duckThreshold = mouseThreshold + DUCK_CREATION_PROBABILITY;
		double wolfThreshold = duckThreshold + WOLF_CREATION_PROBABILITY;
		double bearThreshold = wolfThreshold + BEAR_CREATION_PROBABILITY;

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

				double animalRoll = rand.nextDouble();
				if (animalRoll < BIRD_CREATION_PROBABILITY) {
					Bird bird = new Bird(true, field, location);
					animals.add(bird);
					graphView.setColor(Bird.class, bird.getObjectColor(climate));
				} else if (animalRoll < mouseThreshold) {
					Mouse mouse = new Mouse(true, field, location);
					animals.add(mouse);
					graphView.setColor(Mouse.class, mouse.getObjectColor(climate));
				} else if (animalRoll < duckThreshold) {
					Duck duck = new Duck(true, field, location);
					animals.add(duck);
					graphView.setColor(Duck.class, duck.getObjectColor(climate));
				} else if (animalRoll < wolfThreshold) {
					Wolf wolf = new Wolf(true, field, location);
					animals.add(wolf);
					graphView.setColor(Wolf.class, wolf.getObjectColor(climate));
				} else if (animalRoll < bearThreshold) {
					Bear bear = new Bear(true, field, location);
					animals.add(bear);
					graphView.setColor(Bear.class, bear.getObjectColor(climate));
				}
			}
		}
	}


	private void delay(int millisec) {
		try {
			Thread.sleep(millisec);
		} catch (InterruptedException ie) {

		}
	}
}
