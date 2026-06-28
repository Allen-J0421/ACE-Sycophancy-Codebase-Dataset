import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 01.03.22
 */
public abstract class Animal
{
    // Whether the animal is alive or not.
    private boolean alive;
    // The animal's field.
    private Field field;
    // The animal's position in the field.
    private Location location;
    // Distinguishes between male and female to determine breeding
    private boolean isMale;
    // whether or not the weather is fog
    private boolean fog;
    // The animal's food level
    private int foodLevel;
    //whether the animal has a disease or not
    private boolean disease;
    // The animal's age.
    private int age;

    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(Field field, Location location) {
        alive = true;
        this.field = field;
        setLocation(location);
        fog = false;
        disease = false;
    }

    /**
     * Initialise a newly created animal's gender, age and food level.
     * A newborn (randomAge false) starts at age zero with a full food level;
     * an animal created to seed the simulation (randomAge true) is given a
     * random age and food level.
     * @param randomAge If true, assign a random age and food level.
     */
    protected void initialise(boolean randomAge) {
        setGender();
        if(randomAge) {
            setAge(rand.nextInt(getMaxAge()));
            setFoodLevel(rand.nextInt(getInitialFoodLevel()));
        }
        else {
            setAge(0);
            setFoodLevel(getInitialFoodLevel());
        }
    }

    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     *
     * This is a template method: it defines the fixed daily routine common to
     * every animal (grow older, get hungrier and, while alive and active,
     * spread disease, breed and then feed/move/die). The species-specific
     * decisions are delegated to the hook methods declared below.
     *
     * @param newAnimals A list to receive newly born animals.
     * @param time The current time in the simulation.
     */
    public void act(List<Animal> newAnimals, int time) {
        incrementAge(getMaxAge());
        incrementHunger();
        if(isAlive() && isActive(time)) {
            if(getDisease()) {
                spreadDisease();
            }
            if(giveBirth(getBreedingAge())) {
                Field field = getField();
                List<Location> free = field.getFreeAdjacentLocations(getLocation());
                int births = breed(getBreedingAge(), getBreedingProbability(), getMaxLitterSize());
                for(int b = 0; b < births && free.size() > 0; b++) {
                    Location loc = free.remove(0);
                    newAnimals.add(createYoung(field, loc));
                }
            }
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
    }

    // ---- Species-specific hooks used by the act() template method ----

    /**
     * @return The age beyond which this species dies of old age.
     */
    protected abstract int getMaxAge();

    /**
     * @return The minimum age at which this species can breed.
     */
    protected abstract int getBreedingAge();

    /**
     * @return The probability that a breeding-age animal breeds in a step.
     */
    protected abstract double getBreedingProbability();

    /**
     * @return The maximum number of offspring produced in a single breed.
     */
    protected abstract int getMaxLitterSize();

    /**
     * @return The food level a newborn of this species starts with, and the
     *         upper bound on a randomly-aged animal's starting food level.
     */
    protected abstract int getInitialFoodLevel();

    /**
     * Decide whether this animal is active (awake and behaving) at the given
     * time of day.
     * @param time The current time in the simulation.
     * @return true if the animal should take its turn at this time.
     */
    protected abstract boolean isActive(int time);

    /**
     * Look for food adjacent to the current location, consuming it if found.
     * @return Where food was found, or null if it wasn't.
     */
    protected abstract Location findFood();

    /**
     * Create a newborn of this species at the given place.
     * @param field The field the young is placed in.
     * @param location The location of the young within the field.
     * @return The newly created animal.
     */
    protected abstract Animal createYoung(Field field, Location location);

    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the animal is no longer alive.
     * It is removed from the field.
     */
    protected void setDead() {
        alive = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }

    /**
     * Return the animal's location.
     * @return The animal's location.
     */
    protected Location getLocation()
    {
        return location;
    }
    
    /**
     * Place the animal at the new location in the given field.
     * @param newLocation The animal's new location.
     */
    protected void setLocation(Location newLocation) {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }
    
    /**
     * Return the animal's field.
     * @return The animal's field.
     */
    protected Field getField()
    {
        return field;
    }

    /**
     * Return the animal's gender.
     * @return True if the animal is male, false if it is female.
     */
    protected boolean getGender(){return isMale;}

    /**
     * Randomly assigns an animal a gender
     */
    protected void setGender() {
        isMale = rand.nextInt(2) == 1;
    }

    /**
     * assigns true to fog field
     */
    protected void setFog() { fog = true; }

    /**
     * assigns false to fog field
     */
    protected void resetFog(){ fog = false; }

    /**
     * returns the fog field
     * @return true if the weather is fog, false if it is not
     */
    protected boolean getFog() {return fog;}

    /**
     * takes a food level parameter and assigns it to the foodLevel field
     * @param foodLevel the value to be assigned to foodLevel
     */
    protected void setFoodLevel(int foodLevel){ this.foodLevel = foodLevel; }

    /**
     * decrements foodLevel by 1
     */
    protected void decrementHealth(){ foodLevel--; }

    /**
     * gives an animal a disease
     * 1 in 100 chance of assigning true to disease
     */
    protected void giveDisease() {
        if (rand.nextInt(101) == 1) {
            disease = true;
        }
    }

    /**
     * returns whether the animal has a disease or not
     * @return true if the animal has a disease, false if they don't
     */
    protected boolean getDisease() { return disease; }

    /**
     * assigns false to disease
     */
    protected void resetDisease() { disease = false; }

    /**
     * if an animal has a disease, this method spreads it to animals
     * in adjacent locations
     */
    protected void spreadDisease() {
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Animal) {
                Animal diseaseAnimal = (Animal) animal;
                diseaseAnimal.giveDisease();
                diseaseAnimal.decrementHealth();
            }
        }
    }

    /**
     * takes an age parameter and assigns it to the age field
     * @param age the value to be assigned to the age field
     */
    protected void setAge(int age) { this.age = age; }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    protected int breed(int BREEDING_AGE, double BREEDING_PROBABILITY, int MAX_LITTER_SIZE) {
        int births = 0;
        if(canBreed(BREEDING_AGE) && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * Check whether or not this animal is to give birth at this step.
     * animals are checked to be: of the same species(class), different genders and
     *  of breeding age
     * @param BREEDING_AGE the minimum age this animal must be to breed
     * @retyrn true if the animal can breed, false if it can not
     */
    protected boolean giveBirth(int BREEDING_AGE) {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if (animal != null) {
                if (animal.getClass() == this.getClass()) {
                    Animal adjAnimal = (Animal) animal;
                    if (this.getGender() != adjAnimal.getGender()) {
                        if (age >= BREEDING_AGE) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    /**
     * checks to see if the animal has reached breeding age
     * @param BREEDING_AGE the minimum an animal must be in order to breed
     * @return true if the animal is of breeding age
     */
    protected boolean canBreed(int BREEDING_AGE)
    {
        return age >= BREEDING_AGE;
    }

    /**
     * make the animal more hungry by decreasing its health
     * if the foodLevel is less than or equal to 0, set the animal to dead
     */
    protected void incrementHunger() {
        decrementHealth();
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Increase the age. This could result in the animal's death.
     */
    protected void incrementAge(int MAX_AGE) {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

}
