import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
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
                                                  
    // Display and initial population settings for each actor type.
    private static final List<ActorDefinition> ACTOR_DEFINITIONS = Arrays.asList(
        new ActorDefinition(Grasshopper.class, 0.15, new Color(188, 248, 236),
            (field, location) -> new Grasshopper(true, field, location)),
        new ActorDefinition(HarvesterAnt.class, 0.25, new Color(4, 139, 168),
            (field, location) -> new HarvesterAnt(true, field, location)),
        new ActorDefinition(Termite.class, 0.21, new Color(22, 219, 147),
            (field, location) -> new Termite(true, field, location)),
        new ActorDefinition(Impala.class, 0.15, new Color(239, 234, 90),
            (field, location) -> new Impala(true, field, location)),
        new ActorDefinition(Pangolin.class, 0.125, new Color(242, 158, 76),
            (field, location) -> new Pangolin(true, field, location)),
        new ActorDefinition(Aardvark.class, 0.12, new Color(204, 183, 174),
            (field, location) -> new Aardvark(true, field, location)),
        new ActorDefinition(Mongoose.class, 0.12, new Color(65, 69, 53),
            (field, location) -> new Mongoose(true, field, location)),
        new ActorDefinition(StarGrass.class, 0.05, new Color(125, 97, 103),
            (field, location) -> new StarGrass(field, location)),
        new ActorDefinition(RedOatGrass.class, 0.04, new Color(164, 3, 111),
            (field, location) -> new RedOatGrass(field, location)),
        new ActorDefinition(Acacia.class, 0.04, new Color(187, 214, 134),
            (field, location) -> new Acacia(field, location))
    );
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
        boolean isSizeImpossible = depth <= 0 || width <= 0;
        
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
        
        for (ActorDefinition definition : ACTOR_DEFINITIONS)
        {
            view.setColor(definition.getActorClass(), definition.getColor());
        }
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
                populateLocation(rand, location);
            }
        }
    }

    /**
     * Populate a location with the first actor type selected by chance.
     */
    private void populateLocation(Random rand, Location location)
    {
        for (ActorDefinition definition : ACTOR_DEFINITIONS)
        {
            if (definition.shouldCreate(rand))
            {
                actors.add(definition.create(field, location));
                return;
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
