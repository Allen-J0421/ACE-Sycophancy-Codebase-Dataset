/**
 * Interface that decouples the simulation engine from the GUI.
 * SimulatorView wires its buttons to a SimulationControls instance,
 * so neither class needs to know about the other's internals.
 *
 * @version 2022.03.02
 */
public interface SimulationControls
{
    /** Run exactly one simulation step. */
    void stepOnce();

    /** Run a long simulation (4000 steps) on a background thread. */
    void runLongSimulation();

    /** Stop an in-progress simulation. */
    void stop();

    /** Reset the simulation to its starting state. */
    void reset();

    /** Returns true while a simulation is actively running. */
    boolean isPlaying();
}
