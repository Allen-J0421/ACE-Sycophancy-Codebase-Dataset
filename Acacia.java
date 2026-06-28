/**
 * A simple model of an acacia.
 *
 * @version 18.02.22 (DD:MM:YY)
 */
public class Acacia extends Producer
{
    // Constants shared by all acacia (class variables):
    //   The likelihood of an acacia breeding:
    private static final double BREEDING_PROBABILITY = 0.07;
    //   The maximum number of births:
    private static final int MAX_BIRTHS_AT_ONCE = 1;
    //   The worth of an acacia if consumed:
    private static final int CONSUMPTION_WORTH = 40;
    //   Max Age of the Acacia tree:
    private static final int MAX_AGE = 30;

    /**
     * Create an acacia.
     *
     * @param field    The field currently occupied.
     * @param location The location within the field.
     */
    public Acacia(Field field, Location location)
    {
        super(field, location, CONSUMPTION_WORTH, BREEDING_PROBABILITY,
              MAX_BIRTHS_AT_ONCE, MAX_AGE);
    }

    @Override
    protected Producer createOffspring(Field field, Location location)
    {
        return new Acacia(field, location);
    }
}
