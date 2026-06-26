import java.util.List;
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
                        ABSTRACT BEHAVIOUR HOOKS
    //////////////////////////////////////////////////////////////*/

    protected abstract int getMaxAge();
    protected abstract int getMaxLitterSize();
    protected abstract double getBreedingProbability(Weather weather);
    protected abstract int getBreedingAge();
    protected abstract Location doFindFood();
    protected abstract Animal createOffspring(Field field, Location location, Gender gender);
    public abstract int getFeedingValue();

    /** Override to return true for animals that skip their turn at night. */
    protected boolean isRestrictedToDay() { return false; }

    /*///////////////////////////////////////////////////////////////
                            ANIMAL BEHAVIOUR LOGIC
    //////////////////////////////////////////////////////////////*/

    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     *
     * @param newAnimals A list to receive newly born animals.
     * @param weather The current weather
     * @param dayState The different state of the day
     */
    public void act(List<Actor> newAnimals, Weather weather, DayState dayState)
    {
        if(isRestrictedToDay() && dayState == DayState.NIGHT) {
            return;
        }
        incrementAge(getMaxAge());
        incrementHunger();
        if(!isAlive()) {
            return;
        }
        meet(newAnimals, getMaxLitterSize(), getBreedingProbability(weather), getBreedingAge());
        Location newLocation = doFindFood();
        if(newLocation == null) {
            newLocation = field.freeAdjacentLocation(getLocation());
        }
        if(newLocation != null) {
            setLocation(newLocation);
        } else {
            setDead();
        }
    }

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
     * Imitates the meeting of the animal by breeding new born animals.
     *
     * @param newAnimals list of the animals to be born.
     * @param maxLitter the maximum amount animals that the animal can breed.
     * @param breedingProbability the likelyhood of giving birth.
     * @param breedingAge the minimum age to be able to breed.
     */
    protected void meet(List<Actor> newAnimals, int maxLitter, double breedingProbability, int breedingAge)
    {
        List<Location> adjacent = field.adjacentLocations(getLocation());
        for (Location where : adjacent) {
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
        for (int i = 0; i < births && !free.isEmpty(); i++) {
            Location loc = free.remove(0);
            Gender randomGender = Utils.getRandomEnumValue(Gender.class);
            Animal newBorn = createOffspring(field, loc, randomGender);
            newAnimals.add(newBorn);
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
