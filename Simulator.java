
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing rabbits and foxes.
 *
 * @version 2016.02.29 (2)
 */
public class Simulator
{
    private static final int DEFAULT_WIDTH = 120;
    private static final int DEFAULT_DEPTH = 80;

    private SimulatorState state = new SimulatorState();  


    private TimeTracker timeOfDay = new TimeTracker(); 

    // List of animals in the field.
    private List<Simulatable> animals;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;
    

    public static void main(String[] args) {
        Simulator x = new Simulator();
        x.runLongSimulation();
    }

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
        
        animals = new ArrayList<>();
        field = new Field(depth, width);

        view = new SimulatorView(depth, width);

        // Setup a valid starting point.
        reset();
    }
    
    /**
     * Run the simulation from its current state for a reasonably long period,
     * (4000 steps).
     */
    public void runLongSimulation()
    {
        simulate(10000);
        System.out.println("DONE");
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
            // delay(60);   // uncomment this to run more slowly
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
        state.setCurrentStats(view.getCurrentStats());
        // Provide space for newborn animals.
        List<Simulatable> newAnimals = new ArrayList<>();        
        // Let all rabbits act.
        for(Iterator<Simulatable> it = animals.iterator(); it.hasNext(); ) {
            Simulatable animal = it.next();
            // System.out.println(state.getAggregatedProbabilityReduction());
            animal.act(newAnimals, state);
            if(! animal.isAlive()) {
                it.remove();
            }
        }
               
        // Add the newly born foxes and rabbits to the main lists.
        animals.addAll(newAnimals);

        view.showStatus(step, field, timeOfDay, state.getCurrentWeather());
        state.setNormalisedTime(timeOfDay.normalisedTime());
        state.changeCurrentWeather();
        
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        timeOfDay.setTime(0);
        animals.clear();
        state.setCurrentWeather(Weather.Sunny);
        populate();
        
        // Show the starting state in the view.
        view.showStatus(step, field, timeOfDay, state.getCurrentWeather());
    }
    
    /**
     * Randomly populate the field with foxes and rabbits.
     */
    private void populate()
    {
        
        field.clear();
        Generator gen = new Generator();
        animals = gen.createPlayers(field);
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
