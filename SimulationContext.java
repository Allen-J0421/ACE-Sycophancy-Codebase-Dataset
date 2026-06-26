/**
 * Read-only simulation context exposed to organisms during a step.
 */
public interface SimulationContext {

    Weather getWeather();

    TimeOfDay getTimeOfDay();
}
