 import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;
import javafx.application.Application;

/**
 * A simple predator-prey simulator, based on a rectangular field.
 *
 * @version 18.02.22 (DD:MM:YY)
 */
public class Simulator
{
    // Constants representing configuration information for the simulation:
    //   The default width for the grid:
    private static final int DEFAULT_WIDTH = 120;
    //   The default height for the grid:
    private static final int DEFAULT_DEPTH = 160;
                                                  
    // Constants representing the creation probabilities for the actors:
    //   Primary consumers:
    private static final double GRASSHOPPER_CREATION_PROBABILITY   = 0.15;
    private static final double HARVESTER_ANT_CREATION_PROBABILITY = 0.25;
    private static final double TERMITE_CREATION_PROBABILITY       = 0.21;
    private static final double IMPALA_CREATION_PROBABILITY        = 0.15;
    //   Secondary consumers:
    private static final double PANGOLIN_CREATION_PROBABILITY      = 0.125;
    private static final double AARDVARK_CREATION_PROBABILITY      = 0.12;
    private static final double MONGOOSE_CREATION_PROBABILITY      = 0.12;
    //   Producers:
    private static final double STAR_GRASS_CREATION_PROBABILITY    = 0.05;
    private static final double RED_OAT_GRASS_CREATION_PROBABILITY = 0.04;
    private static final double ACACIA_CREATION_PROBABILITY        = 0.04;
    
    // Constants representing the color of each actor in the simulation view:
    //   Primary consumers:
    private static final Color GRASSHOPPER_COLOR   = new Color(188, 248, 236);
    private static final Color HARVESTER_ANT_COLOR = new Color(4, 139, 168);
    private static final Color TERMITE_COLOR       = new Color(22, 219, 147);
    private static final Color IMPALA_COLOR        = new Color(239, 234, 90);
    //   Secondary consumers:
    private static final Color PANGOLIN_COLOR      = new Color(242, 158, 76);
    private static final Color AARDVARK_COLOR      = new Color(204, 183, 174);
    private static final Color MONGOOSE_COLOR      = new Color(65, 69, 53);
    //   Producers:
    private static final Color STAR_GRASS_COLOR    = new Color(125, 97, 103);
    private static final Color RED_OAT_GRASS_COLOR = new Color(164, 3, 111);
    private static final Color ACACIA_COLOR        = new Color(187, 214, 134);
    // Carcasses of consumers
    private static final Color CARCASS_COLOR       = new Color(202,0,0);
    
    // The number of steps in a day:
    public static final int NUMBER_OF_STEPS_PER_DAY = 25;
    
    // List of actors in the field:
    private List<Actor> actors;
    // The current state of the field:
    private static Field field;
    // The current step of the simulation.
    private static int step = 0;
    // A graphical view of the simulation:
    private static SimulatorView view;
    // A Boolean indicating whether or not to reset the StatisticsView:
    public static boolean resetStatisticsView = false;
    
    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }
    
    /**
     * Create a simulation field with the given size.
     * 
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        boolean isSizeImpossible = depth <= 0 || depth <= 0;
        
        if (isSizeImpossible)
        {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }
        
        actors = new ArrayList<>();
        field = new Field(depth, width);
        
        // Set the initial weather:
        WeatherSystem.changeToNextDay();

        // Create a view of the state of each location in the field:
        view = new SimulatorView(this,depth, width);
        
        view.setColor(Grasshopper.class, GRASSHOPPER_COLOR);
        view.setColor(HarvesterAnt.class, HARVESTER_ANT_COLOR);
        view.setColor(Termite.class, TERMITE_COLOR);
        view.setColor(Impala.class, IMPALA_COLOR);
        view.setColor(Pangolin.class, PANGOLIN_COLOR);
        view.setColor(Aardvark.class, AARDVARK_COLOR);
        view.setColor(Mongoose.class, MONGOOSE_COLOR);
        view.setColor(StarGrass.class, STAR_GRASS_COLOR);
        view.setColor(RedOatGrass.class, RED_OAT_GRASS_COLOR);
        view.setColor(Acacia.class, ACACIA_COLOR);
        view.setColor(Carcass.class, CARCASS_COLOR);
        
        // Show the statistics window:
        new Thread(() -> {
            Application.launch(StatisticsView.class);
        }).start();
        
        // Setup a valid starting point:
        reset();
    }
    
    /**
     * @return The current step in the simulation.
     */
    public static int getCurrentStep() { return step; }
    
    /**
     * @return The current field in the simulation.
     */
    public static Field getCurrentField() { return field; }
    
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
     * 
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        for (int step = 1; step <= numSteps && view.isViable(field); step++)
        {
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
        
        // Update the weather:
        if (TimeSystem.hasDayChanged())
        {
            WeatherSystem.changeToNextDay();
            
        }

        // Provide space for newborn actors:
        List<Actor> newActors = new ArrayList<>();
        
        // Let all rabbits act:
        for (Iterator<Actor> it = actors.iterator(); it.hasNext(); )
        {
            Actor actor = it.next();
            
            actor.act(newActors);
            
            if(!actor.getIsAlive()) it.remove();
        }
               
        // Add the newly born foxes and rabbits to the main lists:
        actors.addAll(newActors);

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
        
        // Show the starting state in the view:
        view.showStatus(step, field);
        
        // Toggle boolean to show StatisticsView should be reset:
        resetStatisticsView = true;
    }
    
    /**
     * Randomly populate the field with foxes and rabbits.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        
        for (int row = 0; row < field.getDepth(); row++)
        {
            for (int col = 0; col < field.getWidth(); col++)
            {
                Location location = new Location(row, col);
                Actor actor;
                
                // Refactor this?
                if (rand.nextDouble() <= GRASSHOPPER_CREATION_PROBABILITY)
                    actors.add(new Grasshopper(true, field, location));
                else if (rand.nextDouble() <= HARVESTER_ANT_CREATION_PROBABILITY)
                    actors.add(new HarvesterAnt(true, field, location));
                else if (rand.nextDouble() <= TERMITE_CREATION_PROBABILITY)
                    actors.add(new Termite(true, field, location));
                else if (rand.nextDouble() <= IMPALA_CREATION_PROBABILITY)
                    actors.add(new Impala(true, field, location));
                else if (rand.nextDouble() <= PANGOLIN_CREATION_PROBABILITY)
                    actors.add(new Pangolin(true, field, location));
                else if (rand.nextDouble() <= AARDVARK_CREATION_PROBABILITY)
                    actors.add(new Aardvark(true, field, location));
                else if (rand.nextDouble() <= MONGOOSE_CREATION_PROBABILITY)
                    actors.add(new Mongoose(true, field, location));
                else if (rand.nextDouble() <= STAR_GRASS_CREATION_PROBABILITY)
                    actors.add(new StarGrass(field, location));
                else if (rand.nextDouble() <= RED_OAT_GRASS_CREATION_PROBABILITY)
                    actors.add(new RedOatGrass(field, location));
                else if (rand.nextDouble() <= ACACIA_CREATION_PROBABILITY)
                    actors.add(new Acacia(field, location));
                
                // Else leave the location empty.
            }
        }
    }
    
    /**
     * Pause for a given time.
     * 
     * @param millisec The time to pause for, in milliseconds
     */
    private void delay(int millisec)
    {
        try
        {
            Thread.sleep(millisec);
        }
        catch (InterruptedException ie)
        {
            // Wake up.
        }
    }
}
