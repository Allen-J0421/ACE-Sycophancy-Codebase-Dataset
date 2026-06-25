import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;
import java.lang.reflect.Constructor;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing rabbits and foxes.
 *
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
    // The default length of an hour in steps.
    private static final int DEFAULT_HOUR_LENGTH = 1;
    // The likelihood of fog weather.
    private static final double FOG_PROBABILITY = 0.1;
    // The likelihood of rain weather.
    private static final double RAIN_PROBABILITY = 0.3;

    // The species will be simulate in the field
    private static final Class[] SPECIES = new Class[]{
        Wolf.class, Mouse.class, Owl.class, Frog.class, Insect.class, Carpetweed.class, Ryegrass.class
    };
    // The probability that a species will be created for the species which will be simulate
    private static final double[] CREATION_PROBABILITY = new double[]{
        0.02, 0.06, 0.02, 0.095, 0.1, 0.2, 0.2
    };
    // The color of species in the view
    private static final Color[] COLORS = new Color[]{
        new Color(255,120,120), Color.ORANGE, new Color(179,101,6), Color.GREEN, new Color(38,217,203), Color.YELLOW, new Color(10,152,31)
    };



    // List of animals in the field.
    private List<Species> animals;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // The length of an hour in steps.
    private int hour_length;
    // The current hour of the day.
    private int hour;
    // The current state of time (day or night).
    private boolean isDay;
    
    // A graphical view of the simulation.
    private SimulatorView view;
    // A random number generator
    private Random rand = Randomizer.getRandom();
    
    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH, DEFAULT_HOUR_LENGTH);
    }
    
    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width, int hourlength)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
            hourlength = DEFAULT_HOUR_LENGTH;
            isDay = false;
        }
        
        animals = new ArrayList<>();
        field = new Field(depth, width);
        hour_length = hourlength;

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        view.simulator = this;
        for(int i=0;i<SPECIES.length;i++){
            view.setColor(SPECIES[i], COLORS[i]);
        }
        // view.setColor(Rabbit.class, Color.ORANGE);
        // view.setColor(Fox.class, Color.BLUE);
        // view.setColor(Owl.class, Color.RED);
        // view.setColor(Frog.class, Color.GREEN);
        // view.setColor(Insect.class, Color.DARK_GRAY);
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
    
    public void run10Steps()
    {
        simulate(10);
    }
    
    public void run40Steps()
    {
        simulate(40);
    }
    
    public void run100Steps()
    {
        simulate(100);
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
            // delay(30);   // uncomment this to run more slowly
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * animal.
     */
    public void simulateOneStep()
    {
        step++;

        // Provide space for newborn animals.
        List<Species> newAnimals = new ArrayList<>();    
        // Let all organisms act.
        for(Iterator<Species> it = animals.iterator(); it.hasNext(); ) {
            Species animal = it.next();
            animal.act(newAnimals, isDay);
            if(! animal.isAlive()) {
                it.remove();
            }
        }
        
        //checks whether to change from night to day, and vice versa.
        if (hour == 5){
            isDay = true;
        } else if (hour == 17){
            isDay = false;
        } else {;}
        //checks whether to step the current hour.
        if (step % (hour_length) == 0) {
            if (hour == 23) {
                hour = 0;   
            } else {hour++;}

            // set weather of field
            if(rand.nextDouble() < FOG_PROBABILITY){
                field.setWeather(Weather.FOG);
            }
            else if(rand.nextDouble() < RAIN_PROBABILITY){
                field.setWeather(Weather.RAIN);
            }
            else{
                field.setWeather(Weather.SUNNY);
            }
        }

        // Add the newly born organisms to the main lists.
        animals.addAll(newAnimals);
        
        view.showStatus(step, hour, isDay, field);
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        hour = 0;
        isDay = false;
        field.setWeather(Weather.SUNNY);
        
        animals.clear();
        Randomizer.reset();
        populate();
        
        // Show the starting state in the view.
        view.showStatus(step, hour, isDay, field);
    }
    
    /**
     * Randomly populate the field with foxes and rabbits.
     */
    private void populate()
    {
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                for(int i=0;i<SPECIES.length;i++){
                    // Check if the species have probability to create
                    if(rand.nextDouble() <= CREATION_PROBABILITY[i]){
                        Constructor[] cons = SPECIES[i].getConstructors();
                        try{
                            Object[] params = new Object[]{true, field, location};
                            animals.add((Species)cons[0].newInstance(params));
                        }catch(Exception e){}
                        break;
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
}
