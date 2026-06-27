/**
 * Day/night phases for the simulation event rules.
 *
 * @version 2022.03.02
 */
public enum TimeOfDay
{
    DAY("Day"),
    NIGHT("Night");

    private final String displayName;

    TimeOfDay(String displayName)
    {
        this.displayName = displayName;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public boolean matches(boolean diurnal)
    {
        return diurnal ? this == DAY : this == NIGHT;
    }
}
