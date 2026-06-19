package events;


/**
 * Receives {@link SimulationEvent}s from the simulation. Any component — a view,
 * a logger, a recorder — can listen without the engine knowing it exists.
 */
@FunctionalInterface
public interface SimulationListener {
	void onEvent(SimulationEvent event);
}
