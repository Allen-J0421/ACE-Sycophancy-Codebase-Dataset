import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of consumers.
 *
 * @version 18.02.22 (DD:MM:YY)
 */
public abstract class Consumer extends Actor
{
    // Properties shared between all consumers:
    private static final int STARTING_SUSTENANCE_LEVEL = 20;
    private static final double NEW_DISEASE = 0.0005;
    private static final double NIGHT_PREY_MISS_PROBABILITY = 0.5;
    private static Random rand = Randomizer.getRandom();

    // Properties unique to this consumer:
    private int breedingAge;
    private int sustenanceLevel;
    private int maxSustenanceLevel;
    private List<Class<?>> prey;
    private Disease disease;
    private boolean ifCarcass;
    private Carcass newCarcass;
    private boolean canEatCarcass;
    private boolean primaryConsumer;

    /**
     * Create a new consumer at a location in the field.
     *
     * @param field               The field currently occupied.
     * @param location            The location within the field.
     * @param consumptionWorth    The worth of the consumer if consumed.
     * @param breedingProbability The liklihood of the consumer breeding.
     * @param maxBirthsAtOnce     The max number of births for the consumer.
     * @param maxAge              The age to which this consumer can live.
     * @param breedingAge         The age at which this consumer can start to breed.
     */
    public Consumer(Field field, Location location, List<Class<?>> prey, int consumptionWorth,
                    double breedingProbability, int maxBirthsAtOnce, int maxAge, int breedingAge,
                    int maxSustenanceLevel, boolean canEatCarcass, boolean primaryConsumer)
    {
        super(field, location, consumptionWorth, breedingProbability, maxBirthsAtOnce,
              maxSustenanceLevel, maxAge);
        this.canEatCarcass = canEatCarcass;
        this.prey = prey;
        this.maxAge = maxAge;
        this.breedingAge = breedingAge;
        this.primaryConsumer = primaryConsumer;
        sustenanceLevel = 30;
    }

    /**
     * Create a new offspring of this consumer type at the given location.
     * Subclasses return an instance of their own concrete type.
     *
     * @param field    The field to place the offspring in.
     * @param location The location to place the offspring at.
     * @return A new offspring Consumer.
     */
    protected abstract Consumer createOffspring(Field field, Location location);

    /**
     * Make this consumer act - that is: make it do
     * whatever it wants/needs to do.
     *
     * @param newConsumers A list to receive newly born consumers.
     */
    public void act(List<Actor> newConsumers)
    {
        incrementAge();
        incrementHunger();
        if (getIsAlive())
        {
            giveBirth(newConsumers);
            boolean wasPossibleToMove = huntForFood();
            if (ifCarcass)
            {
                newConsumers.add(newCarcass);
                ifCarcass = false;
                newCarcass = null;
            }
            if (hasDisease())
            {
                diseaseEffect();
            }
            if (!wasPossibleToMove)
            {
                setDead();
            }
        }
    }

    /** @return True if this consumer is currently diseased. */
    private boolean hasDisease()
    {
        return disease != null;
    }

    /** @return A new Disease instance. */
    private Disease giveDisease()
    {
        return new Disease();
    }

    /** Advance the disease by one step: progress timer, spread, then check lethality. */
    private void diseaseEffect()
    {
        disease.decrementStepsBeforeDeath();
        spreadDisease();
        checkForFatalDisease();
        incrementHunger();
    }

    /** Resolve the disease if it has run its course. */
    private void checkForFatalDisease()
    {
        if (disease.diseaseFinished())
        {
            if (disease.isFatal())
            {
                setDead();
            }
            disease = null;
        }
    }

    /** Spread the disease to all adjacent consumers of the same species. */
    private void spreadDisease()
    {
        Field field = getField();
        for (Location loc : field.adjacentLocations(getLocation()))
        {
            Object animal = field.getObjectAt(loc);
            if (animal != null && animal.getClass() == this.getClass())
            {
                ((Consumer) animal).disease = giveDisease();
            }
        }
    }

    /** Hunt for food by moving toward it. */
    private boolean huntForFood()
    {
        Location newLocation = findFood();

        if (newLocation == null)
        {
            newLocation = getField().freeAdjacentLocation(getLocation());
        }

        if (newLocation != null) setLocation(newLocation);

        return newLocation != null;
    }

    /**
     * Set the starting age of the consumer, dependent on whether a random
     * age has been selected.
     *
     * @param randomAge If true, the age is random, otherwise it's 0.
     */
    protected void setStartingAge(boolean randomAge)
    {
        if (randomAge) currentAge = rand.nextInt(maxAge);
        else           currentAge = 0;
    }

    /**
     * Check whether or not this consumer is to give birth at this step.
     * New births will be made into free adjacent locations.
     *
     * @param newConsumers A list to return newly born consumers.
     */
    private void giveBirth(List<Actor> newConsumers)
    {
        if (!checkForValidMate()) return;

        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());

        int births = breed();
        for (int b = 0; b < births && free.size() > 0; b++)
        {
            Location location = free.remove(0);
            newConsumers.add(createOffspring(field, location));
        }
    }

    /**
     * @return True if there is a valid mate in an adjacent location for the consumer.
     */
    private boolean checkForValidMate()
    {
        Field field = getField();

        for (Location location : field.adjacentLocations(getLocation()))
        {
            Object object = field.getObjectAt(location);

            if (object instanceof Consumer other
                && other.getClass() == this.getClass()
                && other.canBreed()
                && other.getGender() != gender)
            {
                return true;
            }
        }

        return false;
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

        boolean willBreed = rand.nextDouble() <= getBreedingProbability();

        if (canBreed() && willBreed)
            births = rand.nextInt(getMaxBirthsAtOnce()) + 1;

        return births;
    }

    /**
     * A consumer can breed if it has reached the breeding age.
     *
     * @return True if the consumer can breed, false otherwise.
     */
    private boolean canBreed() { return currentAge >= breedingAge; }

    /**
     * Make this consumer more hungry. This could result in the consumer's death.
     */
    private void incrementHunger()
    {
        sustenanceLevel--;
        if (sustenanceLevel <= 0) setDead();
    }

    /**
     * Look for prey adjacent to the current location.
     * Only the first live prey is eaten.
     *
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();

        for (Location where : field.adjacentLocations(getLocation()))
        {
            Object object = field.getObjectAt(where);

            for (Class<?> preyClass : prey)
            {
                if (preyClass.isInstance(object))
                {
                    Actor actor = (Actor) object;
                    if (actor.getIsAlive())
                    {
                        boolean canHit = !TimeSystem.isNightTime()
                                      || rand.nextDouble() <= 1.0 - NIGHT_PREY_MISS_PROBABILITY;
                        if (canHit)
                            return eat(actor);
                    }
                }
            }

            if (object instanceof Carcass carcass && canEatCarcass)
            {
                if (carcass.isDiseased())
                {
                    disease = giveDisease();
                }
                sustenanceLevel += carcass.getConsumptionWorth();
                carcass.setDead();
                return where;
            }
        }

        return null;
    }

    @Override
    protected boolean becomeCarcass()
    {
        return true;
    }

    /**
     * Makes the animal eat the food and generate a carcass if necessary.
     * @param actor The actor who is to be eaten.
     */
    private Location eat(Actor actor)
    {
        Location location = actor.getLocation();
        int actorConsumptionWorth = actor.getConsumptionWorth();
        if (actor.becomeCarcass())
        {
            actor.setDead();
            if ((sustenanceLevel + actorConsumptionWorth) > maxSustenanceLevel)
            {
                int foodLeft = (sustenanceLevel + consumptionWorth) - maxSustenanceLevel;
                sustenanceLevel = this.maxSustenanceLevel;
                newCarcass = new Carcass(getField(), location, foodLeft);
                ifCarcass = true;
                return getLocation();
            }
        }
        if (sustenanceLevel + actorConsumptionWorth <= maxSustenanceLevel)
        {
            sustenanceLevel += actorConsumptionWorth;
            return location;
        }
        return null;
    }
}
