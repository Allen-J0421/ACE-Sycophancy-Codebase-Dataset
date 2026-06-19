package logging;

import events.SimulationEvent;
import events.SimulationListener;
import events.SimulationState;

import java.util.HashSet;
import java.util.Set;


/**
 * Records the simulation's activity by subscribing to the event bus and writing
 * a line through a {@link Logger} for each event. It is just another listener,
 * so logging is added or removed purely by registering it (or not) — the engine
 * is unaware it exists.
 */
public class EventLogger implements SimulationListener {

	private final Logger logger;

	/** Species already announced, so a run start logs each species once, not once per individual. */
	private final Set<Class<?>> knownSpecies = new HashSet<>();


	public EventLogger(Logger logger) {
		this.logger = logger;
	}


	@Override
	public void onEvent(SimulationEvent event) {
		switch (event) {
			case SimulationEvent.StatusUpdated e -> logStatus(e.state());
			case SimulationEvent.SimulationReset e -> reset();
			case SimulationEvent.SpeciesRegistered e -> logNewSpecies(e.species());
		}
	}


	private void reset() {
		knownSpecies.clear();
		logger.log("Simulation reset.");
	}


	private void logNewSpecies(Class<?> species) {
		if (knownSpecies.add(species)) {
			logger.log("Registered species: " + species.getSimpleName());
		}
	}


	private void logStatus(SimulationState state) {
		logger.log(String.format(
				"Step %d | %s | %s | humidity %d%% | infection %d%%",
				state.step(),
				state.timeCycle(),
				state.climate().getCurrentSeason(),
				state.climate().getHumidity(),
				state.sickPercentage()));
	}
}
