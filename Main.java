/**
 * Entry point. Creates the concrete SimulatorView and wires it to Simulator
 * as a SimulationObserver, keeping the simulator itself UI-agnostic.
 */
public class Main {
    public static void main(String[] args) {
        int depth = 80;
        int width = 120;
        SimulatorView view = new SimulatorView(depth, width);
        Simulator sim = new Simulator(depth, width, view);
        sim.runLongSimulation();
    }
}
