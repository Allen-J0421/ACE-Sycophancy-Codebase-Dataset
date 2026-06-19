package app;

import config.SimulationConfig;
import events.EventBus;
import logging.ConsoleLogger;
import logging.EventLogger;
import logging.Logger;
import model.SpeciesRegistry;
import view.GraphView;
import view.SimulatorView;

import java.io.IOException;
import java.nio.file.Path;


/**
 * Entry point and composition root. It loads the externalised
 * {@link SimulationConfig}, turns the configured species names into a populated
 * {@link SpeciesRegistry} via the {@link SpeciesCatalog}, subscribes the views
 * and an {@link EventLogger} to an {@link EventBus}, and hands the simulator its
 * collaborators. Simulation parameters live in the configuration file, so they
 * can be changed without touching or recompiling any code.
 */
public class Main {

	private static final String CONFIG_FILE = "simulation.properties";

	public static void main(String[] args) {
		Logger logger = new ConsoleLogger();
		SimulationConfig config = loadConfig(logger);
		SpeciesRegistry registry = buildRegistry(config, new SpeciesCatalog());

		EventBus events = new EventBus();
		events.register(new SimulatorView(config.fieldDepth(), config.fieldWidth()));
		events.register(new GraphView(1000, 500, 500));
		events.register(new EventLogger(logger));

		Simulator simulator = new Simulator(registry, events, logger, config.fieldDepth(), config.fieldWidth());
		simulator.runLongSimulation();
	}


	/** Load the configuration file, falling back to built-in defaults if it is absent. */
	static SimulationConfig loadConfig(Logger logger) {
		Path path = Path.of(CONFIG_FILE);
		try {
			SimulationConfig config = SimulationConfig.load(path);
			logger.log("Loaded configuration from " + path.toAbsolutePath());
			return config;
		} catch (IOException e) {
			logger.log("No " + CONFIG_FILE + " found; using built-in defaults.");
			return SimulationConfig.defaults();
		}
	}


	/** Translate the configured species names and weights into a populated registry. */
	static SpeciesRegistry buildRegistry(SimulationConfig config, SpeciesCatalog catalog) {
		SpeciesRegistry registry = new SpeciesRegistry();
		for (SimulationConfig.SpeciesWeight rule : config.plantRules()) {
			registry.registerPlant(rule.probability(), catalog.plant(rule.name()));
		}
		registry.defaultPlant(catalog.plant(config.defaultPlant()));
		for (SimulationConfig.SpeciesWeight rule : config.animalRules()) {
			registry.registerAnimal(rule.probability(), catalog.animal(rule.name()));
		}
		return registry;
	}
}
