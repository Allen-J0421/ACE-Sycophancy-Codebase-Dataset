import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JFrame;

/**
 * The GUIHandler handles the GUI by switching between views and coordinating
 * all the GUI's interactions with the simulation's "backend".
 *
 * @version 2022.02.28
 */
public class GUIHandler
{
    private Initializer simulationInitializer;
    private Simulator simulatorOnDisplay;
    private JFrame currentFrame;
    private ArrayList<String> animalChoices;
    private ArrayList<String> habitatChoices;
    private ArrayList<String> scenarioChoices;

    /**
     * Build a GUIHandler with appropriate lists of choices for animals, habitats, and climate change scenarios.
     *
     * @param animalChoices (ArrayList<String>) List of animal choices.
     * @param habitatChoices (ArrayList<String>) List of habitat choices.
     * @param scenarioChoices (ArrayList<String>) List of climate change scenario choices.
     */
    public GUIHandler (Initializer initializer, ArrayList<String> animalChoices, ArrayList<String> habitatChoices, ArrayList<String> scenarioChoices)
    {
        simulationInitializer = initializer;
        this.animalChoices = animalChoices;
        this.habitatChoices = habitatChoices;
        this.scenarioChoices = scenarioChoices;
        showMenuView();
    }

    /**
     * Set the menuView as the view currently on screen. The menu view is the view
     * that allows the user to set their choices concerning the simulation to be run.
     */
    private void showMenuView()
    {
        MenuView menuViewMaker = new MenuView(this, animalChoices, habitatChoices, scenarioChoices);
        JFrame menuFrame = menuViewMaker.createAndShow();
        menuFrame.pack();
        menuFrame.setVisible(true);
        currentFrame = menuFrame;
    }

    /**
     * Switch to simulator view. The actual creation of the simulator view
     * (which is automatically displayed at its creation) is done by the Initializer
     * but this method is crucial as it gives the Initializer the information needed
     * to create the simulation the user asked for and hides the MenuView currently on display.
     *
     * @param chosenHabitat (String) The name of the chosen habitat.
     * @param selectedAnimals (HashMap<String, Integer>) Key-pair associations of animal names and number of those animals to be created.
     * @param chosenScenario (String) The name of the chosen climate change scenario.
     */
    public void switchToSimulatorView(String chosenHabitat,HashMap<String, Integer> selectedAnimals,String chosenScenario)
    {
        simulatorOnDisplay = simulationInitializer.initializeSimulation(chosenHabitat, selectedAnimals, chosenScenario);
        if (simulatorOnDisplay != null) {
            currentFrame.setVisible(false);
        }
    }

    /**
     * Switch to menu view. This method allows users to launch a new simulation
     * in the same "session". It ends the currently running simulation and creates a totally
     * new instance of MenuView to be displayed.
     */
    public void switchToMenuView()
    {
        simulatorOnDisplay.endSimulation();
        currentFrame.setVisible(false);
        showMenuView();
    }

    /**
     * Launches a long simulation (number of steps defined in the Simulator class).
     */
    public void launchLongSimulation()
    {
        new Thread(simulatorOnDisplay::runLongSimulation).start();
    }

    /**
     * Launches a simulation long of 100 steps, allowing user to
     * periodically examine the state of the running simulation.
     */
    public void runHundredSteps()
    {
        new Thread(() -> simulatorOnDisplay.simulate(100)).start();
    }

    /**
     * Launches a single step of simulation, allowing user to
     * have even greater details on the species' behaviors.
     */
    public void runOneStep()
    {
        simulatorOnDisplay.simulate(1);
    }
}