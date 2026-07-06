import java.util.HashSet;
import java.util.Set;


public class SimulationRunner implements SimulatorListener {


	private final Simulator simulator;

	private final SimulatorView gridView;

	private final GraphView graphView;


	public SimulationRunner() {
		simulator = new Simulator();
		Field field = simulator.getField();
		gridView = new SimulatorView(field.getDepth(), field.getWidth());
		graphView = new GraphView(1000, 500, 500);
		simulator.addListener(this);
		// Simulator constructor fired reset() before we registered; sync views now.
		refreshSpeciesColors();
		syncViews();
	}


	public void runLongSimulation() {
		simulate(4000);
	}


	public void simulate(int numSteps) {
		for (int i = 1; i <= numSteps && gridView.isViable(simulator.getField()); i++) {
			simulator.simulateOneStep(); // fires onStep → syncViews
			delay(60);
		}
	}


	public void reset() {
		simulator.reset(); // fires onReset → view reset + syncViews
	}


	@Override
	public void onStep(int step, TimeCycle timeCycle, Field field, Climate climate, int sickPercentage) {
		syncViews();
	}


	@Override
	public void onReset(int step, TimeCycle timeCycle, Field field, Climate climate, int sickPercentage) {
		graphView.reset();
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
			Thread.currentThread().interrupt();
		}
	}
}
