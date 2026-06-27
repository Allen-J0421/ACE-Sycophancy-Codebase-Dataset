import java.util.List;
import java.util.Random;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * A class representing shared characteristics of all animals within the simultion.
 * Animals will find food, have a gender and be able to become infected.
 *
 * Concrete subclasses supply their fixed species characteristics through a single
 * {@link AnimalTraits} instance (see {@link #getTraits()}); the common life cycle,
 * accessors, breeding, feeding and infection behavior all live here so they are
 * defined exactly once.
 *
 * @version 2022.03.02
 */
public abstract class Animal extends Organism
{
    // A singleton shared weather object between all organisms and the simulator
    private static final Weather weather = Weather.getWeather();
    // Gender of the animal
    private boolean isMale;
    // Current health of the animal
    private int currentHealth;
    // Whether the animal is infected
    private boolean isInfected;

    /**
     * Create a new animal at location in field.
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location);

        if (randomAge) {
            currentHealth = getRand().nextInt(getMaxHealth());
        }
        if (getRand().nextInt(2) == 0) {
            isMale = true;
        }
        else {
            isMale = false;
        }

        isInfected = false;
    }

    // Abstract methods

    /**
     * Return the fixed, species-level characteristics for this animal. Each
     * concrete subclass returns its own shared (static) AnimalTraits instance.
     *
     * @return the traits describing this animal's species.
     */
    abstract protected AnimalTraits getTraits();

    // Trait-backed accessors (shared by every subclass)

    /**
     * Return whether the animal is diurnal.
     *
     * @return boolean True if the animal is diurnal, false if nocturnal
     */
    protected boolean getIsDiurnal() { return getTraits().isDiurnal(); }

    /**
     * Return the breeding age of the animal.
     *
     * @return the animal's breeding age
     */
    protected int getBreedingAge() { return getTraits().getBreedingAge(); }

    /**
     * Return the max age of the animal.
     *
     * @return the animal's max age
     */
    protected int getMaxAge() { return getTraits().getMaxAge(); }

    /**
     * Return the breeding probability of the animal.
     *
     * @return the animal's breeding probability
     */
    protected double getBreedingProbability() { return getTraits().getBreedingProbability(); }

    /**
     * Return the max litter size of the animal.
     *
     * @return the animal's max litter size
     */
    protected int getMaxLitterSize() { return getTraits().getMaxLitterSize(); }

    /**
     * Return the max health of the animal.
     *
     * @return the animal's max health
     */
    protected int getMaxHealth() { return getTraits().getMaxHealth(); }

    /**
     * Return the set of class types that this animal is allowed to eat.
     *
     * @return HashSet<Class> of class types that this animal can eat.
     */
    protected HashSet<Class> getFoodSources() { return getTraits().getFoodSources(); }

    /**
     * Return the set of class types that this animal is allowed to kill.
     *
     * @return HashSet<Class> of class types that this animal can kill.
     */
    protected HashSet<Class> getKillable() { return getTraits().getKillable(); }

    /**
     * Determine whether another organism is the same species as this animal.
     *
     * Every animal of a species shares a single {@link AnimalTraits} instance
     * (its species descriptor), so two animals are the same species exactly when
     * they share that descriptor. Comparing descriptors by identity is type-safe
     * and needs no Class literals or getClass() comparisons; a non-animal organism
     * (e.g. a plant) is never a match.
     *
     * @param other The organism being compared.
     * @return true if other is an animal of the same species as this one.
     */
    protected boolean isSameSpeciesAs(Organism other)
    {
        return other instanceof Animal animal && animal.getTraits() == getTraits();
    }

    // Accessor and mutator methods

    /**
     * Returns whether the animal is male
     *
     * @boolean True if the animal is male, false if animal is female
     */
    protected boolean getGender()
    {
        return isMale;
    }

    /**
     * Return the current health of the animal
     *
     * @return int of the animal's current health
     */
    protected int getCurrentHealth() {
        return currentHealth;
    }

    /**
     * Return whether the animal is infected
     *
     * @return True if the animal is infected, false if not
     */
    protected boolean getIsInfected() {
        return isInfected;
    }

    /**
     * Sets the animal to infected state
     */
    protected void infect() {
        isInfected = true;
    }

    // Functional methods

    /**
     * Animals realise the shared {@link Organism#act} lifecycle by getting hungrier
     * each step. The aging, infection, breeding and movement steps are supplied by
     * the base lifecycle and the other hooks below.
     */
    @Override
    protected void updateHealth()
    {
        incrementHealth();
    }

    /**
     * Animals move after breeding: towards a source of food if one is adjacent,
     * otherwise to any free adjacent location, dying of overcrowding if they cannot
     * move at all.
     */
    @Override
    protected void move()
    {
        Location newLocation = findFood();
        if (newLocation == null) {
            // No food found - try to move to a free location.
            newLocation = getField().freeAdjacentLocation(getLocation());
        }
        if (newLocation != null) {
            setLocation(newLocation);
        }
        else {
            // Overcrowding.
            setDead();
        }
    }

    /**
     * Checks if the animal can breed
     *
     * @return true if the animal can breed
     */
    protected boolean canBreed()
    {
        if (isMale == false)
        {
            Field field = getField();
            List<Location> adjacent = field.adjacentLocations(getLocation());
            Iterator<Location> it = adjacent.iterator();
            while (it.hasNext())
            {
                Location where = it.next();
                Object thing = field.getObjectAt(where);
                if (thing instanceof Animal animal && isSameSpeciesAs(animal))
                {
                    return animal.getGender() == true && getAge() >= getBreedingAge();
                }
            }
        }
        return false;
    }

    /**
     * A method that will make the animal object search the adjacent squares around it,
     * then find and eat an eligible food source.
     */
    protected Location findFood() {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if (animal != null) {                       // makes sure to filter out empty squares
                Organism target = (Organism) animal;
                if (this.getFoodSources().contains(target.getClass()) && target.isAlive() && getRand().nextDouble() <= weather.getWeatherModifier()) {
                    target.setDead();
                    //currentHealth = this.getMaxHealth();
                    if (this.getFoodSources().contains(target.getClass())) {
                        currentHealth = this.getMaxHealth();
                    }
                }
            }
        }
        return null;
    }

    /**
     * Decrease this animal's. This could result in the animal's death by hunger.
     */
    protected void incrementHealth()
    {
        currentHealth--;
        if(currentHealth == 0) {
            setDead();
        }
    }
}
