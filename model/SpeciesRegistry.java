package model;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * The catalogue of species that can populate a simulation, decoupling the
 * engine from concrete animal and plant classes. Each species is registered
 * with a spawn probability and a factory; the registry performs the weighted
 * draw, so callers spawn life without ever naming a concrete type.
 *
 * <p>Registration order is significant. Rules are evaluated in order and the
 * first to win its dice roll claims the cell, so earlier rules have priority.
 * Registration methods are fluent to allow a readable one-statement catalogue.
 */
public class SpeciesRegistry {

	private record Weighted<F>(double probability, F factory) {
	}

	private final List<Weighted<AnimalFactory>> animalRules = new ArrayList<>();
	private final List<Weighted<PlantFactory>> plantRules = new ArrayList<>();
	private PlantFactory defaultPlant;


	public SpeciesRegistry registerAnimal(double probability, AnimalFactory factory) {
		animalRules.add(new Weighted<>(probability, factory));
		return this;
	}


	public SpeciesRegistry registerPlant(double probability, PlantFactory factory) {
		plantRules.add(new Weighted<>(probability, factory));
		return this;
	}


	/** The plant grown in any cell no weighted plant rule claims. */
	public SpeciesRegistry defaultPlant(PlantFactory factory) {
		this.defaultPlant = factory;
		return this;
	}


	/**
	 * Choose an animal for a cell, or {@code null} if none of the registered
	 * species win their roll (the cell is left without an animal).
	 */
	public Animal spawnAnimal(Random rand, Field field, Location location) {
		for (Weighted<AnimalFactory> rule : animalRules) {
			if (rand.nextDouble() <= rule.probability()) {
				return rule.factory().create(field, location);
			}
		}
		return null;
	}


	/**
	 * Choose a plant for a cell, falling back to the default plant when no
	 * weighted plant rule wins its roll. Never returns {@code null}.
	 */
	public Plant spawnPlant(Random rand, Field field, Location location) {
		for (Weighted<PlantFactory> rule : plantRules) {
			if (rand.nextDouble() <= rule.probability()) {
				return rule.factory().create(field, location);
			}
		}
		return defaultPlant.create(field, location);
	}
}
