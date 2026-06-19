import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


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

	private SimulatorView gridView;

	private GraphView graphView;

	private TimeCycle currentTimeCycle;

	private Climate climate;

	private FieldPopulator fieldPopulator;

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
		fieldPopulator = new FieldPopulator(graphView, climate);


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
			currentTimeCycle = currentTimeCycle.toggle();
		}

		updateViews();
	}


	public void reset() {
		step = 0;
		fieldPopulator.populate(field, animals, plants);
		currentTimeCycle = DEFAULT_TIMECYCLE;
		climate.setCurrentWeather(Weather.SUN);

		sickPercentage = 0;
		graphView.reset();
		updateViews();
	}


	private void updateViews() {
		sickPercentage = calculateSickPercentage();
		gridView.showStatus(step, currentTimeCycle, field, climate, sickPercentage);
		graphView.showStatus(step, field);
	}


	private int calculateSickPercentage() {
		if (animals.isEmpty()) {
			return 0;
		}

		int sickAnimals = 0;
		for (Animal animal : animals) {
			if (animal.isSick()) {
				sickAnimals++;
			}
		}
		return (sickAnimals * 100) / animals.size();
	}


	private void delay(int millisec) {
		try {
			Thread.sleep(millisec);
		} catch (InterruptedException ie) {

		}
	}
}
