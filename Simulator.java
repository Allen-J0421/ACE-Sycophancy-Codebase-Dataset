import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing rabbits and foxes.
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
        simulate(4000);
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
            delay(5);   // uncomment this to run more slowly
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * fox and rabbit.
     */
    public void simulateOneStep()
    {
        step++;

        // Provide space for newborn organisms.
        List<Organism> newOrganisms = new ArrayList<>();
        // Let all rabbits act.
        for(Iterator<Organism> it = organisms.iterator(); it.hasNext(); ) {
            Organism organism = it.next();
            organism.act(newOrganisms, currentWeather, currentTime);

            if(organism.isRemoved()) {
                it.remove();
            }
        }
               
        // Add the newly born foxes and rabbits to the main lists.
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
