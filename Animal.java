import java.util.*;
import java.util.stream.Collectors;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 2022.03.02
 */
public abstract class Animal extends Organism implements Actor
{
    protected int age;
    protected int foodLevel;
    protected Gender sex;

    protected static final Random rand = Randomizer.getRandom();

    // An animal's chance of contracting a disease at birth
    private static final double RANDOM_CONTRACTION_RATE = 0.002;

    /**
     * Returns this species' immutable configuration constants.
     * Each concrete subclass provides a static AnimalStats instance.
     */
    protected abstract AnimalStats getStats();

    @Override
    protected int FOOD_VALUE() { return getStats().getFoodValue(); }

    /**
     * Create a new animal at location in field.
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param randomAge If true, start with a random age and food level.
     * @param sex The animal's gender.
     */
    public Animal(Field field, Location location, boolean randomAge, Gender sex)
    {
        super(field, location);
        this.sex = sex;
        if (randomAge) {
            this.age = rand.nextInt(getStats().getMaxAge());
            foodLevel = rand.nextInt(getStats().getMaxFoodLevel());
        } else {
            this.age = 0;
            foodLevel = getStats().getMaxFoodLevel();
        }
        randomlyContractDisease();
    }

    /**
     * Make this animal act - that is: make it do whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     * @param environment The environment that the animal resides in.
     */
    public void act(List<Actor> newAnimals, Environment environment)
    {
        if (!isAwake(environment)) {
            return;
        }
        if (isDiseased() && getDisease().getPropagationRate() <= rand.nextDouble()) {
            setDead();
            return;
        }
        randomlyContractDisease();
        incrementAge();
        incrementHunger();
        if (isAlive()) {
            giveBirth(newAnimals, environment);
            Location newLocation = findFood();
            if (newLocation == null) {
                newLocation = getField().freeAdjacentLocation(getLocation());
            }

            List<Location> adjacentGrassSpots = getField().adjacentLocationsWithSpecies(getLocation(), Grass.class);

            if (newLocation != null) {
                setLocation(newLocation);
            } else if (adjacentGrassSpots.size() > 0) {
                getField().clear(getLocation());
                setLocation(adjacentGrassSpots.get(rand.nextInt(adjacentGrassSpots.size())));
            } else {
                // Overcrowding
                setDead();
            }

            if (isDiseased() && getDisease().getLethalityRate() <= rand.nextDouble()) {
                setDead();
            }
        }
    }

    /**
     * Look for food adjacent to the current location.
     * Only the first food item is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    protected Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());

        DiseaseTransmission.scanForProximity(this, adjacent, field);

        for (Location where : adjacent) {
            Object obj = field.getObjectAt(where);
            if (obj != null && getStats().getDiet().contains(obj.getClass())) {
                Organism food = (Organism) obj;
                DiseaseTransmission.tryFoodborne(this, food);
                if (food.isAlive()) {
                    food.setDead();
                    foodLevel = Math.min(foodLevel + food.FOOD_VALUE(), getStats().getMaxFoodLevel());
                    return where;
                }
                return where;
            }
        }
        return null;
    }

    // --- Reproduction ---

    /**
     * Creates a newborn instance of this animal species.
     * Each concrete subclass returns its own type.
     */
    protected abstract Animal createOffspring(Field field, Location location, Gender sex);

    /**
     * Check whether this animal is to give birth at this step.
     * New births will be made into free adjacent locations.
     */
    protected void giveBirth(List<Actor> newAnimals, Environment environment)
    {
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for (int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Gender offspringSex = Randomizer.getRandomSex();
            newAnimals.add(createOffspring(field, loc, offspringSex));
        }
    }

    /**
     * Returns the number of births this step, accounting for mate availability
     * and disease transmission during mating.
     */
    protected int breed()
    {
        int births = 0;
        if (canBreed() && rand.nextDouble() <= getStats().getBreedingProbability()) {
            births = rand.nextInt(getStats().getMaxLitterSize()) + 1;
            List<Organism> mates = getPotentialMates();
            Animal mate = (Animal) mates.get(rand.nextInt(mates.size()));
            DiseaseTransmission.trySexual(this, mate);
        }
        return births;
    }

    /** Returns true if this animal meets the age and mate requirements for breeding. */
    protected boolean canBreed()
    {
        return age >= getStats().getBreedingAge() && getPotentialMates().size() > 0;
    }

    /** Returns adjacent organisms of the same species and opposite sex. */
    protected List<Organism> getPotentialMates()
    {
        List<Organism> potentialMates = new ArrayList<>();
        if (getField() != null) {
            potentialMates = getField().adjacentLocations(getLocation()).stream()
                    .map(s -> getField().getObjectAt(s))
                    .filter(s -> s != null)
                    .filter(s -> s.getClass().equals(this.getClass()))
                    .filter(s -> this.sex != ((Animal) s).sex)
                    .map(s -> (Organism) s)
                    .collect(Collectors.toList());
        }
        return potentialMates;
    }

    // --- Schedule ---

    /** Returns true if the animal is currently awake (respects nocturnal/diurnal schedule). */
    public boolean isAwake(Environment environment)
    {
        if (getStats().isNocturnal()) {
            return !environment.getTime().isDay();
        }
        return environment.getTime().isDay();
    }

    // --- Lifecycle ---

    protected void randomlyContractDisease()
    {
        if (rand.nextDouble() <= RANDOM_CONTRACTION_RATE) {
            setDisease(new Disease());
        }
    }

    /** Increases age; kills the animal if it exceeds its maximum. */
    protected void incrementAge()
    {
        age++;
        if (age > getStats().getMaxAge()) {
            setDead();
        }
    }

    /** Decreases food level; kills the animal if it reaches zero. */
    protected void incrementHunger()
    {
        foodLevel--;
        if (foodLevel <= 0) {
            setDead();
        }
    }
}
