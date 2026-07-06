import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Iterator;

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
    private static Random rand = Randomizer.getRandom();

    // Properties unique to this consumer:
    private int breedingAge;
    private int sustenanceLevel;
    private int maxSustenanceLevel;
    private ArrayList<Class> prey;
    private Disease disease;
    private boolean canEatCarcass;
    private boolean primaryConsumer;

    private final ForagingService foragingService;

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
    public Consumer(Field field, Location location, ArrayList<Class> prey, int consumptionWorth,
                    double breedingProbability, int maxBirthsAtOnce, int maxAge, int breedingAge,int maxSustenanceLevel,boolean canEatCarcass,boolean primaryConsumer)
    {
        super(field, location, consumptionWorth, breedingProbability, maxBirthsAtOnce,maxSustenanceLevel,maxAge);
        this.canEatCarcass = canEatCarcass;
        this.prey = prey;
        this.maxAge = maxAge;
        this.breedingAge = breedingAge;
        this.primaryConsumer = primaryConsumer;
        sustenanceLevel = 30;
        foragingService = new ForagingService(prey, canEatCarcass);
    }

    /**
     * Make this consumer act - that is: make it do
     * whatever it wants/needs to do.
     *
     * @param newConsumers A list to receive newly born consumers.
     */
    public void act(List<Actor> newConsumers)
    {
        giveBirth(newConsumers);
        boolean wasPossibleToMove = huntForFood(newConsumers);
        if (hasDisease())
        {
            diseaseEffect();
        }
        if (!wasPossibleToMove)
        {
            setDead();
        }
    }
    /**
     * Returns true if this consumer is diseased else return false.
     * @return boolean
     */
    private boolean hasDisease()
    {
        return this.disease != null;
    }
    /**
     * Returns a new Disease.
     * @return diseae
     */
    private Disease giveDisease()
    {
        return new Disease();
    }
    /**
     * If the animal is diseased then evolve the disease.
     */
    private void diseaseEffect()
    {
        if(hasDisease())
        {
            disease.decrementStepsBeforeDeath();
            spreadDisease();
            checkForFatalDisease();
            incrementHunger();
        }
    }
    /**
     * Check if the disease is fatal to the animal.
     */
    private void checkForFatalDisease()
    {
        if(hasDisease() && disease.diseaseFinished())
        {
            if(disease.isFatal())
            {
                setDead();
            }
            disease = null;
        }
    }
    /**
     * Spread the disease to different animals of the same species.
     */
    private void spreadDisease()
    {
            Field field = getField();
            List<Location> adjacent = field.adjacentLocations(getLocation());
            Iterator<Location> it = adjacent.iterator();
            while(it.hasNext())

            {
                Location loc = it.next();
                Object animal = field.getObjectAt(loc);
                Actor consumer = (Actor) animal;
                if(consumer != null && consumer.getClass() == this.getClass() && this.hasDisease())
                {
                    ((Consumer) consumer).disease = giveDisease();
                }
            }
    }
    /**
     * Hunt for food by moving toward it.
     * Delegates scanning and eating logic to ForagingService and applies the result.
     */
    private boolean huntForFood(List<Actor> newActors)
    {
        ForagingResult result = foragingService.forage(this);

        if (result.found())
        {
            if (result.clampSustenanceToMax)
            {
                sustenanceLevel = maxSustenanceLevel;
            }
            else
            {
                sustenanceLevel += result.sustenanceDelta;
            }
            if (result.contractedDisease)
            {
                disease = new Disease();
            }
            if (result.generatedCarcass != null)
            {
                newActors.add(result.generatedCarcass);
            }
            setLocation(result.foodLocation);
            return true;
        }

        // No food found - try to move to a free location:
        Location freeLocation = getField().freeAdjacentLocation(getLocation());
        if (freeLocation != null)
        {
            setLocation(freeLocation);
            return true;
        }
        return false;
    }

    /**
     * Set the starting age of the consumer, dependent on whether a random
     * age has been selected.
     *
     * @param randomAge If true, the age is random, otherwise it's 0.
     */
    protected void setStartingAge(boolean randomAge)
    {
        if (randomAge) currentAge = rand.nextInt(maxAge) ;
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
        BirthManager.spawnConsumerOffspring(this, breed(), newConsumers);
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

            if (object instanceof Consumer                 // Is there a consumer in that location?
                && object.getClass() == this.getClass()    // Is the consumer of the same type as this one?
                && ((Consumer) object).canBreed()          // Can that consumer breed?
                && ((Actor) object).getGender() != gender) // Is that consumer of the opposite sex?
            {
                return true; // We have found a valid mate.
            }
        }

        return false; // We didn't manage to find a valid mate.
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

        boolean willBreed = rand.nextDouble()
                            <= getBreedingProbability();

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
    void incrementHunger()
    {
        sustenanceLevel--;
        if (sustenanceLevel <= 0) setDead();
    }

    /** @return This consumer's current sustenance level (read by ForagingService). */
    int getSustenanceLevel() { return sustenanceLevel; }

    /**
     * @return This consumer's own {@code maxSustenanceLevel} field.
     *         Note: this shadows the identically named field in Actor and is never
     *         explicitly set in the constructor, so it defaults to 0.  ForagingService
     *         must use this value to replicate the original eat() overflow check.
     */
    int getMaxSustenanceForEating() { return maxSustenanceLevel; }

    protected boolean becomeCarcass()
    {
        return true;
    }
}
