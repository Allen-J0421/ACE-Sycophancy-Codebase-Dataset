
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing rabbits and foxes.
 *
 * @version 2016.02.29 (2)
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // The probability that a fox will be created in any given grid position.
    // private static final double FOX_CREATION_PROBABILITY = 0.02;
    // The probability that a rabbit will be created in any given grid position.
    // private static final double RABBIT_CREATION_PROBABILITY = 0.08;  

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
    

    public static void main(String [] args ) {
        Simulator x = new Simulator();
        // List<Double> values = new ArrayList<Double>();
        // for(String i : args) {
        //     values.add(Double.parseDouble(i));
        // }
        

        //Six values, 5 animals, 30 values in total, 
        // int partitionSize = 6;
        // List<List<Double>> partitions = new ArrayList<>();

        // for (int i=0; i<values.size(); i += partitionSize) {
        //     partitions.add(values.subList(i, Math.min(i + partitionSize, values.size())));
        // }

        // for (int i = 0; i < partitions.size(); i ++) {
        //     List<Double> controls = partitions.get(i);
        //     PopulationControls newControls = null;
        //     if (partitions.get(i).size() == 6) {
        //         newControls = new PopulationControls(
        //             controls.get(0),
        //             controls.get(1),
        //             controls.get(2),
        //             controls.get(3),
        //             controls.get(4),
        //             controls.get(5)
        //         );
        //     }


        //     switch (i) {
        //     case 0:
        //         Fox.POPULATION_CONTROLS = newControls;
        //         break;
        //     case 1:
        //         Rabbit.POPULATION_CONTROLS = newControls;
        //         break;
        //     case 2:
        //         Squirrel.POPULATION_CONTROLS = newControls;
        //         break;
        //     case 3:
        //         Rat.POPULATION_CONTROLS = newControls;
        //         break;
        //     case 4:
        //         Hawk.POPULATION_CONTROLS = newControls;
        //         break;
        //     default: 
        //         System.out.println("outofbounds");

        //     }
        //     // System.out.println(i);
        // }

        // System.out.println("Finished INIT");


        
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

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        // view.setColor(Rabbit.class, Color.ORANGE);
        // view.setColor(Fox.class, Color.BLUE);
        
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
