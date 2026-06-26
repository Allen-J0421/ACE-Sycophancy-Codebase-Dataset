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
    protected boolean isNocturnal;
    protected int foodLevel;
    protected Gender sex;
    private final AnimalTraits traits;
    private MovementStrategy movementStrategy;
    private ReproductionStrategy reproductionStrategy;

    protected static final Random rand = Randomizer.getRandom();
    private static final MovementStrategy DEFAULT_MOVEMENT_STRATEGY = new PredatorTrackingMovementStrategy();
    private static final ReproductionStrategy DEFAULT_REPRODUCTION_STRATEGY = new StandardSexualReproductionStrategy();

    // An animal is either male or female 
    protected enum Gender {
        MALE,
        FEMALE
    }
    // An animal's chance of contracting a disease at birth
    private static final double RANDOM_CONTRACTION_RATE = 0.002;


    /**
     * Create a new animal at location in field.
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param randomAge The animal's random starting age.
     * @param sex The animal's gender. 
     * @param traits Species-specific animal configuration.
     */
    public Animal(Field field, Location location, boolean randomAge, Gender sex, AnimalTraits traits)
    {
        this(field, location, randomAge, sex, traits, DEFAULT_MOVEMENT_STRATEGY, DEFAULT_REPRODUCTION_STRATEGY);
    }

    /**
     * Create a new animal at location in field.
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param randomAge The animal's random starting age.
     * @param sex The animal's gender.
     * @param traits Species-specific animal configuration.
     * @param movementStrategy Movement behavior for this animal.
     */
    public Animal(Field field, Location location, boolean randomAge, Gender sex, AnimalTraits traits, MovementStrategy movementStrategy)
    {
        this(field, location, randomAge, sex, traits, movementStrategy, DEFAULT_REPRODUCTION_STRATEGY);
    }

    /**
     * Create a new animal at location in field.
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param randomAge The animal's random starting age.
     * @param sex The animal's gender.
     * @param traits Species-specific animal configuration.
     * @param reproductionStrategy Reproduction behavior for this animal.
     */
    public Animal(Field field, Location location, boolean randomAge, Gender sex, AnimalTraits traits, ReproductionStrategy reproductionStrategy)
    {
        this(field, location, randomAge, sex, traits, DEFAULT_MOVEMENT_STRATEGY, reproductionStrategy);
    }

    /**
     * Create a new animal at location in field.
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param randomAge The animal's random starting age.
     * @param sex The animal's gender.
     * @param traits Species-specific animal configuration.
     * @param movementStrategy Movement behavior for this animal.
     * @param reproductionStrategy Reproduction behavior for this animal.
     */
    public Animal(Field field, Location location, boolean randomAge, Gender sex, AnimalTraits traits, MovementStrategy movementStrategy, ReproductionStrategy reproductionStrategy)
    {
        super(field, location);
        this.sex = sex;
        this.traits = traits;
        setMovementStrategy(movementStrategy);
        setReproductionStrategy(reproductionStrategy);
        if(randomAge) {
            this.age = rand.nextInt(traits.getMaxAge());
            foodLevel = rand.nextInt(traits.getMaxFoodLevel());
        }
        else {
            this.age = 0;
            foodLevel = traits.getMaxFoodLevel();
        }
        randomlyContractDisease();
    }

    @Override
    protected int getFoodValue()
    {
        return traits.getFoodValue();
    }

    /**
     * Replace this animal's movement behavior.
     */
    public void setMovementStrategy(MovementStrategy movementStrategy)
    {
        this.movementStrategy = Objects.requireNonNull(movementStrategy, "movementStrategy");
    }

    /**
     * Replace this animal's reproduction behavior.
     */
    public void setReproductionStrategy(ReproductionStrategy reproductionStrategy)
    {
        this.reproductionStrategy = Objects.requireNonNull(reproductionStrategy, "reproductionStrategy");
    }

    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     * @param environment The environment that the animal resides in.
     */
    public void act(List<Actor> newAnimals, Environment environment)
    {
        randomlyContractDisease();
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            reproductionStrategy.reproduce(this, newAnimals, environment);
            movementStrategy.move(this, environment);

            if(isDiseased() && getDisease().getLethalityRate() <= rand.nextDouble()){
                // every step, check if the Animal is diseased
                // if it is Diseased, and a random double is less than the lethality rate, the Animal dies
                setDead();
            }

        }
    }

    /**
     * Look for food adjacent to the current location.
     * Only the first food is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    protected Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        for(Location loc : adjacent){
            if(field.getObjectAt(loc) != null && !(field.getObjectAt(loc) instanceof Hunter)){
                Organism organism = (Organism) field.getObjectAt(loc);
                if (organism.isDiseased() && organism.getDisease().getDiseaseType() != DiseaseType.CONTACT && organism.getDisease().getPropagationRate() <= rand.nextDouble()){
                    // contracts the first contact disease it encounters amongst the adjacent animals
                    this.setDisease(organism.getDisease());
                    break;
                }
            }
        }
        Iterator<Location> it = adjacent.iterator();
        // only eats if it's not full (food level less than max)
        while(it.hasNext() && foodLevel <= traits.getMaxFoodLevel()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal != null && traits.getDiet().contains(animal.getClass()))
            {
                Organism food = (Organism) animal;
                if (food.isDiseased() &&  food.getDisease().getDiseaseType() == DiseaseType.FOODBORNE && food.getDisease().getPropagationRate() <= rand.nextDouble()) 
                {
                    // contracts disease from food if it has a disease and that disease is foodborne
                    setDisease(food.getDisease());
                }
                if(food.isAlive()) 
                {
                    food.setDead();
                    int newFoodLevel = foodLevel + food.getFoodValue();

                    // caps the food level at the maximum
                    foodLevel = Math.min(newFoodLevel, traits.getMaxFoodLevel());

                    return where;
                }
                return where;
            }
        }
        return null;
    }

    /**
     * Look for food in the current environment.
     * Subclasses can override this to account for weather or other context.
     */
    protected Location findFood(Environment environment)
    {
        return findFood();
    }

    /**
     * Returns true if an animal contracts a disease at birth.
     * Returns false if otherwise. 
     */
    protected void randomlyContractDisease()
    {
        if(rand.nextDouble() <= RANDOM_CONTRACTION_RATE) {
            setDisease(new Disease());
        }
    }

    /**
     * Create a newborn animal of the subclass species.
     */
    protected abstract Animal createOffspring(Field field, Location location, Gender sex);

    /**
     * Whether the animal dies when unable to move.
     */
    protected boolean diesFromOvercrowding()
    {
        return true;
    }

    /**
     * Returns true if the animal is awake or not.
     */
    public boolean isAwake(Environment environment)
    {
        if(isNocturnal){
            return !environment.getTime().isDay();
        }
        return environment.getTime().isDay();
    }

    /**
     * Returns a list of potential mates. Mates have to be
     * of a opposite gender. 
     * @return List<Organism> A list of potential mates.
     */
    protected List<Organism> getPotentialMates()
    {
        List<Organism> potentialMates = new ArrayList<>();
        if(getField() != null){
            potentialMates = getField().adjacentLocations(getLocation()).stream()
                    .map(s -> getField().getObjectAt(s))
                    .filter(s -> s != null)
                    .filter(s -> s.getClass().equals(this.getClass()))
                    .filter(s -> this.sex != ((Animal) s).sex)
                    .map(s -> (Organism) s)
                    .collect(Collectors.toList());
            // stream counts the number of Animals of the same species, as well as opposite gender
        }
        return potentialMates;
    }

    int getAge()
    {
        return age;
    }

    int getBreedingAge()
    {
        return traits.getBreedingAge();
    }

    double getBreedingProbability()
    {
        return traits.getBreedingProbability();
    }

    int getMaxLitterSize()
    {
        return traits.getMaxLitterSize();
    }

    MovementStrategy getMovementStrategy()
    {
        return movementStrategy;
    }

    ReproductionStrategy getReproductionStrategy()
    {
        return reproductionStrategy;
    }


    /**
     * Increase the age. This could result in the animal's death.
     */
    protected void incrementAge()
    {
        this.age++;
        if(this.age > traits.getMaxAge()) {
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

}
