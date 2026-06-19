import java.util.Set;


public interface SimulationObserver {

	Set<Class<? extends SimulationEvent>> getSubscribedEventTypes();


	void onSimulationEvent(SimulationEvent event);


	default boolean supports(SimulationEvent event) {
		return getSubscribedEventTypes().stream()
				.anyMatch(eventType -> eventType.isInstance(event));
	}
}
