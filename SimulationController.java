import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Encapsulates the step-execution logic of the simulation, extracted from Simulator
 * to adhere to the Single Responsibility Principle. Simulator owns state and UI
 * coordination; SimulationController owns how steps are advanced.
 *
 * @version 2022.03.01
 */
public class SimulationController
{
    private static final int LONG_SIMULATION_STEP_COUNT = 2000;
    private static final int DEFAULT_DELAY = 0;

    private final List<Species> species;
    private final Field field;
    private final SimulationStep simStep;
    private final SimulatorView view;
    private final Time time;
    private final Habitat simulationHabitat;
    // False after stop() is called; prevents steps from executing once the simulation ends.
    private boolean active;

    /**
     * Create a SimulationController with references to all shared simulation state.
     *
     * @param species (List<Species>) The live species list.
     * @param field (Field) The simulation field.
     * @param simStep (SimulationStep) The step counter.
     * @param view (SimulatorView) The graphical view.
     * @param time (Time) The time tracker.
     * @param simulationHabitat (Habitat) The habitat.
     */
    public SimulationController(List<Species> species, Field field, SimulationStep simStep,
                                SimulatorView view, Time time, Habitat simulationHabitat)
    {
        this.species = species;
        this.field = field;
        this.simStep = simStep;
        this.view = view;
        this.time = time;
        this.simulationHabitat = simulationHabitat;
        this.active = true;
    }

    /**
     * Prevent any further steps from executing. Called by Simulator.endSimulation().
     */
    public void stop()
    {
        active = false;
    }

    /**
     * Run the simulation for a long period defined by the class constant.
     */
    public void runLongSimulation()
    {
        simulate(LONG_SIMULATION_STEP_COUNT);
    }

    /**
     * Run the simulation for 100 steps.
     */
    public void runHundredSteps()
    {
        simulate(100);
    }

    /**
     * Run the simulation for the given number of steps, stopping early if the
     * field is no longer viable.
     *
     * @param numSteps (int) Maximum number of steps to run.
     */
    public void simulate(int numSteps)
    {
        for (int step = 1; step <= numSteps && view.isViable(field); step++) {
            delay(DEFAULT_DELAY);
            simulateOneStep();
        }
    }

    /**
     * Advance the simulation by a single step: update habitat and time, let every
     * species act, remove the dead, add newborns, and refresh the view.
     */
    public void simulateOneStep()
    {
        if (!active) {
            return;
        }

        simStep.incStep();
        simulationHabitat.habitatStep();
        time.timeStep();

        boolean isSpring = simulationHabitat.getIsSpring();
        boolean isNight = time.getIsNight();
        int currentTemperature = simulationHabitat.getCurrentTemperature();
        boolean yearPassed = simulationHabitat.yearPassed();

        List<Species> newSpecies = new ArrayList<>();
        for (Iterator<Species> it = species.iterator(); it.hasNext(); ) {
            Species specie = it.next();
            if (specie instanceof Plant) {
                Plant tempPlant = (Plant) specie;
                if (tempPlant.getIsSpring() != isSpring) {
                    tempPlant.toggleIsSpring();
                }
            }
            specie.act(newSpecies, isNight, currentTemperature, yearPassed);
            if (!specie.isAlive()) {
                it.remove();
            }
        }

        species.addAll(newSpecies);
        view.showStatus(simStep.getCurrentStep(), time.timeString(),
                simulationHabitat.getCurrentSeason(), simulationHabitat.getCurrentTemperature(), field);
    }

    /**
     * Pause execution for the given number of milliseconds.
     *
     * @param millisec (int) Duration to pause.
     */
    private void delay(int millisec)
    {
        try {
            Thread.sleep(millisec);
        } catch (InterruptedException ie) {
            // wake up
        }
    }
}
