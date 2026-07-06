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
		SimulationState initialState = simulator.getState();
		refreshSpeciesColors(initialState);
		syncViews(initialState);
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
	public void onStep(SimulationState state) {
		syncViews(state);
	}


	@Override
	public void onReset(SimulationState state) {
		graphView.reset();
		refreshSpeciesColors(state);
		syncViews(state);
	}


	private void syncViews(SimulationState state) {
		gridView.showStatus(
			state.getStep(),
			state.getTimeCycle(),
			state.getField(),
			state.getClimate(),
			state.getSickPercentage()
		);
		graphView.showStatus(state.getStep(), state.getField());
	}


	private void refreshSpeciesColors(SimulationState state) {
		Set<Class<?>> seen = new HashSet<>();
		for (Animal animal : simulator.getAnimals()) {
			if (seen.add(animal.getClass())) {
				graphView.setColor(animal.getClass(), animal.getObjectColor(state.getClimate()));
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
