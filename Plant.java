import java.util.List;

/**
 * A class representing plants in the simulation. It extends
 * the abstract class Species
 *
 * @version 2022.02.28
 */
public class Plant extends Species
{
    // The plant's maximum health
    private int maxHealth;
    // The probability that the plant's health grows
    private static final double GROWING_PROBABILITY = 0.1;
    // Keep track of the plant's health
    private int currentHealth;
    // true if the plant can regrow; reset each season change, cleared on wilt to prevent same-season re-emergence
    private boolean canRegrow;
    // true if plant appears dead due to temperature circumstances
    private boolean deadDueTemperature;

    /**
     * Create an instance of Plant
     *
     * @param field (Field) The field currently occupied.
     * @param location (Location) The location within the field.
     * @param name (String) The name of the plant
     * @param maximumTemperature (int) The maximum temperature that the plant can withstand
     * @param minimumTemperature (int) The minimum temperature that the plant can withstand
     * @param nutritionalValue (int) The nutritional value given to the specie that eats this plant
     * @param reproductionProbability (double) The probability that this plant will reproduce
     * @param maxHealth (int) The plant's maximum health
     */
    public Plant(Field field, Location location, String name, int maximumTemperature, int minimumTemperature, int nutritionalValue, double reproductionProbability, int maxHealth)
    {
        super(field, location, name, maximumTemperature, minimumTemperature, nutritionalValue, reproductionProbability);
        this.maxHealth = maxHealth;
        currentHealth = maxHealth;
        canRegrow = true;
        deadDueTemperature = false;
    }

    /**
     * Imitate a plant's step by doing the following:
     * 1) if the plant can't survive the temperature, then it dies
     * 2) else if the time is day:
     *      i) if the plant is dead, and it's spring, and the temperature is suitable,
     *         then grow back
     *      ii) else if the plant is alive, then
     *          a) increase max health by 1 if it lived for a year
     *          b) reproduce and grow
     *
     * @param newPlants (List<Species>) A list to return the new plants created.
     * @param isNight (boolean) true if it is night in the simulation
     * @param temperature (int) The current temperature of the simulation
     * @param yearPassed (boolean) True if a year has passed in the simulation
     * @param isSpring (boolean) True if the current season is spring
     * @param seasonChanged (boolean) True if the season just changed this step
     */
    public void act(List<Species> newPlants, boolean isNight, int temperature, boolean yearPassed, boolean isSpring, boolean seasonChanged)
    {
        if (seasonChanged) {
            canRegrow = true;
        }
        // 1)
        if (! deadDueTemperature && ! survivesTemperature(temperature))
        {
            wilt();
        }
        // 2)
        else if (! isNight)
        {
            // i)
            if (deadDueTemperature && survivesTemperature(temperature) && isSpring) {
                regrow();
            }
            // ii)
            else if (! deadDueTemperature) {
                // a)
                if (yearPassed) {
                    maxHealth++;
                }
                // b)
                reproduce(newPlants);
                grow();
            }
        }
    }

    /**
     * Add a new plant in a free neighbouring location if the two
     * following conditions are met:
     * 1- The production probability meets the random number
     * 2- There is a free adjacent location.
     *
     * @param newPlants (list<Species>)A list to return the new plant
     */
    protected void reproduce(List<Species> newPlants)
    {
        if (rand.nextDouble() <= getReproductionProbability())
        {
            Field field = getField();
            if (field != null)
            {
                List<Location> free = field.getFreeAdjacentLocations(getLocation());

                if (free.size() > 0) {
                    Location loc = free.remove(0);
                    Plant newPlant = new Plant(field, loc, getName(), getMaximumTemperature(), getMinimumTemperature(), getNutritionalValue(), getReproductionProbability(), maxHealth);
                    newPlants.add(newPlant);
                }
            }
        }
    }

    /**
     * The plant wilts due to temperature: it is removed from the field but remains in the
     * species list so it can regrow in spring. Unlike permanent death (when eaten), the
     * plant's alive state is unchanged and its field/location references are preserved.
     */
    private void wilt()
    {
        if(getLocation() != null) {
            deadDueTemperature = true;
            canRegrow = false; // prevent immediate regrowth next step
            getField().clear(getLocation());
        }
    }

    /**
     * The dead plant is placed back in the field if its previous location is empty and can regrow, otherwise do nothing.
     * If it grows, then it grows back to full health.
     */
    private void regrow()
    {
        if(getField().getObjectAt(getLocation()) == null && canRegrow)   {
            deadDueTemperature = false;
            getField().place(this, getLocation());
            currentHealth = maxHealth;
        }
    }

    /**
     * Increase the plant's health by one if the random number meets the growing probability
     */
    private void grow()
    {
        if (currentHealth < maxHealth && rand.nextDouble() <= GROWING_PROBABILITY) {
            currentHealth++;
        }
    }

    /**
     * The effect of getting eaten by an animal.
     * Decrement currentHealth by one, and set dead if the current health is less than 1.
     */
    public void eaten()
    {
        currentHealth--;

        if (currentHealth <= 0)    {
            super.setDead();
        }
    }

}
