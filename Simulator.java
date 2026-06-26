import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing Seaweed, Salmon, Cod, Shark, and Whale, with disease and weather events.
 *
 * @version 2022/03/02
 */
public class Simulator
{
    // Default grid dimensions.
    private static final int DEFAULT_WIDTH = 120;
    private static final int DEFAULT_DEPTH = 80;

    // Initial spawn probabilities for each species.
    private static final double SALMON_CREATION_PROBABILITY  = 0.08;
    private static final double COD_CREATION_PROBABILITY     = 0.08;
    private static final double SEAWEED_CREATION_PROBABILITY = 0.03;
    private static final double SHARK_CREATION_PROBABILITY   = 0.04;
    private static final double WHALE_CREATION_PROBABILITY   = 0.03;

    // Environmental event probabilities.
    private static final double STORM_HAPPEN_PROBABILITY = 0.14;

    // Parallel arrays that drive populate(): probabilities and matching constructors.
    private static final double[] SPAWN_PROBABILITIES = {
        SALMON_CREATION_PROBABILITY,
        COD_CREATION_PROBABILITY,
        SEAWEED_CREATION_PROBABILITY,
        SHARK_CREATION_PROBABILITY,
        WHALE_CREATION_PROBABILITY
    };

    @FunctionalInterface
    private interface CreatureFactory {
        Creature create(boolean randomAge, Field field, Location location);
    }

    private static final CreatureFactory[] SPAWN_FACTORIES = {
        Salmon::new,
        Cod::new,
        Seaweed::new,
        Shark::new,
        Whale::new
    };

    // List of creatures in the field.
    private List<Creature> creatures;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;
    private Disease disease;
    private Weather weather;

    // The initial level of dissolved oxygen in the water.
    private double oxygenLevel;

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

        creatures = new ArrayList<>();
        field = new Field(depth, width);
        weather = new Weather(field);
        disease = new Disease();
        oxygenLevel = 1;

        view = new SimulatorView(depth, width);
        view.setColor(Cod.class,     Color.ORANGE);
        view.setColor(Salmon.class,  Color.YELLOW);
        view.setColor(Seaweed.class, Color.RED);
        view.setColor(Shark.class,   Color.BLACK);
        view.setColor(Whale.class,   Color.PINK);
        view.setColor(Weather.class, Color.BLUE);

        reset();
    }

    /**
     * Run the simulation for 1000 steps.
     */
    public void runLongSimulation()
    {
        simulate(1000);
    }

    /**
     * Run the simulation for the given number of steps, stopping early if it is no longer viable.
     */
    public void simulate(int numSteps)
    {
        for(int step = 1; step <= numSteps && view.isViable(field); step++) {
            simulateOneStep();
            delay(60);
        }
    }

    /**
     * Advance the simulation by one step: let every creature act, apply weather and disease,
     * then update the oxygen level and the view.
     */
    public void simulateOneStep()
    {
        step++;
        boolean dayTime = timeOfDay();

        double totalOxygenInvolved = 0;

        disease.creationSourceOfInfection(creatures, step);

        List<Creature> newCreatures = new ArrayList<>();
        for(Iterator<Creature> it = creatures.iterator(); it.hasNext(); ) {
            Creature creature = it.next();
            totalOxygenInvolved += creature.act(newCreatures, dayTime, oxygenLevel, disease, step);
            if(!creature.isAlive()) {
                it.remove();
            }
        }

        if(Randomizer.getRandom().nextDouble() <= STORM_HAPPEN_PROBABILITY) {
            weather.underwaterStorm();
            weather.setStormActive(true);
        } else {
            weather.setStormActive(false);
        }

        oxygenLevel += totalOxygenInvolved;
        disease.updateSpreadState(creatures);
        creatures.addAll(newCreatures);
        view.showStatus(step, field, dayTime, weather, oxygenLevel);
    }

    /**
     * Reset the simulation to its starting state.
     */
    public void reset()
    {
        step = 0;
        creatures.clear();
        populate();
        oxygenLevel = 1;
        disease.reset();
        Animal.resetDiseaseDeathCount();
        view.showStatus(step, field, timeOfDay(), weather, oxygenLevel);
    }

    /**
     * Populate the field with creatures. For each grid cell, the first species whose
     * spawn probability is met (checked in order) is placed there; at most one per cell.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                for(int i = 0; i < SPAWN_PROBABILITIES.length; i++) {
                    if(rand.nextDouble() <= SPAWN_PROBABILITIES[i]) {
                        Location location = new Location(row, col);
                        creatures.add(SPAWN_FACTORIES[i].create(true, field, location));
                        break;
                    }
                }
            }
        }
    }

    /**
     * Pause for the given number of milliseconds.
     */
    private void delay(int millisec)
    {
        try {
            Thread.sleep(millisec);
        }
        catch(InterruptedException ie) {
            // wake up
        }
    }

    /**
     * Return true if the current step is daytime (steps 0–4 within every 10-step cycle).
     */
    public boolean timeOfDay()
    {
        return (step % 10) < 5;
    }

}
