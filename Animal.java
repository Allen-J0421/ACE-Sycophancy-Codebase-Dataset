import java.util.*;
import java.util.stream.Collectors;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 2022.03.02
 */
public abstract class Animal extends MobileForager implements Edible
{
    private static final TargetAcquisitionPolicy TARGET_POLICY = new AnimalTargetAcquisitionPolicy();

    protected int age;
    protected boolean isNocturnal;
    protected int foodLevel;
    protected Gender sex;

    // An animal is either male or female 
    protected enum Gender {
        MALE,
        FEMALE
    }
    // An animal's chance of contracting a disease at birth
    private static final double RANDOM_CONTRACTION_RATE = 0.002;


    // Declaring abstract methods to obtain fields used by subclasses.
    // These methods are implemented by each species.
    protected abstract double BREEDING_AGE();
    protected abstract int MAX_LITTER_SIZE();
    protected abstract double BREEDING_PROBABILITY();
    protected abstract int MAX_AGE();
    protected abstract int MAX_FOOD_LEVEL();
    protected abstract Set<Class<?>> DIET();


    /**
     * Create a new animal at location in field.
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param randomAge The animal's random starting age.
     * @param sex The animal's gender. 
     */
    public Animal(Field field, Location location, boolean randomAge, Gender sex)
    {
        super(field, location);
        this.sex = sex;
        if(randomAge) {
            this.age = rand.nextInt(MAX_AGE());
            foodLevel = rand.nextInt(MAX_FOOD_LEVEL());
        }
        else {
            this.age = 0;
            foodLevel = MAX_FOOD_LEVEL();
        }
        randomlyContractDisease();
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
            giveBirth(newAnimals, environment);
            forageAndMove();

            if(isDiseased() && getDisease().getLethalityRate() <= rand.nextDouble()){
                // every step, check if the Animal is diseased
                // if it is Diseased, and a random double is less than the lethality rate, the Animal dies
                setDead();
            }

        }
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
     * Check whether this animal is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newAnimals A list to return newly born animals.
     * @param environment The environment that the animal resides in. 
     */
    protected abstract void giveBirth(List<Actor> newAnimals, Environment environment);

    @Override
    protected Location locateTargetLocation()
    {
        return TARGET_POLICY.acquireTarget(this);
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
     * Generates a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero)
     */
    protected int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY()) {
            births = rand.nextInt(MAX_LITTER_SIZE()) + 1;
            Animal mate = (Animal) getPotentialMates().get(rand.nextInt(getPotentialMates().size()));
            if(mate.isDiseased() && mate.getDisease().getDiseaseType() == DiseaseType.SEXUAL){
                this.setDisease(mate.getDisease());
            }
        }
        return births;
    }

    /**
     * Returns true if the animal is able to breed.
     * Returns false if otherwise. 
     */
    protected boolean canBreed()
    {
        return (age >= BREEDING_AGE() && getPotentialMates().size() > 0);
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


    /**
     * Increase the age. This could result in the animal's death.
     */
    protected void incrementAge()
    {
        this.age++;
        if(this.age > MAX_AGE()) {
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
     * Returns the food value of this animal.
     */
    public abstract int getFoodValue();

}
