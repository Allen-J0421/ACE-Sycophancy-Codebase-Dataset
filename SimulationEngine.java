import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * Drives the predator-prey simulation: it owns the simulation state (the field,
 * the organisms, the current step, time and weather) and advances it each step.
 *
 * The engine does not know about any view. Instead it broadcasts {@link
 * SimulationEvent}s to registered {@link SimulationListener}s when it resets or
 * completes a step, so a GUI (or a logger, a test probe, ...) can observe the
 * simulation without the engine depending on it.
 *
 * @version 2022.03.02
 */
public class SimulationEngine
{
    // List of organisms in the field.
    private final List<Entity> organisms;
    // The current state of the field.
    private final Field field;
    // Seeds the field with organisms at the start of each run.
    private final Populator populator;
    // Parties observing the simulation (e.g. the GUI).
    private final List<SimulationListener> listeners;
    // The current step of the simulation.
    private int step;
    // The current hour of the simulation.
    private int hour;
    // Indicates current state of time in the simulation.
    private TimeOfDay currentTime;
    // The current weather in the simulation.
    private Weather currentWeather;

    /**
     * Create a simulation engine for a field of the given size, seeded by the
     * given populator.
     *
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     * @param populator The populator used to seed the field.
     */
    public SimulationEngine(int depth, int width, Populator populator)
    {
        this.organisms = new ArrayList<>();
        this.field = new Field(depth, width);
        this.populator = populator;
        this.listeners = new ArrayList<>();
    }

    /**
     * Register a listener to be notified of simulation events. Listeners should
     * be added before the simulation is reset or run so they receive every
     * event.
     *
     * @param listener The listener to add.
     */
    public void addListener(SimulationListener listener)
    {
        listeners.add(listener);
    }

    /**
     * Run the simulation from its current state for a reasonably long period,
     * (4000 steps).
     */
    public void runLongSimulation()
    {
        simulate(4000);
    }

    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        for(int s = 1; s <= numSteps && isViable(); s++) {
            simulateOneStep();
            delay(5);   // uncomment this to run more slowly
        }
    }

    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each organism.
     */
    public void simulateOneStep()
    {
        step++;

        // Provide space for newborn organisms.
        List<Entity> newOrganisms = new ArrayList<>();
        // Let all organisms act.
        for(Iterator<Entity> it = organisms.iterator(); it.hasNext(); ) {
            Entity entity = it.next();
            Organism organism = (Organism) entity;

            organism.act(newOrganisms, currentWeather, currentTime);

            if(organism.isRemoved()) {
                it.remove();
            }
        }

        // Add the newly born organisms to the main list.
        organisms.addAll(newOrganisms);

        int day = step/120 +1;
        hour = (step/5) % 24 + 1;

        // Notify observers that a step has completed.
        fireStepCompleted(day, hour);

        // every hour, generate new weather if we are done with current weather
        if (step % 5 == 0) {
            currentWeather.generate();
        }

        // update time
        if ((hour % 4 == 0) && (step % 5 == 0)) {
            currentTime = currentTime.next();
        }
    }

    public int getHour() {
        return hour;
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;

        currentTime = TimeOfDay.SUNRISE;
        currentWeather = new Weather(WeatherType.SUN); // make this random at start

        organisms.clear();

        populator.populate(organisms, field);

        // Notify observers of the fresh starting state.
        fireSimulationReset();
    }

    /**
     * Determine whether the simulation should continue to run: it is viable
     * while more than one species remains alive in the field.
     *
     * @return true if more than one species is present.
     */
    private boolean isViable()
    {
        Set<Class<?>> speciesPresent = new HashSet<>();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Object organism = field.getObjectAt(row, col);
                if(organism != null) {
                    speciesPresent.add(organism.getClass());
                }
            }
        }
        return speciesPresent.size() > 1;
    }

    /**
     * Broadcast a reset event describing the current starting state.
     */
    private void fireSimulationReset()
    {
        int day = step/120 + 1;
        int hourNow = (step/5) % 24 + 1;
        SimulationEvent event = new SimulationEvent(step, field, day, hourNow, currentWeather, currentTime);
        for(SimulationListener listener : listeners) {
            listener.simulationReset(event);
        }
    }

    /**
     * Broadcast a step-completed event describing the current state.
     *
     * @param day The current day.
     * @param hour The current hour.
     */
    private void fireStepCompleted(int day, int hour)
    {
        SimulationEvent event = new SimulationEvent(step, field, day, hour, currentWeather, currentTime);
        for(SimulationListener listener : listeners) {
            listener.simulationStepCompleted(event);
        }
    }

    /**
     * Pause for a given time.
     * @param millisec  The time to pause for, in milliseconds
     */
    private void delay(int millisec)
    {
        try {
            Thread.sleep(millisec);
        }
        catch (InterruptedException ie) {
            // wake up
        }
    }
}
