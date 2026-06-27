import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 01.03.22
 */
public abstract class Animal implements Eater, Interactable
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
    protected static final Random rand = Randomizer.getRandom();

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
     * Create a new animal with optional random age and food level.
     *
     * @param randomAge If true, the animal will have random age and food level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param maxAge The maximum age for this species.
     * @param initialFoodLevel The initial (and maximum) food level for this species.
     */
    protected Animal(boolean randomAge, Field field, Location location, int maxAge, int initialFoodLevel) {
        this(field, location);
        setGender();
        if (randomAge) {
            setAge(rand.nextInt(maxAge));
            setFoodLevel(rand.nextInt(initialFoodLevel));
        } else {
            setAge(0);
            setFoodLevel(initialFoodLevel);
        }
    }

    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     * @param time the current time in the simulation
     */
    public void act(List<Animal> newAnimals, int time) {
        incrementAge(getMaxAge());
        incrementHunger();
        if (isAlive() && isActiveAt(time)) {
            if (getDisease()) {
                spreadDisease();
            }
            if (giveBirth(getBreedingAge())) {
                Field field = getField();
                List<Location> free = field.getFreeAdjacentLocations(getLocation());
                int births = breed(getBreedingAge(), getBreedingProbability(), getMaxLitterSize());
                for (int b = 0; b < births && free.size() > 0; b++) {
                    Location loc = free.remove(0);
                    Animal young = createOffspring(field, loc);
                    young.setGender();
                    newAnimals.add(young);
                }
            }
            Location newLocation = findFood();
            if (newLocation == null) {
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            if (newLocation != null) {
                setLocation(newLocation);
            } else {
                setDead();
            }
        }
    }

    protected abstract int getMaxAge();
    protected abstract int getBreedingAge();
    protected abstract double getBreedingProbability();
    protected abstract int getMaxLitterSize();
    protected abstract boolean isActiveAt(int time);
    protected abstract Animal createOffspring(Field field, Location location);

    /**
     * Default: this animal cannot be consumed. Prey subclasses override this
     * to dispatch to the appropriate eater.eatXxx() method.
     */
    public int acceptInteraction(Eater eater) { return -1; }

    /**
     * Scan adjacent locations for food. Delegates all type-specific decisions
     * to the Eater and Interactable interfaces — no instanceof checks needed.
     * @return the location where food was found, or null.
     */
    protected Location findFood() {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while (it.hasNext()) {
            Location where = it.next();
            Object obj = field.getObjectAt(where);
            if (obj instanceof Interactable) {
                Interactable target = (Interactable) obj;
                if (target.isAlive()) {
                    int result = target.acceptInteraction(this);
                    if (result >= 0) {
                        if (result > 0) setFoodLevel(result);
                        return where;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Check whether the animal is alive or not.
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
        Random rand = Randomizer.getRandom();
        if (rand.nextInt(2) == 1) {
            isMale = true;
        }
        else {
            isMale = false;
        }
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
        Random rand = Randomizer.getRandom();
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
