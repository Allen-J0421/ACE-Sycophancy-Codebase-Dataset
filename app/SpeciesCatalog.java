package app;

import model.AnimalFactory;
import model.Bear;
import model.Bird;
import model.Duck;
import model.Flower;
import model.Grass;
import model.Mouse;
import model.PlantFactory;
import model.Wolf;

import java.util.Map;


/**
 * Resolves the species names used in configuration to the factories that build
 * them. This is the one spot coupled to concrete species: adding a creature
 * means a new class plus one entry here, after which it can be enabled and
 * weighted purely from the configuration file.
 */
final class SpeciesCatalog {

	private final Map<String, AnimalFactory> animals = Map.of(
			"Bird", (field, location) -> new Bird(true, field, location),
			"Mouse", (field, location) -> new Mouse(true, field, location),
			"Duck", (field, location) -> new Duck(true, field, location),
			"Wolf", (field, location) -> new Wolf(true, field, location),
			"Bear", (field, location) -> new Bear(true, field, location));

	private final Map<String, PlantFactory> plants = Map.of(
			"Flower", Flower::new,
			"Grass", Grass::new);


	AnimalFactory animal(String name) {
		AnimalFactory factory = animals.get(name);
		if (factory == null) {
			throw new IllegalArgumentException("Unknown animal species in configuration: " + name);
		}
		return factory;
	}


	PlantFactory plant(String name) {
		PlantFactory factory = plants.get(name);
		if (factory == null) {
			throw new IllegalArgumentException("Unknown plant species in configuration: " + name);
		}
		return factory;
	}
}
