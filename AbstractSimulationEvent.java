public abstract class AbstractSimulationEvent implements SimulationEvent {

	private final SimulationSnapshot snapshot;


	protected AbstractSimulationEvent(SimulationSnapshot snapshot) {
		this.snapshot = snapshot;
	}


	@Override
	public SimulationSnapshot getSnapshot() {
		return snapshot;
	}
}
