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

	/** Every view observing the simulation; notified uniformly each step. */
	private final List<SimulationView> views;

	/** Reused to test whether the simulation is still viable. */
	private final FieldStats stats = new FieldStats();

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

		views = List.of(
				new SimulatorView(depth, width),
				new GraphView(1000, 500, 500));

		reset();
	}


	public void runLongSimulation() {
		simulate(4000);
	}


	public void simulate(int numSteps) {
		for (int s = 1; s <= numSteps && isViable(); s++) {
			simulateOneStep();
			delay(60);
		}
	}


	/** The simulation is viable while more than one species survives. */
	private boolean isViable() {
		stats.reset();
		return stats.isViable(field);
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
		publish();
	}


	/** Publish the current state to every view. */
	private void publish() {
		SimulationState state = new SimulationState(step, currentTimeCycle, field, climate, sickPercentage);
		for (SimulationView view : views) {
			view.showStatus(state);
		}
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
		for (SimulationView view : views) {
			view.reset();
		}
		publish();
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
					registerSpecies(animal);
				}
			}
		}
	}


	/** Tell every view which colour represents a newly seen species. */
	private void registerSpecies(Animal animal) {
		for (SimulationView view : views) {
			view.setColor(animal.getClass(), animal.getObjectColor(climate));
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
