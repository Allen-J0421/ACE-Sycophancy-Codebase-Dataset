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
    private static final String PLAY_LABEL = "Play";
    private static final String PAUSE_LABEL = "Pause";
    private static final int LONG_SIMULATION_STEPS = 500;

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

    // List of animals in the field.
    private List<LivingOrganism> animals;
    // List of plants in the field
    private List<LivingOrganism> plants;
    // The current state of the field.
    private Field field;
    // The simulation clock.
    private Time time;
    // The simulation weather system.
    private Weather weather;
    // A graphical view of the simulation.
    private SimulatorView view;
    // Buttons used to control the simulation.
    private JButton playPauseButton;
    private JButton runLongSimulationButton;
    private JButton simulateOneStepButton;
    private JButton resetButton;
    private JButton quitButton;
    
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
        time = new Time();
        weather = new Weather(time);
        field = new Field(depth, width, time, weather);

        createButtons();
        updateButtonState();

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width, getButtons());
        configureViewColors();
        
        // Setup a valid starting point.
        reset();
    }
    
    /**
     * Controls the functionality of the Play/Pause button. This depends
     * on whether the simulation is paused or not.
     */
    private void handlePlayPause() 
    {
        boolean isPaused = time.getIsPaused();
        
        time.toggleIsPaused();
        
        if (isPaused) {
            if (! time.getIsFinished()) {
                simulate();
            }
        }
        
        updateButtonState();
    }
    
    /**
     * Run the simulation from its current state for a reasonably long 
     * period: 500 steps.
     */
    private void runLongSimulation()
    {
        simulate(LONG_SIMULATION_STEPS);
    }
    
    /**
     * Run the simulation from its current state for the given number of 
     * steps.
     * @param numSteps The number of steps to run for.
     */
    private void simulate(int numSteps)
    {
        // Sets the number of steps that the simulation run started at
        time.setStepsToRunFor(numSteps);
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
        time.setIsPaused(false);
        
        // Adjust button visibility.
        updateButtonState();
        
        while (! time.getIsPaused() && ! time.getIsFinished() && view.isViable(field)) {
            simulateOneStep();
        }
        
        time.setIsPaused(true);
        updateButtonState();
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * predator, prey, and plant.
     */
    private void simulateOneStep()
    {
        time.incrementStep();
        weather.updateWeather();

        // Provide space for newborn animals and plants;
        List<LivingOrganism> newAnimals = new ArrayList<>();
        List<LivingOrganism> newPlants = new ArrayList<>();

        advanceOrganisms(animals, newAnimals);
        advanceOrganisms(plants, newPlants);
        
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
        time.resetStep();
        updateButtonState();
        
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
                populateLocation(rand, row, col);
            }
        }
    }

    /**
     * Build the control buttons once during construction.
     */
    private void createButtons()
    {
        playPauseButton = createButton(PLAY_LABEL, this::handlePlayPause, true);
        runLongSimulationButton = createButton("Run Long Sim", this::runLongSimulation, true);
        simulateOneStepButton = createButton("Sim One Step", this::simulateOneStep, false);
        resetButton = createButton("Reset", this::reset, false);
        quitButton = createButton("Quit", this::quit, false);
    }

    /**
     * Create a button with the supplied action.
     */
    private JButton createButton(String label, Runnable action, boolean runInBackground)
    {
        JButton button = new JButton(label);
        button.addActionListener(e -> {
            if (runInBackground)
            {
                new Thread(action).start();
            }
            else
            {
                action.run();
            }
        });

        return button;
    }

    /**
     * Return the control buttons in the order expected by the view.
     */
    private JButton[] getButtons()
    {
        return new JButton[] {
            playPauseButton,
            runLongSimulationButton,
            simulateOneStepButton,
            resetButton,
            quitButton
        };
    }

    /**
     * Register the colors used by each simulation class.
     */
    private void configureViewColors()
    {
        view.setColor(Zebra.class, Color.BLACK, Color.WHITE);
        view.setColor(Giraffe.class, Color.YELLOW, Color.BLACK);
        view.setColor(Lemur.class, Color.BLUE, Color.WHITE);
        view.setColor(Lion.class, Color.RED, Color.WHITE);
        view.setColor(Cheetah.class, Color.ORANGE, Color.BLACK);
        view.setColor(Plant.class, Color.GREEN, Color.BLACK);
    }

    /**
     * Let a collection of organisms act and remove any that die this step.
     */
    private void advanceOrganisms(List<LivingOrganism> organisms, List<LivingOrganism> newbornOrganisms)
    {
        for(Iterator<LivingOrganism> it = organisms.iterator(); it.hasNext(); ) 
        {
            LivingOrganism organism = it.next();
            
            if(organism != null) 
            {
                organism.act(newbornOrganisms);
                
                if (! organism.isAlive())
                {
                    it.remove();
                }
            }
        }
    }

    /**
     * Populate one field location with its starting plant and optional animal.
     */
    private void populateLocation(Random rand, int row, int col)
    {
        Location location = new Location(row, col);

        // Plants fill the entire field initially.
        plants.add(new Plant(true, field, location));

        Animal animal = createRandomAnimal(rand, location);
        if(animal != null) 
        {
            animals.add(animal);
        }
    }

    /**
     * Randomly create an initial animal for a location.
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
     * Sets the usability and label of the buttons to match the current state.
     */
    private void updateButtonState() 
    {
        boolean isFinished = time.getIsFinished();
        boolean isStopped = time.getIsPaused() || isFinished;

        // Adjust button usability.
        playPauseButton.setEnabled(!isFinished);
        runLongSimulationButton.setEnabled(isStopped);
        simulateOneStepButton.setEnabled(isStopped);
        resetButton.setEnabled(isStopped);
        
        // Set the label on the play/pause button.
        playPauseButton.setText(isStopped ? PLAY_LABEL : PAUSE_LABEL);
    }
    
    /**
     * Used by the quit button to close the application.
     */
    private void quit()
    {
        System.exit(0);
    }
}
