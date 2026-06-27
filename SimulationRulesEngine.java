/**
 * Centralizes the event rules that control time-of-day and weather changes.
 *
 * @version 2022.03.02
 */
public class SimulationRulesEngine
{
    private static final int DAY_NIGHT_CYCLE_LENGTH = 80;
    private static final int DAYLIGHT_DURATION = 55;
    private static final int WEATHER_CHANGE_INTERVAL = 450;

    private final Weather weather;

    public SimulationRulesEngine(Weather weather)
    {
        this.weather = weather;
    }

    /**
     * Apply step-based environment events such as periodic weather changes.
     *
     * @param step The simulation step being executed.
     */
    public void applyStepEvents(int step)
    {
        if (step % WEATHER_CHANGE_INTERVAL == 0) {
            weather.changeWeather();
        }
    }

    /**
     * Determine whether an organism should act during the current step.
     *
     * @param organism The organism being considered.
     * @param step The current simulation step.
     * @return True if the organism is active this step.
     */
    public boolean canAct(Organism organism, int step)
    {
        return getTimeOfDay(step).matches(organism.isDiurnal());
    }

    /**
     * Return the time-of-day phase for the given step.
     *
     * @param step The current simulation step.
     * @return The time-of-day phase.
     */
    public TimeOfDay getTimeOfDay(int step)
    {
        if (step % DAY_NIGHT_CYCLE_LENGTH <= DAYLIGHT_DURATION) {
            return TimeOfDay.DAY;
        }
        return TimeOfDay.NIGHT;
    }

    /**
     * Return the weather label for the current environment.
     *
     * @return The current weather name.
     */
    public String getCurrentWeather()
    {
        return weather.getCurrentWeather();
    }

    /**
     * Set the weather explicitly.
     *
     * @param weatherName The new weather to apply.
     */
    public void setWeather(String weatherName)
    {
        weather.setWeather(weatherName);
    }

    /**
     * Reset the event rules to their initial state.
     */
    public void reset()
    {
        weather.resetWeather();
    }
}
