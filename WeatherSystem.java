/**
 * Strategy interface for weather management.
 *
 * Implementations decide which {@link Weather} state is current and how
 * transitions occur.  The default implementation ({@link RandomWeatherSystem})
 * picks the next state at random; alternative strategies (seasonal patterns,
 * deterministic test fixtures, etc.) can be substituted at construction time
 * via {@link SimulatorState#SimulatorState(WeatherSystem)}.
 */
interface WeatherSystem
{
    /** @return the current weather state */
    Weather getCurrent();

    /**
     * Force the weather to a specific state (e.g., for simulation reset).
     * @param weather the state to set
     */
    void set(Weather weather);

    /**
     * Advance the weather by one simulation step.
     * Implementations may transition to a new state or remain unchanged.
     */
    void step();
}
