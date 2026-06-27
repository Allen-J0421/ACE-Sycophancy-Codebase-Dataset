import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 2022.02.24 (2)
 */
public abstract class Animal extends LivingBeing
{
    private static final Random rand = Randomizer.getRandom();

    private enum Sex { MALE, FEMALE }

    private final Sex sex;
    private final int maxAge;
    private int age;
    private int foodLevel;

    protected static final class FoodValue
    {
        private final Class<? extends LivingBeing> type;
        private final int value;

        private FoodValue(Class<? extends LivingBeing> type, int value)
        {
            this.type = type;
            this.value = value;
        }
    }

    /**
     * Create a new animal at location in field.
     * @param randomAge If true, the animal will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param maxAge The age to which the animal can live.
     * @param initialFoodLevel The food level to use as the random upper bound.
     * @param newbornFoodLevel The food level assigned to newborn animals.
     */
    public Animal(boolean randomAge, Field field, Location location, int maxAge,
                  int initialFoodLevel, int newbornFoodLevel)
    {
        super(field, location);
        this.maxAge = maxAge;
        sex = randomSex();

        if(randomAge) {
            age = rand.nextInt(maxAge);
            foodLevel = rand.nextInt(initialFoodLevel);
        }
        else {
            age = 0;
            foodLevel = newbornFoodLevel;
        }
    }

    private Sex randomSex()
    {
        Sex[] values = Sex.values();
        return values[rand.nextInt(values.length)];
    }

    protected static FoodValue food(Class<? extends LivingBeing> type, int value)
    {
        return new FoodValue(type, value);
    }

    protected static Map<Class<? extends LivingBeing>, Integer> foodValues(FoodValue... values)
    {
        Map<Class<? extends LivingBeing>, Integer> foods = new LinkedHashMap<>();
        for(FoodValue value : values) {
            foods.put(value.type, value.value);
        }
        return Collections.unmodifiableMap(foods);
    }

    /**
     * Run the standard animal lifecycle for one simulation step.
     * @param newAnimals A list to receive newly born animals.
     * @param activeAtNight Whether this species moves, feeds and breeds at night.
     * @param breedingAge The age at which this species can start breeding.
     * @param breedingProbability The likelihood of this species breeding.
     * @param maxLitterSize The maximum number of births per breeding event.
     * @param foodValues Food classes and the energy gained from eating each one.
     */
    protected void live(List<LivingBeing> newAnimals, boolean activeAtNight,
                        int breedingAge, double breedingProbability, int maxLitterSize,
                        Map<Class<? extends LivingBeing>, Integer> foodValues)
    {
        incrementAge();
        incrementHunger();

        if(isNight() != activeAtNight || !isAlive()) {
            return;
        }

        giveBirth(newAnimals, breedingAge, breedingProbability, maxLitterSize);

        Location newLocation = findFood(foodValues);
        if(newLocation == null && getLocation() != null) {
            newLocation = getField().freeAdjacentLocation(getLocation());
        }

        if(newLocation != null) {
            setLocation(newLocation);
        }
    }

    private void incrementAge()
    {
        age++;
        if(age > maxAge) {
            setDead();
        }
    }

    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    private Location findFood(Map<Class<? extends LivingBeing>, Integer> foodValues)
    {
        Field field = getField();
        if(field == null || getLocation() == null) {
            return null;
        }

        for(Location where : field.adjacentLocations(getLocation())) {
            LivingBeing occupant = field.getObjectAt(where);
            for(Map.Entry<Class<? extends LivingBeing>, Integer> food : foodValues.entrySet()) {
                if(food.getKey().isInstance(occupant)) {
                    if(occupant.isAlive()) {
                        occupant.setDead();
                        foodLevel += food.getValue();
                        return where;
                    }
                }
            }
        }
        return null;
    }

    private void giveBirth(List<LivingBeing> newAnimals, int breedingAge,
                           double breedingProbability, int maxLitterSize)
    {
        Field field = getField();
        if(field == null || getLocation() == null || !canBreed(breedingAge)) {
            return;
        }

        for(Location where : field.adjacentLocations(getLocation())) {
            LivingBeing occupant = field.getObjectAt(where);
            if(getClass().isInstance(occupant) && hasOppositeSex((Animal) occupant)) {
                List<Location> free = field.getFreeAdjacentLocations(getLocation());
                int births = breed(breedingProbability, maxLitterSize);
                for(int b = 0; b < births && free.size() > 0; b++) {
                    Location loc = free.remove(0);
                    newAnimals.add(createOffspring(field, loc));
                }
            }
        }
    }

    private boolean hasOppositeSex(Animal other)
    {
        return sex != other.sex;
    }

    private int breed(double breedingProbability, int maxLitterSize)
    {
        if(rand.nextDouble() <= breedingProbability) {
            return rand.nextInt(maxLitterSize) + 1;
        }
        return 0;
    }

    private boolean canBreed(int breedingAge)
    {
        return age >= breedingAge;
    }
    
    /**
     * Create a newborn of this animal's species.
     * @param field The field the newborn will occupy.
     * @param location The newborn's location.
     * @return The newborn animal.
     */
    protected abstract Animal createOffspring(Field field, Location location);

    /**
     * Abstract method act, defined in subclasses (meant to be overridden)
     * @param newAnimals a list of new animals 
     */
    public abstract void act(List<LivingBeing> newAnimals);
    
}
