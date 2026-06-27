/**
 * Immutable query object describing the current simulation state.
 *
 * @version 2022.03.02
 */
public class SimulationState
{
    private final int step;
    private final FieldSnapshot fieldSnapshot;
    private final String currentWeather;
    private final TimeOfDay timeOfDay;
    private final boolean viable;

    public SimulationState(int step, FieldSnapshot fieldSnapshot,
                           String currentWeather, TimeOfDay timeOfDay,
                           boolean viable)
    {
        this.step = step;
        this.fieldSnapshot = fieldSnapshot;
        this.currentWeather = currentWeather;
        this.timeOfDay = timeOfDay;
        this.viable = viable;
    }

    public int getStep()
    {
        return step;
    }

    public FieldSnapshot getFieldSnapshot()
    {
        return fieldSnapshot;
    }

    public String getCurrentWeather()
    {
        return currentWeather;
    }

    public TimeOfDay getTimeOfDay()
    {
        return timeOfDay;
    }

    public boolean isViable()
    {
        return viable;
    }
}
