import java.util.ArrayList;
import java.util.List;

/**
 * A class representing shared characteristics of animals,
 * could be initialised for prey animals.
 *
 * @version 2022.02.28
 */

public class Animal extends Species
{
    // Fields defining a special kind of animal, they can not be changed after initialization

    // The age at which an animal can start to breed.
    private final int breedingAge;
    // The age to which an animal can live.
    private final int maxAge;
    // The maximum number of births at once.
    private final int maxLitterSize;
    // true if the animal's sex if female
    private final boolean isFemale;
    // true if the animal hibernates during cold temperatures
    private final boolean hibernates;
    // true if the animal is active at night
    private final boolean isNocturnal;
    // the number of steps that should pass until an animal in hibernation moves
    private static final int STAY_STEPS = 10;

    // Fields prone to change during the animal's life

    // The animal's food level
    protected int foodLevel;
    // number of steps where the animal is in hibernation
    private int hiberSteps;
    // true if the animal is currently hibernating
    private boolean inHibernation;
    // The animal's age.
    private int age;

    /**
     * Create a new animal with given specifications. An animal can be created with random age (or start at age 0) and
     * with a random foodLevel.
     *
     * @param field (Field) the field where the simulation takes place
     * @param location (Location) the Location at which the animal should appear
     * @param name (String) the animal's name (its species' name)
     * @param maximumTemperature (int) the maximum temperature the animal can survive to
     * @param minimumTemperature (int) the minimum temperature an animal can survive to
     * @param nutritionalValue (int) the animal's nutritional value
     * @param reproductionProbability (double) the probability that the animal reproduces at each step after a given minimum breeding age
     * @param maxAge (int) the animal's life expectancy
     * @param breedingAge (int) the age at which animal can start to reproduce
     * @param maxLitterSize (int) the maximum number of children the animal can have in one reproduction
     * @param randomAge (boolean) whether an animal should be created with a random age
     * @param hibernates (boolean) whether an animal is able to hibernate
     * @param isNocturnal (boolean) whether an animal is more active at night
     */
    public Animal(Field field, Location location, String name, int maximumTemperature, int minimumTemperature, int nutritionalValue, double reproductionProbability, int maxAge, int breedingAge, int maxLitterSize, boolean randomAge, boolean hibernates, boolean isNocturnal)
    {
        super(field, location, name, maximumTemperature, minimumTemperature, nutritionalValue, reproductionProbability);

        this.breedingAge = breedingAge;
        this.maxAge = maxAge;
        this.maxLitterSize = maxLitterSize;
        this.isFemale = randomSex();
        this.hibernates = hibernates;
        this.isNocturnal = isNocturnal;
        // Default value is nutritionalValue to simulate the nutriment apart from the mother's milk/ other parental feeding.
        this.foodLevel = randomFoodLevel();
        inHibernation = false;
        hiberSteps = 0;

        if (randomAge) {
            age = rand.nextInt(maxAge);
        } else {
            age = 0;
        }
    }

    /**
     * Imitate an animal's step by doing the following:
     * 1) increment the animal's age if a year has passed.
     * 2) if the animal is alive, then:
     *      i) check if the animal should hibernate.
     *      ii) if the animal is in hibernation, then:
     *          a) if it passed STAY_STEPS steps without moving, then move and increment hunger.
     *          b) increment hiberSteps.
     *      iii) if the animal can't withstand the current temperature, then it dies.
     *      iv) if the animal is not in hibernation, then:
     *          a) if it is night and the animal is nocturnal, then move.
     *          b) if it is day, then all animals move and increment hunger.
     *
     * @param newSpecies (List<Species>) A list to receive newly born animals.
     * @param isNight (boolean) true if it is night in the simulation
     * @param temperature (int) the current temperature of the simulation
     * @param yearPassed true if a year has passed in the simulation
     */
    public void act(List<Species> newSpecies, boolean isNight, int temperature, boolean yearPassed)
    {
        if (yearPassed) {
            incrementAge();
        }

        if (!isAlive()) {
            return;
        }

        handleLivingStep(newSpecies, isNight, temperature);
    }

    /**
     * Handle the portion of the step where the animal is alive.
     *
     * @param newSpecies a list to receive newborn species.
     * @param isNight true if it is currently night.
     * @param temperature the current temperature.
     */
    private void handleLivingStep(List<Species> newSpecies, boolean isNight, int temperature)
    {
        checkHibernation(temperature);

        if (inHibernation) {
            handleHibernatingStep(newSpecies);
        }
        else {
            handleAwakeOrTemperatureDeathStep(newSpecies, isNight, temperature);
        }
    }

    /**
     * Handle the awake branch and temperature death transition.
     *
     * @param newSpecies a list to receive newborn species.
     * @param isNight true if it is currently night.
     * @param temperature the current temperature.
     */
    private void handleAwakeOrTemperatureDeathStep(List<Species> newSpecies, boolean isNight, int temperature)
    {
        if (isTemperatureLethal(temperature)) {
            onTemperatureDeath();
            return;
        }
        handleAwakeStep(newSpecies, isNight);
    }

    /**
     * Handle a step while the animal is hibernating.
     *
     * @param newSpecies a list to receive newborn species.
     */
    private void handleHibernatingStep(List<Species> newSpecies)
    {
        if (hiberSteps % STAY_STEPS == 0) {
            makeMove(newSpecies);
            incrementHunger();
        }
        incrementHiberSteps();
    }

    /**
     * Handle a step while the animal is awake.
     *
     * @param newSpecies a list to receive newborn species.
     * @param isNight true if it is currently night.
     */
    private void handleAwakeStep(List<Species> newSpecies, boolean isNight)
    {
        if (shouldMoveThisStep(isNight)) {
            makeMove(newSpecies);
        }
        if (!isNight) {
            incrementHunger();
        }
    }

    /**
     * @param isNight true if it is currently night.
     * @return true if the animal should move on this step.
     */
    private boolean shouldMoveThisStep(boolean isNight)
    {
        return !isNight || isNocturnal;
    }

    /**
     * An animal's movement. It first tries to reproduce, then to eat if a plant is in one of the neighboring cells and finally to move
     * if an adjacent cell is available. If no adjacent cell is available, it dies of overcrowding.
     *
     * @param newSpecies (List<Species>) A list to receive newly born animals.
     */
    protected void makeMove(List<Species> newSpecies)
    {
        List<Animal> neighboringAnimalsList = getNeighboringAnimalsList();
        reproduceIfPossible(newSpecies, neighboringAnimalsList);

        // Eats if it is possible
        if (isNotFull()) {
            findFoodAndEat();
        }

        moveToFreeAdjacentLocationOrDie();
    }

    /**
     * Returns a list of animals located in neighboring cells.
     *
     * @return (ArrayList) list of neighboring animals
     */
    protected List<Animal> getNeighboringAnimalsList()
    {
        Field field = getField();
        ArrayList<Animal> neighboringAnimals = new ArrayList<>();
        for (Location where : field.adjacentLocations(getLocation())) {
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
     * Increase the age. This could result in the animal's death.
     */
    protected void incrementAge()
    {
        age++;
        if(age > maxAge) {
            setDead();
        }
    }

    /**
     * Make this animal more hungry. This could result in the animal's death.
     */
    protected void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * @return (boolean) true if the animal is not full and therefore can eat and false if it is full.
     */
    protected boolean isNotFull()
    {
        return foodLevel < getNutritionalValue()*1.5;
    }
    
    /**
     * Increment hiberSteps by 1
     */
    protected void incrementHiberSteps()
    {
        hiberSteps++;
    }

    /**
     * Look for plants adjacent to the current location.
     * Only the first plant is eaten.
     */
    private void findFoodAndEat()
    {
        Field field = getField();
        for (Location where : field.adjacentLocations(getLocation())) {
            Object species = field.getObjectAt(where);
            if(species instanceof Plant) {
                Plant plantSquare = (Plant) species;
                if(plantSquare.isAlive()) {
                    plantSquare.isEaten();
                    incrementFoodLevel(plantSquare.getNutritionalValue());
                    break;
                }
            }
        }
    }

    /**
     * If the animal is a female, check if a male of the same species is in one of the neighboring cells. If it is the case, animal is able to reproduce
     *  and should go to the cel where the male is.
     *  Note: The task of reproducing is handled only by females so that the same reproduction can not happen twice in the same simulator step.
     *
     * @param neighboringAnimalsList a list of the animals located in neighboring cells.
     * @return true if the animal can reproduce.
     */
    protected boolean canReproduce(List<Animal> neighboringAnimalsList)
    {
        // task to reproduce is handed to women only so that the same reproduction does not happen twice
        if (this.isFemale)
        {
            for (Animal neighbor : neighboringAnimalsList) {
                if (!neighbor.isFemale && neighbor.getName().equals(this.getName())) {
                    // The neighbor is a male of the same species
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Creates the appropriate number of animals of the same species. These new animals of course share the same features as their "parent"
     * except the sex which is randomized,  their age and foodLevel are not randomized.
     *
     * @param  speciesInSimulation (List<Species>) List of Species objects in the simulation for the newborns to be added to it.
     */
    protected void reproduce(List<Species> speciesInSimulation)
    {
        Field field = getField();
        if (field != null) {
            reproduceAtFreeLocations(speciesInSimulation, field.getFreeAdjacentLocations(getLocation()), numberOfBirths());
        }
    }

    /**
     * Create a newborn animal of the current species.
     *
     * @param location the location at which the newborn should appear.
     * @return the newborn species.
     */
    @Override
    protected Species createOffspring(Location location)
    {
        return new Animal(getField(), location, getName(), getMaximumTemperature(), getMinimumTemperature(), getNutritionalValue(), getReproductionProbability(), maxAge, breedingAge, maxLitterSize, false, hibernates, isNocturnal);
    }

    /**
     * Move to a free adjacent cell if one exists, otherwise die from overcrowding.
     */
    protected void moveToFreeAdjacentLocationOrDie()
    {
        moveToLocationOrDie(getFreeAdjacentLocation());
    }

    /**
     * Look up a free adjacent location around the current animal.
     *
     * @return a free adjacent location, or null if none exists.
     */
    protected Location getFreeAdjacentLocation()
    {
        return getField().freeAdjacentLocation(getLocation());
    }

    /**
     * Reproduce if the current neighboring animals allow it.
     *
     * @param speciesInSimulation the list receiving newborn species.
     * @param neighboringAnimalsList the neighboring animals.
     */
    protected final void reproduceIfPossible(List<Species> speciesInSimulation, List<Animal> neighboringAnimalsList)
    {
        if (canReproduce(neighboringAnimalsList)) {
            reproduce(speciesInSimulation);
        }
    }

    /**
     * Move to the given location if possible, otherwise die from overcrowding.
     *
     * @param newLocation the destination location, or null if none is available.
     */
    protected void moveToLocationOrDie(Location newLocation)
    {
        if (newLocation != null) {
            setLocation(newLocation);
        }
        else {
            setDead();
        }
    }

    /**
     * Returns a random boolean to randomize the sex of newborns.
     *
     * @return true if female, otherwise false (male)
     */
    private boolean randomSex()
    {
        return Math.random() <= 0.5;
    }
    
    /**
     * Return a random foodLevel more than the animal's nutritional value divided by half.
     *
     * @return (int) the foodLevel.
     */
    private int randomFoodLevel()
    {
        int lowBound = getNutritionalValue()/2;
        return rand.nextInt(lowBound) + lowBound;
    }

    /**
     * Generate a number representing the number of births, if it can breed.
     *
     * @return (int) The number of births (can be zero).
     */
    protected int numberOfBirths()
    {
        int births = 0;
        if(canGiveBirth() && rand.nextDouble() <= getReproductionProbability()) {
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
     * Check if the animal should be in hibernation.
     *
     * Change inHibernation to true if the animal can hibernate
     * and the current temperature is less than or equal the
     * minimum temperature of the animal + 5
     *
     * @param currentTemperature (int) the current temperature of the simulation
     */
    protected void checkHibernation(int currentTemperature)
    {
        if (hibernates && currentTemperature <= getMinimumTemperature() + 5)  {
            inHibernation = true;
        }
        else {
            inHibernation = false;
            hiberSteps = 0;
        }
    }

    /**
     * @return (int) The maximum age to which an animal can live
     */
    protected int getMaxAge ()
    {
        return maxAge;
    }

    /**
     * @return (int) The age at which an animal can start to breed
     */
    protected int getBreedingAge ()
    {
        return breedingAge;
    }

    /**
     * @return (int) The maximum number of births at once
     */
    protected int getMaxLitterSize ()
    {
        return maxLitterSize;
    }

    /**
     * @return (boolean) true if the animal hibernates
     */
    protected boolean getHibernates()
    {
        return hibernates;
    }

    /**
     * @return (boolean) true if the animal is active at night
     */
    protected boolean getIsNocturnal()
    {
        return isNocturnal;
    }

    /**
     * Increments the animal's food level by a given number (the nutritional value of the food he just ate).
     * It is public because a predator's food level can be incremented another predator in the case that it attacks it.
     *
     * @param value (int) the number to increment foodLevel by.
     */
    public void incrementFoodLevel(int value) {
        foodLevel += value;
    }

}
