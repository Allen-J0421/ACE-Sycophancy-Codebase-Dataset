package events;


/**
 * The publish side of the event bus: all the simulation engine is allowed to do.
 * Depending on this (rather than the concrete {@link EventBus}) keeps the engine
 * unable to enumerate or manage listeners — it can only announce what happened.
 */
public interface EventPublisher {
	void publish(SimulationEvent event);
}
