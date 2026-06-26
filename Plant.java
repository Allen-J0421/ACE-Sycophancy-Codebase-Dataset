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
    // The immutable profile used to create this plant and its offspring.
    private final PlantProfile profile;
    // The probability that the plant's health grows
    private static final double GROWING_PROBABILITY = 0.1;
    // Keep track of the plant's health
    private int currentHealth;
    // true if the current season is Spring
    private boolean isSpring;
    // true if the plant can regrow, needs at least one season till it is true again
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
        this(field, location, new PlantProfile(name, maximumTemperature, minimumTemperature, nutritionalValue, reproductionProbability, maxHealth));
    }

    /**
     * Create an instance of Plant from an immutable profile.
     *
     * @param field (Field) The field currently occupied.
     * @param location (Location) The location within the field.
     * @param profile (PlantProfile) immutable plant species configuration
     */
    public Plant(Field field, Location location, PlantProfile profile)
    {
        super(field, location, profile);
        this.profile = profile;
        this.maxHealth = profile.getMaxHealth();
        currentHealth = maxHealth;
        isSpring = true;
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
     * @param conditions (SimulationConditions) environmental conditions for this step
     */
    public void act(List<Species> newPlants, SimulationConditions conditions)
    {
        updateSpringState(conditions);

        // 1)
        if (! deadDueTemperature && ! survivesTemperature(conditions.getTemperature()))
        {
            setDead();
        }
        // 2)
        else if (! conditions.isNight())
        {
            // i)
            if (deadDueTemperature && survivesTemperature(conditions.getTemperature()) && isSpring) {
                regrow();
            }
            // ii)
            else if (! deadDueTemperature) {
                // a)
                if (conditions.hasYearPassed()) {
                    maxHealth++;
                }
                // b)
                reproduce(newPlants);
                grow();
            }
        }
    }

    /**
     * Synchronise this plant with the current season when that information is available.
     *
     * @param conditions (SimulationConditions) environmental conditions for this step
     */
    private void updateSpringState(SimulationConditions conditions)
    {
        if (conditions.hasSeason() && getIsSpring() != conditions.isSpring()) {
            toggleIsSpring();
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
                    Plant newPlant = new Plant(field, loc, profile);
                    newPlant.setIsSpring(isSpring);
                    newPlants.add(newPlant);
                }
            }
        }
    }

    /**
     * The plant dies because of the temperature or because it was eaten.
     * Its location in the field is cleared, but it still remembers its field and location
     */
    protected void setDead()
    {
        if(getLocation() != null) {
            deadDueTemperature = true;
            canRegrow = false; // set to false because if left as true, it could regrow the next step
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
    public void isEaten()
    {
        currentHealth--;

        if (currentHealth <= 0)    {
            super.setDead();
        }
    }

    /**
     * If isSpring is true, then change it to false, vice versa.
     *Also, set canRegrow as true because at least a season has passed since the plant died.
     */
    public void toggleIsSpring()
    {
        isSpring = ! isSpring;
        canRegrow = true;
    }

    /**
     * @return (boolean) true if the current season is spring, false otherwise.
     */
    public boolean getIsSpring()
    {
        return isSpring;
    }

    /**
     * Set the isSpring value to the given parameter
     *
     * @param spring (boolean) true if it is spring, false otherwise.
     */
    private void setIsSpring(boolean spring)
    {
        isSpring = spring;
    }
}
