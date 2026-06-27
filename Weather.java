/**
 * Enumeration of all weather states that the simulator supports.
 *
 * Each constant declares its full behavioural profile as constructor arguments,
 * so the effect of adding or adjusting a weather type is localised to this
 * file.  Consumers query named properties ({@link #getActivityReduction()},
 * {@link #precipitates()}) rather than comparing against specific constants,
 * which means new weather types automatically participate in all existing
 * behaviour without requiring changes to consumers.
 *
 * @version 8.6.5
 */
enum Weather {

    //                     activityReduction  precipitates
    Rainy  (0.50,          true),
    Sunny  (1.00,          false),
    Cloudy (0.85,          false),
    Stormy (0.20,          false);

    /** 0–1 multiplier applied to animal activity probability. 1 = full activity. */
    private final double  activityReduction;

    /** Whether this weather type produces rainfall (e.g., triggers grass growth). */
    private final boolean precipitates;

    Weather(double activityReduction, boolean precipitates)
    {
        this.activityReduction = activityReduction;
        this.precipitates      = precipitates;
    }

    /**
     * @return a 0–1 multiplier: 1 means no suppression, lower values reduce
     *         the probability that animals act on a given step
     */
    public double getActivityReduction()
    {
        return activityReduction;
    }

    /**
     * @return true if this weather type produces rain (grass growth bonus,
     *         etc.); false otherwise
     */
    public boolean precipitates()
    {
        return precipitates;
    }
}
