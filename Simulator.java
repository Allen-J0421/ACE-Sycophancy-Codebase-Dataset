import java.util.*;
import java.util.function.Consumer;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing different organisms.
 *
 * @version 2022.03.02
 */
public class Simulator
{
    private static boolean playingSimulation = false;
    private static int remainingSteps;

    private static final Random rand = Randomizer.getRandom();

    // Configuration data for the simulation setup.
    private final SimulationConfig config;
    // List of actors in the field.
    private final List<Actor> actors;
    // The current state of the field.
    private final Field field;
    // The current step of the simulation.
    private int step;
    // Controls time progression and weather updates.
    private final EnvironmentController environmentController;
    // Environment in the simulation.
    private final Environment environment;
    // Factories used to create initial actors in population order.
    private final Map<Class<?>, ActorFactory> actorFactories;
    // Observers interested in simulation state changes.
    private final List<SimulationListener> listeners;
    // Statistics used to determine whether the simulation remains viable.
    private final FieldStats viabilityStats;
    // Number of hunters created in the current population.
    private int hunterCount;

    private interface ActorFactory
    {
        Actor create(Location location, Animal.Gender sex);
    }


    /**
     * A simulator constructor to set the CREATION_PROBABILITY of each actor.
     * This constructor can be used to test different combinations of probabilities to find an optimal combination
     *
     * @param GrassProbability Probability of Grass being generated
     * @param DeerProbability Probability of Deer being generated
     * @param CoyoteProbability Probability of Coyote being generated
     * @param WolfProbability Probability of Wolf being generated
     * @param EagleProbability Probability of Eagle being generated
     * @param HunterProbability Probability of Hunter being generated
     * @param MouseProbability Probability of Mouse being generated
     */
    public Simulator(double grassProbability,
                     double deerProbability,
                     double coyoteProbability,
                     double wolfProbability,
                     double eagleProbability,
                     double hunterProbability,
                     double mouseProbability)
    {
        this(SimulationConfig.withPopulationRates(
                grassProbability,
                deerProbability,
                coyoteProbability,
                wolfProbability,
                eagleProbability,
                hunterProbability,
                mouseProbability));
    }

    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(SimulationConfig.defaultConfig());
    }

    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        this(createConfigWithDimensions(depth, width));
    }

    /**
     * Create a simulation from a configuration object.
     * @param config The simulation setup data.
     */
    public Simulator(SimulationConfig config)
    {
        this(config, new SimulationRegistry());
    }

    /**
     * Create a simulation from a configuration object and component registry.
     * @param config The simulation setup data.
     * @param registry The component factory registry.
     */
    public Simulator(SimulationConfig config, SimulationRegistry registry)
    {
        this.config = Objects.requireNonNull(config, "config");
        registry = Objects.requireNonNull(registry, "registry");
        actors = new ArrayList<>();
        field = registry.createField(config);
        environmentController = registry.createEnvironmentController();
        environment = environmentController.getEnvironment();

        actorFactories = new LinkedHashMap<>();
        registerActorFactories();
        listeners = new ArrayList<>();
        viabilityStats = registry.createFieldStats();

        // Setup a valid starting point.
        reset();
    }

    /**
     * Run the simulation from its current state for a reasonably long period,
     * (4000 steps).
     */
    public void runLongSimulation()
    {
        remainingSteps += 4000;
        simulate(remainingSteps);
    }

    public void runOneStep()
    {
        if(playingSimulation) {
            System.out.println("Stop the simulation first");
        }
        else {
            playingSimulation = true;
            simulateOneStep();
            playingSimulation = false;
        }
    }

    public void stopSimulation()
    {
        playingSimulation = false;
        Thread.currentThread().interrupt();
    }

    public boolean isPlayingSimulation()
    {
        return playingSimulation;
    }

    public SimulationConfig getConfig()
    {
        return config;
    }

    public void addSimulationListener(SimulationListener listener)
    {
        listeners.add(Objects.requireNonNull(listener, "listener"));
        listener.simulationReset(createEvent());
    }

    public void removeSimulationListener(SimulationListener listener)
    {
        listeners.remove(listener);
    }


    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        playingSimulation = true;
        remainingSteps = numSteps;

        int backupCounter = 1;
        while(playingSimulation){
            for(int step = 1; step <= numSteps && isViable(); step++) {
                simulateOneStep();
                backupCounter++;
//            delay(60);   // uncomment this to run more slowly
            }
            if(!isViable()){
                // System.out.println("STEPS COMPLETED: "+backupCounter);
                // uncomment this while optimizing for population stability
                Thread.currentThread().interrupt();
                if(backupCounter > SimulationInfo.HIGHEST_STEPS){
                    // If multiple instances of the simulation are created in the same session (for testing),
                    // the fields in SimulationInfo can be used for optimizing the default probabilities
                    SimulationInfo.HIGHEST_STEPS = backupCounter;
                    SimulationInfo.HIGHEST_STEP_PROBS = config.getPopulationRates();
                }
                playingSimulation = false;
                break;
            }
        }
        remainingSteps = numSteps - backupCounter;
    }

    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * organism.
     */
    public void simulateOneStep()
    {
        if(!playingSimulation){ return; }
        step++;
        environmentController.advance(step);


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
                if(step % ((Plant) actor).STEPS_PER_STAGE()==0){
                    ((Plant) actor).incrementGrowth();
                }
            }

            if(! actor.isAlive()) {
                it.remove();
            }



        }
        actors.addAll(newActors);
        plantGrassInPatches();

        notifyPopulationChanged();
        notifyStepCompleted();
    }

    /**
     * Randomly generate grass in free patches on the field, only if it is raining
     */
    private void plantGrassInPatches(){
        // new grass is randomly added in patches
        double grassRate = config.getPopulationRate(Grass.class);
        for(Location location:field.getRandomFreePatches(grassRate)){
            if(rand.nextDouble() <= grassRate && environmentController.isRaining()) {
                Grass grass = new Grass(field, location);
                actors.add(grass);
            }
        }
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        actors.clear();
        populate();
        environmentController.reset();

        notifySimulationReset();
        notifyPopulationChanged();
    }

    /**
     * Randomly populate the field with organisms.
     */
    private void populate()
    {
        field.clear();
        hunterCount = 0;
        forEachFieldLocation(this::addInitialActorAt);
    }

    private void forEachFieldLocation(Consumer<Location> locationConsumer)
    {
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                locationConsumer.accept(new Location(row, col));
            }
        }
    }

    private void registerActorFactories()
    {
        registerActorFactory(Grass.class, (location, sex) -> new Grass(field, location));
        registerActorFactory(Deer.class, (location, sex) -> new Deer(true, field, location, sex));
        registerActorFactory(Coyote.class, (location, sex) -> new Coyote(true, field, location, sex));
        registerActorFactory(Wolf.class, (location, sex) -> new Wolf(true, field, location, sex));
        registerActorFactory(Eagle.class, (location, sex) -> new Eagle(true, field, location, sex));
        registerActorFactory(Mouse.class, (location, sex) -> new Mouse(true, field, location, sex));
        registerActorFactory(Hunter.class, (location, sex) -> createHunter(location));
    }

    private void registerActorFactory(Class<?> actorClass, ActorFactory actorFactory)
    {
        actorFactories.put(actorClass, actorFactory);
    }

    private void addInitialActorAt(Location location)
    {
        Actor actor = createInitialActor(location);
        if(actor != null) {
            actors.add(actor);
        }
    }

    private Actor createInitialActor(Location location)
    {
        Animal.Gender sex = Randomizer.getRandomSex();
        for(Map.Entry<Class<?>, ActorFactory> entry : actorFactories.entrySet()) {
            if(rand.nextDouble() <= getCreationProbability(entry.getKey())) {
                return entry.getValue().create(location, sex);
            }
        }
        return null;
    }

    private double getCreationProbability(Class<?> actorClass)
    {
        return config.getPopulationRate(actorClass);
    }

    private Actor createHunter(Location location)
    {
        if(hunterCount >= config.getHunterLimit()) {
            return null;
        }
        hunterCount++;
        return new Hunter(field, location, environment);
    }

    private static SimulationConfig createConfigWithDimensions(int depth, int width)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            return SimulationConfig.defaultConfig();
        }
        return SimulationConfig.withDimensions(depth, width);
    }

    private boolean isViable()
    {
        return viabilityStats.isViable(field);
    }

    private void notifyStepCompleted()
    {
        SimulationEvent event = createEvent();
        for(SimulationListener listener : listeners) {
            listener.stepCompleted(event);
        }
    }

    private void notifySimulationReset()
    {
        SimulationEvent event = createEvent();
        for(SimulationListener listener : listeners) {
            listener.simulationReset(event);
        }
    }

    private void notifyPopulationChanged()
    {
        SimulationEvent event = createEvent();
        for(SimulationListener listener : listeners) {
            listener.populationChanged(event);
        }
    }

    private SimulationEvent createEvent()
    {
        return new SimulationEvent(
                this,
                step,
                field,
                environmentController.getCurrentWeather(),
                environmentController.getCurrentTimeString());
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
