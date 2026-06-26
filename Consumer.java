import java.util.List;
import java.util.Random;
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
    private static Random rand = Randomizer.getRandom();

    // Properties unique to this consumer:
    private int breedingAge;
    private int sustenanceLevel;
    private List<Class<? extends Actor>> prey;
    private Disease disease;
    private boolean ifCarcass;
    private Carcass newCarcass;
    private boolean canEatCarcass;
    
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
    public Consumer(Field field, Location location, List<Class<? extends Actor>> prey,
                    int consumptionWorth, double breedingProbability,
                    int maxBirthsAtOnce, int maxAge, int breedingAge,
                    int maxSustenanceLevel, boolean canEatCarcass)
    {
        super(field, location, consumptionWorth, breedingProbability,
              maxBirthsAtOnce, maxSustenanceLevel, maxAge);
        this.canEatCarcass = canEatCarcass;
        this.prey = prey;
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
            if(ifCarcass)
            {
                newConsumers.add(newCarcass);
                ifCarcass = false;
                newCarcass = null;
            }
            if(hasDisease())
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
        // Ensure this consumer has a valid mate in a neighboring location before continuing.
        if (!checkForValidMate()) return;
        
        // Get a list of free adjacent locations:
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        
        // Work out the number of births this producer will have this step:
        int births = breed();
        // Add each birth into an adjacent location:
        for (int b = 0; b < births && !free.isEmpty(); b++)
        {
            Location location = free.remove(0);
            newConsumers.add(createChild(field, location));
        }
    }

    /**
     * Create a child consumer of the same species.
     */
    private Actor createChild(Field field, Location location)
    {
        try
        {
            return this.getClass()
                       .getDeclaredConstructor(boolean.class, Field.class, Location.class)
                       .newInstance(true, field, location);
        }
        catch (ReflectiveOperationException e)
        {
            throw new IllegalStateException("Unable to create consumer child.", e);
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
        Field field = getField();
        List<Location> adjacentLocations = field.adjacentLocations(getLocation());
        
        Iterator<Location> it = adjacentLocations.iterator();
        
        while (it.hasNext())
        {
            Location where = it.next();
            Object object = field.getObjectAt(where);
            
            Location foodLocation = tryEatPrey(object);
            if (foodLocation != null) return foodLocation;

            foodLocation = tryEatCarcass(object, where);
            if (foodLocation != null) return foodLocation;
        }

        return null;
    }

    /**
     * Try to eat a live prey actor.
     *
     * @param object The object in an adjacent location.
     * @return The location to move to, or null if no prey was eaten.
     */
    private Location tryEatPrey(Object object)
    {
        for (Class<? extends Actor> preyClass : prey)
        {
            if (preyClass.isInstance(object))
            {
                Actor actor = (Actor) object;
                if (actor.getIsAlive() && canCatchPrey())
                {
                    return eat(actor);
                }
            }
        }

        return null;
    }

    /**
     * Try to eat a carcass from an adjacent location.
     *
     * @param object The object in an adjacent location.
     * @param location The location of the object.
     * @return The location to move to, or null if no carcass was eaten.
     */
    private Location tryEatCarcass(Object object, Location location)
    {
        if (!canEatCarcass || !Carcass.class.isInstance(object))
        {
            return null;
        }

        Carcass carcass = (Carcass) object;
        if (carcass.isDiseased())
        {
            disease = giveDisease();
        }

        sustenanceLevel += carcass.getConsumptionWorth();
        carcass.setDead();
        return location;
    }

    /**
     * At night a consumer may miss otherwise valid prey.
     */
    private boolean canCatchPrey()
    {
        return !TimeSystem.isNightTime()
               || rand.nextDouble() <= 1.0 - NIGHT_PREY_MISS_PROBABILITY;
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
        if(actor.becomeCarcass())
        {   
            actor.setDead();
            if((sustenanceLevel + actorConsumptionWorth) > maxSustenanceLevel)
            {   
                int foodLeft = (sustenanceLevel + actorConsumptionWorth) - maxSustenanceLevel;
                sustenanceLevel = maxSustenanceLevel;
                Carcass carcass = new Carcass(getField(),location,foodLeft);
                newCarcass = carcass;
                ifCarcass = true;
                return getLocation();
            }  
        }
        if(sustenanceLevel + actorConsumptionWorth <= maxSustenanceLevel)
        {
            sustenanceLevel += actorConsumptionWorth;
            return location;
        }
        return null;
    }
}
