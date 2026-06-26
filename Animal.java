import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

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

    // An animal is either male or female 
    protected enum Gender {
        MALE,
        FEMALE
    }


    // Declaring abstract methods to obtain fields used by subclasses
    // These methods have been declared in order to use common methods in the Animal class, reducing repeatability
    protected abstract int getBreedingAge();
    protected abstract int getMaxLitterSize();
    protected abstract double getBreedingProbability();
    protected abstract int getMaxAge();
    protected abstract int getMaxFoodLevel();
    protected abstract Set<Class<? extends Organism>> getDiet();


    /**
     * Create a new animal at location in field.
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param randomAge The animal's random starting age.
     * @param sex The animal's gender. 
     */
    public Animal(SimulationContext context, Field field, Location location, boolean randomAge, Gender sex)
    {
        super(context, field, location);
        this.sex = sex;
        Random rand = getSimulationContext().getRandomProvider().getRandom();
        if(randomAge) {
            this.age = rand.nextInt(getMaxAge());
            foodLevel = rand.nextInt(getMaxFoodLevel());
        }
        else {
            this.age = 0;
            foodLevel = getMaxFoodLevel();
        }
    }

    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     * @param environment The environment that the animal resides in.
     */
    public void act(List<Actor> newAnimals, Environment environment)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newAnimals, environment);
            move(environment);
        }
    }

    /**
     * Look for food adjacent to the current location.
     * Only the first food is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    protected MovementService.MovementDecision findFood(Environment environment)
    {
        Field field = getField();
        SimulationContext context = getSimulationContext();
        MovementService movementService = context.getMovementService();
        DiseaseService diseaseService = context.getDiseaseService();
        List<Location> adjacent = movementService.getAdjacentLocations(field, getLocation());
        diseaseService.applyAdjacentExposure(this, adjacent, field);
        return movementService.resolveAnimalMovement(this, environment, adjacent);
    }

    /**
     * Check whether this animal is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newAnimals A list to return newly born animals.
     * @param environment The environment that the animal resides in. 
     */
    protected void giveBirth(List<Actor> newAnimals, Environment environment)
    {
        Field field = getField();
        List<Location> free = getSimulationContext().getMovementService().getFreeAdjacentLocations(field, getLocation());
        int births = breed();
        for(int b = 0; b < births && !free.isEmpty(); b++) {
            Location loc = free.remove(0);
            Gender sex = getSimulationContext().getRandomProvider().getRandom().nextBoolean() ? Gender.MALE : Gender.FEMALE;
            newAnimals.add(createYoung(field, loc, sex));
        }
    }

    protected Animal createYoung(Field field, Location location, Gender sex)
    {
        return getSimulationContext().getOrganismFactory()
                .createOffspring(getClass().asSubclass(Animal.class), field, location, sex);
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
        Random rand = getSimulationContext().getRandomProvider().getRandom();
        int births = 0;
        if(canBreed() && rand.nextDouble() <= getBreedingProbability()) {
            births = rand.nextInt(getMaxLitterSize()) + 1;
            Animal mate = (Animal) getPotentialMates().get(rand.nextInt(getPotentialMates().size()));
            getSimulationContext().getDiseaseService().applySexualExposure(this, mate);
        }
        return births;
    }

    /**
     * Returns true if the animal is able to breed.
     * Returns false if otherwise. 
     */
    protected boolean canBreed()
    {
        return age >= getBreedingAge() && !getPotentialMates().isEmpty();
    }

    /**
     * Returns a list of potential mates. Mates have to be
     * of a opposite gender. 
     * @return List<Organism> A list of potential mates.
     */
    protected List<Organism> getPotentialMates()
    {
        if(getField() != null){
            return getSimulationContext().getMovementService().findPotentialMates(this);
        }
        return new ArrayList<>();
    }


    /**
     * Increase the age. This could result in the animal's death.
     */
    protected void incrementAge()
    {
        this.age++;
        if(this.age > getMaxAge()) {
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

    private void move(Environment environment)
    {
        MovementService.MovementDecision movementDecision = findFood(environment);
        if(movementDecision.consumedOrganism() != null) {
            consume(movementDecision.consumedOrganism());
        }

        if(movementDecision.targetLocation() != null) {
            getSimulationContext().getMovementService().moveOrganism(this, movementDecision.targetLocation());
        }
        else if(movementDecision.overcrowded()) {
            setDead();
        }
    }

    private void consume(Organism food)
    {
        getSimulationContext().getDiseaseService().applyFoodborneExposure(this, food);
        if(food.isAlive()) {
            food.setDead();
            int newFoodLevel = foodLevel + food.getFoodValue();
            foodLevel = Math.min(newFoodLevel, getMaxFoodLevel());
        }
    }

}
