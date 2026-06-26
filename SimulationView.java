import java.awt.Color;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * The view abstraction used to configure how the simulation is displayed -
 * specifically, the colour used to draw each species. Per-step display updates
 * are delivered separately through the {@link SimulationListener} event
 * mechanism, so the simulation engine does not depend on this interface.
 *
 * @version 2022.03.02
 */
public interface SimulationView {

    /**
     * Register the colour used to draw a given species.
     *
     * @param animalClass The species' Class object.
     * @param color The colour to use for that species.
     */
    void setColor(Class<?> animalClass, Color color);
}
