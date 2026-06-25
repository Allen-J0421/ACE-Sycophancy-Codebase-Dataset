import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A predator-prey simulator, based on a rectangular field
 * containing animals and nature elements.
 *
 * @version 2022.25.02
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // The probability that Reddy will be created in any given grid position.
    private static final double REDDY_CREATION_PROBABILITY = 0.01;
    // The probability that Yellowy will be created in any given grid position.
    private static final double YELLOWY_CREATION_PROBABILITY = 0.01;
    // The probability that Purpely will be created in any given grid position.
    private static final double PURPLY_CREATION_PROBABILITY = 0.01;
    // The probability that Greeny will be created in any given grid position.
    private static final double GREENY_CREATION_PROBABILITY = 0.04; // 02;
    // The probability that Cyany will be created in any given grid position.
    private static final double CYANY_CREATION_PROBABILITY = 0.06;
    // The probability that Bluey will be created in any given grid position.
    private static final double BLUY_CREATION_PROBABILITY = 0.15;  //0.08;
    
    // The probability that a tree will be created in any given grid position
    private static final double TREE_CREATION_PROBABILITY = 0.001;
    
    
    public static int HOURS;

    // List of animals in the field.
    private List<Animal> animals;
    // List of all natural creations in the field.
    private List<Nature> nature;
    // The current state of the field.
    private Field field;
    // The current state of the nature field
    private NatureField natureField;
    //The current state of the weather
    private Weather weather;
    // The current step of the simulation.
    private int step;
    
    
    // The current time in the simulation
    private String time;
    
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
        nature = new ArrayList<>();
        field = new Field(depth, width);
        natureField = new NatureField(depth, width);
        weather = new Weather();
        
        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width, field, weather);
        view.setColor(Reddy.class, Color.RED);
        view.setColor(Yellowy.class, Color.YELLOW);
        view.setColor(Purply.class, new Color(192,0,255));
        view.setColor(Bluy.class, Color.BLUE);
        view.setColor(Greeny.class, Color.GREEN);
        view.setColor(Cyany.class, Color.CYAN);
        view.setColor(Tree.class, Color.PINK);
        view.setColor(Weed.class, Color.BLACK);
        
        
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
            // delay(60);   // uncomment this to run more slowly
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * animal and nature element.
     */
    public void simulateOneStep()
    {
        Random rand = Randomizer.getRandom();
        
        // Weather changes every 4 hours
        if (step%240 == 0) {
            weather.weatherAct(field, natureField);
        }
        
        step++;
        
        // Calculating time according to steps that have passed 24hrs = 1440 steps
        double currentDaySteps = (double)(step%1440);
        double generate24Hours = currentDaySteps/60;
        HOURS = (int)generate24Hours;
        int totalMinutesDay = (int)(generate24Hours*60);
        int minutes = totalMinutesDay%60;
        if (HOURS < 10 && minutes <10) {
            time = "0" + HOURS + ":0" + minutes;
        }
        else if (HOURS < 10){
            time = "0" + HOURS + ":" + minutes;
        }
        else if (HOURS >= 10 && minutes >=10) {
            time = "" + HOURS + ":" + minutes;
        }
        else if(minutes <10) {
            time = "" + HOURS + ":0" + minutes;
        }
        
        if (weather.getDaysNotRained() < 2) {
            // Grow weed at a given rate (every 10 steps) or have a chance to place a tree when the weed is growing
            for(int row = 0; row < field.getDepth(); row++) {
                for(int col = 0; col < field.getWidth(); col++) {
                    Location location = new Location(row, col);
                    if (natureField.getObjectAt(location) == null) {
                        natureField.place(0,location);
                    }
                    else if (natureField.getObjectAt(location) instanceof Weed || natureField.getObjectAt(location) instanceof Tree) {
                        // do nothing
                    }
                    else if ( (int) natureField.getObjectAt(location) < 10 && rand.nextDouble() <= 0.000001) {
                        Tree tree = new Tree(false, field, natureField, location);
                        nature.add(tree);
                    }
                    else if ((int) natureField.getObjectAt(location) < 10) {
                        natureField.place( (int) natureField.getObjectAt(location)+1, location);
                    }
                    else {
                        Weed weed = new Weed(false, field, natureField, location);
                        nature.add(weed);
                    }
                }
            }
        }
        
        // Provide space for newborn animals.
        List<Animal> newAnimals = new ArrayList<>();        
        // Let all animals act.
        for(Iterator<Animal> it = animals.iterator(); it.hasNext(); ) {
            Animal animal = it.next();
            animal.act(newAnimals, weather.isFogging());
            if(! animal.isAlive()) {
                it.remove();
            }
        }
               
        // Add the newly born animals to the main lists.
        animals.addAll(newAnimals);
        
        // Provide space for newly created nature elements.
        List<Nature> newNature = new ArrayList<>();
        // Let all nature elements act.
        for(Iterator<Nature> it = nature.iterator(); it.hasNext(); ) {
            Nature nature = it.next();
            nature.act(newNature, weather.isRaining());
            if(! nature.getIsAlive()) {
                it.remove();
            }
        }
               
        // Add the newly created nature elements to the main lists.
        nature.addAll(newNature);

        view.showStatus(step, field, natureField, time);
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        Random rand = Randomizer.getRandom();
        
        step = 0;
        animals.clear();
        populate();
        time = "00:00";
        
        for (int i = 0; i<500; i++) {
            Animal infectAnimal = animals.get(rand.nextInt(animals.size()));
            infectAnimal.switchIsInfected();
        }
        
        // Show the starting state in the view.
        view.showStatus(step, field, natureField, time);
    }
    
    /**
     * Randomly populate the field with animals and nature elements
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                Weed weed = new Weed(true, field, natureField, location);
                nature.add(weed);
                if (rand.nextDouble() <= TREE_CREATION_PROBABILITY) {
                    Tree tree = new Tree(true, field, natureField, location);
                    nature.add(tree);
                }
                else if (rand.nextDouble() <= REDDY_CREATION_PROBABILITY) {
                    Reddy reddy = new Reddy(true, field, location);
                    animals.add(reddy);
                }
                else if(rand.nextDouble() <= GREENY_CREATION_PROBABILITY) {
                    Greeny greeny = new Greeny(true, field, location);
                    animals.add(greeny);
                }
                else if(rand.nextDouble() <= BLUY_CREATION_PROBABILITY) {
                    Bluy bluy = new Bluy(true, field, location);
                    bluy.setNatureField(natureField);
                    animals.add(bluy);
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
