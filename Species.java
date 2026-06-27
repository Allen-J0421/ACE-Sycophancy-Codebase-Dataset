import java.util.List;
import java.util.Random;

/**
 * This is an abstract class representing all the species of
 * the simulation. It defines some characteristics and methods
 * of the species.
 *
 * @version 2022.02.28
 */
public abstract class Species
{
    // The probability for an individual to die if exposed to too high or too cold temperatures.
    private static final double DYING_OF_COLD_OR_HEAT_PROBABILITY = 0.8;
    // Whether the species is alive or not.
    private boolean alive;
    // The animal's field.
    private Field field;
    // The species' position in the field.
    private Location location;
    // The species' name
    private final String name;
    // The maximum temperature at which the species can survive
    private final int maximumTemperature;
    // The minimum temperature at which the species can survive
    private final int minimumTemperature;
    // The food value animal provides when eaten
    private final int nutritionalValue;
    // The likelihood of a specie to reproduce.
    private final double reproductionProbability;
    // A random number generator
    protected static final Random rand = Randomizer.getRandom();

    /**
     * Create a new specie at location in field.
     *
     * @param field (Field) The field currently occupied.
     * @param location (Location) The location within the field.
     * @param name (String) The name of the specie
     * @param maximumTemperature (int) The maximum temperature that the specie can withstand
     * @param minimumTemperature (int) The minimum temperature that the specie can withstand
     * @param nutritionalValue (int) The nutritional value given to the specie that eats this specie
     * @param reproductionProbability (double) The probability that this specie will reproduce
     */
    public Species(Field field, Location location, String name, int maximumTemperature, int minimumTemperature, int nutritionalValue, double reproductionProbability)
    {
        alive = true;
        this.field = field;
        setLocation(location);
        this.name = name;
        this.maximumTemperature = maximumTemperature;
        this.minimumTemperature = minimumTemperature;
        this.nutritionalValue = nutritionalValue;
        this.reproductionProbability = reproductionProbability;
    }

    /**
     * Make this animal act - that is: make it do whatever it wants/needs to do.
     *
     * @param newSpecies (List<Species>) A list to receive newly born animals.
     * @param isNight (boolean) If it is currently night.
     * @param yearPassed (boolean) If a year is passing on this step (next step is the first of a nw year).
     */
    abstract public void act(List<Species> newSpecies, boolean isNight, int temperature, boolean yearPassed, boolean isSpring, boolean seasonChanged);

    /**
     * An abstract class to enforce all subclasses to make their elements reproduce
     *
     * @param newOfThisKind List of Species objects in the simulation for the newborns to be added to it
     */
    abstract void reproduce(List<Species> newOfThisKind);

    /**
     * Check whether the animal is alive or not.
     *
     * @return (boolean) true if the animal is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the animal is no longer alive. It is removed from the field.
     */
    protected void setDead()
    {
        alive = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }

    /**
     * Return the animal's location.
     *
     * @return (Location)The animal's location.
     */
    protected Location getLocation()
    {
        return location;
    }

    /**
     * Place the animal at the new location in the given field.
     *
     * @param newLocation (Location) The animal's new location.
     */
    protected void setLocation(Location newLocation)
    {
        if(location  != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }

    /**
     * @return (Field) The animal's field.
     */
    protected Field getField()
    {
        return field;
    }

    /**
     * Check if animal can survive the current temperature. If temperature is too low or too high, animal has a high probability of dying
     * (probability defined as a class constant to allow easy adjusting).
     *
     * @return (Field) The animal's field.
     */
    protected boolean survivesTemperature(int temperature)
    {
        if (temperature > maximumTemperature || temperature < minimumTemperature) {
            return rand.nextDouble() > DYING_OF_COLD_OR_HEAT_PROBABILITY;
        }
        return true;
    }

    /**
     * @return (String) The specie's name.
     */
    protected String getName()
    {
        return name;
    }

    /**
     * @return (int) The maximum temperature specie can survive to.
     */
    protected int getMaximumTemperature()
    {
        return maximumTemperature;
    }

    /**
     * @return (int) The minimum temperature specie can survive to.
     */
    protected int getMinimumTemperature()
    {
        return minimumTemperature;
    }

    /**
     * @return (int) The specie's nutritional value.
     */
    protected int getNutritionalValue()
    {
        return nutritionalValue;
    }

    /**
     * @return (double) The specie's reproduction probability.
     */
    protected double getReproductionProbability()
    {
        return reproductionProbability;
    }

}
