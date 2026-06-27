import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing bears, badgers, wolves, frogs and hedgehogs.
 *
 * @version 2022.02.24 (2)
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.

    private static final int DEFAULT_WIDTH = 150;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 100;
    // The number of simulation steps between day/night transitions.
    private static final int DAY_NIGHT_CYCLE_LENGTH = 35;

    // The probability that a hedgehog will be created in any given grid position.
    private static final double HEDGEHOG_CREATION_PROBABILITY = 0.115;
    // The probability that a bear will be created in any given grid position.
    private static final double BEAR_CREATION_PROBABILITY = 0.005;
    // The probability that a badger will be created in any given grid position.
    private static final double BADGER_CREATION_PROBABILITY = 0.1;//0.2;
    // The probability that a frog will be created in any given grid position.
    private static final double FROG_CREATION_PROBABILITY = 0.125;
    // The probability that a wolf will be created in any given grid position.
    private static final double WOLF_CREATION_PROBABILITY = 0.005;//0.25;
    // The probability that a plant will be created in any given grid position.
    private static final double PLANT_CREATION_PROBABILITY = 0.2;

    // List of animals in the field.
    private List<LivingBeing> animals;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;

    private boolean night;
    // A graphical view of the simulation.
    private SimulatorView view;
    
    public static void main(String[] args) {
        Simulator sim = new Simulator();
        sim.runMediumSimulation();
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
        
        //colours of the creatures
        view.setColor(Hedgehog.class, Color.YELLOW);
        view.setColor(Bear.class, Color.RED);
        view.setColor(Badger.class, Color.CYAN);
        view.setColor(Frog.class, Color.MAGENTA);
        view.setColor(Wolf.class, Color.PINK);
        view.setColor(Plant.class, Color.GREEN);

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
     * Run the simulation from its current state for a medium period, 1000 steps
     */
    public void runMediumSimulation() {
        simulate(1000);
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
            // delay(60);   // uncomment this to run more slowl
        }
    }

    /**
     * Returns if it is night time or not
     */
    public boolean isNightTime() {
        return night;
    }

    /**
     * Switches between night time and day time
     */
    private void switchNight() {
        night = !night;
        LivingBeing.setNight(night);
        view.setNight(night);
    }

    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * animal
     */
    public void simulateOneStep()
    {
        step++;
        if(step % DAY_NIGHT_CYCLE_LENGTH == 0) {
            switchNight();
        }

        // Provide space for newborn animals.
        List<LivingBeing> newAnimals = new ArrayList<>();        
        // Let all animals act.
        for(Iterator<LivingBeing> it = animals.iterator(); it.hasNext(); ) {

            LivingBeing animal = it.next();
            animal.act(newAnimals);

            if(! animal.isAlive()) {
                it.remove();
            }
        }

        // Add the newly born animals to the main lists.
        animals.addAll(newAnimals);

        view.showStatus(step, field);
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        night = false;
        LivingBeing.setNight(night);
        view.setNight(night);
        animals.clear();
        populate();

        // Show the starting state in the view.
        view.showStatus(step, field);
    }

    /**
     * Randomly populate the field with foxes and rabbits and hedgehogs
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        
        //goes through each cell and populates based on given probabilty 
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                if(rand.nextDouble() <= HEDGEHOG_CREATION_PROBABILITY) {
                    Hedgehog hedgehog = new Hedgehog(true, field, location);
                    animals.add(hedgehog);
                }
                else if(rand.nextDouble() <= BEAR_CREATION_PROBABILITY) {
                    Bear bear = new Bear(true, field, location);
                    animals.add(bear);
                }
                else if(rand.nextDouble() <= BADGER_CREATION_PROBABILITY) {
                    Badger badger = new Badger(true, field, location);
                    animals.add(badger);
                }
                else if(rand.nextDouble() <= FROG_CREATION_PROBABILITY) {
                    Frog frog = new Frog(true,field,location);
                    animals.add(frog);
                }
                else if(rand.nextDouble() <= WOLF_CREATION_PROBABILITY) {
                    Wolf wolfie = new Wolf(true,field,location);
                    animals.add(wolfie);
                }
                else if(rand.nextDouble() <= PLANT_CREATION_PROBABILITY) {
                    Plant plant = new Plant(true,field,location);
                    animals.add(plant);
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
}
