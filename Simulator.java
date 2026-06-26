import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
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
    // Factory for building and seeding simulations.
    private final SimulationFactory simulationFactory;
    
    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(SimulationFactory.DEFAULT_DEPTH, SimulationFactory.DEFAULT_WIDTH);
    }
    
    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        simulationFactory = new SimulationFactory();
        animals = new ArrayList<>();
        plants = new ArrayList<>();
        
        initializeSimulation(depth, width);
        updateButtonState();
        
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
        simulationFactory.populate(field, animals, plants);
        
        // Show the starting state in the view.
        view.showStatus(field);
    }

    /**
     * Build the core simulation state and UI.
     */
    private void initializeSimulation(int depth, int width)
    {
        SimulationFactory.SimulationComponents components =
            simulationFactory.createSimulation(depth, width,
                                               this::handlePlayPause,
                                               this::runLongSimulation,
                                               this::simulateOneStep,
                                               this::reset,
                                               this::quit);

        field = components.getField();
        time = components.getTime();
        weather = components.getWeather();
        view = components.getView();

        SimulationFactory.SimulationButtons buttons = components.getButtons();
        playPauseButton = buttons.getPlayPauseButton();
        runLongSimulationButton = buttons.getRunLongSimulationButton();
        simulateOneStepButton = buttons.getSimulateOneStepButton();
        resetButton = buttons.getResetButton();
        quitButton = buttons.getQuitButton();
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
