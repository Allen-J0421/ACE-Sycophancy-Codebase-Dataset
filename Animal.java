import java.util.List;
import java.util.Iterator;
/**
 * A class representing shared characteristics of animals.
 *
 * @version 25/02/2022
 */
public abstract class Animal extends LivingOrganism
{
    // Indicates whether an animal is a female or not;
    protected boolean isFemale;
    // The age to which an animal can live.
    protected int maxAge;
    // The animals's age.
    protected int age;
    // The animals's food level, which is increased by eating prey.
    protected int foodLevel;
    // The maximum food level that an animal will eat at.
    protected int maxFoodLevel;

    // Behaviour components — wired at construction time.
    private final HealthBehaviour       health;
    private final MovementBehaviour     movement;
    private final DietaryBehaviour      diet;
    private final ReproductiveBehaviour reproduction;

    /**
     * Create a new animal at location in field.
     *
     * @param field        The field currently occupied.
     * @param location     The location within the field.
     * @param infected     Initial infection state.
     * @param immune       Initial immune state.
     * @param movement     Controls activity gating and wander probability.
     * @param diet         Controls food search and consumption.
     * @param reproduction Supplies breeding parameters.
     */
    public Animal(Field field, Location location, boolean infected, boolean immune,
                  MovementBehaviour movement, DietaryBehaviour diet, ReproductiveBehaviour reproduction)
    {
        super(field, location);
        alive = true;
        this.health       = new StandardHealthBehaviour(infected, immune);
        this.movement     = movement;
        this.diet         = diet;
        this.reproduction = reproduction;

        isFemale = rand.nextBoolean();
    }

    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     *
     * @param newAnimals A list to receive newly born animals.
     */
    public void act(List<LivingOrganism> newAnimals)
    {
        if (!movement.canActThisStep(rand)) return;

        incrementAge();
        incrementHunger();

        if (health.progressDisease(rand))
        {
            setDead();
        }

        if (isAlive())
        {
            health.tryAcquireDisease(this, getField(), rand);

            if (this.getIsFemale())
            {
                if (canBreed() && rand.nextDouble() <= reproduction.getBreedingProbability())
                {
                    populate(newAnimals);
                }
            }

            Location newLocation = findFood();

            if (newLocation == null)
            {
                Location possibleNewLocation = getField().freeAdjacentLocation(getLocation(), Animal.class);

                if (possibleNewLocation == null)
                {
                    if (rand.nextDouble() < 0.3)
                    {
                        setDead();
                    }
                }

                if (rand.nextDouble() <= movement.getMovementProbability())
                {
                    newLocation = possibleNewLocation;
                }
            }

            if (newLocation != null)
            {
                setLocation(newLocation);
            }
        }
    }

    /**
     * Delegate food search to the dietary behaviour component.
     *
     * @return Where food was found, or null.
     */
    protected Location findFood()
    {
        return diet.findFood(this, getField(), rand);
    }

    /**
     * Indicate that the animal is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        alive = false;

        if (location != null)
        {
            field.clear(location, Animal.class);
            location = null;
        }

        if (this.getClass().equals(Lion.class) || this.getClass().equals(Cheetah.class))
        {
            location = null;
        }
    }

    /**
     * When called, the animal is killed and returns its food value.
     *
     * @return The food value of the animal eaten.
     */
    protected int beEaten()
    {
        setDead();
        return foodValue;
    }

    /**
     * Place the animal at the new location in the given field.
     *
     * @param newLocation The animal's new location.
     */
    protected void setLocation(Location newLocation)
    {
        if (location != null)
        {
            if (field.getObjectAt(location, Animal.class) != null)
            {
                field.clear(location, Animal.class);
            }
        }

        location = newLocation;
        field.place(this, newLocation);
    }

    /** @return true if this animal is female. */
    protected boolean getIsFemale()
    {
        return isFemale;
    }

    /**
     * Increase the age. This could result in the animal's death.
     */
    protected void incrementAge()
    {
        age++;
        if (age > maxAge)
        {
            setDead();
        }
    }

    /** Increase hunger. This could result in the animal's death. */
    protected void incrementHunger()
    {
        foodLevel--;
        if (foodLevel <= 0)
        {
            setDead();
        }
    }

    /**
     * New births will be made into free adjacent locations.
     *
     * @param newAnimals A list to return newly born animals.
     */
    protected void populate(List<LivingOrganism> newAnimals)
    {
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation(), Animal.class);

        int births = breed();

        for (int b = 0; b < births && free.size() > 0; b++)
        {
            Animal newAnimal = createNewOffspring(this.getClass(), free, health.isInfected(), health.isImmune(), foodLevel);
            newAnimals.add(newAnimal);
        }
    }

    /**
     * Creates a new offspring at the first free location.
     *
     * @param classOfAnimal    Species of the offspring.
     * @param free             Available adjacent locations.
     * @param motherIsInfected Whether the mother is currently infected.
     * @param motherIsImmune   Whether the mother is immune.
     * @param motherFoodLevel  The mother's current food level.
     * @return The newly created animal.
     */
    protected Animal createNewOffspring(Class<?> classOfAnimal, List<Location> free,
                                        boolean motherIsInfected, boolean motherIsImmune,
                                        int motherFoodLevel)
    {
        Location loc = free.remove(0);

        boolean offspringIsInfected = motherIsInfected;
        boolean offspringIsImmune   = motherIsImmune;

        if (!motherIsImmune && motherIsInfected && rand.nextDouble() < 0.15)
        {
            offspringIsImmune   = true;
            offspringIsInfected = false;
        }
        else if (motherIsImmune && rand.nextDouble() < 0.9)
        {
            offspringIsImmune = false;
        }

        return AnimalFactory.createOffspring(classOfAnimal, field, loc, offspringIsInfected, offspringIsImmune);
    }

    /**
     * Count births for this step by scanning adjacent animals of the same
     * species for a male partner.
     *
     * @return Number of births (may be zero).
     */
    protected int breed()
    {
        int births = 0;

        Field field = getField();
        Iterator<Location> it = field.adjacentLocations(getLocation());

        while (it.hasNext())
        {
            Location where = it.next();
            Animal   animal = (Animal) field.getObjectAt(where, Animal.class);
            Class    typeOfOtherAnimal = null;

            if (field.getObjectAt(where, Animal.class) != null)
            {
                typeOfOtherAnimal = field.getObjectAt(where, Animal.class).getClass();
            }

            if (this.getClass().equals(typeOfOtherAnimal))
            {
                if (animal.getIsFemale() == false)
                {
                    births = rand.nextInt(reproduction.getMaxLitterSize()) + 1;
                }
            }
        }

        return births;
    }

    /**
     * An animal can breed if it has reached the breeding age.
     *
     * @return true if the animal can breed.
     */
    protected boolean canBreed()
    {
        return age >= reproduction.getBreedingAge();
    }

    /** @return true if this animal is currently infected. */
    public boolean getIsInfected()
    {
        return health.isInfected();
    }

    /** @return true if this animal is currently immune. */
    public boolean getIsImmune()
    {
        return health.isImmune();
    }

    /**
     * Attempt to infect this animal from an external source (e.g. prey consumed
     * by a predator). Delegates to the health component.
     *
     * @param sourceIsInfected true if the infection source is infected.
     * @param rand             The shared random generator.
     */
    protected void tryInfectFrom(boolean sourceIsInfected, SimRandom rand)
    {
        health.tryInfectFrom(sourceIsInfected, rand);
    }

    /**
     * Check whether any adjacent animal is infected.
     *
     * @return true if at least one neighbour is infected.
     */
    protected boolean surroundingsInfected()
    {
        boolean surroundingsAreInfected = false;

        Field field = getField();
        Iterator<Location> it = field.adjacentLocations(getLocation());

        while (it.hasNext() && !surroundingsAreInfected)
        {
            Location where  = it.next();
            Animal   animal = (Animal) field.getObjectAt(where, Animal.class);

            if (field.getObjectAt(where, Animal.class) != null)
            {
                if (animal.getIsInfected())
                {
                    surroundingsAreInfected = true;
                }
            }
        }

        return surroundingsAreInfected;
    }
}
