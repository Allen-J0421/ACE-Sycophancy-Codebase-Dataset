import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing rabbits and foxes.
 *
 * @version 2022.03.01
 */
public class Simulator
{
     // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 150;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 120;
    // The probability that a hyena will be created in any given grid position.
    private static final double HYENA_CREATION_PROBABILITY = 0.02;
    // The probability that a lion will be created in any given grid position.
    private static final double LION_CREATION_PROBABILITY = 0.02;
    // The probability that a gazelle will be created in any given grid position.
    private static final double GAZELLE_CREATION_PROBABILITY = 0.02;    
    // The probability that a mouse will be created in any given grid position.
    private static final double MOUSE_CREATION_PROBABILITY = 0.02; 
    // The probability that a fennec fox will be created in any given grid position.
    private static final double FENNECFOX_CREATION_PROBABILITY = 0.02;  
    // The probability that grass will be created in any given grid position.
    private static final double GRASS_CREATION_PROBABILITY = 0.2;  
    // The probability that a lake will be created in any given grid position.
    private static final double LAKE_CREATION_PROBABILITY = 0.005;
    // List of all actors in simulation
    private List<Actor> actors;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;
    // Whether it is currently day or night - determines each animal's behaviour
    private boolean nightTime;
    
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
        // Starts simulator during day time
        nightTime = false;
        
        // Creates list to hold each actor
        actors = new ArrayList<>();
        field = new Field(depth, width);
        
        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width, this);
        view.setColor(Gazelle.class, Color.ORANGE);
        view.setColor(Lion.class, Color.YELLOW);
        view.setColor(Hyena.class, Color.RED);
        view.setColor(Mouse.class, Color.GRAY);
        view.setColor(FennecFox.class, Color.MAGENTA);
        view.setColor(Grass.class, Color.GREEN);
        view.setColor(Lake.class, Color.BLUE);
        
        // Setup a valid starting point.
        reset();
    }
    
    /**
     * Run the simulation from its current state for a reasonably long period,
     * (500 steps).
     */
    public void runLongSimulation()
    {
        simulate(750);
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
            delay(60);   // uncomment this to run more slowly  
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each animal.
     */
    public void simulateOneStep()
    {
        step++;
        
        // Alternates between day and night time every 2 steps
        if (step > 2 && step % 2 == 1) {
            nightTime = !nightTime;
        }
        
        // Provide space for newborn organisms
        List<Actor> newOrganisms = new ArrayList<>();
        SimulationContext context = new SimulationContext(actors, newOrganisms, nightTime);

        // Let all actors advance by one tick.
        Iterator<Actor> it = actors.iterator();
        while(it.hasNext()) {
            Actor actor = it.next();
            actor.tick(context);
            if (actor.isExpired()) {
                it.remove();
            }
        }
        // Add the newly born actors to the main list.
        actors.addAll(newOrganisms);
        
        // If it has rained, create new water
        if (context.shouldGenerateWater()) {
            generateWater();
        }
        
        view.showStatus(step, field);
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        actors.clear();

        populate();
        nightTime = false;
        
        // Show the starting state in the view.
        view.showStatus(step, field);
    }
    
    /**
     * Randomly populate the field with animals.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        
        // Creates default actors (diseases and weather)
        actors.add(new Covid(field));
        actors.add(new Leptospirosis(field));
        actors.add(new Weather());
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                populateCell(rand, row, col);
            }
        }
    }
    
    /** 
     * Creates random water spaces in free locations in field
     * Used when it rains to create new water sources
     */
    public void generateWater()
    {
        Random rand = Randomizer.getRandom();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                // if there is an empty space, chance of creating a new water source
                if (rand.nextDouble() < LAKE_CREATION_PROBABILITY/120) {
                    addLakeCluster(new Location(row, col), rand, true);
                }
            }
        }
    }

    /**
     * Populate one field cell according to the configured probabilities.
     * @param rand Shared random source.
     * @param row The row to populate.
     * @param col The column to populate.
     */
    private void populateCell(Random rand, int row, int col)
    {
        Location location = new Location(row, col);
        if(rand.nextDouble() <= LAKE_CREATION_PROBABILITY) {
            addLakeCluster(location, rand, false);
        }
        else if(rand.nextDouble() <= HYENA_CREATION_PROBABILITY) {
            actors.add(new Hyena(true, field, location));
        }
        else if(rand.nextDouble() <= LION_CREATION_PROBABILITY) {
            actors.add(new Lion(true, field, location));
        }
        else if(rand.nextDouble() <= GAZELLE_CREATION_PROBABILITY) {
            actors.add(new Gazelle(true, field, location));
        }
        else if(rand.nextDouble() <= MOUSE_CREATION_PROBABILITY) {
            actors.add(new Mouse(true, field, location));
        }
        else if(rand.nextDouble() <= FENNECFOX_CREATION_PROBABILITY) {
            actors.add(new FennecFox(true, field, location));
        }
        else if(rand.nextDouble() <= GRASS_CREATION_PROBABILITY) {
            actors.add(new Grass(true, field, location));
        }
    }

    /**
     * Create a lake and optionally extend it to adjacent locations.
     * @param location The origin location.
     * @param rand Shared random source.
     * @param sparse Whether adjacent lake tiles should be created probabilistically.
     */
    private void addLakeCluster(Location location, Random rand, boolean sparse)
    {
        actors.add(new Lake(true, field, location));
        List<Location> adjacent = field.adjacentLocations(location);
        for (Location adjacentLocation : adjacent) {
            if (!sparse || rand.nextBoolean()) {
                actors.add(new Lake(true, field, adjacentLocation));
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
     * Returns the current step of the simulator
     */
    public int getStep() 
    {
        return step;
    }
    
    /**
     * Returns whether it is day or night time
     */
    public boolean isNight()
    {
        return nightTime;
    }
}
