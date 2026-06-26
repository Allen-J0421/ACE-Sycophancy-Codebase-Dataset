/**
 * This class is used to start the simulation
 * @version 2022.03.2
 */
public class Main {
    public static void main(String[] args) {
        Simulator sim = new Simulator();
        SimulationState initialState = sim.getCurrentState();
        SimulatorView view = new SimulatorView(
                initialState.getField().getDepth(),
                initialState.getField().getWidth()
        );
        bindViewControls(sim, view);
        sim.addObserver(view);
        //runSim(sim);
        // Uncomment above line to have the simulation run as soon as the window is rendered
    }

    public static void runSim(Simulator sim) {
        sim.runLongSimulation();
    }

    private static void bindViewControls(Simulator sim, SimulatorView view)
    {
        view.addOneStepButtonListener(e -> sim.stepOnce());
        view.addStopButtonListener(e -> sim.stop());
        view.addResetButtonListener(e -> sim.reset());
        view.addLongButtonListener(e -> sim.startLongSimulation());
    }
}
