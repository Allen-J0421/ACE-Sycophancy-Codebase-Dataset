import java.util.List;

/**
 * A class representing shared characteristics of producers.
 *
 * @version 18.02.22 (DD:MM:YY)
 */
public abstract class Producer extends Actor
{
    // Fraction of the normal breeding probability applied when there is no rain:
    private static final double NO_RAIN_BREEDING_FACTOR = 0.2;

    /**
     * Create a new producer at a location in the field.
     *
     * @param field            The field currently occupied.
     * @param location         The location within the field.
     * @param consumptionWorth The worth of the producer if consumed.
     */
    public Producer(Field field, Location location, int consumptionWorth,
                    double breedingProbability, int maxBirthsAtOnce, int maxAge)
    {
        super(field, location, consumptionWorth, breedingProbability,
              maxBirthsAtOnce, 0, maxAge);
    }

    /**
     * Make this producer act - that is: make it do
     * whatever it wants/needs to do.
     *
     * @param newProducers A list to receive newly born producers.
     */
    public void act(List<Actor> newProducers)
    {
        incrementAge();
        if (getIsAlive())
        {
            giveBirth(newProducers);
        }
    }

    /**
     * Create a new offspring of this producer type at the given location.
     * Subclasses return an instance of their own concrete type.
     *
     * @param field    The field to place the offspring in.
     * @param location The location to place the offspring at.
     * @return A new offspring Producer.
     */
    protected abstract Producer createOffspring(Field field, Location location);

    /**
     * Check whether or not this producer is to give birth at this step.
     * New births will be made into free adjacent locations.
     *
     * @param newProducers A list to return newly born producers.
     */
    protected void giveBirth(List<Actor> newProducers)
    {
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());

        int births = breed();

        for (int b = 0; b < births && free.size() > 0; b++)
        {
            Location location = free.remove(0);
            newProducers.add(createOffspring(field, location));
        }
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     *
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births = 0;

        double actualBreedingProbability = getBreedingProbability();
        if (!WeatherSystem.getIsRaining())
            actualBreedingProbability *= NO_RAIN_BREEDING_FACTOR;

        if (rand.nextDouble() <= actualBreedingProbability)
            births = rand.nextInt(getMaxBirthsAtOnce()) + 1;

        return births;
    }
}
