import java.util.HashSet;
import java.util.Set;


public class SimulationRunner {


	private final Simulator simulator;

	private final SimulatorView gridView;

	private final GraphView graphView;


	public SimulationRunner() {
		simulator = new Simulator();
		Field field = simulator.getField();
		gridView = new SimulatorView(field.getDepth(), field.getWidth());
		graphView = new GraphView(1000, 500, 500);
		refreshSpeciesColors();
		syncViews();
	}


	public void runLongSimulation() {
		simulate(4000);
	}


	public void simulate(int numSteps) {
		for (int i = 1; i <= numSteps && gridView.isViable(simulator.getField()); i++) {
			simulator.simulateOneStep();
			syncViews();
			delay(60);
		}
	}


	public void reset() {
		graphView.reset();
		simulator.reset();
		refreshSpeciesColors();
		syncViews();
	}


	private void syncViews() {
		gridView.showStatus(
			simulator.getStep(),
			simulator.getCurrentTimeCycle(),
			simulator.getField(),
			simulator.getClimate(),
			simulator.getSickPercentage()
		);
		graphView.showStatus(simulator.getStep(), simulator.getField());
	}


	private void refreshSpeciesColors() {
		Set<Class<?>> seen = new HashSet<>();
		for (Animal animal : simulator.getAnimals()) {
			if (seen.add(animal.getClass())) {
				graphView.setColor(animal.getClass(), animal.getObjectColor(simulator.getClimate()));
			}
		}
	}


	private void delay(int millisec) {
		try {
			Thread.sleep(millisec);
		} catch (InterruptedException ie) {
			// restore interrupt status
			Thread.currentThread().interrupt();
		}
	}
}
