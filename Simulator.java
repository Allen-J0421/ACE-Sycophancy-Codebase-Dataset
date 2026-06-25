import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This is the main class of the simulation, as it connects all the other classes
 * and runs the simulation. In general, the classes simulate a habitat in which
 * there are preys who feed on plants, and predators who compete for the preys.
 *
 * @version 2022.02.28
 */
public class Simulator
{
    private static final int LONG_SIMULATION_STEP_COUNT = 2000;
    // List of species in the field.
    private List<Species> species;
    // The current state of the field.
    private Field field;
    // keep track of the simulation steps.
    private SimulationStep simStep;
    // A graphical view of the simulation.
    private SimulatorView view;
    // keep track of the time in the simulation
    private Time time;
    // the habitat of the simulation
    private Habitat simulationHabitat;
    private boolean simulationIsOn;

    private static final int DEFAULT_DELAY = 0;

    /**
     * Construct a simulation field with default size.
     */
    public Simulator(Habitat simulationHabitat, Time time, List<Species> speciesInSimulation, Field field, SimulationStep simulationStepCounter, SimulatorView simulatorView)
    {
        this.simulationHabitat = simulationHabitat;
        this.time = time;
        this.species = speciesInSimulation;
        this.field = field;
        this.simStep = simulationStepCounter;
        this.view = simulatorView;
        this.simulationIsOn = true;

        view.showStatus(simStep.getCurrentStep(), time.timeString(), simulationHabitat.getCurrentSeason(), simulationHabitat.getCurrentTemperature(), field);
    }


    /**
     * Run the simulation from its current state for a reasonably long period set as a class constant.
     */
    public void runLongSimulation()
    {
        simulate(LONG_SIMULATION_STEP_COUNT);
    }
    
    public void runHundredSteps()
    {
        simulate(100);
    }

    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     *
     * @param numSteps (int) The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        for(int step = 1; step <= numSteps && view.isViable(field); step++) {
            delay(DEFAULT_DELAY);
            simulateOneStep();
            // delay(200);   // uncomment this to run more slowly
        }
    }

    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each specie.
     */
    public void simulateOneStep()
    {
        if (simulationIsOn) {
            simStep.incStep();
            simulationHabitat.habitatStep();
            time.timeStep();

            // the following variables are added here to reduce method calls:
            boolean isSpring = simulationHabitat.getIsSpring();
            boolean isNight = time.getIsNight();
            int currentTemperature = simulationHabitat.getCurrentTemperature();
            boolean yearPassed = simulationHabitat.yearPassed();

            // Provide space for newborn species.
            List<Species> newSpecies = new ArrayList<>();
            // Let all species act.
            for(Iterator<Species> it = species.iterator(); it.hasNext(); )
            {
                Species specie = it.next();
                // Update the status of isSpring in the plants (done here to reduce coupling)
                if (specie instanceof Plant)
                {
                    Plant tempPlant = (Plant) specie;
                    if (tempPlant.getIsSpring() != isSpring) {
                        tempPlant.toggleIsSpring();
                    }
                }

                specie.act(newSpecies, isNight, currentTemperature, yearPassed);
                if(! specie.isAlive()) {
                    it.remove();
                }
            }

            // Add the newly born species to the main lists.
            species.addAll(newSpecies);
            view.showStatus(simStep.getCurrentStep(), time.timeString(), simulationHabitat.getCurrentSeason(), simulationHabitat.getCurrentTemperature(), field);
        }
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void endSimulation()
    {
        simStep.reset();
        species.clear();
        simulationIsOn = false;
    }

    /**
     * Pause for a given time.
     * @param millisec (int) The time to pause for, in milliseconds
     */
    private void delay(int millisec)
    {
        try {
            Thread.sleep(millisec);
        }
        catch (InterruptedException ie) {
            // wake up
        }
    }
}
