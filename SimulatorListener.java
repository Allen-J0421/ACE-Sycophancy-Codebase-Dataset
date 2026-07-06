
public interface SimulatorListener {

	void onStep(SimulationState state);

	void onReset(SimulationState state);
}
