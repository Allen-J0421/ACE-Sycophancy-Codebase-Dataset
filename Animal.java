import java.util.List;
import java.util.Iterator;
import java.util.Map;
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
            if (getField().getWeatherAttributeValueAt(WeatherAttribute.VISIBILITY, getLocation()) > 0.4){
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
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()){
            Location where = it.next();
            Object actor = field.getObjectAt(where);
            if (actor instanceof Animal) {
                Animal animal = (Animal) actor;
                if (animal.getActorName().equals(getActorName())&& !animal.female){
                    giveDiseases(animal);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Transfer diseases from the male mate to the female and vice versa.
     * @param mate The male mate that the female breeds with.
     */
    protected void giveDiseases(Animal mate)
    {
        for (Disease disease: mate.setDiseases){
            if (disease.isSpreadByBirth()){
                setDiseases.add(disease);
            }
        }
        for (Disease disease: setDiseases){
            if (disease.isSpreadByBirth()){
                mate.getActorDiseaseSet().add(disease);
            }
        }
    }

    /**
     * Look for rabbits adjacent to the current location.
     * Only the first live rabbit is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    protected Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            if(canOccupy(where)) {
                Object actor = field.getObjectAt(where);
                if (actor instanceof Actor) {
                    Actor prey = (Actor) actor;
                    Integer foodValue = getPreyFoodValuesMap().get(prey.getActorName());
                    if(foodValue != null && prey.isAlive()) {
                        for (Disease disease: prey.setDiseases){
                            if (disease.isSpreadByEating() && disease.getActorsAffectedMap().containsKey(getActorName())){
                                setDiseases.add(disease);
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
     * Returns how many animals are produced during birth
     * @return The number of animals birthed
     */
    abstract protected int breed();

    /**
     * Creates a new animal 
     * @param location The new location of the child
     * @param Set<Disease> The diseases that the parent had is passed down
     * @return The new animal created
     */
    abstract protected Animal birth(Location loc, Set<Disease> parentDiseases);

    /**
     * Returns the Map of the prey of the animal and how much food they provide it
     * @return The Map of the prey of the animal and how much food they provide it
     */
    abstract protected Map<String, Integer> getPreyFoodValuesMap();

    /**
     * Returns the max food value that the animal can have
     * @return The max food value that the animal can have
     */
    abstract protected int getMaxFood();
}
