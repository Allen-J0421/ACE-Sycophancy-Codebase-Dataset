import java.util.List;
import java.util.Iterator;
import java.util.Random;
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
        Location newLocation = findFood();
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
     * Look for food in the adjacent locations, consuming and moving onto the
     * first edible thing found. The search skeleton is shared; carnivores and
     * herbivores differ only in what counts as food and how it is consumed,
     * which they supply by overriding {@link #eatAt(Location)}.
     *
     * @return the location fed at, or null if no food was found.
     */
    protected Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            if(eatAt(where)) {
                return where;
            }
        }
        return null;
    }

    /**
     * Attempt to consume food at the given adjacent location. Implementations
     * inspect the location, and if it holds something this animal can and
     * does eat, update this animal's food level, remove the food and return
     * true.
     *
     * @param where the adjacent location to inspect.
     * @return true if food was found and consumed, false otherwise.
     */
    protected abstract boolean eatAt(Location where);

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
     * Hook invoked after {@link #findFood()} successfully located and consumed
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
            int births = breed(maxLitter, breedingProbability, breedingAge);
            giveBirth(newAnimals, births);
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
     */
    private void giveBirth(List<Actor> newAnimals, int births)
    {
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        for (int i = 0; i < births && free.size() > 0; i++) {
            Location loc = free.remove(0);
            Gender randomGender = Utils.getRandomEnumValue(Gender.class);
            Animal newBorn = reproduce(field, loc, randomGender);
            newAnimals.add(newBorn);
        }
    }

    /**
     * Factory method creating a new offspring of this animal's own species.
     * Implemented by each concrete species, replacing reflective instantiation.
     *
     * @param field The field the newborn occupies.
     * @param location The newborn's location within the field.
     * @param gender The newborn's gender.
     * @return a newly created animal of the same species.
     */
    protected abstract Animal reproduce(Field field, Location location, Gender gender);
    
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
