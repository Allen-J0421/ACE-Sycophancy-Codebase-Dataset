import java.util.List;
import java.util.Iterator;
import java.util.Random;
import java.lang.reflect.*;
/**
 * A class representing shared characteristics of animals.
 *
 * @version 1.3
 */
public abstract class Animal implements Actor
{
    
    /*///////////////////////////////////////////////////////////////
                                  STATE
    //////////////////////////////////////////////////////////////*/
    
    private boolean alive;
    private Field field;
    private Location location;
    protected int age;
    protected final Gender gender;
    protected int foodLevel;
    protected Integer infectionTimestamp;
    
    private static final Random rand = Randomizer.getRandom();
    private static final Class[] ANIMAL_CONSTRUCTOR_SIGNATURE = new Class[] {boolean.class, Field.class, Location.class, Gender.class};
    
    /*///////////////////////////////////////////////////////////////
                                CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/
    
    /**
     * Create a new animal at location in field.
     * 
     * @param randomAge flag indicating whether to randomly generate age and foodLevel or not
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param gender Gender of the animal
     * @param baseLevel base food level of an animal
     * @param maxAge maximum age the animal can take
     */
    public Animal(boolean randomAge, Field field, Location location, Gender gender,int baseLevel, int maxAge)
    {
        if (randomAge) {
            this.age = rand.nextInt(maxAge);
            this.foodLevel = rand.nextInt(baseLevel);
        } else {
            this.foodLevel = baseLevel;
            this.age = 0;
        }
        this.alive = true;
        this.field = field;
        this.gender = gender;
        this.infectionTimestamp = null;
        setLocation(location);
    }
    
    /*///////////////////////////////////////////////////////////////
                            ANIMAL BEHAVIOUR LOGIC
    //////////////////////////////////////////////////////////////*/
    
    /**
     * Increments the age of the animal.
     * 
     * @param maxAge The maximum age of the animal before he dies.
     */
    protected void incrementAge(int maxAge)
    {
        age++;
        if(age > maxAge) { 
            setDead();
        }
    }
    
    /**
     * Increment the hunger of the animal.
     */
    protected void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();  
        }
    }
    
    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     *
     * This is a template method capturing the life cycle every animal shares
     * during a step: it ages, gets hungrier, attempts to breed, then either
     * moves towards food / a free cell or dies from overcrowding. Species
     * customise the steps through the hooks below rather than reimplementing
     * the whole skeleton.
     *
     * @param newAnimals A list to receive newly born animals.
     * @param weather The current weather
     * @param dayState The different state of the day
     */
    public final void act(List<Actor> newAnimals, Weather weather, DayState dayState)
    {
        // Some animals (e.g. diurnal predators) do not act at night.
        if(!actsAtNight() && dayState == DayState.NIGHT) {
            return;
        }
        incrementAge(getMaxAge());
        incrementHunger();
        if(!isAlive()) {
            return;
        }
        meet(newAnimals, getMaxLitterSize(), getBreedingProbability(weather), getBreedingAge());
        Location newLocation = seekFood();
        if(newLocation != null) {
            onFoodFound();
        }
        else {
            // No food found - try to move to a free location.
            newLocation = getField().freeAdjacentLocation(getLocation());
        }
        // See if it was possible to move.
        if(newLocation != null) {
            setLocation(newLocation);
        }
        else {
            // Overcrowding.
            setDead();
        }
    }

    /*///////////////////////////////////////////////////////////////
                            TEMPLATE METHOD HOOKS
    //////////////////////////////////////////////////////////////*/

    /**
     * Look for and consume food in an adjacent location, returning the
     * location moved into to feed. Carnivores and herbivores supply their own
     * search by overriding this hook.
     *
     * @return the location fed at, or null if no food was found.
     */
    protected abstract Location seekFood();

    /**
     * @return the maximum age this species can reach before dying.
     */
    protected abstract int getMaxAge();

    /**
     * @return the minimum age at which this species can start breeding.
     */
    protected abstract int getBreedingAge();

    /**
     * @return the maximum number of offspring produced in a single litter.
     */
    protected abstract int getMaxLitterSize();

    /**
     * Breeding likelihood, possibly adjusted for the current weather.
     *
     * @param weather The current weather.
     * @return the probability of breeding this step.
     */
    protected abstract double getBreedingProbability(Weather weather);

    /**
     * Whether this animal is active during the night. Defaults to true;
     * nocturnally-inactive species override this to return false.
     *
     * @return true if the animal acts at night.
     */
    protected boolean actsAtNight()
    {
        return true;
    }

    /**
     * Hook invoked after {@link #seekFood()} successfully located and consumed
     * food. Defaults to doing nothing; species that need to react (e.g. to
     * record statistics) override it.
     */
    protected void onFoodFound()
    {
    }
    
    /**
     * Returns the amount by which the hungerlevel would increment by if the animal were to be eaten.
     * 
     * @return the feeding value
     */
    abstract public int getFeedingValue();
    
    /**
     * Imitates the meeting of the animal by breeding new born animals.
     * 
     * @param newAnimals list of the animals to be born.
     * @param maxLitter the maximum amount animals that the animal can breed.
     * @param breedingProbability the likelyhood of giving birth.
     * @param breedingAge the minimum age to be able to breed.
     */
    protected void meet(List<Actor> newAnimals,int maxLitter, double breedingProbability, int breedingAge)
    {
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object obj = field.getObjectAt(where);
            if(obj == null || !this.getClass().equals(obj.getClass())) {
                continue;
            }
            Animal animal = (Animal) obj;
            if(animal.getGender() == this.getGender()) {
                continue;
            }
            try
            {
                Constructor cons = this.getClass().getConstructor(ANIMAL_CONSTRUCTOR_SIGNATURE);
                int births = breed(maxLitter, breedingProbability, breedingAge);
                giveBirth(newAnimals, births, cons);
            }
            catch (NoSuchMethodException nsme)
            {
                nsme.printStackTrace();
            }
        }
    }
    
    /**
     * Returns the amount of animals to breed.
     * 
     * @param maxLitter the maximum amount of animals that the animal can breed.
     * @param breedingProbability indicates the likelyhood of breeding.
     * @param breedingAge The minimum age to be able to breed.
     * @return an integer representing the amount of animals to give birth.
     */
    private int breed(int maxLitter, double breedingProbability, int breedingAge)
    {
        int births = 0;
        if(age >= breedingAge){
            double randomValue = rand.nextDouble();
            if(randomValue <= breedingProbability) {
                births = rand.nextInt(maxLitter) + 1;
            }
        }
        return births;
    }
    
    /**
     * Gives birth to animal by appending the animals to the input list.
     * 
     * @param newAnimals list of the new born animals.
     * @param births number of animals to give birth.
     * @param consutructor of the animals to give birth.
     */
    private void giveBirth(List<Actor> newAnimals, int births, Constructor cons)
    {
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        for (int i = 0; i < births && free.size() > 0; i++) {
            Location loc = free.remove(0);
            Gender randomGender = Utils.getRandomEnumValue(Gender.class);
            try {
                Object newObj = cons.newInstance(false, field, loc, randomGender);
                Animal newBorn = (Animal) newObj;
                newAnimals.add(newBorn);        
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
    
    /*///////////////////////////////////////////////////////////////
                          ACCESSOR AND MUTATORS
    //////////////////////////////////////////////////////////////*/
    
    /**
     * Accessor method for the gender of the animal.
     * 
     * @return the gender of the animal.
     */
    protected Gender getGender()
    {
        return this.gender;
    }
    
    /**
     * Accessor method for the timestamp of the infection.
     * 
     * @return the step at which the animal got infected.
     */
    public Integer getInfectionTimestamp()
    {
        return infectionTimestamp;
    }
    
    /**
     * Mutator method for the timestamp of the infection.
     * 
     * @param step The step at which the animal got infected.
     */
    public void setInfectionTimestamp(int step)
    {
        infectionTimestamp = step;
    }
    
    /**
     * Check whether the animal is alive or not.
     * 
     * @return true if the animal is still alive.
     */
    public boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the animal is no longer alive.
     * 
     * It is removed from the field.
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
     * @return The animal's location.
     */
    protected Location getLocation()
    {
        return location;
    }
    
    /**
     * Place the animal at the new location in the given field.
     * 
     * @param newLocation The animal's new location.
     */
    protected void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }
    
    /**
     * Return the animal's field.
     * 
     * @return The animal's field.
     */
    protected Field getField()
    {
        return field;
    }
}
