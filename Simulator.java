import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing lions, zebras, and other organisms.
 *
 * @version 2022.03.02
 */
public class Simulator
{
    // Default grid dimensions.
    private static final int DEFAULT_WIDTH = 200;
    private static final int DEFAULT_DEPTH = 200;

    // Simulation time constants.
    private static final int STEPS_PER_HOUR   = 5;
    private static final int HOURS_PER_DAY    = 24;
    private static final int HOURS_PER_PERIOD = 4;   // how often TimeOfDay advances

    // All organisms currently in the simulation.
    private List<Organism> organisms;
    // The grid.
    private Field field;
    // Current step count.
    private int step;
    // The GUI.
    private SimulatorView view;
    // Current hour within the day (1-based).
    private int hour;
    // Current time-of-day period.
    private TimeOfDay currentTime;
    // Current weather.
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
        field     = new Field(depth, width);
        view      = new SimulatorView(depth, width);

        reset();
    }

    /**
     * Run the simulation from its current state for a reasonably long period
     * (4000 steps).
     */
    public void runLongSimulation()
    {
        simulate(4000);
    }

    /**
     * Run the simulation from its current state for the given number of steps.
     * Stops early if the simulation ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        for(int step = 1; step <= numSteps && view.isViable(field); step++) {
            simulateOneStep();
            delay(5);
        }
    }

    /**
     * Run the simulation from its current state for a single step.
     * Each organism acts, newborns are collected, then environment is updated.
     */
    public void simulateOneStep()
    {
        step++;

        List<Organism> newOrganisms = new ArrayList<>();
        for(Iterator<Organism> it = organisms.iterator(); it.hasNext(); ) {
            Organism organism = it.next();
            organism.act(newOrganisms, currentWeather, currentTime);
            if(organism.isRemoved()) {
                it.remove();
            }
        }
        organisms.addAll(newOrganisms);

        int day = step / (STEPS_PER_HOUR * HOURS_PER_DAY) + 1;
        hour = (step / STEPS_PER_HOUR) % HOURS_PER_DAY + 1;

        view.showStatus(step, field);
        view.updateTimeLabel(day, hour);
        view.updateEnvironmentLabel(currentWeather, currentTime);

        if(step % STEPS_PER_HOUR == 0) {
            currentWeather.generate();
        }

        if(hour % HOURS_PER_PERIOD == 0 && step % STEPS_PER_HOUR == 0) {
            currentTime = currentTime.next();
        }
    }

    public int getHour() { return hour; }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        currentTime    = TimeOfDay.SUNRISE;
        currentWeather = new Weather(WeatherType.SUN);

        organisms.clear();

        Populator populator = new Populator(view);
        populator.populate(organisms, field);

        view.showStatus(step, field);
    }

    /**
     * Pause for a given time.
     * @param millisec The time to pause for, in milliseconds.
     */
    private void delay(int millisec)
    {
        try {
            Thread.sleep(millisec);
        }
        catch(InterruptedException ie) {
            // wake up
        }
    }
}
