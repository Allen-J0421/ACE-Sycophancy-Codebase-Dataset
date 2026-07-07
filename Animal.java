import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/**
 * A class representing shared characteristics of animals,
 * could be initialised for prey animals.
 *
 * @version 2022.02.28
 */
public class Animal extends Species
{
    // The age at which an animal can start to breed.
    private final int breedingAge;
    // The age to which an animal can live.
    private final int maxAge;
    // The maximum number of births at once.
    private final int maxLitterSize;
    // true if the animal's sex is female
    private final boolean isFemale;
    // true if the animal hibernates during cold temperatures
    private final boolean hibernates;
    // true if the animal is active at night
    private final boolean isNocturnal;
    // the number of steps that should pass until an animal in hibernation moves
    private static final int STAY_STEPS = 10;

    // The animal's food level
    protected int foodLevel;
    // number of steps where the animal is in hibernation
    private int hiberSteps;
    // true if the animal is currently hibernating
    private boolean inHibernation;
    // The animal's age.
    private int age;

    // Strategy controlling how this animal finds and consumes food.
    protected FoodStrategy foodStrategy;
    // Strategy controlling how this animal reproduces.
    protected ReproductionStrategy reproductionStrategy;

    /**
     * Create a new animal with given specifications.
     *
     * @param field (Field) the field where the simulation takes place
     * @param location (Location) the Location at which the animal should appear
     * @param name (String) the animal's name (its species' name)
     * @param maximumTemperature (int) the maximum temperature the animal can survive to
     * @param minimumTemperature (int) the minimum temperature an animal can survive to
     * @param nutritionalValue (int) the animal's nutritional value
     * @param reproductionProbability (double) the probability that the animal reproduces each step
     * @param maxAge (int) the animal's life expectancy
     * @param breedingAge (int) the age at which animal can start to reproduce
     * @param maxLitterSize (int) the maximum number of children at once
     * @param randomAge (boolean) whether an animal should be created with a random age
     * @param hibernates (boolean) whether an animal is able to hibernate
     * @param isNocturnal (boolean) whether an animal is more active at night
     */
    public Animal(Field field, Location location, String name, int maximumTemperature,
                  int minimumTemperature, int nutritionalValue, double reproductionProbability,
                  int maxAge, int breedingAge, int maxLitterSize, boolean randomAge,
                  boolean hibernates, boolean isNocturnal)
    {
        super(field, location, name, maximumTemperature, minimumTemperature, nutritionalValue,
                reproductionProbability);

        this.breedingAge = breedingAge;
        this.maxAge = maxAge;
        this.maxLitterSize = maxLitterSize;
        this.isFemale = randomSex();
        this.hibernates = hibernates;
        this.isNocturnal = isNocturnal;
        this.foodLevel = randomFoodLevel();
        inHibernation = false;
        hiberSteps = 0;

        if (randomAge) {
            age = rand.nextInt(maxAge);
        } else {
            age = 0;
        }

        foodStrategy = new PlantFoodStrategy();
        reproductionStrategy = new AnimalReproductionStrategy();
    }

    /**
     * Imitate an animal's step:
     * 1) increment age if a year has passed.
     * 2) if alive: check hibernation, handle temperature death, gate movement
     *    by day/night and nocturnal flag.
     *
     * @param newSpecies (List<Species>) A list to receive newly born animals.
     * @param isNight (boolean) true if it is night in the simulation
     * @param temperature (int) the current temperature of the simulation
     * @param yearPassed (boolean) true if a year has passed in the simulation
     */
    public void act(List<Species> newSpecies, boolean isNight, int temperature, boolean yearPassed)
    {
        if (yearPassed) {
            incrementAge();
        }

        if (isAlive()) {
            checkHibernation(temperature);

            if (inHibernation) {
                if (hiberSteps % STAY_STEPS == 0) {
                    makeMove(newSpecies);
                    incrementHunger();
                }
                incrementHiberSteps();
            } else if (!survivesTemperature(temperature)) {
                setDead();
            } else {
                if (isNight && isNocturnal) {
                    makeMove(newSpecies);
                } else if (!isNight) {
                    makeMove(newSpecies);
                }
                if (!isNight) {
                    incrementHunger();
                }
            }
        }
    }

    /**
     * Execute one movement cycle:
     * 1) Call beforeMove() — a hook for subclass pre-move actions (e.g. attack check).
     * 2) If still alive: reproduce if possible, find food via foodStrategy, move to
     *    a free adjacent cell, or die of overcrowding.
     *
     * @param newSpecies (List<Species>) A list to receive newly born animals.
     */
    protected void makeMove(List<Species> newSpecies)
    {
        ArrayList<Animal> neighbors = getNeighboringAnimalsList();
        beforeMove(neighbors);

        if (!isAlive()) {
            return;
        }

        if (canReproduce(neighbors)) {
            reproduce(newSpecies);
        }

        Location newLocation = null;
        if (isNotFull()) {
            newLocation = foodStrategy.findFoodAndEat(this, neighbors);
        }

        if (newLocation == null) {
            newLocation = getField().freeAdjacentLocation(getLocation());
        }

        if (newLocation != null) {
            setLocation(newLocation);
        } else {
            setDead();
        }
    }

    /**
     * Hook called at the start of makeMove() before any reproduce/eat/move logic.
     * No-op for base Animal; Predator overrides this to run its attack check.
     *
     * @param neighbors (ArrayList<Animal>) Live animals in adjacent cells.
     */
    protected void beforeMove(ArrayList<Animal> neighbors) { }

    /**
     * Returns a list of animals located in neighboring cells.
     *
     * @return (ArrayList<Animal>) list of neighboring animals
     */
    protected ArrayList<Animal> getNeighboringAnimalsList()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> locationIterator = adjacent.iterator();

        ArrayList<Animal> neighboringAnimals = new ArrayList<>();
        while (locationIterator.hasNext()) {
            Location where = locationIterator.next();
            Object species = field.getObjectAt(where);
            if (species instanceof Animal) {
                Animal neighboringAnimal = (Animal) species;
                if (neighboringAnimal.isAlive()) {
                    neighboringAnimals.add(neighboringAnimal);
                }
            }
        }
        return neighboringAnimals;
    }

    /**
     * Delegate reproduction to the reproductionStrategy, satisfying the abstract
     * method declared in Species.
     *
     * @param newOfThisKind (List<Species>) List of Species for the newborns.
     */
    void reproduce(List<Species> newOfThisKind)
    {
        reproductionStrategy.reproduce(this, newOfThisKind);
    }

    /**
     * Increase the age. This could result in the animal's death.
     */
    protected void incrementAge()
    {
        age++;
        if (age > maxAge) {
            setDead();
        }
    }

    /**
     * Make this animal more hungry. This could result in the animal's death.
     */
    protected void incrementHunger()
    {
        foodLevel--;
        if (foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * @return (boolean) true if the animal can still eat more.
     */
    protected boolean isNotFull()
    {
        return foodLevel < getNutritionalValue() * 1.5;
    }

    /**
     * Increment hiberSteps by 1.
     */
    protected void incrementHiberSteps()
    {
        hiberSteps++;
    }

    /**
     * If the animal is a female, check if a male of the same species is in one
     * of the neighboring cells.
     *
     * @param neighboringAnimalsList (ArrayList<Animal>) Neighboring live animals.
     * @return (boolean) true if animal can reproduce.
     */
    protected boolean canReproduce(ArrayList<Animal> neighboringAnimalsList)
    {
        if (this.isFemale) {
            for (Animal neighbor : neighboringAnimalsList) {
                if (!neighbor.isFemale && neighbor.getName().equals(this.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns a random boolean to randomize the sex of newborns.
     */
    private boolean randomSex()
    {
        return Math.random() <= 0.5;
    }

    /**
     * Return a random foodLevel above half the animal's nutritional value.
     */
    private int randomFoodLevel()
    {
        int lowBound = getNutritionalValue() / 2;
        return rand.nextInt(lowBound) + lowBound;
    }

    /**
     * Generate the number of births for this reproduction event.
     *
     * @return (int) The number of births (can be zero).
     */
    protected int numberOfBirths()
    {
        int births = 0;
        if (canGiveBirth() && rand.nextDouble() <= getReproductionProbability()) {
            births = rand.nextInt(maxLitterSize) + 1;
        }
        return births;
    }

    /**
     * An animal can give birth if it has reached the minimal breeding age.
     */
    protected boolean canGiveBirth()
    {
        return age >= breedingAge;
    }

    /**
     * Update the hibernation state based on current temperature.
     *
     * @param currentTemperature (int) the current simulation temperature
     */
    protected void checkHibernation(int currentTemperature)
    {
        if (hibernates && currentTemperature <= getMinimumTemperature() + 5) {
            inHibernation = true;
        } else {
            inHibernation = false;
            hiberSteps = 0;
        }
    }

    /** @return (int) The maximum age to which an animal can live */
    protected int getMaxAge() { return maxAge; }

    /** @return (int) The age at which an animal can start to breed */
    protected int getBreedingAge() { return breedingAge; }

    /** @return (int) The maximum number of births at once */
    protected int getMaxLitterSize() { return maxLitterSize; }

    /** @return (boolean) true if the animal hibernates */
    protected boolean getHibernates() { return hibernates; }

    /** @return (boolean) true if the animal is active at night */
    protected boolean getIsNocturnal() { return isNocturnal; }

    /**
     * Increment the animal's food level by the given value.
     * Public so predators can share nutritional value after killing prey.
     *
     * @param value (int) the amount to add to foodLevel.
     */
    public void incrementFoodLevel(int value)
    {
        foodLevel += value;
    }
}
