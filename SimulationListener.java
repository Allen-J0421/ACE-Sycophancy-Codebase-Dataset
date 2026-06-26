/**
 * This file is part of the Predator-Prey Simulation.
 *
 * Observes the simulation. Instead of the engine pushing individual display
 * updates to a known view, interested parties (such as the GUI) register as
 * listeners and are notified of simulation events, reading whatever state they
 * need from the {@link SimulationEvent}.
 *
 * @version 2022.03.02
 */
public interface SimulationListener {

    /**
     * Called when the simulation has been reset to a fresh starting state.
     *
     * @param event A snapshot of the starting state.
     */
    void simulationReset(SimulationEvent event);

    /**
     * Called when the simulation has completed a single step.
     *
     * @param event A snapshot of the state after the step.
     */
    void simulationStepCompleted(SimulationEvent event);
}
