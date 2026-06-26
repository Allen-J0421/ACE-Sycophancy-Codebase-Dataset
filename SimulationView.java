import java.awt.Color;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * An abstraction of the view that displays the simulation. The simulation
 * engine drives a SimulationView without depending on any particular GUI
 * toolkit, so the Swing/JFrame-based {@link SimulatorView} can be swapped for
 * an alternative (for example a headless or test view) without changing the
 * simulation logic.
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

    /**
     * Display the current state of the field.
     *
     * @param step Which iteration step it is.
     * @param field The field whose status is to be displayed.
     */
    void showStatus(int step, Field field);

    /**
     * Determine whether the simulation should continue to run.
     *
     * @param field The field to inspect.
     * @return true if the simulation is still viable.
     */
    boolean isViable(Field field);

    /**
     * Display the current day and hour.
     *
     * @param day The current day.
     * @param hour The current hour.
     */
    void updateTimeLabel(int day, int hour);

    /**
     * Display the current weather and time of day.
     *
     * @param weather The current weather.
     * @param time The current time of day.
     */
    void updateEnvironmentLabel(Weather weather, TimeOfDay time);
}
