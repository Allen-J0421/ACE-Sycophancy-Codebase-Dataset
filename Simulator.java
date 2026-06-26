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
    private static final int STEPS_PER_HOUR = 5;
    private static final int HOURS_PER_DAY = 24;
    private static final int STEPS_PER_DAY = STEPS_PER_HOUR * HOURS_PER_DAY;
    private static final int HOURS_PER_TIME_OF_DAY = 4;

    // List of organisms in the field.
    private List<Organism> organisms;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;
    // The current hour of the simulation.
    private int hour;
    // Indicates current state of time in the simulation.
    private TimeOfDay currentTime;

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
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }
        
        organisms = new ArrayList<>();
        field = new Field(depth, width);

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);

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
        step++;

        // Provide space for newborn organisms.
        List<Organism> newOrganisms = new ArrayList<>();
        // Let all organisms act.
        for(Iterator<Organism> it = organisms.iterator(); it.hasNext(); ) {
            Organism organism = it.next();

            organism.act(newOrganisms, currentWeather, currentTime);

            if(organism.isRemoved()) {
                it.remove();
            }
        }
               
        // Add the newly born organisms to the main list.
        organisms.addAll(newOrganisms);

        int day = step / STEPS_PER_DAY + 1;
        hour = (step / STEPS_PER_HOUR) % HOURS_PER_DAY + 1;


        view.showStatus(step, field);
        view.updateTimeLabel(day,hour);
        view.updateEnvironmentLabel(currentWeather, currentTime);

        // Every hour, generate new weather if we are done with current weather.
        if (step % STEPS_PER_HOUR == 0) {
            currentWeather.generate();
        }

        // Update time of day at the configured interval.
        if ((hour % HOURS_PER_TIME_OF_DAY == 0) && (step % STEPS_PER_HOUR == 0)) {
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
        currentWeather = new Weather(WeatherType.SUN);

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
