import java.util.*;

/**
 * Manages the core simulation state and per-step logic: the actor list,
 * the field, the environment, population seeding, and grass regrowth.
 * GUI and run-control concerns live in Simulator.
 *
 * @version 2022.03.02
 */
public class SimulationEngine
{
    private static final Random rand = Randomizer.getRandom();

    static final int DEFAULT_DEPTH = 160;
    static final int DEFAULT_WIDTH = 240;

    private static final int HUNTER_LIMIT = 5;
    private int hunterCount = 0;

    private Map<Class<?>, Double> creationProbabilities;

    private List<Actor> actors;
    private Field field;
    private int step;
    private Environment environment;

    /**
     * Create an engine with default creation probabilities.
     */
    public SimulationEngine(int depth, int width)
    {
        creationProbabilities = new HashMap<>(Map.ofEntries(
                Map.entry(Coyote.class, 0.010),
                Map.entry(Deer.class,   0.080),
                Map.entry(Wolf.class,   0.010),
                Map.entry(Eagle.class,  0.010),
                Map.entry(Mouse.class,  0.080),
                Map.entry(Grass.class,  0.030),
                Map.entry(Hunter.class, 0.030)
        ));
        actors = new ArrayList<>();
        field = new Field(depth, width);
        environment = new Environment(new Time(), new Weather());
        reset();
    }

    /**
     * Create an engine with custom creation probabilities.
     */
    public SimulationEngine(int depth, int width, Map<Class<?>, Double> probabilities)
    {
        creationProbabilities = probabilities;
        actors = new ArrayList<>();
        field = new Field(depth, width);
        environment = new Environment(new Time(), new Weather());
        reset();
    }

    /**
     * Advance the simulation by one step: tick time/weather, let all actors act,
     * prune dead actors, and grow new grass patches.
     */
    public void step()
    {
        step++;
        environment.getTime().incrementTime();
        environment.getWeather().checkWeatherChange(step);

        List<Actor> newActors = new ArrayList<>();
        for(Iterator<Actor> it = actors.iterator(); it.hasNext(); ) {
            Actor actor = it.next();
            if(actor instanceof Animal) {
                if(((Animal) actor).isAwake(environment)) {
                    if(((Animal) actor).isDiseased() && ((Animal) actor).getDisease().getPropagationRate() <= rand.nextDouble()) {
                        ((Animal) actor).setDead();
                    }
                    actor.act(newActors, environment);
                }
            }
            else {
                actor.act(newActors, environment);
            }

            if(actor instanceof Plant) {
                if(step % ((Plant) actor).STEPS_PER_STAGE() == 0) {
                    ((Plant) actor).incrementGrowth();
                }
            }

            if(!actor.isAlive()) {
                it.remove();
            }
        }
        actors.addAll(newActors);
        plantGrassInPatches();
    }

    /**
     * Reset to step 0 and repopulate the field from scratch.
     */
    public void reset()
    {
        step = 0;
        actors.clear();
        populate();
        environment.getTime().reset();
    }

    public int getStep()                              { return step; }
    public Field getField()                           { return field; }
    public Environment getEnvironment()               { return environment; }
    public Map<Class<?>, Double> getCreationProbabilities() { return creationProbabilities; }

    /**
     * Randomly populate the field with organisms according to CREATION_PROBABILITIES.
     */
    private void populate()
    {
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Animal.Gender sex = Randomizer.getRandomSex();
                Location location = new Location(row, col);

                if(rand.nextDouble() <= creationProbabilities.get(Grass.class)) {
                    actors.add(new Grass(field, location));
                }
                else if(rand.nextDouble() <= creationProbabilities.get(Deer.class)) {
                    actors.add(new Deer(true, field, location, sex));
                }
                else if(rand.nextDouble() <= creationProbabilities.get(Coyote.class)) {
                    actors.add(new Coyote(true, field, location, sex));
                }
                else if(rand.nextDouble() <= creationProbabilities.get(Wolf.class)) {
                    actors.add(new Wolf(true, field, location, sex));
                }
                else if(rand.nextDouble() <= creationProbabilities.get(Eagle.class)) {
                    actors.add(new Eagle(true, field, location, sex));
                }
                else if(rand.nextDouble() <= creationProbabilities.get(Mouse.class)) {
                    actors.add(new Mouse(true, field, location, sex));
                }
                else if(rand.nextDouble() <= creationProbabilities.get(Hunter.class)) {
                    if(hunterCount < HUNTER_LIMIT) {
                        actors.add(new Hunter(field, location, environment));
                        hunterCount++;
                    }
                }
                // else leave the location empty.
            }
        }
    }

    /**
     * Randomly grow new grass in free patches, but only when it is raining.
     */
    private void plantGrassInPatches()
    {
        for(Location location : field.getRandomFreePatches(creationProbabilities.get(Grass.class))) {
            if(rand.nextDouble() <= creationProbabilities.get(Grass.class)
                    && environment.getWeather().getCurrentWeather() == WeatherType.RAINING) {
                actors.add(new Grass(field, location));
            }
        }
    }
}
