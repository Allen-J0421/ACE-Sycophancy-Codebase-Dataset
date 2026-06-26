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
    private boolean playingSimulation = false;

    private static final Random rand = Randomizer.getRandom();

    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 240;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 160;

    // A map of the creation probabilities for the organisms in the simulation
    private static Map<Class<?>, Double> defaultCreationProbabilities() {
        return Map.ofEntries(
            Map.entry(Coyote.class, 0.010),
            Map.entry(Deer.class, 0.080),
            Map.entry(Wolf.class, 0.010),
            Map.entry(Eagle.class, 0.01),
            Map.entry(Mouse.class, 0.080),
            Map.entry(Grass.class, 0.030),
            Map.entry(Hunter.class, 0.03)
        );
    }

    // Maximum number of hunters in the simulation
    private static final int HUNTER_LIMIT = 5;
    private int hunterCount = 0;


    // List of actors in the field.
    private final List<Actor> actors;
    // The current state of the field.
    private final Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private final SimulatorView view;
    // Environment in the simulation.
    private final Environment environment;
    // Creation probabilities for this simulation instance.
    private final Map<Class<?>, Double> creationProbabilities;


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
        this(DEFAULT_DEPTH, DEFAULT_WIDTH, Map.ofEntries(
                Map.entry(Coyote.class, CoyoteProbability),
                Map.entry(Deer.class, DeerProbability),
                Map.entry(Wolf.class, WolfProbability),
                Map.entry(Eagle.class, EagleProbability),
                Map.entry(Mouse.class, MouseProbability),
                Map.entry(Grass.class, GrassProbability),
                Map.entry(Hunter.class, HunterProbability)
        ));
    }

    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH, defaultCreationProbabilities());
    }

    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        this(depth, width, defaultCreationProbabilities());
    }

    private Simulator(int depth, int width, Map<Class<?>, Double> creationProbabilities)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }

        this.creationProbabilities = Map.copyOf(creationProbabilities);
        actors = new ArrayList<>();
        field = new Field(depth, width);
        environment = new Environment(new Time(), new Weather());

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        for(Class<?> cls : SimulationInfo.DEFAULT_COLOR_MAP.keySet()) {
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
        simulate(4000);
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

        int completedSteps = 0;
        while(playingSimulation && completedSteps < numSteps) {
            if(!view.isViable(field)){
                break;
            }
            simulateOneStep();
            completedSteps++;
//            delay(60);   // uncomment this to run more slowly
        }
        if(!view.isViable(field) && completedSteps > SimulationInfo.HIGHEST_STEPS){
            // If multiple instances of the simulation are created in the same session (for testing),
            // the fields in SimulationInfo can be used for optimizing the default probabilities
            SimulationInfo.HIGHEST_STEPS = completedSteps;
            SimulationInfo.HIGHEST_STEP_PROBS = creationProbabilities;
        }
        playingSimulation = false;
    }

    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * organism.
     */
    public void simulateOneStep()
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

        view.showStatus(step, environment.getWeather().getCurrentWeather().toString(), environment.getTime().getCurrentTimeString(), field);
    }

    /**
     * Randomly generate grass in free patches on the field, only if it is raining
     */
    private void plantGrassInPatches(){
        // new grass is randomly added in patches
        for(Location location:field.getRandomFreePatches(creationProbabilities.get(Grass.class))){
            if(rand.nextDouble() <= creationProbabilities.get(Grass.class) && environment.getWeather().getCurrentWeather() == WeatherType.RAINING) {
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
        hunterCount = 0;
        actors.clear();
        populate();
        environment.getTime().reset();
        environment.getWeather().reset();

        // Show the starting state in the view.
        view.showStatus(step,environment.getWeather().getCurrentWeather().toString(), environment.getTime().getCurrentTimeString().toString(), field);
    }

    /**
     * Randomly populate the field with organisms.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Animal.Gender sex = Randomizer.getRandomSex();
                Location location = new Location(row, col);

                if(rand.nextDouble() <= creationProbabilities.get(Grass.class)) {
                    Grass grass = new Grass(field, location);
                    actors.add(grass);
                }

                else if(rand.nextDouble() <= creationProbabilities.get(Deer.class)) {
                    Deer deer = new Deer(true, field, location, sex);
                    actors.add(deer);
                }
                else if(rand.nextDouble() <= creationProbabilities.get(Coyote.class)) {
                    Coyote coyote = new Coyote(true, field, location, sex);
                    actors.add(coyote);
                }

                else if(rand.nextDouble() <= creationProbabilities.get(Wolf.class)) {
                    Wolf wolf = new Wolf(true, field, location, sex);
                    actors.add(wolf);
                }
                else if(rand.nextDouble() <= creationProbabilities.get(Eagle.class)) {
                    Eagle eagle = new Eagle(true, field, location, sex);
                    actors.add(eagle);
                }
                else if(rand.nextDouble() <= creationProbabilities.get(Mouse.class)) {
                    Mouse mouse = new Mouse(true, field, location, sex);
                    actors.add(mouse);
                }

                else if(rand.nextDouble() <= creationProbabilities.get(Hunter.class)) {
                    if(!(hunterCount >= HUNTER_LIMIT)){
                        Hunter hunter = new Hunter(field, location);
                        actors.add(hunter);
                        hunterCount++;
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
