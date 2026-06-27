import java.util.Collections;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

/**
 * A class representing shared characteristics of all animals within the simultion.
 * Animals will find food, have a gender and be able to become infected.
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
        isMale = getRand().nextInt(2) == 0;

        isInfected = false;
    }

    // Abstract methods

    /**
     * Return this animal type's fixed lifecycle traits.
     */
    abstract protected AnimalProfile getProfile();

    @Override
    protected boolean getIsDiurnal()
    {
        return getProfile().isDiurnal();
    }

    @Override
    protected int getBreedingAge()
    {
        return getProfile().getBreedingAge();
    }

    @Override
    protected int getMaxAge()
    {
        return getProfile().getMaxAge();
    }

    @Override
    protected double getBreedingProbability()
    {
        return getProfile().getBreedingProbability();
    }

    @Override
    protected int getMaxLitterSize()
    {
        return getProfile().getMaxLitterSize();
    }

    /**
     * Return the max health of this animal.
     */
    protected int getMaxHealth()
    {
        return getProfile().getMaxHealth();
    }

    /**
     * Return whether the target object is the same animal type.
     */
    protected boolean getAnimalClass(Object thing)
    {
        return getClass().isInstance(thing);
    }

    /**
     * Abstract method to return the list of food sources of a specific subclass of animal
     *
     * @return Set<Class<?>> of class types that a subclass of animal is allowed to eat.
     */
    abstract protected Set<Class<?>> getFoodSources();

    /**
     * Abstract method to return the list of killable classes of a specific subclass of animal
     *
     * @return Set<Class<?>> of class types that a subclass of animal is allowed to kill.
     */
    abstract protected Set<Class<?>> getKillable();
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
     * Create a set of classes for species-specific diet and attack rules.
     */
    protected static Set<Class<?>> createClassSet(Class<?>... classes)
    {
        HashSet<Class<?>> classSet = new HashSet<>();
        for (Class<?> clazz : classes) {
            classSet.add(clazz);
        }
        return Collections.unmodifiableSet(classSet);
    }

    /**
     * This is what animals do each simulation step: age, lose health,
     * handle disease, breed, eat, and move.
     */
    @Override
    public void act(List<Organism> newOrganisms)
    {
        incrementAge();
        incrementHealth();
        handleIllness();
        if (isAlive()) {
            spreadDiseaseIfInfectable();
            giveBirth(newOrganisms);
            moveOrDie();
        }
    }

    /**
     * Apply infection damage when this animal supports infection.
     */
    private void handleIllness()
    {
        if (isAlive() && this instanceof Infectable) {
            ((Infectable) this).illness();
        }
    }

    /**
     * Spread disease when this animal supports infection.
     */
    private void spreadDiseaseIfInfectable()
    {
        if (this instanceof Infectable) {
            ((Infectable) this).spreadDisease();
        }
    }

    /**
     * Checks if the animal can breed
     *
     * @return true if the animal can breed
     */
    @Override
    protected boolean canBreed()
    {
        if (!isMale)
        {
            Field field = getField();
            for (Location where : field.adjacentLocations(getLocation()))
            {
                Object thing = field.getObjectAt(where);
                if (getAnimalClass(thing))
                {
                    Animal animal = (Animal) thing;
                    return animal.getGender() && getAge() >= getBreedingAge();
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
        for (Location where : field.adjacentLocations(getLocation())) {
            Object animal = field.getObjectAt(where);
            if (animal != null) {                       // makes sure to filter out empty squares
                Organism target = (Organism) animal;
                eatIfPossible(target);
            }
        }
        return null;
    }

    /**
     * Eat an eligible target and restore health when hunting succeeds.
     */
    private void eatIfPossible(Organism target)
    {
        if (canEat(target)) {
            target.setDead();
            currentHealth = getMaxHealth();
        }
    }

    /**
     * Return whether this animal can eat the target on this step.
     */
    private boolean canEat(Organism target)
    {
        return getFoodSources().contains(target.getClass())
            && target.isAlive()
            && getRand().nextDouble() <= weather.getWeatherModifier();
    }

    /**
     * Move to food or a free adjacent location. Die if trapped.
     */
    private void moveOrDie()
    {
        Location newLocation = findFood();
        if(newLocation == null) {
            newLocation = getField().freeAdjacentLocation(getLocation());
        }
        if(newLocation != null) {
            setLocation(newLocation);
        }
        else {
            setDead();
        }
    }

    /**
     * Decides whether this animal will infect another eligible animal.
     */
    public void spreadDisease() {
        Field field = getField();
        for (Location where : field.adjacentLocations(getLocation())) {
            Object object = field.getObjectAt(where);
            if (object != null) {
                Organism organism = (Organism) object;
                if (canInfect(organism)) {
                    Animal target = (Animal) organism;
                    target.infect();
                }
            }
        }
    }

    /**
     * Return whether a target can be infected by this animal on this step.
     */
    private boolean canInfect(Organism organism)
    {
        return organism instanceof Infectable
            && organism.isAlive()
            && getRand().nextDouble() <= Infectable.SPREAD_PROBABILITY;
    }

    /**
     * Makes an infected animal take extra damage.
     */
    public void illness() {
        if (getIsInfected()) {
            incrementHealth();
        }
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

    protected static final class AnimalProfile
    {
        private final int breedingAge;
        private final int maxAge;
        private final double breedingProbability;
        private final int maxLitterSize;
        private final int maxHealth;
        private final boolean diurnal;

        public AnimalProfile(
            int breedingAge,
            int maxAge,
            double breedingProbability,
            int maxLitterSize,
            int maxHealth,
            boolean diurnal)
        {
            this.breedingAge = breedingAge;
            this.maxAge = maxAge;
            this.breedingProbability = breedingProbability;
            this.maxLitterSize = maxLitterSize;
            this.maxHealth = maxHealth;
            this.diurnal = diurnal;
        }

        public int getBreedingAge()
        {
            return breedingAge;
        }

        public int getMaxAge()
        {
            return maxAge;
        }

        public double getBreedingProbability()
        {
            return breedingProbability;
        }

        public int getMaxLitterSize()
        {
            return maxLitterSize;
        }

        public int getMaxHealth()
        {
            return maxHealth;
        }

        public boolean isDiurnal()
        {
            return diurnal;
        }
    }
}
