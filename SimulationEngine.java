import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * Drives the predator-prey simulation: it owns the simulation state (the field,
 * the organisms, the current step, time and weather) and advances it each step.
 *
 * The engine talks to the view only through the {@link SimulationView}
 * abstraction, so the simulation logic is decoupled from any particular GUI
 * (such as the Swing/JFrame-based SimulatorView).
 *
 * @version 2022.03.02
 */
public class SimulationEngine
{
    // List of organisms in the field.
    private final List<Entity> organisms;
    // The current state of the field.
    private final Field field;
    // The view used to display the simulation and report viability.
    private final SimulationView view;
    // The current step of the simulation.
    private int step;
    // The current hour of the simulation.
    private int hour;
    // Indicates current state of time in the simulation.
    private TimeOfDay currentTime;
    // The current weather in the simulation.
    private Weather currentWeather;

    /**
     * Create a simulation engine for a field of the given size, displayed
     * through the given view.
     *
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     * @param view The view used to display the simulation.
     */
    public SimulationEngine(int depth, int width, SimulationView view)
    {
        this.organisms = new ArrayList<>();
        this.field = new Field(depth, width);
        this.view = view;

        // Setup a valid starting point
        reset();
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
        for(int s = 1; s <= numSteps && view.isViable(field); s++) {
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


        view.showStatus(step, field);
        view.updateTimeLabel(day,hour);
        view.updateEnvironmentLabel(currentWeather, currentTime);

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

        Populator populator = new Populator(view);
        populator.populate(organisms, field);

        // Show the starting state in the view.
        view.showStatus(step, field);
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
