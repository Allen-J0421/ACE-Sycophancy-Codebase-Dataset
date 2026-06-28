import java.util.List;

/**
 * A simple model of a carcass.
 *
 * @version 27.02.22 (DD:MM:YY)
 */
public class Carcass extends Actor
{
    // Whether this carcass has decomposed long enough to become diseased:
    private boolean diseased;
    // Steps remaining before the corpse becomes diseased:
    private int stepsBeforeInfection;

    /**
     * Create a new carcass at a location in the field.
     *
     * @param field            The field currently occupied.
     * @param location         The location within the field.
     * @param consumptionWorth The worth of the carcass if consumed.
     */
    public Carcass(Field field, Location loc, int consumptionWorth)
    {
        super(field, loc, consumptionWorth, 0, 0, 0, 1);
        stepsBeforeInfection = rand.nextInt(15);
    }

    /**
     * Make this carcass act - that is: decompose and potentially become diseased.
     *
     * @param newCarcasses Unused; carcasses do not spawn offspring.
     */
    public void act(List<Actor> newCarcasses)
    {
        stepsBeforeInfection--;
        if (stepsBeforeInfection == 0)
        {
            diseased = true;
        }
    }

    /**
     * @return True if the carcass is diseased.
     */
    public boolean isDiseased()
    {
        return diseased;
    }
}
