import java.util.*;
import java.util.stream.Collectors;

/**
 * A class representing shared characteristics of animals.
 *
 * Concrete species supply their fixed characteristics as an {@link AnimalTraits}
 * profile and implement {@link #createOffspring} to produce newborns; all of the
 * common behaviour (ageing, hunger, movement, feeding, breeding and disease) lives
 * here.
 *
 * @version 2022.03.02
 */
public abstract class Animal extends Organism implements Actor
{
    // Fixed characteristics of this animal's species.
    protected final AnimalTraits traits;

    protected int age;
    protected int foodLevel;
    protected Gender sex;

    protected static final Random rand = Randomizer.getRandom();

    // An animal is either male or female
    protected enum Gender {
        MALE,
        FEMALE
    }
    // An animal's chance of contracting a disease at birth
    private double RANDOM_CONTRACTION_RATE = 0.002;


    /**
     * Create a new animal at location in field.
     *
     * @param traits The fixed characteristics of this animal's species.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param randomAge The animal's random starting age.
     * @param sex The animal's gender.
     */
    public Animal(AnimalTraits traits, Field field, Location location, boolean randomAge, Gender sex)
    {
        super(field, location);
        this.traits = traits;
        this.sex = sex;
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

    /**
     * The food value an animal provides when eaten.
     */
    protected int getFoodValue()
    {
        return traits.getFoodValue();
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
            // Move towards a source of food if found.
            Location newLocation = findFood(environment);
            if(newLocation == null) {
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }

            // list of adjacent locations that contain an instance of Grass
            List<Location> adjacentGrassSpots = getField().adjacentLocationsWithSpecies(getLocation(), Grass.class);

            if(newLocation != null) {
                // See if it was possible to move.
                setLocation(newLocation);
            }
            else if (adjacentGrassSpots.size() > 0) {
                // if there is grass adjacent to the animal, clear the current location
                // and move to a random location that contained grass
                getField().clear(getLocation());
                setLocation(adjacentGrassSpots.get(rand.nextInt(adjacentGrassSpots.size())));
            }
            else if (diesFromOvercrowding()) {
                // Overcrowding
                setDead();
            }

            if(isDiseased() && getDisease().getLethalityRate() <= rand.nextDouble()){
                // every step, check if the Animal is diseased
                // if it is Diseased, and a random double is less than the lethality rate, the Animal dies
                setDead();
            }

        }
    }

    /**
     * Look for food adjacent to the current location, taking the environment
     * into account. By default the environment is ignored; species whose
     * foraging depends on conditions (e.g. weather) override this.
     * @param environment The environment that the animal resides in.
     * @return Where food was found, or null if it wasn't.
     */
    protected Location findFood(Environment environment)
    {
        return findFood();
    }

    /**
     * Look for food adjacent to the current location. While scanning, the animal
     * may catch a contact disease from a neighbour; it then eats the first
     * organism in its diet (if still hungry).
     * @return Where food was found, or null if it wasn't.
     */
    protected Location findFood()
    {
        // The adjacency is computed once and shared so both passes scan the same
        // (already shuffled) order.
        List<Location> adjacent = getField().adjacentLocations(getLocation());
        maybeContractContactDisease(adjacent);
        return eatFromAdjacent(adjacent);
    }

    /**
     * Possibly catch a non-CONTACT-type disease from the first diseased
     * neighbour encountered (hunters excluded).
     * @param adjacent The adjacent locations to scan.
     */
    private void maybeContractContactDisease(List<Location> adjacent)
    {
        Field field = getField();
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
    }

    /**
     * Eat the first adjacent organism that is in this animal's diet, provided it
     * is still hungry. Foodborne disease may be contracted from the meal.
     * @param adjacent The adjacent locations to scan.
     * @return Where food was found, or null if it wasn't.
     */
    private Location eatFromAdjacent(List<Location> adjacent)
    {
        Field field = getField();
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
     * Whether this animal dies when it cannot find anywhere to move (i.e. it is
     * overcrowded). Most animals do; apex species may override this to survive.
     * @return true if overcrowding is fatal for this species.
     */
    protected boolean diesFromOvercrowding()
    {
        return true;
    }

    /**
     * Check whether this animal is to give birth at this step.
     * New births are made into free adjacent locations.
     * @param newAnimals A list to return newly born animals.
     * @param environment The environment that the animal resides in.
     */
    protected void giveBirth(List<Actor> newAnimals, Environment environment)
    {
        // New animals are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            newAnimals.add(createOffspring(field, loc));
        }
    }

    /**
     * Create a single newborn of this species at the given location.
     * @param field The field the newborn is placed in.
     * @param location The location for the newborn.
     * @return The newly created animal.
     */
    protected abstract Animal createOffspring(Field field, Location location);

    /**
     * Returns true if the animal is awake or not.
     */
    public boolean isAwake(Environment environment)
    {
        if(traits.isNocturnal()){
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
        if(canBreed() && rand.nextDouble() <= traits.getBreedingProbability()) {
            births = rand.nextInt(traits.getMaxLitterSize()) + 1;
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
        return (age >= traits.getBreedingAge() && getPotentialMates().size() > 0);
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
