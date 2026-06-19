package app;

import events.EventBus;
import model.Bear;
import model.Bird;
import model.Duck;
import model.Flower;
import model.Grass;
import model.Mouse;
import model.SpeciesRegistry;
import model.Wolf;
import view.GraphView;
import view.SimulatorView;


/**
 * Entry point and composition root: the one place that knows the concrete
 * species and views. It wires the species into a {@link SpeciesRegistry},
 * subscribes the views to an {@link EventBus}, and hands the simulator only the
 * registry and the publish side of the bus — so the engine stays oblivious to
 * both which creatures exist and who is watching.
 */
public class Main {

	private static final int DEPTH = 200;
	private static final int WIDTH = 320;

	public static void main(String[] args) {
		EventBus events = new EventBus();
		events.register(new SimulatorView(DEPTH, WIDTH));
		events.register(new GraphView(1000, 500, 500));

		Simulator simulator = new Simulator(standardSpecies(), events, DEPTH, WIDTH);
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
