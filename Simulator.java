import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
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
    // List of potential diseases that could occur in simulation
    private List<Disease> diseases;
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

        Weather weather = null;
        
        // Let all actors act
        Iterator<Actor> it = actors.iterator();
        while(it.hasNext()) {
            boolean canAct = true;
            Actor actor = it.next();
            if (actor instanceof Weather) {
                weather = (Weather) actor;
            }
            if (actor instanceof Organism) {
                // low chance of each animal being infected wiht a disease
                generateDisease(actor);
                Organism organism = (Organism) actor;
                if (organism instanceof Animal) {
                    Animal animal = (Animal) organism;   
                    // animals only act during certain times of day (i.e. if day but nocturnal, won't act)
                    if (nightTime != animal.isNocturnal()) {
                        canAct = false;
                    }
                }
                if (canAct) {
                    actor.act(newOrganisms);
                }
                if(!(organism.isAlive())) {
                    it.remove();
                }
            }
            else {
                actor.act(actors);  
            }
            
            // Removes any empty water sources from the field
            if (actor instanceof WaterSources) {
                WaterSources water = (WaterSources) actor;
                // if a water source is empty, remove it from simulation
                if (water.isEmpty()) {
                    it.remove();
                }
            }
        }
        // Add the newly born animals to the main lists.
        actors.addAll(newOrganisms);      
        
        // If it has rained, create new water
        if (weather != null && weather.generateNewWater()) {
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
        diseases = new ArrayList<>();
        Disease covid = new Covid(field);
        Disease leptospirosis = new Leptospirosis(field);
        diseases.addAll(Arrays.asList(covid, leptospirosis));
        
        Weather weather = new Weather(this);
        
        actors.addAll(diseases);
        actors.add(weather);
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= LAKE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Lake lake = new Lake(true, field, location);
                    actors.add(lake);
                    // creates lakes over an area of 9 grid spaces
                    List<Location> adjacent = field.adjacentLocations(location);
                    for (int i = 0; i<adjacent.size(); i++) {
                        Lake lake2 = new Lake(true, field, adjacent.get(i));
                        actors.add(lake2);
                    }
                }
                else if(rand.nextDouble() <= HYENA_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Hyena hyena = new Hyena(true, field, location);
                    actors.add(hyena);
                }
                else if(rand.nextDouble() <= LION_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Lion lion = new Lion(true, field, location);
                    actors.add(lion);
                }
                else if(rand.nextDouble() <= GAZELLE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Gazelle gazelle= new Gazelle(true, field, location);
                    actors.add(gazelle);
                }
                else if(rand.nextDouble() <= MOUSE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Mouse mouse= new Mouse(true, field, location);
                    actors.add(mouse);
                }
                else if(rand.nextDouble() <= FENNECFOX_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    FennecFox fennecFox= new FennecFox(true, field, location);
                    actors.add(fennecFox);
                }
                else if(rand.nextDouble() <= GRASS_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Grass grass = new Grass(true, field, location);
                    actors.add(grass);
                }
                // else leave the location empty.
            }
        }
    }
    
    /**
     * Random chance of a disease being generated in the population
     * @param actor An individual which could be infected
     */
    private void generateDisease(Actor actor)
    {
        Random rand = Randomizer.getRandom();
        for (Disease disease: diseases) {
            // random low chance of an individual developing a disease
            if(rand.nextDouble() <= disease.getProbability()) {
                // only infects certain species (must be organisms)
                if (disease.getSpecies().contains(actor.getClass().getName())) {
                    // adds infected individual to diseased list and sets infected to true
                    disease.addIndividual(actor);
                    Organism organism = (Organism) actor;
                    organism.setInfected();
                }
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
        ArrayList<WaterSources> newWater  = new ArrayList<>();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                // if there is an empty space, chance of creating a new water source
                if (rand.nextDouble() < LAKE_CREATION_PROBABILITY/120) {
                    Location location = new Location(row, col);
                    Lake lake = new Lake(true, field, location);
                    actors.add(lake);
                    List<Location> adjacent = field.adjacentLocations(location);
                    for (int i = 0; i<adjacent.size(); i++) {
                        if (rand.nextBoolean()) {
                            Lake lake2 = new Lake(true, field, adjacent.get(i));
                            actors.add(lake2);
                        }
                    }
                }
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
