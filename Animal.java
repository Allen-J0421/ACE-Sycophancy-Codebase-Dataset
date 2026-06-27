import java.util.List;
import java.util.Iterator;
import java.util.Collections;
import java.util.HashMap;
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
    protected interface AnimalFactory
    {
        Animal create(Time time, Field field, Location location, Set<Disease> parentDiseases);
    }

    protected static class AnimalProfile
    {
        private final String name;
        private final int breedingAge;
        private final int maxAge;
        private final double breedingProbability;
        private final int maxLitterSize;
        private final int maxFood;
        private final Map<String, Integer> preyFoodValues;
        private final boolean nocturnal;
        private final boolean canGoLand;
        private final boolean canGoWater;
        private final AnimalFactory factory;

        protected AnimalProfile(String name, int breedingAge, int maxAge,
                                double breedingProbability, int maxLitterSize, int maxFood,
                                Map<String, Integer> preyFoodValues, boolean nocturnal,
                                boolean canGoLand, boolean canGoWater, AnimalFactory factory)
        {
            this.name = name;
            this.breedingAge = breedingAge;
            this.maxAge = maxAge;
            this.breedingProbability = breedingProbability;
            this.maxLitterSize = maxLitterSize;
            this.maxFood = maxFood;
            this.preyFoodValues = Collections.unmodifiableMap(new HashMap<>(preyFoodValues));
            this.nocturnal = nocturnal;
            this.canGoLand = canGoLand;
            this.canGoWater = canGoWater;
            this.factory = factory;
        }
    }

    protected int foodLevel;
    protected boolean female;
    protected boolean nocturnal;
    private final AnimalProfile profile;
    private final Random rand;

    /**
     * Create a new animal at location in field with time as well.
     * With a set that will add any disease that it catches
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param time The time in the simulation
     */
    protected Animal(Time time, Field field, Location location, AnimalProfile profile, Random rand)
    {
        this(time, field, location, profile, rand, null);
    }

    /**
     * Create a new born animal with inherited diseases.
     *
     * @param time The time in the simulation.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param profile The species profile for this animal.
     * @param rand The shared random generator for this species.
     * @param parentDiseases The diseases inherited from the parent.
     */
    protected Animal(Time time, Field field, Location location, AnimalProfile profile,
                     Random rand, Set<Disease> parentDiseases)
    {
        super(time, field, location);
        this.profile = profile;
        this.rand = rand;
        female = rand.nextBoolean();
        nocturnal = profile.nocturnal;
        canGoLand = profile.canGoLand;
        canGoWater = profile.canGoWater;

        if(parentDiseases == null) {
            age = rand.nextInt(profile.maxAge);
            foodLevel = rand.nextInt(profile.maxFood) + 1;
            addStartingDiseases(profile.name, setDiseases, rand);
        }
        else {
            age = 0;
            foodLevel = profile.maxFood;
            inheritBirthDiseases(setDiseases, parentDiseases);
        }
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
    protected final int breed()
    {
        return calculateBreedingCount(profile.breedingAge, profile.breedingProbability,
                                      profile.maxLitterSize, rand);
    }

    /**
     * Creates a new animal 
     * @param location The new location of the child
     * @param Set<Disease> The diseases that the parent had is passed down
     * @return The new animal created
     */
    protected final Animal birth(Location loc, Set<Disease> parentDiseases)
    {
        Animal young = profile.factory.create(getTime(), getField(), loc, parentDiseases);
        young.placeInField();
        return young;
    }

    /**
     * Returns the Map of the prey of the animal and how much food they provide it
     * @return The Map of the prey of the animal and how much food they provide it
     */
    protected final Map<String, Integer> getPreyFoodValuesMap()
    {
        return profile.preyFoodValues;
    }

    /**
     * Returns the max food value that the animal can have
     * @return The max food value that the animal can have
     */
    protected final int getMaxFood()
    {
        return profile.maxFood;
    }

    /**
     * Returns the actors Name.
     * @return The actor name.
     */
    protected final String getActorName()
    {
        return profile.name;
    }

    /**
     * Returns the actors max age.
     * @return The max age.
     */
    protected final int getMaxAge()
    {
        return profile.maxAge;
    }
}
