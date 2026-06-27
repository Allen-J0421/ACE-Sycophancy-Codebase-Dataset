import java.util.*;
import java.awt.Color;

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
    // The probability that a dingo will be created in any given grid position.
    private static final double DINGO_CREATION_PROBABILITY = 0.09;
    // The probability that an ant will be created in any given grid position.
    private static final double ANT_CREATION_PROBABILITY = 0.13;
    // The probability that a rat will be created in any given grid position.
    private static final double RAT_CREATION_PROBABILITY = 0.11;
    // The probability that an eagle will be created in any given grid position.
    private static final double EAGLE_CREATION_PROBABILITY = 0.12;
    // The probability that a snake will be created in any given grid position.
    private static final double SNAKE_CREATION_PROBABILITY = 0.12;
    // The probability that an emu will be created in any given grid position.
    private static final double EMU_CREATION_PROBABILITY = 0.08;
    // The probability that acacia will be created in any given grid position.
    private static final double ACACIA_CREATION_PROBABILITY = 0.34;
    // The probability that grass will be created in any given grid position.
    private static final double GRASS_CREATION_PROBABILITY = 0.36;

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
    // A graphical view of the simulation.
    private SimulatorView view;

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
    public Simulator(int depth, int width) {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }

        entities = new ArrayList<>();
        weather = "none";
        field = new Field(depth, width);

        view = new SimulatorView(depth, width);
        view.setColor(Ant.class, Color.GRAY);
        view.setColor(Dingo.class, Color.ORANGE);
        view.setColor(Eagle.class, Color.RED);
        view.setColor(Snake.class, Color.BLACK);
        view.setColor(Rat.class, Color.PINK);
        view.setColor(Emu.class, Color.YELLOW);
        view.setColor(Acacia.class, Color.GREEN);
        view.setColor(Grass.class, Color.CYAN);

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

    public static void main(String[] args) {
        Simulator sim = new Simulator();
        sim.runLongSimulation();
    }

    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps) {
        for(int step = 1; step <= numSteps && view.isViable(field); step++) {
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

        if(weather.equals("rain")) {
            resetWeather();
            for(LivingEntity e : entities) {
                if(e instanceof Plant) ((Plant) e).setRain();
            }
        }
        else if(weather.equals("flood")) {
            resetWeather();
            List<LivingEntity> ratsAndAnts = new ArrayList<>();
            for(LivingEntity e : entities) {
                if(e instanceof Ant || e instanceof Rat) ratsAndAnts.add(e);
            }
            Collections.shuffle(ratsAndAnts);
            for(int i = 0; i < ratsAndAnts.size() / 5; i++) {
                ratsAndAnts.get(i).setDead();
            }
        }
        else if(weather.equals("drought")) {
            resetWeather();
            List<LivingEntity> allPlants = new ArrayList<>();
            for(LivingEntity e : entities) {
                if(e instanceof Plant) allPlants.add(e);
            }
            Collections.shuffle(allPlants);
            for(int i = 0; i < allPlants.size() / 5; i++) {
                allPlants.get(i).setDead();
            }
        }
        else if(weather.equals("fog")) {
            resetWeather();
            for(LivingEntity e : entities) {
                if(e instanceof Animal) ((Animal) e).setFog();
            }
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

        view.showStatus(step, field, time, weather);
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset() {
        step = 0;
        entities.clear();
        populate();

        // Show the starting state in the view.
        view.showStatus(step, field, time, weather);
    }

    /**
     * Randomly populate the field with animals and plants.
     */
    private void populate() {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                if(rand.nextDouble() <= DINGO_CREATION_PROBABILITY) {
                    entities.add(new Dingo(true, field, location));
                }
                else if(rand.nextDouble() <= ANT_CREATION_PROBABILITY) {
                    entities.add(new Ant(true, field, location));
                }
                else if(rand.nextDouble() <= SNAKE_CREATION_PROBABILITY) {
                    entities.add(new Snake(true, field, location));
                }
                else if(rand.nextDouble() <= RAT_CREATION_PROBABILITY) {
                    entities.add(new Rat(true, field, location));
                }
                else if(rand.nextDouble() <= EAGLE_CREATION_PROBABILITY) {
                    entities.add(new Eagle(true, field, location));
                }
                else if(rand.nextDouble() <= EMU_CREATION_PROBABILITY) {
                    entities.add(new Emu(true, field, location));
                }
                else if(rand.nextDouble() <= ACACIA_CREATION_PROBABILITY) {
                    entities.add(new Acacia(field, location));
                }
                else if(rand.nextDouble() <= GRASS_CREATION_PROBABILITY) {
                    entities.add(new Grass(field, location));
                }
                // else leave the location empty.
            }
        }
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
     * Reset the weather conditions across all entities.
     */
    private void resetWeather() {
        for(LivingEntity e : entities) {
            if(e instanceof Animal) ((Animal) e).resetFog();
            else if(e instanceof Plant) ((Plant) e).resetRain();
        }
    }

    /**
     * Simulate disease by calling giveDisease on every animal in the simulation.
     */
    public void simulateDisease() {
        for(LivingEntity e : entities) {
            if(e instanceof Animal) ((Animal) e).giveDisease();
        }
    }

    /**
     * Reset the disease for all the animals in the simulation.
     */
    private void resetDisease() {
        for(LivingEntity e : entities) {
            if(e instanceof Animal) ((Animal) e).resetDisease();
        }
    }
}
