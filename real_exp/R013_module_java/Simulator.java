import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing rabbits and foxes.
 *
 * @version 2022.02.xx 
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid. 
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid. 
    private static final int DEFAULT_DEPTH = 80;
    // The probability that a fox will be created in any given grid position.
    private static final double LION_CREATION_PROBABILITY = 0.01;
    // The probability that a rabbit will be created in any given grid position.
    private static final double DEER_CREATION_PROBABILITY = 0.02;    
    // The probability that a owl will be created in any given grid position.
    private static final double OWL_CREATION_PROBABILITY = 0.05;
    // The probability that a cat will be created in any given grid position.
    private static final double CAT_CREATION_PROBABILITY = 0.05;
    // The probability that a mouse will be created in any given grid position.
    private static final double MOUSE_CREATION_PROBABILITY = 0.04;    
    // The probability that a grass will be created in any given grid position.
    private static final double GRASS_CREATION_PROBABILITY = 0.4;    

    // List of animals in the field.
    private List<Animal> animals;
    // List of animals in the field.
    private List<Plant> plants;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // The current weather of the simulation.
    private String weather;
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

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        view.setColor(Deer.class, Color.BLUE);
        view.setColor(Lion.class, Color.RED);
        view.setColor(Mouse.class, Color.YELLOW);
        view.setColor(Owl.class, Color.ORANGE);
        view.setColor(Cat.class, Color.PINK);
        view.setColor(Grass.class, Color.GREEN);
        
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
     * fox and rabbit.
     */
    public void simulateOneStep()
    {
        step++;
        sim_weather();
        
        // Provide space for newborn animals.
        List<Animal> newAnimals = new ArrayList<>();        
        // Let all animals act.
        for(Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
            Animal animal = it.next();
            animal.act(newAnimals, step, weather);
            if(! animal.isAlive()) {
                it.remove();
            }
        }
               
        // Add the newly born foxes and rabbits to the main lists.
        animals.addAll(newAnimals);

        // Let all plants act.
        for(Iterator<Plant> it = plants.iterator(); it.hasNext(); ) {
            Plant plant = it.next();
            plant.act(step, weather);
            if(! plant.isAlive()) {
                it.remove();
            }
        }
        
        view.showStatus(step, field, weather);
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        animals.clear();
        plants.clear();
        populate();
        sim_weather();
        
        // Show the starting state in the view.
        view.showStatus(step, field, weather);
    }
    
    /**
     * Randomly populate the field with Lions, Cats, Owls, Deers and mouses.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= LION_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Lion lion = new Lion(true, field, location);
                    animals.add(lion);
                }
                else if(rand.nextDouble() <= DEER_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Deer deer = new Deer(true, field, location);
                    animals.add(deer);
                }
                else if(rand.nextDouble() <= OWL_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Owl owl = new Owl(true, field, location);
                    animals.add(owl);
                }
                else if(rand.nextDouble() <= MOUSE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Mouse mouse = new Mouse(true, field, location);
                    animals.add(mouse);
                }
                else if(rand.nextDouble() <= CAT_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Cat cat = new Cat(true, field, location);
                    animals.add(cat);
                }
                else if(rand.nextDouble() <= GRASS_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Grass grass = new Grass(true, field, location);
                    plants.add(grass);
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
     * 
     * 
     */
    private void sim_weather()
    {
        Random rand = Randomizer.getRandom();
        if(rand.nextDouble() <= 0.33) {
            weather = "Foggy";
        }
        else if (rand.nextDouble() <= 0.33) {
            weather = "Rainy";
        }
        else {
            weather = "Sunny";
        }
    }

    /**
     * 
     * 
     */
    private String get_weather()
    {
        return weather;
    }
}
