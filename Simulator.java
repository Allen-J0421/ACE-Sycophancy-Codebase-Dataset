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

    // The data-driven population table. Each grid cell is offered to every group
    // in turn; within a group the species are tried in order and at most one is
    // created (the first whose probability roll succeeds), exactly mirroring the
    // original nested if/else-if chains. To add or rebalance a species, edit this
    // table rather than the populate() logic.
    private static final List<List<SpeciesSpawn>> SPAWN_GROUPS = List.of(
        List.of(
            new SpeciesSpawn(HIPPOPOTAMUS_CREATION_PROBABILITY, Hippopotamus::new),
            new SpeciesSpawn(LEOPARD_CREATION_PROBABILITY, Leopard::new),
            new SpeciesSpawn(BEAR_CREATION_PROBABILITY, Bear::new),
            new SpeciesSpawn(MONKEY_CREATION_PROBABILITY, Monkey::new)
        ),
        List.of(
            new SpeciesSpawn(SLOTH_CREATION_PROBABILITY, Sloth::new),
            new SpeciesSpawn(PLANT_CREATION_PROBABILITY, Plant::new)
        )
    );

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
        // Collect newborn organisms produced this step in a shared nursery.
        Nursery nursery = new Nursery();
        // Let all organisms act.
        for(Iterator<Organism> it = organisms.iterator(); it.hasNext(); ) {
            Organism organism = it.next();

            if (organism.getIsDiurnal() && step % 80 <= 55) {
                organism.act(nursery);
            }
            else if (!organism.getIsDiurnal() && step % 80 > 55) {
                organism.act(nursery);
            }
            if(! organism.isAlive()) {
                it.remove();
            }
        }

        // Add the newly born organisms to the main population.
        organisms.addAll(nursery.getNewborns());
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
                for(List<SpeciesSpawn> group : SPAWN_GROUPS) {
                    for(SpeciesSpawn spawn : group) {
                        if(rand.nextDouble() <= spawn.creationProbability) {
                            Location location = new Location(row, col);
                            organisms.add(spawn.factory.create(true, field, location));
                            break;
                        }
                    }
                }
                // Any cell left untouched by every group stays empty.
            }
        }
    }

    /**
     * Creates an organism of a particular species at a position in the field.
     * Implemented by each species' constructor (e.g. {@code Bear::new}).
     */
    @FunctionalInterface
    private interface OrganismFactory
    {
        Organism create(boolean randomAge, Field field, Location location);
    }

    /**
     * A single entry in the population table: the per-cell probability that a
     * species is created, together with the factory that creates it.
     */
    private static class SpeciesSpawn
    {
        // The probability this species is created at any given grid cell.
        private final double creationProbability;
        // The factory used to create an individual of this species.
        private final OrganismFactory factory;

        private SpeciesSpawn(double creationProbability, OrganismFactory factory)
        {
            this.creationProbability = creationProbability;
            this.factory = factory;
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
