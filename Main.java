
public class Main {
	public static void main(String[] args) {
		int depth = 200;
		int width = 320;
		Simulator simulator = new Simulator(depth, width);
		simulator.addObserver(new SimulatorView(depth, width));
		simulator.addObserver(new GraphView(1000, 500, 500));
		simulator.runLongSimulation();
	}
}
