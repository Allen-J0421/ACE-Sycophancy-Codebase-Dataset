import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 01.03.22
 */
public abstract class Animal extends Organism
{
    // The age to which the animal can live.
    private final int maxAge;
    // The age at which the animal can start to breed.
    private final int breedingAge;
    // The likelihood of the animal breeding.
    private final double breedingProbability;
    // The maximum number of births.
    private final int maxLitterSize;
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
    public Animal(Field field, Location location, int maxAge, int breedingAge,
            double breedingProbability, int maxLitterSize, boolean randomAge,
            Random random, int foodValue) {
        super(field, location);
        this.maxAge = maxAge;
        this.breedingAge = breedingAge;
        this.breedingProbability = breedingProbability;
        this.maxLitterSize = maxLitterSize;
        fog = false;
        disease = false;
        initializeAnimal(randomAge, random, foodValue);
    }
    
    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     */
    public final void act(List<Animal> newAnimals, int time) {
        performAct(newAnimals, time, maxAge, breedingAge, breedingProbability, maxLitterSize);
    }

    /**
     * Determine whether this animal is active at the current time.
     * @param time The current time in the simulation.
     * @return true if the animal should act.
     */
    abstract protected boolean isActiveAt(int time);

    /**
     * Look for food adjacent to the current location.
     * @return Where food was found, or null if it wasn't.
     */
    abstract protected Location findFood();

    /**
     * Create a newborn instance of the current species.
     * @param field The field in which the offspring will live.
     * @param location The offspring location.
     * @return A new animal of the current species.
     */
    abstract protected Animal createYoung(Field field, Location location);

    /**
     * Apply the common initialization for a new animal instance.
     * @param randomAge If true, use random age and food level.
     * @param random The random source for initial state.
     * @param foodValue The initial or maximum food value for the species.
     */
    private void initializeAnimal(boolean randomAge, Random random, int foodValue) {
        setGender();
        if(randomAge) {
            setAge(random.nextInt(maxAge));
            setFoodLevel(random.nextInt(foodValue));
        }
        else {
            setAge(0);
            setFoodLevel(foodValue);
        }
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
     * @return A shuffled list of adjacent locations.
     */
    protected final List<Location> adjacentLocations() {
        return getField().adjacentLocations(getLocation());
    }

    /**
     * if an animal has a disease, this method spreads it to animals
     * in adjacent locations
     */
    protected void spreadDisease() {
        Field field = getField();
        Iterator<Location> it = adjacentLocations().iterator();
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
     * Execute the common animal lifecycle for one simulation step.
     * @param newAnimals A list to receive newly born animals.
     * @param time The current time in the simulation.
     * @param maxAge The maximum age before death.
     * @param breedingAge The minimum breeding age.
     * @param breedingProbability The probability of breeding.
     * @param maxLitterSize The maximum number of offspring.
     */
    protected final void performAct(List<Animal> newAnimals, int time, int maxAge,
            int breedingAge, double breedingProbability, int maxLitterSize) {
        incrementAge(maxAge);
        incrementHunger();

        if(!isAlive() || !isActiveAt(time)) {
            return;
        }

        if(getDisease()) {
            spreadDisease();
        }

        createOffspring(newAnimals, breedingAge, breedingProbability, maxLitterSize);
        moveToNewLocation();
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

    /**
     * Find and eat the first adjacent animal of the given type.
     * @param adjacent The adjacent locations to search.
     * @param preyType The target animal type.
     * @param foodValue The food gained from eating the prey.
     * @return Where prey was found, or null if it wasn't.
     */
    protected final Location eatAdjacentAnimal(List<Location> adjacent,
            Class<? extends Animal> preyType, int foodValue) {
        return consumeAdjacentOrganism(adjacent, preyType, foodValue);
    }

    /**
     * Find and eat the first adjacent plant of the given type.
     * @param adjacent The adjacent locations to search.
     * @param plantType The target plant type.
     * @param foodValue The food gained from eating the plant.
     * @return Where food was found, or null if it wasn't.
     */
    protected final Location eatAdjacentPlant(List<Location> adjacent,
            Class<? extends Plant> plantType, int foodValue) {
        return consumeAdjacentOrganism(adjacent, plantType, foodValue);
    }

    /**
     * Remove the first adjacent plant without gaining food.
     * @param adjacent The adjacent locations to search.
     * @return Where a plant was found, or null if it wasn't.
     */
    protected final Location trampleAdjacentPlant(List<Location> adjacent) {
        return consumeAdjacentOrganism(adjacent, Plant.class, 0);
    }

    /**
     * @param random The random source to use.
     * @return true if fog does not prevent the food search this step.
     */
    protected final boolean canSearchInFog(Random random) {
        return !getFog() || random.nextInt(2) == 0;
    }

    private void createOffspring(List<Animal> newAnimals, int breedingAge,
            double breedingProbability, int maxLitterSize) {
        if(!giveBirth(breedingAge)) {
            return;
        }

        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed(breedingAge, breedingProbability, maxLitterSize);
        for (int b = 0; b < births && free.size() > 0; b++) {
            Location location = free.remove(0);
            Animal young = createYoung(field, location);
            young.setGender();
            newAnimals.add(young);
        }
    }

    private void moveToNewLocation() {
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

    private Location consumeAdjacentOrganism(List<Location> adjacent,
            Class<?> targetType, int foodValue) {
        Field field = getField();
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object organism = field.getObjectAt(where);
            if(targetType.isInstance(organism)) {
                Organism target = (Organism) organism;
                if(target.isAlive()) {
                    target.setDead();
                    if(foodValue > 0) {
                        setFoodLevel(foodValue);
                    }
                    return where;
                }
            }
        }
        return null;
    }

}
