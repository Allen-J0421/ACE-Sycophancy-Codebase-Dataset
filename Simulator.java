import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing organisms.
 *
 * @version 2022.03.02
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 200;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 200;
    private static final int LONG_SIMULATION_STEPS = 4000;
    private static final int SIMULATION_DELAY_MS = 5;

    // Active entities in the simulation.
    private final List<Entity> entities;
    // The current state of the field.
    private final Field field;
    // A graphical view of the simulation.
    private final SimulatorView view;
    // The current simulation clock.
    private final SimulationClock clock;

    // The current weather in the simulation.
    private Weather currentWeather;

    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }
    
    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        int simulationDepth = depth > 0 ? depth : DEFAULT_DEPTH;
        int simulationWidth = width > 0 ? width : DEFAULT_WIDTH;
        
        entities = new ArrayList<>();
        field = new Field(simulationDepth, simulationWidth);
        clock = new SimulationClock();

        // Create a view of the state of each location in the field.
        view = new SimulatorView(simulationDepth, simulationWidth);

        // Setup a valid starting point
        reset();
    }
    
    /**
     * Run the simulation from its current state for a reasonably long period,
     * (4000 steps).
     */
    public void runLongSimulation()
    {
        simulate(LONG_SIMULATION_STEPS);
    }
    
    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        for(int step = 1; step <= numSteps && view.isViable(field); step++) {
            simulateOneStep();
            delay(SIMULATION_DELAY_MS);
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * organism.
     */
    public void simulateOneStep()
    {
        clock.advanceStep();

        // Provide space for newborn entities.
        List<Entity> newEntities = new ArrayList<>();
        // Let all entities act.
        for(Iterator<Entity> it = entities.iterator(); it.hasNext(); ) {
            Entity entity = it.next();

            entity.act(newEntities, currentWeather, clock.getCurrentTime());

            if(entity.isRemoved()) {
                it.remove();
            }
        }
               
        // Add the newly born entities to the main list.
        entities.addAll(newEntities);

        view.showStatus(clock.getStep(), field);
        view.updateTimeLabel(clock.getDay(), clock.getHour());
        view.updateEnvironmentLabel(currentWeather, clock.getCurrentTime());

        // Every hour, generate new weather if we are done with current weather.
        if (clock.isStartOfHour()) {
            currentWeather.generate();
        }

        // Update time of day at the configured interval.
        clock.advanceTimeOfDayIfNeeded();
    }

    public int getHour() {
        return clock.getHour();
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        clock.reset();
        currentWeather = new Weather(WeatherType.SUN);

        entities.clear();

        Populator populator = new Populator(view);
        populator.populate(entities, field);
        
        // Show the starting state in the view.
        view.showStatus(clock.getStep(), field);
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
            Thread.currentThread().interrupt();
        }
    }
}
