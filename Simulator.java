import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;


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


	private final List<SimulatorListener> listeners = new CopyOnWriteArrayList<>();

	private List<Animal> animals;

	private List<Plant> plants;

	private Field field;

	private int step;

	private TimeCycle currentTimeCycle;

	private Climate climate;

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

		reset();
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
		fireOnStep();
	}


	public void reset() {
		step = 0;
		animals.clear();
		plants.clear();
		populate();
		currentTimeCycle = DEFAULT_TIMECYCLE;
		climate.setCurrentWeather(DEFAULT_WEATHER);
		sickPercentage = 0;
		fireOnReset();
	}


	public void addListener(SimulatorListener listener) {
		listeners.add(listener);
	}


	public void removeListener(SimulatorListener listener) {
		listeners.remove(listener);
	}


	private void fireOnStep() {
		for (SimulatorListener l : listeners) {
			l.onStep(step, currentTimeCycle, field, climate, sickPercentage);
		}
	}


	private void fireOnReset() {
		for (SimulatorListener l : listeners) {
			l.onReset(step, currentTimeCycle, field, climate, sickPercentage);
		}
	}


	private void populate() {
		Random rand = Randomizer.getRandom();
		field.clear();

		for (int row = 0; row < field.getDepth(); row++) {
			for (int col = 0; col < field.getWidth(); col++) {
				Location location = new Location(row, col);


				if (rand.nextDouble() <= FLOWER_CREATION_PROBABILITY) {
					plants.add(new Flower(field, location));
				} else {
					plants.add(new Grass(field, location));
				}


				if (rand.nextDouble() <= BIRD_CREATION_PROBABILITY) {
					animals.add(new Bird(true, field, location));
				} else if (rand.nextDouble() <= MOUSE_CREATION_PROBABILITY) {
					animals.add(new Mouse(true, field, location));
				} else if (rand.nextDouble() <= DUCK_CREATION_PROBABILITY) {
					animals.add(new Duck(true, field, location));
				} else if (rand.nextDouble() <= WOLF_CREATION_PROBABILITY) {
					animals.add(new Wolf(true, field, location));
				} else if (rand.nextDouble() <= BEAR_CREATION_PROBABILITY) {
					animals.add(new Bear(true, field, location));
				}

			}
		}
	}


	public int getStep() {
		return step;
	}


	public Field getField() {
		return field;
	}


	public Climate getClimate() {
		return climate;
	}


	public TimeCycle getCurrentTimeCycle() {
		return currentTimeCycle;
	}


	public int getSickPercentage() {
		return sickPercentage;
	}


	public List<Animal> getAnimals() {
		return animals;
	}
}
