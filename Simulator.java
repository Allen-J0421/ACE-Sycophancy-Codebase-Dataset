import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;
import javax.swing.JButton;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing rabbits and foxes.
 *
 * @version 26/02/2022
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 270;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 180;
    // The probability that a lion will be created in any given grid position.
    private static final double LION_CREATION_PROBABILITY = 0.0125; //0.015
    // The probability that a cheetah will be created in any given grid position.
    private static final double CHEETAH_CREATION_PROBABILITY = 0.0125; //0.015
    // The probability that a zebra will be created in any given grid position.
    private static final double ZEBRA_CREATION_PROBABILITY = 0.08; //0.08
    // The probability that a giraffe will be created in any given grid position.
    private static final double GIRAFFE_CREATION_PROBABILITY = 0.08; //0.08
    // The probability that a giraffe will be created in any given grid position.
    private static final double LEMUR_CREATION_PROBABILITY = 0.081; //0.08

    private static final int PLAY_PAUSE_BUTTON = 0;
    private static final int RUN_LONG_SIM_BUTTON = 1;
    private static final int SIM_ONE_STEP_BUTTON = 2;
    private static final int RESET_BUTTON = 3;
    private static final int QUIT_BUTTON = 4;
    private static final int BUTTON_COUNT = 5;

    // List of animals in the field.
    private final List<Animal> animals;
    // List of plants in the field
    private final List<Plant> plants;
    // The current state of the field.
    private final Field field;
    // A graphical view of the simulation.
    private final SimulatorView view;
    // List of buttons that are used to control the simulation.
    private final JButton[] buttons;

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

        animals = new ArrayList<>();
        plants = new ArrayList<>();
        field = new Field(depth, width);
        buttons = new JButton[BUTTON_COUNT];

        // Creates the buttons for the view
        buttons[PLAY_PAUSE_BUTTON] = createButton("Play", () -> runInBackground(this::playPauseButton));
        buttons[RUN_LONG_SIM_BUTTON] = createButton("Run Long Sim", () -> runInBackground(this::runLongSimulation));
        buttons[SIM_ONE_STEP_BUTTON] = createButton("Sim One Step", this::simulateOneStep);
        buttons[RESET_BUTTON] = createButton("Reset", this::reset);
        buttons[QUIT_BUTTON] = createButton("Quit", this::quit);

        // Sets the visibility of the buttons in accordance to the current state of the simulation.
        buttonToggle();

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width, buttons);
        setViewColors();

        // Setup a valid starting point.
        reset();
    }

    /**
     * Register the view colors for each simulation participant.
     */
    private void setViewColors()
    {
        view.setColor(Zebra.class, Color.BLACK, Color.WHITE);
        view.setColor(Giraffe.class, Color.YELLOW, Color.BLACK);
        view.setColor(Lemur.class, Color.BLUE, Color.WHITE);
        view.setColor(Lion.class, Color.RED, Color.WHITE);
        view.setColor(Cheetah.class, Color.ORANGE, Color.BLACK);
        view.setColor(Plant.class, Color.GREEN, Color.BLACK);
    }

    /**
     * Create a button wired to a simulator action.
     */
    private JButton createButton(String label, Runnable action)
    {
        JButton button = new JButton(label);
        button.addActionListener(e -> action.run());
        return button;
    }

    /**
     * Run a simulator action on a background thread.
     */
    private void runInBackground(Runnable action)
    {
        new Thread(action).start();
    }

    /**
     * Controls the functionality of the Play/Pause button. This depends
     * on whether the simulation is paused or not.
     */
    private void playPauseButton()
    {
        boolean isPaused = Time.getIsPaused();

        Time.toggleIsPaused();

        if (isPaused) {
            if (! Time.getIsFinished()) {
                simulate();
            }
        }

        buttonToggle();
    }

    /**
     * Run the simulation from its current state for a reasonably long
     * period: 500 steps.
     */
    private void runLongSimulation()
    {
        simulate(500);
    }

    /**
     * Run the simulation from its current state for the given number of
     * steps.
     * @param numSteps The number of steps to run for.
     */
    private void simulate(int numSteps)
    {
        // Sets the number of steps that the simulation run started at
        Time.setStepsToRunFor(numSteps);
        simulate();
    }

    /**
     * Run the simulation from its current state for the given number of
     * steps in the Time Class.
     * Stop before the given number of steps if it ceases to be viable.
     * This is considered to be when only one species remains.
     */
    private void simulate()
    {
        Time.setIsPaused(false);

        // Adjust button visibility.
        buttonToggle();

        while (! Time.getIsPaused() && ! Time.getIsFinished() && view.isViable(field)) {
            simulateOneStep();
        }

        Time.setIsPaused(true);
        buttonToggle();
    }

    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * predator, prey, and plant.
     */
    private void simulateOneStep()
    {
        Time.incrementStep();
        Weather.updateWeather();

        // Provide space for newborn animals and plants;
        List<Animal> newAnimals = new ArrayList<>();
        List<Plant> newPlants = new ArrayList<>();

        // Let all animals act.
        for(Iterator<Animal> it = animals.iterator(); it.hasNext(); )
        {
            Animal animal = it.next();

            if(animal != null)
            {
                animal.act(newAnimals);

                if (! animal.isAlive())
                {
                    it.remove();
                }
            }
        }

        // Let all plants act.
        for(Iterator<Plant> it = plants.iterator(); it.hasNext(); ) {
            Plant plant = it.next();
            if(plant != null)
            {
                plant.act(newPlants);

                if (! plant.isAlive())
                {
                    it.remove();
                }
            }
        }

        // Add the newly born animals and plants to the main lists.
        animals.addAll(newAnimals);
        plants.addAll(newPlants);

        // Show the current state in the view.
        view.showStatus(field);
    }

    /**
     * Reset the simulation to a starting position.
     */
    private void reset()
    {
        // Resets the visibility of the buttons and resets the step number
        Time.resetStep();
        buttonToggle();

        // Clears all current animals and plants then repopulates the area
        animals.clear();
        plants.clear();
        populate();

        // Show the starting state in the view.
        view.showStatus(field);
    }

    /**
     * Randomly populate the field with predators, prey, and plants.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++)
        {
            for(int col = 0; col < field.getWidth(); col++)
            {
                //plants will fill the entire field initially.
                Location location = new Location(row, col);
                Plant plant = new Plant(true, field, location);
                plants.add(plant);

                Animal animal = createRandomAnimal(rand, location);

                if(animal != null)
                {
                    animals.add(animal);
                }
            }
        }
    }

    /**
     * Create an animal for a location using the configured species probabilities.
     */
    private Animal createRandomAnimal(Random rand, Location location)
    {
        if(rand.nextDouble() <= LION_CREATION_PROBABILITY)
        {
            return new Lion(true, field, location, false, false);
        }
        else if(rand.nextDouble() <= CHEETAH_CREATION_PROBABILITY)
        {
            return new Cheetah(true, field, location, false, false);
        }
        else if(rand.nextDouble() <= ZEBRA_CREATION_PROBABILITY)
        {
            return new Zebra(true, field, location, false, false);
        }
        else if(rand.nextDouble() <= GIRAFFE_CREATION_PROBABILITY)
        {
            return new Giraffe(true, field, location, false, false);
        }
        else if(rand.nextDouble() <= LEMUR_CREATION_PROBABILITY)
        {
            return new Lemur(true, field, location, false, false);
        }

        return null;
    }

    /**
     * Sets the visibility of the buttons in accordance with the current
     * state of the simulation.
     */
    private void buttonToggle()
    {
        boolean isPaused = Time.getIsPaused();
        boolean isFinished = Time.getIsFinished();

        boolean isStopped = isPaused || isFinished;

        // Adjust button usability.
        buttons[PLAY_PAUSE_BUTTON].setEnabled(!isFinished);
        buttons[RUN_LONG_SIM_BUTTON].setEnabled(isStopped);
        buttons[SIM_ONE_STEP_BUTTON].setEnabled(isStopped);
        buttons[RESET_BUTTON].setEnabled(isStopped);

        // Set the label on the play/pause button.
        if (isStopped)
        {
            buttons[PLAY_PAUSE_BUTTON].setText("Play");
        }
        else {
            buttons[PLAY_PAUSE_BUTTON].setText("Pause");
        }
    }

    /**
     * Used by the quit button to close the application.
     */
    private void quit()
    {
        System.exit(0);
    }
}
