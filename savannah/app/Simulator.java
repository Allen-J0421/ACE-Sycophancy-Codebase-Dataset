package savannah.app;

import javax.swing.JButton;

import savannah.config.SimulationConfig;
import savannah.engine.SimulationEngine;
import savannah.model.OrganismFactory;
import savannah.model.SpeciesRegistry;
import savannah.model.SpeciesType;
import savannah.model.Time;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing rabbits and foxes.
 *
 * @version 26/02/2022
 */
public class Simulator
{
    private static final SimulationConfig CONFIG = SimulationConfig.DEFAULT;

    // Owns the simulation state.
    private final SimulationEngine engine;
    // A graphical view of the simulation.
    private SimulatorView view;
    // List of buttons that are used to control the simulation.
    private JButton[] buttons;
    
    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(CONFIG.defaultDepth, CONFIG.defaultWidth);
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
            depth = CONFIG.defaultDepth;
            width = CONFIG.defaultWidth;
        }
        
        engine = new SimulationEngine(depth, width, CONFIG);
        buttons = new JButton[5];
        
        // Creates the buttons for the view
        JButton playPause = new JButton("Play");
            playPause.addActionListener(e -> new Thread(() -> playPauseButton()).start());
            buttons[0] = playPause;
            
        JButton runLongSim = new JButton("Run Long Sim");
            runLongSim.addActionListener(e -> new Thread(() -> runLongSimulation()).start());
            buttons[1] = runLongSim;
        
        JButton simOneStep = new JButton("Sim One Step");
            simOneStep.addActionListener(e -> simulateOneStep());
            buttons[2] = simOneStep;
            
        JButton reset = new JButton("Reset");
            reset.addActionListener(e -> reset());
            buttons[3] = reset;
            
        JButton quit = new JButton("Quit");
            quit.addActionListener(e -> quit());
            buttons[4] = quit;
        
        // Sets the visibility of the buttons in accordance to the current state of the simulation.
        buttonToggle();

        // Create a view of the state of each location in the field.
        view = new SimulatorView(engine.getDepth(), engine.getWidth(), buttons, CONFIG);
        for (SpeciesType speciesType : SpeciesType.values()) {
            OrganismFactory factory = SpeciesRegistry.INSTANCE.getFactory(speciesType);
            view.setColor(speciesType, factory.getFillColor(), factory.getTextColor());
        }
        
        // Setup a valid starting point.
        reset();
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
        
        while (! Time.getIsPaused() && ! Time.getIsFinished() && view.isViable(engine.getField())) {
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
        engine.step();
        // Show the current state in the view.
        view.showStatus(engine.getField());
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    private void reset()
    {
        engine.reset();
        buttonToggle();

        // Show the starting state in the view.
        view.showStatus(engine.getField());
    }
    
    /**
     * Sets the visibility of the buttons in accordance with the current
     * state of the simulation.
     */
    private void buttonToggle() 
    {
        boolean isPaused = Time.getIsPaused();
        boolean isFinished = Time.getIsFinished();
        
        boolean isStopped = false;
        if (isPaused || isFinished) 
        {
            isStopped = true;
        }
        
        // Adjust button usability.
        buttons[0].setEnabled(!isFinished);
        buttons[1].setEnabled(isStopped);
        buttons[2].setEnabled(isStopped);
        buttons[3].setEnabled(isStopped);
        
        // Set the label on the play/pause button.
        if (isStopped) 
        {
            buttons[0].setText("Play");   
        }
        else {
            buttons[0].setText("Pause");
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
