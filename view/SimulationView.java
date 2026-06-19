package view;

import events.SimulationEvent;
import events.SimulationListener;
import events.SimulationState;

import java.awt.Color;


/**
 * A renderable view of the simulation. It listens for {@link SimulationEvent}s
 * and the default {@link #onEvent} bridge translates each event into one of the
 * focused hooks below, so concrete views implement plain rendering methods and
 * never have to unpack events themselves. Views that don't track per-species
 * colour or need a between-run reset inherit harmless no-ops.
 */
public interface SimulationView extends SimulationListener {

	/** Render the latest state of the simulation. */
	void showStatus(SimulationState state);

	/** Prepare for a fresh run. */
	default void reset() {
	}

	/** Associate a species with the colour used to depict it. */
	default void setColor(Class<?> species, Color color) {
	}


	@Override
	default void onEvent(SimulationEvent event) {
		switch (event) {
			case SimulationEvent.StatusUpdated e -> showStatus(e.state());
			case SimulationEvent.SimulationReset e -> reset();
			case SimulationEvent.SpeciesRegistered e -> setColor(e.species(), e.color());
		}
	}
}
