/**
 * This class is used to start the simulation
 * @version 2022.03.2
 */
public class Main {
    public static void main(String[] args) {
        Simulator sim = new Simulator();
        SimulatorView view = new SimulatorView(sim.getConfig().getDepth(), sim.getConfig().getWidth());
        for(Class<?> cls : SimulationInfo.DEFAULT_COLOR_MAP.keySet()) {
            view.setColor(cls, SimulationInfo.DEFAULT_COLOR_MAP.get(cls));
        }
        sim.addSimulationListener(view);
        view.addLongButtonListener(e -> runLongSimulation(sim));
        view.addOneStepButtonListener(e -> sim.runOneStep());
        view.addStopButtonListener(e -> sim.stopSimulation());
        view.addResetButtonListener(e -> {
            sim.stopSimulation();
            sim.reset();
        });
        //runSim(sim);
        // Uncomment above line to have the simulation run as soon as the window is rendered
    }


    public static void runSim(Simulator sim) {
        sim.runLongSimulation();
    }

    private static void runLongSimulation(Simulator sim)
    {
        Thread runLongSimThread = new Thread("SimulationRunThread"){
            public void run() { sim.runLongSimulation(); }
        };

        if(!sim.isPlayingSimulation()) {
            runLongSimThread.start();
        }
        else {
            System.out.println("Stop the simulation first");
        }
    }
}
