import java.util.HashMap;

/**
 * Coordinates the simulation lifecycle: initializing, running, and ending simulations.
 * GUIHandler delegates all simulation orchestration here, keeping itself focused
 * on UI event handling and frame management.
 *
 * @version 2022.02.28
 */
public class SimulationCoordinator
{
    private final Initializer simulationInitializer;
    private Simulator simulatorOnDisplay;

    /**
     * Create a coordinator backed by the given initializer.
     *
     * @param initializer (Initializer) The initializer used to build new simulations.
     */
    public SimulationCoordinator(Initializer initializer)
    {
        this.simulationInitializer = initializer;
    }

    /**
     * Initialize and store a new simulation with the given user choices.
     *
     * @param chosenHabitat (String) The name of the chosen habitat.
     * @param selectedAnimals (HashMap<String, Integer>) Animal names mapped to counts.
     * @param chosenScenario (String) The name of the chosen climate change scenario.
     * @return (boolean) true if initialization succeeded, false otherwise.
     */
    public boolean initializeSimulation(String chosenHabitat, HashMap<String, Integer> selectedAnimals, String chosenScenario)
    {
        simulatorOnDisplay = simulationInitializer.initializeSimulation(chosenHabitat, selectedAnimals, chosenScenario);
        return simulatorOnDisplay != null;
    }

    /**
     * End the currently running simulation.
     */
    public void endSimulation()
    {
        simulatorOnDisplay.endSimulation();
    }

    /**
     * Launch a long simulation on a background thread.
     */
    public void launchLongSimulation()
    {
        new Thread(simulatorOnDisplay::runLongSimulation).start();
    }

    /**
     * Run 100 simulation steps on a background thread.
     */
    public void runHundredSteps()
    {
        new Thread(simulatorOnDisplay::runHundredSteps).start();
    }

    /**
     * Run a single simulation step on the current thread.
     */
    public void runOneStep()
    {
        simulatorOnDisplay.simulate(1);
    }
}
