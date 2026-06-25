import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to start the simulation
 * @version 2022.03.2
 */
public class Main {
    private static List<Double[]> allCombs = new ArrayList<>();

    public static void main(String[] args) {
        Simulator sim = new Simulator();
        //runSim(sim);
        // Uncomment above line to have the simulation run as soon as the window is rendered
    }


    public static void runSim(Simulator sim) {
        sim.runLongSimulation();
    }
}

