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
    // Rules describing the main animal population.
    private static final PopulationRule[] PRIMARY_POPULATION_RULES = {
        new PopulationRule(HIPPOPOTAMUS_CREATION_PROBABILITY, Hippopotamus::new,
                           Species.HIPPOPOTAMUS, Color.DARK_GRAY),
        new PopulationRule(LEOPARD_CREATION_PROBABILITY, Leopard::new,
                           Species.LEOPARD, Color.MAGENTA),
        new PopulationRule(BEAR_CREATION_PROBABILITY, Bear::new,
                           Species.BEAR, Color.RED),
        new PopulationRule(MONKEY_CREATION_PROBABILITY, Monkey::new,
                           Species.MONKEY, Color.ORANGE)
    };
    // Rules describing slower secondary occupants when no primary animal appears.
    private static final PopulationRule[] SECONDARY_POPULATION_RULES = {
        new PopulationRule(SLOTH_CREATION_PROBABILITY, Sloth::new,
                           Species.SLOTH, Color.YELLOW),
        new PopulationRule(PLANT_CREATION_PROBABILITY, Plant::new,
                           Species.PLANT, Color.GREEN)
    };

    // List of animals in the field.
    private List<Organism> organisms;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;
    // Centralized engine for weather and time-of-day event rules.
    private SimulationRulesEngine rules;
    
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
        rules = new SimulationRulesEngine();

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width, rules);
        for(PopulationRule rule : PRIMARY_POPULATION_RULES) {
            view.setColor(rule.getSpecies(), rule.getColor());
        }
        for(PopulationRule rule : SECONDARY_POPULATION_RULES) {
            view.setColor(rule.getSpecies(), rule.getColor());
        }
        
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
        for(int step = 1; step <= numSteps && view.isViable(field.createSnapshot()); step++) {
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
        rules.applyStepEvents(step);
        // Provide space for newborn animals.
        List<Organism> newOrganisms = new ArrayList<>();        
        // Let all rabbits act.
        for(Iterator<Organism> it = organisms.iterator(); it.hasNext(); ) {
            Organism organism = it.next();
            
            if (rules.canAct(organism, step)) {
                organism.act(newOrganisms);
            }
            if(! organism.isAlive()) {
                it.remove();
            }
        }
                   
        // Add the newly born organisms to the main lists.
        organisms.addAll(newOrganisms);
        view.showStatus(step, field.createSnapshot());
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        organisms.clear();
        populate();
        rules.reset();
        
        // Show the starting state in the view.
        view.showStatus(step, field.createSnapshot());
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
                seedLocation(rand, new Location(row, col));
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
     * Populate one location by selecting the first species whose creation
     * probability passes. Each cell can contain at most one organism.
     */
    private void seedLocation(Random rand, Location location)
    {
        if(seedFromRules(rand, location, PRIMARY_POPULATION_RULES)) {
            return;
        }
        seedFromRules(rand, location, SECONDARY_POPULATION_RULES);
    }

    /**
     * Try each creation rule in sequence until one organism is created.
     *
     * @return True if a species was placed at the location.
     */
    private boolean seedFromRules(Random rand, Location location, PopulationRule[] rules)
    {
        for(PopulationRule rule : rules) {
            if(rand.nextDouble() <= rule.getCreationProbability()) {
                organisms.add(rule.create(field, location));
                return true;
            }
        }
        return false;
    }
}
