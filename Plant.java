import java.util.List;

/**
 * A class representing plants in the simulation. It extends
 * the abstract class Species
 *
 * @version 2022.02.28
 */
public class Plant extends Species
{
    /**
     * Plant lifecycle states.
     */
    private enum PlantState {
        ALIVE,
        TEMPORARILY_DEAD,
        PERMANENTLY_DEAD
    }

    // The plant's maximum health
    private int maxHealth;
    // The probability that the plant's health grows
    private static final double GROWING_PROBABILITY = 0.1;
    // Keep track of the plant's health
    private int currentHealth;
    // true if the current season is Spring
    private boolean isSpring;
    // true once a season has passed after temperature death
    private boolean regrowthEnabled;
    // the plant's current lifecycle state
    private PlantState plantState;

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
        isSpring = true;
        regrowthEnabled = true;
        plantState = PlantState.ALIVE;
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
     */
    public void act(List<Species> newPlants, boolean isNight, int temperature, boolean yearPassed)
    {
        boolean temperatureIsLethal = isTemperatureLethal(temperature);

        if (plantState == PlantState.PERMANENTLY_DEAD) {
            return;
        }

        if (plantState == PlantState.TEMPORARILY_DEAD) {
            handleRegrowth(isNight, temperatureIsLethal);
            return;
        }

        if (temperatureIsLethal) {
            onTemperatureDeath();
            return;
        }

        if (! isNight) {
            if (yearPassed) {
                maxHealth++;
            }
            reproduce(newPlants);
            grow();
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
    @Override
    protected void reproduce(List<Species> newPlants)
    {
        Field field = getField();
        if (field != null && rand.nextDouble() <= getReproductionProbability()) {
            reproduceAtFreeLocations(newPlants, field.getFreeAdjacentLocations(getLocation()), 1);
        }
    }

    /**
     * Create a newborn plant of the current species.
     *
     * @param location the location at which the newborn should appear.
     * @return the newborn plant.
     */
    @Override
    protected Species createOffspring(Location location)
    {
        Plant newPlant = new Plant(getField(), location, getName(), getMaximumTemperature(), getMinimumTemperature(), getNutritionalValue(), getReproductionProbability(), maxHealth);
        newPlant.setSpringState(isSpring);
        return newPlant;
    }

    /**
     * The plant dies because of the temperature.
     * Its location in the field is cleared, but it still remembers its field and location.
     */
    @Override
    protected void onTemperatureDeath()
    {
        if (getLocation() != null) {
            plantState = PlantState.TEMPORARILY_DEAD;
            regrowthEnabled = false;
            getField().clear(getLocation());
        }
    }

    /**
     * The plant dies permanently when it has been eaten out.
     * It is removed from the field and no longer participates in the simulation.
     */
    @Override
    protected void setDead()
    {
        plantState = PlantState.PERMANENTLY_DEAD;
        regrowthEnabled = false;
        super.setDead();
    }

    /**
     * Try to regrow after a temperature death if the season and temperature allow it.
     *
     * @param isNight true if it is currently night.
     * @param temperatureIsLethal true if the current temperature is lethal.
     */
    private void handleRegrowth(boolean isNight, boolean temperatureIsLethal)
    {
        if (isRegrowthPossible(isNight, temperatureIsLethal)) {
            restoreAfterTemperatureDeath();
        }
    }

    /**
     * @param isNight true if it is currently night.
     * @param temperatureIsLethal true if the current temperature is lethal.
     * @return true if the plant can regrow this step.
     */
    private boolean isRegrowthPossible(boolean isNight, boolean temperatureIsLethal)
    {
        return !isNight && plantState == PlantState.TEMPORARILY_DEAD && regrowthEnabled && isSpring && !temperatureIsLethal;
    }

    /**
     * Restore a temperature-dead plant back to the field.
     */
    private void restoreAfterTemperatureDeath()
    {
        if (getLocation() != null && getField().getObjectAt(getLocation()) == null) {
            plantState = PlantState.ALIVE;
            regrowthEnabled = false;
            getField().place(this, getLocation());
            currentHealth = maxHealth;
        }
    }

    /**
     * Increase the plant's health by one if the random number meets the growing probability.
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
            setDead();
        }
    }

    /**
     * Synchronize the plant's spring state with the habitat.
     * The regrowth window is reopened only when the season actually changes.
     */
    public void syncSeason(boolean isSpring)
    {
        if (this.isSpring != isSpring) {
            this.isSpring = isSpring;
            regrowthEnabled = true;
        }
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
    private void setSpringState(boolean isSpring)
    {
        this.isSpring = isSpring;
    }
}
