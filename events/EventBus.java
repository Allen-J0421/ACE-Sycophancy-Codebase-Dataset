package events;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple synchronous event bus. Listeners register once; every published event
 * is delivered to each of them in registration order. The engine sees only the
 * {@link EventPublisher} side, so wiring up listeners is purely a composition
 * concern handled by the application's entry point.
 */
public class EventBus implements EventPublisher {

	private final List<SimulationListener> listeners = new ArrayList<>();


	public void register(SimulationListener listener) {
		listeners.add(listener);
	}


	@Override
	public void publish(SimulationEvent event) {
		for (SimulationListener listener : listeners) {
			listener.onEvent(event);
		}
	}
}
