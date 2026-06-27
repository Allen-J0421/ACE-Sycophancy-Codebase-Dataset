import java.util.*;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing animals and plants
 *
 * @version 01.03.22
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;

    // Unified list of all living entities (animals and plants) in the simulation.
    private List<LivingEntity> entities;
    // The current weather of the simulation.
    private String weather;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // The current daytime of the simulation.
    private int time;
    // Observer notified of each simulation step and viability queries.
    private SimulationObserver observer;
    // Factory that owns per-species configuration and population logic.
    private EntityFactory factory;

    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     * @param observer Observer to be notified of each simulation step.
     */
    public Simulator(int depth, int width, SimulationObserver observer) {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }

        this.observer = observer;
        entities = new ArrayList<>();
        weather = "none";
        field = new Field(depth, width);
        factory = new EntityFactory();
        factory.registerColors(observer);

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
    public void simulate(int numSteps) {
        for(int step = 1; step <= numSteps && observer.isViable(field); step++) {
            simulateOneStep();
            if (time == 24) {
                time = 0;
            }
            if (step % 5 == 0) {
                time++;
            }
            if (step % 50 == 0) {
                simulateWeather();
            }
            if (step % 100 == 0) {
                resetDisease();
                simulateDisease();
            }
            delay(20);
        }
    }

    /**
     * Run the simulation from its current state for a single step.
     * All entities are simulated in a single unified pass.
     * Apply the effects of weather on the simulation.
     */
    public void simulateOneStep() {
        step++;

        if(weather.equals("rain") || weather.equals("fog")) {
            resetWeather();
            for(LivingEntity e : entities) e.applyWeatherEffect(weather);
        }
        else if(weather.equals("flood")) {
            resetWeather();
            killFractionOf(collectVulnerable(e -> e.isFloodVulnerable()));
        }
        else if(weather.equals("drought")) {
            resetWeather();
            killFractionOf(collectVulnerable(e -> e.isDroughtVulnerable()));
        }

        // Simulate all entities — animals and plants — in a single pass.
        List<LivingEntity> newEntities = new ArrayList<>();
        for(Iterator<LivingEntity> it = entities.iterator(); it.hasNext(); ) {
            LivingEntity entity = it.next();
            entity.act(newEntities, time);
            if(!entity.isAlive()) {
                it.remove();
            }
        }
        entities.addAll(newEntities);

        observer.showStatus(step, field, time, weather);
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset() {
        step = 0;
        entities.clear();
        populate();

        // Show the starting state in the view.
        observer.showStatus(step, field, time, weather);
    }

    /**
     * Randomly populate the field with animals and plants.
     */
    private void populate() {
        factory.populate(field, entities);
    }

    /**
     * Pause for a given time.
     * @param millisec The time to pause for, in milliseconds.
     */
    private void delay(int millisec) {
        try {
            Thread.sleep(millisec);
        }
        catch(InterruptedException ie) {
            // wake up
        }
    }

    /**
     * Assign a weather condition to the current step of the simulation.
     */
    private void simulateWeather() {
        Random rand = Randomizer.getRandom();
        if(rand.nextInt(5) == 0) {
            weather = "rain";
        }
        else if(rand.nextInt(5) == 1) {
            weather = "flood";
        }
        else if(rand.nextInt(5) == 2) {
            weather = "drought";
        }
        else if(rand.nextInt(5) == 3) {
            weather = "fog";
        }
        else if(rand.nextInt(5) == 4) {
            weather = "none";
        }
    }

    /**
     * Reset weather state on every entity.
     */
    private void resetWeather() {
        for(LivingEntity e : entities) e.resetWeatherEffects();
    }

    /**
     * Collect all entities that satisfy the given vulnerability predicate.
     */
    private List<LivingEntity> collectVulnerable(java.util.function.Predicate<LivingEntity> test) {
        List<LivingEntity> result = new ArrayList<>();
        for(LivingEntity e : entities) {
            if(test.test(e)) result.add(e);
        }
        return result;
    }

    /**
     * Shuffle the given list and kill the first fifth of it.
     */
    private void killFractionOf(List<LivingEntity> pool) {
        Collections.shuffle(pool);
        for(int i = 0; i < pool.size() / 5; i++) {
            pool.get(i).setDead();
        }
    }

    /**
     * Simulate disease by calling giveDisease on every entity in the simulation.
     * Animals respond; plants use the no-op default.
     */
    public void simulateDisease() {
        for(LivingEntity e : entities) e.giveDisease();
    }

    /**
     * Reset the disease state for all entities in the simulation.
     */
    private void resetDisease() {
        for(LivingEntity e : entities) e.resetDisease();
    }
}
