import java.util.*;

/**
 * Manages the core simulation state and per-step logic: the actor list,
 * the field, the environment, population seeding, and grass regrowth.
 * How the field is initially populated is delegated to a PopulationStrategy.
 * GUI and run-control concerns live in Simulator.
 *
 * @version 2022.03.02
 */
public class SimulationEngine
{
    private static final Random rand = Randomizer.getRandom();

    static final int DEFAULT_DEPTH = 160;
    static final int DEFAULT_WIDTH = 240;

    private final PopulationStrategy populationStrategy;

    private List<Actor> actors;
    private Field field;
    private FieldAnalyzer fieldAnalyzer;
    private int step;
    private Environment environment;

    /**
     * Create an engine with default creation probabilities.
     */
    public SimulationEngine(int depth, int width)
    {
        this(depth, width, new DefaultPopulationStrategy(new HashMap<>(Map.ofEntries(
                Map.entry(Coyote.class, 0.010),
                Map.entry(Deer.class,   0.080),
                Map.entry(Wolf.class,   0.010),
                Map.entry(Eagle.class,  0.010),
                Map.entry(Mouse.class,  0.080),
                Map.entry(Grass.class,  0.030),
                Map.entry(Hunter.class, 0.030)
        ))));
    }

    /**
     * Create an engine with a custom population strategy.
     * @param strategy The strategy that will seed the field on each reset.
     */
    public SimulationEngine(int depth, int width, PopulationStrategy strategy)
    {
        if(width <= 0 || depth <= 0) {
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }
        this.populationStrategy = strategy;
        actors = new ArrayList<>();
        field = new Field(depth, width);
        fieldAnalyzer = new FieldAnalyzer(field);
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
        populationStrategy.populate(field, environment, actors);
        environment.getTime().reset();
    }

    public int getStep()                              { return step; }
    public Field getField()                           { return field; }
    public Environment getEnvironment()               { return environment; }
    public Map<Class<?>, Double> getCreationProbabilities() { return populationStrategy.getCreationProbabilities(); }

    /**
     * Randomly grow new grass in free patches, but only when it is raining.
     */
    private void plantGrassInPatches()
    {
        double grassProb = populationStrategy.getCreationProbabilities().get(Grass.class);
        for(Location location : fieldAnalyzer.getRandomFreePatches(grassProb)) {
            if(rand.nextDouble() <= grassProb
                    && environment.getWeather().getCurrentWeather() == WeatherType.RAINING) {
                actors.add(new Grass(field, location));
            }
        }
    }
}
