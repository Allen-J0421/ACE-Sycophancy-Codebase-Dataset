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
    // foodLevel cap as a multiple of nutritional value — above this threshold the animal stops eating
    private static final double FOOD_LEVEL_FULL_MULTIPLIER = 1.5;

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
    public void act(List<Species> newSpecies, boolean isNight, int temperature, boolean yearPassed, boolean isSpring, boolean seasonChanged)
    {
        // 1)
        if (yearPassed) {
            incrementAge();
        }

        // 2)
        if(isAlive())
        {
            // i)
            checkHibernation(temperature);
            
            // ii)
            if (inHibernation)
            {
                // a)
                if (hiberSteps % STAY_STEPS == 0)   {
                    makeMove(newSpecies);
                    incrementHunger();
                }
                // b)
                incrementHiberSteps();
            }
            // iii)
            else if (! survivesTemperature(temperature))
            {
                setDead();
            }
            // iv)
            else
            {
                // a)
                if (isNight && isNocturnal) {
                    makeMove(newSpecies);
                }
                // b)
                else if (! isNight) {
                    makeMove(newSpecies);
                }
                if  (! isNight) {
                    incrementHunger();
                }
            }
        }
    }

    /**
     * An animal's movement. It first tries to reproduce, then to eat if a plant is in one of the neighboring cells and finally to move
     * if an adjacent cell is available. If no adjacent cell is available, it dies of overcrowding.
     *
     * @param newSpecies (List<Species>) A list to receive newly born animals.
     */
    protected void makeMove(List<Species> newSpecies)
    {
        ArrayList<Animal> neighboringAnimalsList = getNeighboringAnimalsList();

        if (canReproduce(neighboringAnimalsList)) {
            reproduce(newSpecies);
        }

        // Eats if it is possible
        if (isNotFull()) {
            findFoodAndEat();
        }

        // Find a free location in adjacent cells
        Location newLocation = getField().freeAdjacentLocation(getLocation());

        // See if it was possible to move.
        if(newLocation != null) {
            setLocation(newLocation);
        }
        else {
            // Overcrowding.
            setDead();
        }
    }

    /**
     * Returns a list of animals located in neighboring cells.
     *
     * @return (ArrayList) list of neighboring animals
     */
    protected ArrayList<Animal> getNeighboringAnimalsList()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> locationIterator = adjacent.iterator();

        ArrayList<Animal> neighboringAnimals = new ArrayList<>();
        while (locationIterator.hasNext())
        {
            Location where = locationIterator.next();
            Species species = field.getObjectAt(where);

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
        return foodLevel < getNutritionalValue() * FOOD_LEVEL_FULL_MULTIPLIER;
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
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();

        while(it.hasNext())
        {
            Location where = it.next();
            Species species = field.getObjectAt(where);
            if(species instanceof Plant)
            {
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
     * @param  neighboringAnimalsList (ArrayList<Animal>) Array List of the Animal objects located in neighboring cells.
     * @return (boolean) if animal can reproduce.
     *
     */
    protected boolean canReproduce(ArrayList<Animal> neighboringAnimalsList)
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
        if (field != null)
        {
            List<Location> free = field.getFreeAdjacentLocations(getLocation());
            int births = numberOfBirths();
            for(int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Animal young = new Animal(field, loc, getName(), getMaximumTemperature(), getMinimumTemperature(), getNutritionalValue(), getReproductionProbability(), maxAge, breedingAge, maxLitterSize,false, hibernates, isNocturnal);
                speciesInSimulation.add(young);
            }
        }
    }

    /**
     * Returns a random boolean to randomize the sex of newborns.
     *
     * @return true if female, otherwise false (male)
     */
    private boolean randomSex()
    {
        return rand.nextBoolean();
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
