import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JFrame;

/**
 * The GUIHandler handles the GUI by switching between views and forwarding
 * user actions to the SimulationCoordinator. It is responsible solely for
 * UI event handling and frame management.
 *
 * @version 2022.02.28
 */
public class GUIHandler
{
    private final SimulationCoordinator coordinator;
    private JFrame currentFrame;
    private ArrayList<String> animalChoices;
    private ArrayList<String> habitatChoices;
    private ArrayList<String> scenarioChoices;

    /**
     * Build a GUIHandler with appropriate lists of choices for animals, habitats, and climate change scenarios.
     *
     * @param initializer (Initializer) The initializer used to set up simulations.
     * @param animalChoices (ArrayList<String>) List of animal choices.
     * @param habitatChoices (ArrayList<String>) List of habitat choices.
     * @param scenarioChoices (ArrayList<String>) List of climate change scenario choices.
     */
    public GUIHandler(Initializer initializer, ArrayList<String> animalChoices, ArrayList<String> habitatChoices, ArrayList<String> scenarioChoices)
    {
        this.coordinator = new SimulationCoordinator(initializer);
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
     * Switch to simulator view. Delegates initialization to the coordinator and
     * hides the current menu frame on success.
     *
     * @param chosenHabitat (String) The name of the chosen habitat.
     * @param selectedAnimals (HashMap<String, Integer>) Key-pair associations of animal names and counts.
     * @param chosenScenario (String) The name of the chosen climate change scenario.
     */
    public void switchToSimulatorView(String chosenHabitat, HashMap<String, Integer> selectedAnimals, String chosenScenario)
    {
        if (coordinator.initializeSimulation(chosenHabitat, selectedAnimals, chosenScenario)) {
            currentFrame.setVisible(false);
        }
    }

    /**
     * Switch to menu view. Ends the current simulation and opens a fresh MenuView.
     */
    public void switchToMenuView()
    {
        coordinator.endSimulation();
        currentFrame.setVisible(false);
        showMenuView();
    }

    /**
     * Launches a long simulation (number of steps defined in the Simulator class).
     */
    public void launchLongSimulation()
    {
        coordinator.launchLongSimulation();
    }

    /**
     * Launches a simulation long of 100 steps, allowing user to
     * periodically examine the state of the running simulation.
     */
    public void runHundredSteps()
    {
        coordinator.runHundredSteps();
    }

    /**
     * Launches a single step of simulation, allowing user to
     * have even greater details on the species' behaviors.
     */
    public void runOneStep()
    {
        coordinator.runOneStep();
    }
}
