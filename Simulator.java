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
    // List of all actors in the simulation.
    private List<Actor> actors;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;
    // Whether it is currently day or night - determines each animal's behaviour.
    private boolean nightTime;
    // Handles initial population and dynamic entity creation.
    private PopulationManager populationManager;

    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(SimulationConfiguration.GRID_DEPTH, SimulationConfiguration.GRID_WIDTH);
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
            depth = SimulationConfiguration.GRID_DEPTH;
            width = SimulationConfiguration.GRID_WIDTH;
        }
        nightTime = false;
        actors = new ArrayList<>();
        field = new Field(depth, width);
        populationManager = new PopulationManager(field, this);

        view = new SimulatorView(depth, width, this);
        view.setColor(Gazelle.class, Color.ORANGE);
        view.setColor(Lion.class, Color.YELLOW);
        view.setColor(Hyena.class, Color.RED);
        view.setColor(Mouse.class, Color.GRAY);
        view.setColor(FennecFox.class, Color.MAGENTA);
        view.setColor(Grass.class, Color.GREEN);
        view.setColor(Lake.class, Color.BLUE);

        reset();
    }

    /**
     * Run the simulation from its current state for a reasonably long period.
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
            delay(60);
        }
    }

    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each actor.
     */
    public void simulateOneStep()
    {
        step++;

        // Alternates between day and night time every 2 steps.
        if(step > 2 && step % 2 == 1) {
            nightTime = !nightTime;
        }

        List<Actor> newOrganisms = new ArrayList<>();
        Weather weather = null;

        Iterator<Actor> it = actors.iterator();
        while(it.hasNext()) {
            boolean canAct = true;
            Actor actor = it.next();

            if(actor instanceof Weather) {
                weather = (Weather) actor;
            }

            if(actor instanceof Organism) {
                populationManager.generateDisease(actor);
                Organism organism = (Organism) actor;
                if(organism instanceof Animal) {
                    Animal animal = (Animal) organism;
                    if(nightTime != animal.isNocturnal()) {
                        canAct = false;
                    }
                }
                if(canAct) {
                    actor.act(newOrganisms);
                }
                if(!organism.isAlive()) {
                    it.remove();
                }
            }
            else {
                actor.act(actors);
            }

            if(actor instanceof WaterSources) {
                WaterSources water = (WaterSources) actor;
                if(water.isEmpty()) {
                    it.remove();
                }
            }
        }

        actors.addAll(newOrganisms);

        if(weather != null && weather.generateNewWater()) {
            actors.addAll(populationManager.generateWater());
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
        actors.addAll(populationManager.populate());
        nightTime = false;
        view.showStatus(step, field);
    }

    /**
     * Pause for a given time.
     * @param millisec The time to pause for, in milliseconds.
     */
    private void delay(int millisec)
    {
        try {
            Thread.sleep(millisec);
        }
        catch(InterruptedException ie) {
            // wake up
        }
    }

    /**
     * @return The current step of the simulator.
     */
    public int getStep()
    {
        return step;
    }

    /**
     * @return Whether it is currently night time.
     */
    public boolean isNight()
    {
        return nightTime;
    }
}
