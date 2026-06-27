import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

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
    // List of animals in the field.
    private List<Organism> organisms;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulationDisplay view;
    // Event bus for lifecycle and environment notifications.
    private final SimulationEventBus eventBus;
    // Centralized engine for weather and time-of-day event rules.
    private SimulationRulesEngine rules;
    // Rules describing the main animal population.
    private final PopulationRule[] primaryPopulationRules;
    // Rules describing slower secondary occupants when no primary animal appears.
    private final PopulationRule[] secondaryPopulationRules;
    
    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(new SimulationContainer(), DEFAULT_DEPTH, DEFAULT_WIDTH, false);
    }
    
    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        this(new SimulationContainer(), depth, width, false);
    }

    public Simulator(SimulationContainer container, int depth, int width)
    {
        this(container, depth, width, false);
    }

    /**
     * Create a simulation field with the given size using a headless display.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     * @return A simulator that never instantiates Swing UI objects.
     */
    public static Simulator createHeadless(int depth, int width)
    {
        return new Simulator(new SimulationContainer(), depth, width, true);
    }

    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     * @param headless If true, a headless display is always used.
     */
    public static Simulator createHeadless(SimulationContainer container,
                                           int depth, int width)
    {
        return new Simulator(container, depth, width, true);
    }

    private Simulator(SimulationContainer container, int depth, int width,
                      boolean headless)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }
        
        eventBus = new SimulationEventBus();
        organisms = new ArrayList<>();
        field = new Field(depth, width);
        rules = container.createRulesEngine(eventBus);
        primaryPopulationRules = container.getPrimaryPopulationRules();
        secondaryPopulationRules = container.getSecondaryPopulationRules();

        // Create a view of the state of each location in the field.
        this.view = container.createDisplay(depth, width, headless, eventBus);
        for(PopulationRule rule : primaryPopulationRules) {
            this.view.setColor(rule.getSpecies(), rule.getColor());
        }
        for(PopulationRule rule : secondaryPopulationRules) {
            this.view.setColor(rule.getSpecies(), rule.getColor());
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

    /**
     * Return an immutable view of the current simulation state.
     *
     * @return The current simulation state.
     */
    public SimulationState getState()
    {
        FieldSnapshot snapshot = field.createSnapshot();
        return new SimulationState(step, snapshot,
                                   rules.getCurrentWeather(),
                                   rules.getTimeOfDay(step),
                                   snapshot.getActiveSpeciesCount() > 1);
    }

    /**
     * Return a snapshot of the field without updating the display.
     *
     * @return The current field snapshot.
     */
    public FieldSnapshot getFieldSnapshot()
    {
        return getState().getFieldSnapshot();
    }

    /**
     * Determine whether the simulation is still viable.
     *
     * @return True if more than one species is alive.
     */
    public boolean isViable()
    {
        return getState().isViable();
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
        for(int step = 1; step <= numSteps && isViable(); step++) {
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
        eventBus.publish(new SimulationStepStartedEvent(step));
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
        eventBus.publish(new SimulationStepCompletedEvent(getState()));
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        organisms.clear();
        populate();
        eventBus.publish(new SimulationResetRequestedEvent());
        eventBus.publish(new SimulationResetCompletedEvent(getState()));
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
        if(seedFromRules(rand, location, primaryPopulationRules)) {
            return;
        }
        seedFromRules(rand, location, secondaryPopulationRules);
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
                Organism organism = rule.create(field, location, eventBus);
                organisms.add(organism);
                eventBus.publish(new OrganismBornEvent(organism.getSpecies(),
                                                       location,
                                                       BirthSource.INITIAL_POPULATION));
                return true;
            }
        }
        return false;
    }
}
