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
    private final SimulationController controller;
    private WeatherEvent currentWeather;
    private int step;
    private int time;
    private SimulatorView view;

    /**
     * Construct a simulation field with default size.
     */
    public Simulator() {
        this(SimulationConfig.DEFAULT);
    }

    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width) {
        this(validatedConfig(depth, width));
    }

    private Simulator(SimulationConfig config) {
        controller = new SimulationController(config);
        currentWeather = new NoWeather();
        view = new SimulatorView(config.depth, config.width);
        view.setColor(Ant.class, Color.GRAY);
        view.setColor(Dingo.class, Color.ORANGE);
        view.setColor(Eagle.class, Color.RED);
        view.setColor(Snake.class, Color.BLACK);
        view.setColor(Rat.class, Color.PINK);
        view.setColor(Emu.class, Color.YELLOW);
        view.setColor(Acacia.class, Color.GREEN);
        view.setColor(Grass.class, Color.CYAN);
        reset();
    }

    private static SimulationConfig validatedConfig(int depth, int width) {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            return SimulationConfig.DEFAULT;
        }
        return SimulationConfig.DEFAULT.withDimensions(depth, width);
    }

    /**
     * Run the simulation from its current state for a reasonably long period,
     * (4000 steps).
     */
    public void runLongSimulation() {
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
        for(int step = 1; step <= numSteps && view.isViable(controller.getField()); step++) {
            simulateOneStep();
            if(time == 24) {
                time = 0;
            }
            if(step % 5 == 0) {
                time++;
            }
            if(step % 50 == 0) {
                simulateWeather();
            }
            if(step % 100 == 0) {
                resetDisease();
                simulateDisease();
            }
            delay(20);
        }
    }

    /**
     * Run the simulation from its current state for a single step.
     * Applies the current weather effect, then advances all animals and plants.
     */
    public void simulateOneStep() {
        step++;
        currentWeather.apply(controller.getAnimals(), controller.getPlants());
        controller.stepAnimals(time);
        controller.stepPlants();
        view.showStatus(step, controller.getField(), time, currentWeather.getName());
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset() {
        step = 0;
        controller.reset();
        view.showStatus(step, controller.getField(), time, currentWeather.getName());
    }

    /**
     * Pause for a given time.
     * @param millisec  The time to pause for, in milliseconds
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
     * Randomly assign a weather condition for the current period.
     */
    private void simulateWeather() {
        Random rand = Randomizer.getRandom();
        if(rand.nextInt(5) == 0) {
            currentWeather = new RainWeather();
        }
        else if(rand.nextInt(5) == 1) {
            currentWeather = new FloodWeather();
        }
        else if(rand.nextInt(5) == 2) {
            currentWeather = new DroughtWeather();
        }
        else if(rand.nextInt(5) == 3) {
            currentWeather = new FogWeather();
        }
        else if(rand.nextInt(5) == 4) {
            currentWeather = new NoWeather();
        }
    }

    /**
     * Simulate disease by calling giveDisease on every animal in the simulation.
     */
    public void simulateDisease() {
        for(Animal animal : controller.getAnimals()) {
            animal.giveDisease();
        }
    }

    /**
     * Reset the disease for all the animals in the simulation.
     */
    private void resetDisease() {
        for(Animal animal : controller.getAnimals()) {
            animal.resetDisease();
        }
    }
}
