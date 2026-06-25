import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

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
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // The probability that a rabbit will be created in any given grid position.
    private static final double HIPPOPOTAMUS_CREATION_PROBABILITY = 0.01;
    // The probability that a rabbit will be created in any given grid position.
    private static final double LEOPARD_CREATION_PROBABILITY = 0.03; 
    // The probability that a rabbit will be created in any given grid position.
    private static final double BEAR_CREATION_PROBABILITY = 0.03; 
    // The probability that a rabbit will be created in any given grid position.
    private static final double MONKEY_CREATION_PROBABILITY = 0.08; 
    // The probability that a rabbit will be created in any given grid position.
    private static final double SLOTH_CREATION_PROBABILITY = 0.08; 
    // The probability that a plant will be created in any given grid position.
    private static final double PLANT_CREATION_PROBABILITY = 0.13;
    // A shared Weather object between organisms and the simulator
    public static final Weather weather = Weather.getWeather();


    // List of animals in the field.
    private List<Organism> organisms;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;
    
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
        view = new SimulatorView(depth, width, step);
        view.setColor(Hippopotamus.class, Color.DARK_GRAY);
        view.setColor(Leopard.class, Color.MAGENTA);
        view.setColor(Bear.class, Color.RED);
        view.setColor(Monkey.class, Color.ORANGE);
        view.setColor(Sloth.class, Color.YELLOW);        
        view.setColor(Plant.class, Color.GREEN);
        
        // Setup a valid starting point.
        reset();
    }
    
    // Accessor and mutator methods
    
    /**
     * Return the current step number of the simulation
     * 
     * @return int of the current step in the simulation
     */
    public int getStepNumber()
    {
        return step;
    }
    
    // Functional methods
    
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
            //delay(60);   // uncomment this to run more slowly
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
        randomWeather();
        // Provide space for newborn animals.
        List<Organism> newOrganisms = new ArrayList<>();        
        // Let all rabbits act.
        for(Iterator<Organism> it = organisms.iterator(); it.hasNext(); ) {
            Organism organism = it.next();
            
            if (organism.getIsDiurnal() && step % 80 <= 55) {
                organism.act(newOrganisms);
            }
            else if (!organism.getIsDiurnal() && step % 80 > 55) {
                organism.act(newOrganisms);
            }
            if(! organism.isAlive()) {
                it.remove();
            }
        }
                   
        // Add the newly born organisms to the main lists.
        organisms.addAll(newOrganisms);
        view.showStatus(step, field);
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        organisms.clear();
        populate();
        weather.resetWeather();
        
        // Show the starting state in the view.
        view.showStatus(step, field);
    }
    
    /**
     * Randomly populate the field with foxes and rabbits.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= HIPPOPOTAMUS_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Hippopotamus hippopotamus = new Hippopotamus(true, field, location);
                    organisms.add(hippopotamus);
                }
                else if(rand.nextDouble() <= LEOPARD_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Leopard leopard = new Leopard(true, field, location);
                    organisms.add(leopard);
                }
                else if(rand.nextDouble() <= BEAR_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Bear bear = new Bear(true, field, location);
                    organisms.add(bear);
                }
                else if(rand.nextDouble() <= MONKEY_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Monkey monkey = new Monkey(true, field, location);
                    organisms.add(monkey);
                }
                if(rand.nextDouble() <= SLOTH_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Sloth sloth = new Sloth(true, field, location);
                    organisms.add(sloth);
                }
                else if (rand.nextDouble() <= PLANT_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Plant plant = new Plant(true, field, location);
                    organisms.add(plant);
                }
                // else leave the location empty.
            }
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
    
    /**
     * Randomises the weather
     */
    public void randomWeather() {
        if (step % 450 == 0) {
            weather.changeWeather();
        }
    }
}
