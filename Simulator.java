import java.awt.event.ActionListener;
import java.util.*;

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

    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 240;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 160;

    // A map of the creation probabilities for the organisms in the simulation
    private static Map<Class<?>, Double> CREATION_PROBABILITIES = Map.ofEntries(
            Map.entry(Coyote.class, 0.010),
            Map.entry(Deer.class, 0.080),
            Map.entry(Wolf.class, 0.010),
            Map.entry(Eagle.class, 0.01),
            Map.entry(Mouse.class, 0.080),
            Map.entry(Grass.class, 0.030),
            Map.entry(Hunter.class, 0.03)

    );

    // Maximum number of hunters in the simulation
    private static final int HUNTER_LIMIT = 5;
    private static int HUNTER_COUNT = 0;

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
                if(HUNTER_COUNT >= HUNTER_LIMIT) {
                    return null;
                }
                HUNTER_COUNT++;
                return new Hunter(f, l, e);
            });


    // List of actors in the field.
    private List<Actor> actors;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;
    // Environment in the simulation.
    private Environment environment;


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
    public Simulator(double GrassProbability, double DeerProbability, double CoyoteProbability, double WolfProbability, double EagleProbability, double HunterProbability, double MouseProbability){
        this(DEFAULT_WIDTH, DEFAULT_DEPTH);

        CREATION_PROBABILITIES = Map.ofEntries(
                Map.entry(Coyote.class, CoyoteProbability),
                Map.entry(Deer.class, DeerProbability),
                Map.entry(Wolf.class, WolfProbability),
                Map.entry(Eagle.class, EagleProbability),
                Map.entry(Mouse.class, MouseProbability),
                Map.entry(Grass.class, GrassProbability),
                Map.entry(Hunter.class, HunterProbability)
        );

        actors = new ArrayList<>();
        field = new Field(DEFAULT_DEPTH, DEFAULT_WIDTH);
        environment = new Environment(new Time(), new Weather());

        reset();
    }

    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
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
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }

        actors = new ArrayList<>();
        field = new Field(depth, width);
        environment = new Environment(new Time(), new Weather());

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        for(Class cls : SimulationInfo.DEFAULT_COLOR_MAP.keySet()) {
            view.setColor(cls, SimulationInfo.DEFAULT_COLOR_MAP.get(cls));
        }

        // adding additional buttons to the GUI
        view.addLongButtonListener(longButtonListener);
        view.addOneStepButtonListener(startButtonListener);
        view.addStopButtonListener(stopButtonListener);
        view.addResetButtonListener(resetButtonListener);

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
                    SimulationInfo.HIGHEST_STEP_PROBS = this.CREATION_PROBABILITIES;
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

        view.showStatus(step, environment.getWeather().getCurrentWeather().toString(), environment.getTime().getCurrentTimeString(), field);
    }

    /**
     * Randomly generate grass in free patches on the field, only if it is raining
     */
    private void plantGrassInPatches(){
        // new grass is randomly added in patches
        for(Location location:field.getRandomFreePatches(CREATION_PROBABILITIES.get(Grass.class))){
            if(rand.nextDouble() <= CREATION_PROBABILITIES.get(Grass.class) && environment.getWeather().getCurrentWeather() == WeatherType.RAINING) {
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
        environment.getTime().reset();

        // Show the starting state in the view.
        view.showStatus(step,environment.getWeather().getCurrentWeather().toString(), environment.getTime().getCurrentTimeString().toString(), field);
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
                    if(rand.nextDouble() <= CREATION_PROBABILITIES.get(species)) {
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
