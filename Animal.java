import java.util.List;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 27.02.22
 */
public abstract class Animal extends Actor
{
    protected int foodLevel;
    protected boolean female;
    protected boolean nocturnal;

    /**
     * Create a new animal at location in field with time as well.
     * With a set that will add any disease that it catches
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param time The time in the simulation
     */
    public Animal(Time time, Field field, Location location)
    {
        super(time, field, location);

    }

    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * Checks if the time of day matches if the animal is nocturnal or not,
     * then attempts to find food and move.
     * @param newAnimals A list to receive newly born animals.
     * @param weather The weather condition of the simulation.
     */
    public void act(List<Actor> newAnimals, WeatherCond weather)
    {
        super.act(newAnimals,weather);
        incrementHunger();
        if(isAlive()&&(getTime().isDay() != nocturnal)) {
            giveBirth(newAnimals);            
            // Move towards a source of food if found.
            Location newLocation = null;
            if (getField().getWeatherAttributeValueAt("visibility", getLocation()) > 0.4){
                newLocation = findFood();
            }
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(this, getLocation());
            }
            // See if it was possible to move.
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }

    /**
     * Make this animal more hungry. This could result in the animal's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Checks if the animal is female and looks for a mate
     * @param newAnimals The List of new animals that have been born to add to the simulator List of actors
     */
    protected void giveBirth(List<Actor> newAnimals)
    {
        // New animals are born into adjacent locations.
        // Get a list of adjacent free locations.
        if (female&&findMate()) {
            super.giveBirth(newAnimals);
        }
    }

    /**
     * Searches for a mate to breed with.
     * Searches first for an adjacent animal of the same dynamic type,
     * then if that animal is male.
     * @return true if a mate is found.
     */
    protected boolean findMate()
    {
        Field field = getField();
        for(Location where : field.adjacentLocations(getLocation())) {
            Object actor = field.getObjectAt(where);
            if (actor instanceof Animal) {
                Animal animal = (Animal) actor;
                if (animal.getClass() == getClass() && !animal.female){
                    giveDiseases(animal);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Transfer diseases between mates. Snapshot own diseases first to avoid
     * re-sending diseases that were just received from the mate.
     * @param mate The male mate that the female breeds with.
     */
    protected void giveDiseases(Animal mate)
    {
        Set<Disease> ownDiseasesSnapshot = new HashSet<>(setDiseases);
        for (Disease disease: mate.getActorDiseaseSet()){
            if (disease.isSpreadByBirth()){
                contractDisease(disease);
            }
        }
        for (Disease disease: ownDiseasesSnapshot){
            if (disease.isSpreadByBirth()){
                mate.contractDisease(disease);
            }
        }
    }

    /**
     * Look for prey adjacent to the current location.
     * Only the first live prey is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    protected Location findFood()
    {
        Field field = getField();
        for(Location where : field.adjacentLocations(getLocation())) {
            if (canAccessLocation(where)) {
                Object actor = field.getObjectAt(where);
                if (actor instanceof Actor) {
                    Actor prey = (Actor) actor;
                    Integer foodValue = getPreyFoodValuesMap().get(prey.getClass());
                    if(foodValue != null && prey.isAlive()) {
                        for (Disease disease: prey.getActorDiseaseSet()){
                            if (disease.isSpreadByEating() && disease.getActorsAffectedMap().containsKey(getClass())){
                                contractDisease(disease);
                            }
                        }
                        prey.setDead();
                        foodLevel = Math.min(foodLevel + foodValue, getMaxFood());
                        return where;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns how many animals are produced during birth.
     * Uses the abstract breeding parameters defined by each subclass.
     * @return The number of births (may be zero).
     */
    public int breed()
    {
        Random rand = Randomizer.getRandom();
        if(canBreed() && rand.nextDouble() <= getBreedingProbability()) {
            return rand.nextInt(getMaxLitterSize()) + 1;
        }
        return 0;
    }

    private boolean canBreed()
    {
        return age >= getBreedingAge();
    }

    abstract protected int getBreedingAge();
    abstract protected double getBreedingProbability();
    abstract protected int getMaxLitterSize();

    abstract protected Animal spawnOffspring(Location loc, Set<Disease> parentDiseases);
    abstract protected Animal spawnRandom(Location loc);

    /**
     * Returns the Map of the prey of the animal and how much food they provide it
     * @return The Map of the prey of the animal and how much food they provide it
     */
    abstract protected Map<Class<? extends Actor>, Integer> getPreyFoodValuesMap();

    /**
     * Returns the max food value that the animal can have
     * @return The max food value that the animal can have
     */
    abstract protected int getMaxFood();
}