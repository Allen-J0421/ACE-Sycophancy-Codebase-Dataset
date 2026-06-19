import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Simulator {


	private static final int DEFAULT_WIDTH = 320;

	private static final int DEFAULT_DEPTH = 200;

	private static final TimeCycle DEFAULT_TIMECYCLE = TimeCycle.DAY;

	private static final Weather DEFAULT_WEATHER = Weather.SUN;

	private static final int TIMECYCLE_LENGTH = 4;


	private List<Animal> animals;

	private List<Plant> plants;

	private FieldEnvironment field;

	private int step;

	private TimeCycle currentTimeCycle;

	private Climate climate;

	private FieldPopulator fieldPopulator;

	private final List<SimulationObserver> observers;

	private final FieldStats fieldStats;

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
		observers = new ArrayList<>();
		fieldStats = new FieldStats();
		field = new Field(depth, width);
		climate = new Climate(DEFAULT_WEATHER);
		fieldPopulator = new FieldPopulator();


		reset();
	}


	public void runLongSimulation() {
		simulate(4000);
	}


	public void simulate(int numSteps) {
		for (int step = 1; step <= numSteps && isViable(); step++) {
			simulateOneStep();
			delay(60);
		}
	}


	public void simulateOneStep() {
		step++;
		climate.updateClimate(step);

		plants.forEach(plant -> plant.increaseStage(climate));


		List<Animal> newAnimals = new ArrayList<>();
		animals.forEach(animal -> animal.act(newAnimals, currentTimeCycle));
		animals = Stream.concat(
				animals.stream().filter(Animal::isAlive),
				newAnimals.stream())
				.collect(Collectors.toCollection(ArrayList::new));


		if (step % TIMECYCLE_LENGTH == 0) {
			currentTimeCycle = currentTimeCycle.toggle();
		}

		notifyObservers();
	}


	public void reset() {
		step = 0;
		fieldPopulator.populate(field, animals, plants);
		currentTimeCycle = DEFAULT_TIMECYCLE;
		climate.setCurrentWeather(Weather.SUN);

		sickPercentage = 0;
		notifyObservers();
	}


	public void addObserver(SimulationObserver observer) {
		observers.add(observer);
		observer.onSimulationStateChanged(createSnapshot());
	}


	public void removeObserver(SimulationObserver observer) {
		observers.remove(observer);
	}


	private void notifyObservers() {
		sickPercentage = calculateSickPercentage();
		SimulationSnapshot snapshot = createSnapshot();
		observers.forEach(observer -> observer.onSimulationStateChanged(snapshot));
	}


	private int calculateSickPercentage() {
		if (animals.isEmpty()) {
			return 0;
		}

		long sickAnimals = animals.stream()
				.filter(Animal::isSick)
				.count();
		return (int) ((sickAnimals * 100) / animals.size());
	}


	private boolean isViable() {
		return fieldStats.isViable(field);
	}


	private SimulationSnapshot createSnapshot() {
		return new SimulationSnapshot(step, currentTimeCycle, field, climate, sickPercentage, isViable());
	}


	private void delay(int millisec) {
		try {
			Thread.sleep(millisec);
		} catch (InterruptedException ie) {

		}
	}
}
