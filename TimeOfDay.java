/**
 * The repeating time-of-day cycle used by the simulation.
 */
public enum TimeOfDay
{
    MORNING("Morning "),
    AFTERNOON("Afternoon "),
    EVENING("Evening "),
    NIGHT("Night ");

    private static final TimeOfDay[] VALUES = values();

    private final String displayName;

    TimeOfDay(String displayName)
    {
        this.displayName = displayName;
    }

    /**
     * Derive the current time of day from the simulation step.
     */
    public static TimeOfDay fromStep(int step)
    {
        int index = Math.floorMod(step - 1, VALUES.length);
        return VALUES[index];
    }

    /**
     * @return true when the current time period is night.
     */
    public boolean isNight()
    {
        return this == NIGHT;
    }

    @Override
    public String toString()
    {
        return displayName;
    }
}
