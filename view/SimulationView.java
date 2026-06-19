package view;

import java.awt.Color;


/**
 * A renderable view of the simulation. The engine publishes a
 * {@link SimulationState} after every step; the optional hooks let a view that
 * tracks per-species colour or needs to clear itself between runs opt in,
 * while views that don't care inherit harmless no-ops.
 */
public interface SimulationView {

	/** Render the latest state of the simulation. */
	void showStatus(SimulationState state);

	/** Prepare for a fresh run. */
	default void reset() {
	}

	/** Associate a species with the colour used to depict it. */
	default void setColor(Class<?> species, Color color) {
	}
}
