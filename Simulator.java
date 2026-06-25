import java.util.Random;
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
    // The probability that a shark will be created in any given grid position.
    private static final double SHARK_CREATION_PROBABILITY = 0.015; 
    // The probability that an orca will be created in any given grid position.
    private static final double ORCA_CREATION_PROBABILITY = 0.005; //0.005
    // The probability that a dolphin will be created in any given grid position.
    private static final double DOLPHIN_CREATION_PROBABILITY = 0.1;//0.1 
    // The probability that a seabird will be created in any given grid position.
    private static final double SEABIRD_CREATION_PROBABILITY = 0.1; //0.1
    // The probability that a shrimp will be created in any given grid position.
    private static final double SHRIMP_CREATION_PROBABILITY = 0.12; //0.12
    // The probability that a seaweed will be created in any given grid position.
    private static final double SEAWEED_CREATION_PROBABILITY = 0.08; 
    // The defualt start hour of a day. 
    private static final int startHour = 0;
    
    // List of animals in the field.
    private List<Animal> animals;
    // List of plants in the field. 
    private List<Plant> plants;
    // The current state of the field.
    private Field field;
    // The current hour of the day.
    private Time time;
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
        
        animals = new ArrayList<>();
        plants = new ArrayList<>();
        field = new Field(depth, width);
        
        // Set a start time of the simulator with a default value zreo. 
        time = new Time(startHour);

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        view.setColor(Shark.class, Color.BLUE);
        view.setColor(Orca.class, Color.RED);
        view.setColor(Dolphin.class, Color.YELLOW);
        view.setColor(Seabird.class, Color.BLACK);
        view.setColor(Shrimp.class, Color.ORANGE);
        view.setColor(Seaweed.class, Color.GREEN);
        
        // Setup a valid starting point.
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
            delay(60);   // uncomment this to run more slowly
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * shark, orca, dolphin, seabird, shrimp, and seaweed.
     * Simulating one step means passing an hour of the day.
     * The weather might change through each hour or not.
     */
    public void simulateOneStep()
    {
        step++;
        
        // Increase an hour of the current hour.
        time.timeClick();
        
        // Set a random weather of the current hour.
        Weather weather = randomWeather();
        
        // Provide space for newborn animals.
        List<Animal> newAnimals = new ArrayList<>(); 
        
        // Let all animals act.
        for(Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
            Animal animal = it.next();
            animal.act(newAnimals);
            if(! animal.isAlive()) {
                it.remove();
            }
        }
        // Let all plants grow.
        for(Iterator<Plant> it = plants.iterator(); it.hasNext(); ) {
            Plant plant = it.next();
            plant.act(weather);
            if(! plant.isAlive()) {
                it.remove();
            }
        }
               
        // Add the newly born sharks and dolphins to the main lists.
        animals.addAll(newAnimals);

        view.showStatus(step, field);
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        animals.clear();
        
        populate();
        
        // Show the starting state in the view.
        view.showStatus(step, field);
    }
    
    /**
     * Randomly populate the field with sharks, orcas, dolphins, seabirds, shrimps, and seaweeds.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        
        //populate animals
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= SHARK_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Shark shark = new Shark(true, field, location);
                    animals.add(shark);
                }
                else if(rand.nextDouble() <= ORCA_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Orca orca = new Orca(true, field, location);
                    animals.add(orca);
                }
                else if(rand.nextDouble() <= DOLPHIN_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Dolphin dolphin = new Dolphin(true, field, location);
                    animals.add(dolphin);
                }
                else if(rand.nextDouble() <= SEABIRD_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Seabird seabird = new Seabird(true, field, location, time);
                    animals.add(seabird);
                }
                else if(rand.nextDouble() <= SHRIMP_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Shrimp shrimp = new Shrimp(true, field, location);
                    animals.add(shrimp);
                }
                // else leave the location empty.
            }
        }
        
        //populate plants
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= SEAWEED_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Seaweed seaweed = new Seaweed(true, field, location);
                    plants.add(seaweed);
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
     * Get the weather of the current hour.
     * @return The random weather of the current hour. 
     */
    public Weather randomWeather() {
        Random rand = Randomizer.getRandom();
        int index = rand.nextInt(Weather.values().length);
        return Weather.values()[index];
    }
}
