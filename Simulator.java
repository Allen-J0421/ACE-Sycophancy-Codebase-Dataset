import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing rabbits and foxes.
 *
 * Preferred construction via the fluent Builder API:
 *
 *   Simulator sim = new Simulator.Builder()
 *       .gridDepth(80)
 *       .gridWidth(100)
 *       .lionCreationProbability(0.03)
 *       .rainProbability(0.08)
 *       .build();
 *
 * The no-arg and (depth, width) constructors remain for convenience.
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
    // Whether it is currently day or night - determines each animal's behaviour.
    private boolean nightTime;
    // Handles initial population and dynamic entity creation.
    private PopulationManager populationManager;
    // Observers notified after each simulation step.
    private final List<SimulationObserver> observers = new ArrayList<>();
    // Runtime configuration built by Builder.
    private final SimulatorConfig config;

    // -------------------------------------------------------------------------
    // Builder
    // -------------------------------------------------------------------------

    /**
     * Fluent builder for Simulator.  All parameters default to the values
     * defined in SimulationConfiguration so callers only need to override
     * what they want to change.
     */
    public static class Builder
    {
        // Grid
        int gridDepth = SimulationConfiguration.GRID_DEPTH;
        int gridWidth = SimulationConfiguration.GRID_WIDTH;

        // Simulation pace
        int stepDelayMs = 60;

        // Creation probabilities
        double hyenaCreationProbability     = SimulationConfiguration.HYENA_CREATION_PROBABILITY;
        double lionCreationProbability      = SimulationConfiguration.LION_CREATION_PROBABILITY;
        double gazelleCreationProbability   = SimulationConfiguration.GAZELLE_CREATION_PROBABILITY;
        double mouseCreationProbability     = SimulationConfiguration.MOUSE_CREATION_PROBABILITY;
        double fennecFoxCreationProbability = SimulationConfiguration.FENNECFOX_CREATION_PROBABILITY;
        double grassCreationProbability     = SimulationConfiguration.GRASS_CREATION_PROBABILITY;
        double lakeCreationProbability      = SimulationConfiguration.LAKE_CREATION_PROBABILITY;

        // Weather probabilities
        double rainProbability     = SimulationConfiguration.RAIN_PROBABILITY;
        double fogProbability      = SimulationConfiguration.FOG_PROBABILITY;
        double heatwaveProbability = SimulationConfiguration.HEATWAVE_PROBABILITY;

        public Builder gridDepth(int depth)   { this.gridDepth = depth;   return this; }
        public Builder gridWidth(int width)   { this.gridWidth = width;   return this; }
        public Builder stepDelayMs(int ms)    { this.stepDelayMs = ms;    return this; }

        public Builder hyenaCreationProbability(double p)     { this.hyenaCreationProbability = p;     return this; }
        public Builder lionCreationProbability(double p)      { this.lionCreationProbability = p;      return this; }
        public Builder gazelleCreationProbability(double p)   { this.gazelleCreationProbability = p;   return this; }
        public Builder mouseCreationProbability(double p)     { this.mouseCreationProbability = p;     return this; }
        public Builder fennecFoxCreationProbability(double p) { this.fennecFoxCreationProbability = p; return this; }
        public Builder grassCreationProbability(double p)     { this.grassCreationProbability = p;     return this; }
        public Builder lakeCreationProbability(double p)      { this.lakeCreationProbability = p;      return this; }

        public Builder rainProbability(double p)     { this.rainProbability = p;     return this; }
        public Builder fogProbability(double p)      { this.fogProbability = p;      return this; }
        public Builder heatwaveProbability(double p) { this.heatwaveProbability = p; return this; }

        /**
         * Validate, freeze into a SimulatorConfig, and construct the Simulator.
         */
        public Simulator build()
        {
            return new Simulator(buildConfig());
        }

        /** Package-private: also used by the convenience constructors below. */
        SimulatorConfig buildConfig()
        {
            if(gridDepth <= 0) {
                System.out.println("Depth must be greater than zero. Using default.");
                gridDepth = SimulationConfiguration.GRID_DEPTH;
            }
            if(gridWidth <= 0) {
                System.out.println("Width must be greater than zero. Using default.");
                gridWidth = SimulationConfiguration.GRID_WIDTH;
            }
            return new SimulatorConfig(this);
        }
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Construct a simulation with all default parameters.
     */
    public Simulator()
    {
        this(new Builder().buildConfig());
    }

    /**
     * Construct a simulation with custom grid dimensions and all other
     * parameters at their defaults.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        this(new Builder().gridDepth(depth).gridWidth(width).buildConfig());
    }

    /**
     * Primary constructor — all initialisation flows through here.
     * @param config Immutable configuration produced by Builder.
     */
    private Simulator(SimulatorConfig config)
    {
        this.config = config;
        nightTime = false;
        actors = new ArrayList<>();
        field = new Field(config.getGridDepth(), config.getGridWidth());
        populationManager = new PopulationManager(field, this);

        SimulatorView view = new SimulatorView(config.getGridDepth(), config.getGridWidth());
        view.setColor(Gazelle.class, Color.ORANGE);
        view.setColor(Lion.class, Color.YELLOW);
        view.setColor(Hyena.class, Color.RED);
        view.setColor(Mouse.class, Color.GRAY);
        view.setColor(FennecFox.class, Color.MAGENTA);
        view.setColor(Grass.class, Color.GREEN);
        view.setColor(Lake.class, Color.BLUE);
        addObserver(view);

        reset();
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * @return The runtime configuration this Simulator was built with.
     */
    public SimulatorConfig getConfig()
    {
        return config;
    }

    /**
     * Register an observer to be notified after each simulation step
     * and consulted for viability checks.
     * @param observer The observer to register.
     */
    public void addObserver(SimulationObserver observer)
    {
        observers.add(observer);
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
        for(int step = 1; step <= numSteps && checkViable(field); step++) {
            simulateOneStep();
            delay(config.getStepDelayMs());
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

        notifyObservers(new SimulationEvent(step, field, nightTime));
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
        notifyObservers(new SimulationEvent(step, field, nightTime));
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

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private void delay(int millisec)
    {
        try {
            Thread.sleep(millisec);
        }
        catch(InterruptedException ie) {
            // wake up
        }
    }

    private void notifyObservers(SimulationEvent event)
    {
        for(SimulationObserver obs : observers) {
            obs.onStepCompleted(event);
        }
    }

    private boolean checkViable(Field field)
    {
        if(observers.isEmpty()) return false;
        for(SimulationObserver obs : observers) {
            if(!obs.isViable(field)) return false;
        }
        return true;
    }
}
