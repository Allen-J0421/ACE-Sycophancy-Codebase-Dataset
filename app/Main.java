package app;

import model.Bear;
import model.Bird;
import model.Duck;
import model.Flower;
import model.Grass;
import model.Mouse;
import model.SpeciesRegistry;
import model.Wolf;


/**
 * Entry point and composition root: this is the one place that knows the
 * concrete species, wiring them into a {@link SpeciesRegistry} and injecting
 * it into the {@link Simulator}. The simulator itself stays oblivious to which
 * animals and plants exist.
 */
public class Main {

	public static void main(String[] args) {
		Simulator simulator = new Simulator(standardSpecies());
		simulator.runLongSimulation();
	}


	/** The default catalogue of species and their spawn probabilities. */
	private static SpeciesRegistry standardSpecies() {
		return new SpeciesRegistry()
				.registerPlant(0.07, Flower::new)
				.defaultPlant(Grass::new)
				.registerAnimal(0.07, (field, location) -> new Bird(true, field, location))
				.registerAnimal(0.07, (field, location) -> new Mouse(true, field, location))
				.registerAnimal(0.07, (field, location) -> new Duck(true, field, location))
				.registerAnimal(0.03, (field, location) -> new Wolf(true, field, location))
				.registerAnimal(0.03, (field, location) -> new Bear(true, field, location));
	}
}
