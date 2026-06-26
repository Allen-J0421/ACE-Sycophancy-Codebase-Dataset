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

    // Per-species counts of how many have been spawned, used to enforce
    // Species.maxCount(). Static and never reset, so a cap (e.g. the hunter
    // limit) applies globally across resets - matching the original behaviour.
    private static final Map<Species, Integer> spawnedCounts = new EnumMap<>(Species.class);

    // The creation probabilities, keyed by class. Initialised from the Species
    // defaults but mutable so that probability sweeps (see TestingMain) can
    // substitute their own combinations.
    private static Map<Class<?>, Double> creationProbabilities = defaultProbabilities();

    private static Map<Class<?>, Double> defaultProbabilities()
    {
        Map<Class<?>, Double> probabilities = new HashMap<>();
        for(Species species : Species.values()) {
            probabilities.put(species.actorClass(), species.defaultProbability());
        }
        return probabilities;
    }

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
                // creation probability succeeds is placed here (subject to its
                // population cap).
                for(Species species : Species.values()) {
                    if(rand.nextDouble() <= creationProbabilities.get(species.actorClass())) {
                        if(spawnedCounts.getOrDefault(species, 0) < species.maxCount()) {
                            actors.add(species.spawn(field, location, sex, environment));
                            spawnedCounts.merge(species, 1, Integer::sum);
                        }
                        break;
                    }
                }
                // else leave the location empty.
            }
        }
    }
}
