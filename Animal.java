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
    /**
     * A food type this animal can eat, and the food level it restores.
     */
    protected static class FoodSource
    {
        private Class<?> foodClass;
        private int foodValue;

        public FoodSource(Class<?> foodClass, int foodValue) {
            this.foodClass = foodClass;
            this.foodValue = foodValue;
        }

        private boolean matches(Object object) {
            return foodClass.isInstance(object);
        }

        private int getFoodValue() {
            return foodValue;
        }
    }

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
    // Tunable settings for the simulation.
    private SimulationConfig config;

    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(Field field, Location location) {
        this(field, location, SimulationConfig.defaultConfig());
    }

    /**
     * Create a new animal at location in field.
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param config The simulation configuration to use.
     */
    public Animal(Field field, Location location, SimulationConfig config) {
        alive = true;
        this.config = config;
        this.field = field;
        setLocation(location);
        fog = false;
        disease = false;
    }
    
    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     */
    public void act(List<Animal> newAnimals, int time) {
        incrementAge(getMaxAge());
        incrementHunger();

        if(isAlive() && isActive(time)) {
            if(getDisease()) {
                spreadDisease();
            }
            produceOffspring(newAnimals);
            move();
        }
    }

    /**
     * Return the oldest age this animal can reach.
     */
    protected abstract int getMaxAge();

    /**
     * Return the age at which this animal can start to breed.
     */
    protected abstract int getBreedingAge();

    /**
     * Return the likelihood of this animal breeding.
     */
    protected abstract double getBreedingProbability();

    /**
     * Return the maximum number of births for this animal.
     */
    protected abstract int getMaxLitterSize();

    /**
     * Return whether this animal is active at the current simulation time.
     */
    protected abstract boolean isActive(int time);

    /**
     * Create a newborn animal of this species.
     */
    protected abstract Animal createYoung(Field field, Location location);

    /**
     * Return the foods this animal can eat, in search priority order.
     */
    protected abstract FoodSource[] getFoodSources();

    /**
     * Return the simulation configuration.
     */
    protected SimulationConfig getConfig() {
        return config;
    }

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
    protected int breed(int breedingAge, double breedingProbability, int maxLitterSize) {
        int births = 0;
        if(canBreed(breedingAge) && rand.nextDouble() <= breedingProbability) {
            births = rand.nextInt(maxLitterSize) + 1;
        }
        return births;
    }

    /**
     * Check whether or not this animal is to give birth at this step.
     * animals are checked to be: of the same species(class), different genders and
     *  of breeding age
     * @param breedingAge the minimum age this animal must be to breed
     * @retyrn true if the animal can breed, false if it can not
     */
    protected boolean giveBirth(int breedingAge) {
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
                        if (age >= breedingAge) {
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
     * @param breedingAge the minimum an animal must be in order to breed
     * @return true if the animal is of breeding age
     */
    protected boolean canBreed(int breedingAge)
    {
        return age >= breedingAge;
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
    protected void incrementAge(int maxAge) {
        age++;
        if(age > maxAge) {
            setDead();
        }
    }

    /**
     * Check whether fog or another species-specific condition prevents finding food.
     */
    protected boolean canFindFood() {
        return true;
    }

    /**
     * Return whether this animal can trample plants it does not eat.
     */
    protected boolean canTramplePlants() {
        return true;
    }

    /**
     * Check whether this animal is active between two times, inclusive.
     */
    protected boolean isActiveBetween(int time, int start, int end) {
        if(start <= end) {
            return time >= start && time <= end;
        }
        else {
            return time >= start || time <= end;
        }
    }

    /**
     * Check whether this animal is active in a time range that crosses midnight.
     */
    protected boolean isActiveOutside(int time, int inactiveStart, int inactiveEnd) {
        return isActiveBetween(time, inactiveEnd, inactiveStart);
    }

    /**
     * Add any newborn animals to the new animals list.
     */
    private void produceOffspring(List<Animal> newAnimals) {
        int breedingAge = getBreedingAge();
        if (giveBirth(breedingAge)) {
            Field field = getField();
            List<Location> free = field.getFreeAdjacentLocations(getLocation());
            int births = breed(breedingAge, getBreedingProbability(), getMaxLitterSize());
            for (int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                newAnimals.add(createYoung(field, loc));
            }
        }
    }

    /**
     * Move toward food if available, otherwise move to a free adjacent location.
     */
    private void move() {
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

    /**
     * Look for food adjacent to the current location.
     * Only the first live food source is eaten. Other plants may be trampled.
     * @return where food was found, or null if it wasn't.
     */
    private Location findFood() {
        if(!canFindFood()) {
            return null;
        }

        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object object = field.getObjectAt(where);
            FoodSource foodSource = foodSourceFor(object);

            if(foodSource != null && isAliveFood(object)) {
                setDeadFood(object);
                setFoodLevel(foodSource.getFoodValue());
                return where;
            }
            else if(canTramplePlants() && object instanceof Plant) {
                Plant plant = (Plant) object;
                if(plant.isAlive()) {
                    plant.setDead();
                    return where;
                }
            }
        }
        return null;
    }

    /**
     * Return the food source matched by the object, if any.
     */
    private FoodSource foodSourceFor(Object object) {
        for(FoodSource foodSource : getFoodSources()) {
            if(foodSource.matches(object)) {
                return foodSource;
            }
        }
        return null;
    }

    /**
     * Check whether a matched food object is alive.
     */
    private boolean isAliveFood(Object object) {
        if(object instanceof Animal) {
            return ((Animal) object).isAlive();
        }
        else if(object instanceof Plant) {
            return ((Plant) object).isAlive();
        }
        return false;
    }

    /**
     * Remove a matched food object from the field.
     */
    private void setDeadFood(Object object) {
        if(object instanceof Animal) {
            ((Animal) object).setDead();
        }
        else if(object instanceof Plant) {
            ((Plant) object).setDead();
        }
    }

}
