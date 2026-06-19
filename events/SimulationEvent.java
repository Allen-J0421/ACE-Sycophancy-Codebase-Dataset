package events;

import java.awt.Color;


/**
 * Something worth telling observers about. The sealed hierarchy enumerates every
 * kind of notification the engine emits, so listeners can dispatch on them
 * exhaustively and the compiler flags any new event type that goes unhandled.
 */
public sealed interface SimulationEvent {

	/** The simulation advanced; carries the latest state to render. */
	record StatusUpdated(SimulationState state) implements SimulationEvent {
	}

	/** The simulation was reset to a fresh run. */
	record SimulationReset() implements SimulationEvent {
	}

	/** A species first appeared, paired with the colour used to depict it. */
	record SpeciesRegistered(Class<?> species, Color color) implements SimulationEvent {
	}
}
