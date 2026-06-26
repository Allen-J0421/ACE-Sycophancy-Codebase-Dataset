import java.awt.event.ActionListener;
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
    // A graphical view of the simulation.
    private final SimulatorView view;
    // Controls time progression and weather updates.
    private final EnvironmentController environmentController;
    // Environment in the simulation.
    private final Environment environment;
    // Factories used to create initial actors in population order.
    private final Map<Class<?>, ActorFactory> actorFactories;
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
        this.config = Objects.requireNonNull(config, "config");
        actors = new ArrayList<>();
        field = new Field(config.getDepth(), config.getWidth());
        environmentController = new EnvironmentController();
        environment = environmentController.getEnvironment();

        // Create a view of the state of each location in the field.
        view = new SimulatorView(config.getDepth(), config.getWidth());
        for(Class cls : SimulationInfo.DEFAULT_COLOR_MAP.keySet()) {
            view.setColor(cls, SimulationInfo.DEFAULT_COLOR_MAP.get(cls));
        }

        // adding additional buttons to the GUI
        view.addLongButtonListener(longButtonListener);
        view.addOneStepButtonListener(startButtonListener);
        view.addStopButtonListener(stopButtonListener);
        view.addResetButtonListener(resetButtonListener);

        actorFactories = new LinkedHashMap<>();
        registerActorFactories();

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

    /**
     * Whenever the startButton is pressed,the simulateOneStep()
     * method is called.
     */
    private ActionListener startButtonListener = e -> {
        if(playingSimulation){
            System.out.println("Stop the simulation first");
        }
        else {
            playingSimulation = true;
            simulateOneStep();
            playingSimulation = false;
        }
    };

    /**
     * Whenever the stopButton is pressed, the simulation is stopped.
     */
    private ActionListener stopButtonListener = e -> {
        playingSimulation = false;
        Thread.currentThread().interrupt();
    };

    /**
     * Whenever the resetButton is pressed, the simulation is reset.
     */
    private ActionListener resetButtonListener = e -> {
        playingSimulation = false;
        reset();
    };

    /**
     * Whenever the longButton is pressed,runLongSimulation() is called.
     */
    private ActionListener longButtonListener = e -> {
        Thread runLongSimThread = new Thread("SimulationRunThread"){
            public void run() { runLongSimulation(); }
        };

        if (!playingSimulation){
            runLongSimThread.start();
        }
        else{
            System.out.println("Stop the simulation first");
        }
    };


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
            for(int step = 1; step <= numSteps && view.isViable(field); step++) {
                simulateOneStep();
                backupCounter++;
//            delay(60);   // uncomment this to run more slowly
            }
            if(!view.isViable(field)){
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

        view.showStatus(step, environmentController.getCurrentWeather().toString(), environmentController.getCurrentTimeString(), field);
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

        // Show the starting state in the view.
        view.showStatus(step, environmentController.getCurrentWeather().toString(), environmentController.getCurrentTimeString(), field);
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
