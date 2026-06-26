import java.util.List;
import java.util.Iterator;

/**
 * A class representing shared characteristics of consumers.
 *
 * @version 18.02.22 (DD:MM:YY)
 */
public abstract class Consumer extends Actor
{
    // Properties shared between all consumers:
    private static final int STARTING_SUSTENANCE_LEVEL = 30;
    private static final double NIGHT_PREY_MISS_PROBABILITY = 0.5;

    // Properties unique to this consumer:
    private final int breedingAge;
    private int sustenanceLevel;
    private final List<Class<? extends Actor>> prey;
    private Disease disease;
    private boolean shouldDropCarcass;
    private Carcass pendingCarcass;
    private final boolean canEatCarcass;
    
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
    public Consumer(Field field, Location location, List<Class<? extends Actor>> prey, int consumptionWorth,
                    double breedingProbability, int maxBirthsAtOnce, int maxAge, int breedingAge,
                    int maxSustenanceLevel, boolean canEatCarcass)
    {
        super(field, location, consumptionWorth, breedingProbability, maxBirthsAtOnce,
              maxSustenanceLevel, maxAge);
        this.canEatCarcass = canEatCarcass;
        this.prey = prey;
        this.maxAge = maxAge;
        this.breedingAge = breedingAge;
        sustenanceLevel = STARTING_SUSTENANCE_LEVEL;
    }
    
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
            if (shouldDropCarcass)
            {
                newConsumers.add(pendingCarcass);
                shouldDropCarcass = false;
                pendingCarcass = null;
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
        if (hasDisease())
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
        if (hasDisease() && disease.diseaseFinished())
        {   
            if (disease.isFatal())
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

        for (Location loc : adjacent)
        {
            Object neighbor = field.getObjectAt(loc);

            if (neighbor != null && neighbor.getClass() == getClass() && hasDisease())
            {
                ((Consumer) neighbor).disease = giveDisease();
            }
        }
    }
    /**
     * Hunt for food by moving toward it.
     */
    private boolean huntForFood()
    {
        // Move towards a source of food if found:
        Location newLocation = findFood();
        
        if (newLocation == null)
        { 
            // No food found - try to move to a free location:
            newLocation = getField().freeAdjacentLocation(getLocation());
        }
        
        // See if it was possible to move.
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
            newConsumers.add(createChild(field, location));
        }
    }

    /**
     * Create a newborn consumer of this species.
     *
     * @param field The field the child will be placed in.
     * @param location The child's starting location.
     * @return A new consumer instance.
     */
    protected abstract Actor createChild(Field field, Location location);
    
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
        // Get adjacent locations:
        Field field;
        field = getField();
        List<Location> adjacentLocations = field.adjacentLocations(getLocation());
        
        Iterator<Location> it = adjacentLocations.iterator();
        
        while (it.hasNext())
        {
            Location where = it.next();
            Object object = field.getObjectAt(where);
            
            for (Class<? extends Actor> preyClass : prey)
            {   
                if (preyClass.isInstance(object))
                {   
                    Actor actor = (Actor) object;
                    
                    if (actor.getIsAlive())
                    {   
                        // If it's night, there is a probability of missing the prey:
                        if (TimeSystem.isNightTime())
                        {   
                            if (rand.nextDouble() <= 1.0 - NIGHT_PREY_MISS_PROBABILITY)
                            {   
                                
                                return eat(actor);
                            }
                        }
                        else
                        {
                            
                            return eat(actor);
                        }
                    }
                }
            }
            if (Carcass.class.isInstance(object))
            {   
                if (canEatCarcass)
                {
                    Carcass carcass = (Carcass) object;
                    if (carcass.isDiseased())
                    {
                        this.disease = giveDisease();
                    }
                    sustenanceLevel += carcass.getConsumptionWorth();
                    carcass.setDead();
                    return where;
                }
            }
        }
        
        return null;
    }
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
                int foodLeft = (sustenanceLevel + actorConsumptionWorth) - maxSustenanceLevel;
                sustenanceLevel = maxSustenanceLevel;
                Carcass carcass = new Carcass(getField(),location,foodLeft);
                pendingCarcass = carcass;
                shouldDropCarcass = true;
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
