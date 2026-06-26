import java.util.*;

/**
 * The headless simulation model.
 *
 * It owns the field, the list of actors and the environment, and advances them
 * one step at a time. It has no knowledge of any user interface, so it can be
 * driven and tested without a display. {@link Simulator} wraps an engine to add
 * the Swing view and run controls.
 *
 * @version 2022.03.02
 */
public class SimulationEngine
{
    private static final Random rand = Randomizer.getRandom();

    // The creation probabilities for each species. Mutable so that probability
    // sweeps (see TestingMain) can substitute their own combinations.
    private static Map<Class<?>, Double> creationProbabilities = Map.ofEntries(
            Map.entry(Coyote.class, 0.010),
            Map.entry(Deer.class, 0.080),
            Map.entry(Wolf.class, 0.010),
            Map.entry(Eagle.class, 0.01),
            Map.entry(Mouse.class, 0.080),
            Map.entry(Grass.class, 0.030),
            Map.entry(Hunter.class, 0.03)
    );

    // Maximum number of hunters in the simulation.
    private static final int HUNTER_LIMIT = 5;
    private static int hunterCount = 0;

    /**
     * The order in which species are considered for each cell when populating
     * the field. The creation probabilities are checked in this order and the
     * first match wins, so the order is significant.
     */
    private static final List<Class<?>> SPAWN_ORDER = List.of(
            Grass.class, Deer.class, Coyote.class, Wolf.class, Eagle.class, Mouse.class, Hunter.class);

    /**
     * Creates a single actor of a species at a location, or returns null when
     * one cannot be created (e.g. the hunter limit has been reached).
     */
    @FunctionalInterface
    private interface ActorSpawner {
        Actor spawn(Field field, Location location, Animal.Gender sex, Environment environment);
    }

    // The factory used to create each species, keyed by its class.
    private static final Map<Class<?>, ActorSpawner> SPAWNERS = Map.of(
            Grass.class,  (f, l, s, e) -> new Grass(f, l),
            Deer.class,   (f, l, s, e) -> new Deer(true, f, l, s),
            Coyote.class, (f, l, s, e) -> new Coyote(true, f, l, s),
            Wolf.class,   (f, l, s, e) -> new Wolf(true, f, l, s),
            Eagle.class,  (f, l, s, e) -> new Eagle(true, f, l, s),
            Mouse.class,  (f, l, s, e) -> new Mouse(true, f, l, s),
            Hunter.class, (f, l, s, e) -> {
                if(hunterCount >= HUNTER_LIMIT) {
                    return null;
                }
                hunterCount++;
                return new Hunter(f, l, e);
            });

    // List of actors in the field.
    private final List<Actor> actors = new ArrayList<>();
    // The current state of the field.
    private Field field;
    // Environment (time and weather) in the simulation.
    private final Environment environment;
    // The current step of the simulation.
    private int step;

    /**
     * Create an engine with a field of the given size.
     * @param depth Depth of the field.
     * @param width Width of the field.
     */
    public SimulationEngine(int depth, int width)
    {
        field = new Field(depth, width);
        environment = new Environment(new Time(), new Weather());
    }

    /**
     * @return The field being simulated.
     */
    public Field getField()
    {
        return field;
    }

    /**
     * @return The simulation's environment (time and weather).
     */
    public Environment getEnvironment()
    {
        return environment;
    }

    /**
     * @return The current step number.
     */
    public int getStep()
    {
        return step;
    }

    /**
     * @return The creation probabilities currently in use.
     */
    public Map<Class<?>, Double> getCreationProbabilities()
    {
        return creationProbabilities;
    }

    /**
     * Replace the creation probabilities (used by probability sweeps).
     * @param probabilities The new probabilities, keyed by species class.
     */
    public void setCreationProbabilities(Map<Class<?>, Double> probabilities)
    {
        creationProbabilities = probabilities;
    }

    /**
     * Reset the model to a freshly populated starting state.
     */
    public void reset()
    {
        step = 0;
        actors.clear();
        populate();
        environment.getTime().reset();
    }

    /**
     * Advance the model by a single step: update the environment, let every
     * actor act, collect newborns and grow new grass. Performs no rendering.
     */
    public void step()
    {
        step++;
        environment.getTime().incrementTime();
        environment.getWeather().checkWeatherChange(step);

        // Provide space for newborn animals.
        List<Actor> newActors = new ArrayList<>();
        // Let all actors act.
        for(Iterator<Actor> it = actors.iterator(); it.hasNext(); ) {
            Actor actor = it.next();
            if(actor instanceof Animal){
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

            if(actor instanceof Plant){
                if(step % ((Plant) actor).getStepsPerStage()==0){
                    ((Plant) actor).incrementGrowth();
                }
            }

            if(! actor.isAlive()) {
                it.remove();
            }
        }
        actors.addAll(newActors);
        plantGrassInPatches();
    }

    /**
     * Randomly generate grass in free patches on the field, only if it is raining.
     */
    private void plantGrassInPatches()
    {
        // new grass is randomly added in patches
        for(Location location : field.getRandomFreePatches(creationProbabilities.get(Grass.class))){
            if(rand.nextDouble() <= creationProbabilities.get(Grass.class) && environment.getWeather().getCurrentWeather() == WeatherType.RAINING) {
                Grass grass = new Grass(field, location);
                actors.add(grass);
            }
        }
    }

    /**
     * Randomly populate the field with organisms.
     */
    private void populate()
    {
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Animal.Gender sex = Randomizer.getRandomSex();
                Location location = new Location(row, col);

                // Consider each species in priority order; the first whose
                // creation probability succeeds is placed here (if any).
                for(Class<?> species : SPAWN_ORDER) {
                    if(rand.nextDouble() <= creationProbabilities.get(species)) {
                        Actor actor = SPAWNERS.get(species).spawn(field, location, sex, environment);
                        if(actor != null) {
                            actors.add(actor);
                        }
                        break;
                    }
                }
                // else leave the location empty.
            }
        }
    }
}
