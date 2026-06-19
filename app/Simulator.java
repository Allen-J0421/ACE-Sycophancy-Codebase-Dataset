package app;

import model.*;
import view.*;
import config.Randomizer;

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

	private static final TimeCycle DEFAULT_TIMECYCLE = TimeCycle.DAY;

	private static final Weather DEFAULT_WEATHER = Weather.SUN;

	private static final int TIMECYCLE_LENGTH = 4;


	/** Supplies the animal and plant species used to populate the field. */
	private final SpeciesRegistry registry;

	private final List<Animal> animals;

	private final List<Plant> plants;

	private final Field field;

	private int step;

	private final SimulatorView gridView;

	private final GraphView graphView;

	private TimeCycle currentTimeCycle;

	private final Climate climate;

	private int sickPercentage;


	public Simulator(SpeciesRegistry registry) {
		this(registry, DEFAULT_DEPTH, DEFAULT_WIDTH);
	}


	public Simulator(SpeciesRegistry registry, int depth, int width) {
		if (width <= 0 || depth <= 0) {
			System.out.println("The dimensions must be greater than zero.");
			System.out.println("Using default values.");
			depth = DEFAULT_DEPTH;
			width = DEFAULT_WIDTH;
		}

		this.registry = registry;
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
				plants.add(registry.spawnPlant(rand, field, location));

				Animal animal = registry.spawnAnimal(rand, field, location);
				if (animal != null) {
					animals.add(animal);
					graphView.setColor(animal.getClass(), animal.getObjectColor(climate));
				}
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
