/**
 * Shared day/night cycle rules for the simulation.
 *
 * @version 2022.03.02
 */
public final class DayCycle
{
    private static final int CYCLE_LENGTH = 80;
    private static final int DAY_END = 55;

    private DayCycle()
    {
    }

    /**
     * Return whether the given simulation step is during the day.
     */
    public static boolean isDay(int step)
    {
        return step % CYCLE_LENGTH <= DAY_END;
    }

    /**
     * Return whether an organism should act on the given simulation step.
     */
    public static boolean isActive(Organism organism, int step)
    {
        return organism.getIsDiurnal() == isDay(step);
    }

    /**
     * Return a display label for the time at the given simulation step.
     */
    public static String getTimeOfDay(int step)
    {
        if (isDay(step)) {
            return "Day";
        }
        return "Night";
    }
}
