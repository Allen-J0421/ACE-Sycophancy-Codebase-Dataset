import java.util.List;

/**
 * Manages the collection of Species, the Field, and UI coordination for the
 * simulation. Step-execution logic is delegated to SimulationController.
 *
 * @version 2022.02.28
 */
public class Simulator
{
    // The live species list — retained here for endSimulation() cleanup.
    private final List<Species> species;
    // The step counter — retained here for endSimulation() reset.
    private final SimulationStep simStep;
    // Owns all step-execution logic.
    private final SimulationController controller;

    /**
     * Construct a Simulator, wire up the SimulationController, and show the
     * initial field state.
     *
     * @param simulationHabitat (Habitat) The simulation's habitat.
     * @param time (Time) The time tracker.
     * @param speciesInSimulation (List<Species>) Initial species list.
     * @param field (Field) The simulation field.
     * @param simulationStepCounter (SimulationStep) The step counter.
     * @param simulatorView (SimulatorView) The graphical view.
     */
    public Simulator(Habitat simulationHabitat, Time time, List<Species> speciesInSimulation,
                     Field field, SimulationStep simulationStepCounter, SimulatorView simulatorView)
    {
        this.species = speciesInSimulation;
        this.simStep = simulationStepCounter;
        this.controller = new SimulationController(speciesInSimulation, field, simulationStepCounter,
                simulatorView, time, simulationHabitat);

        simulatorView.showStatus(simulationStepCounter.getCurrentStep(), time.timeString(),
                simulationHabitat.getCurrentSeason(), simulationHabitat.getCurrentTemperature(), field);
    }

    /**
     * Run the simulation for a long period (step count defined in SimulationController).
     */
    public void runLongSimulation()
    {
        controller.runLongSimulation();
    }

    /**
     * Run the simulation for 100 steps.
     */
    public void runHundredSteps()
    {
        controller.runHundredSteps();
    }

    /**
     * Run the simulation for the given number of steps.
     *
     * @param numSteps (int) Number of steps to run.
     */
    public void simulate(int numSteps)
    {
        controller.simulate(numSteps);
    }

    /**
     * End the simulation: stop the controller, reset the step counter, and clear
     * all species from the field.
     */
    public void endSimulation()
    {
        controller.stop();
        simStep.reset();
        species.clear();
    }
}
