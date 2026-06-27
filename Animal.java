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
    private final AnimalProfile profile;
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

    protected static final class AnimalProfile
    {
        private final int maxAge;
        private final int initialFoodLevel;
        private final int newbornFoodLevel;
        private final boolean activeAtNight;
        private final int breedingAge;
        private final double breedingProbability;
        private final int maxLitterSize;
        private final Map<Class<? extends LivingBeing>, Integer> foodValues;

        private AnimalProfile(int maxAge, int initialFoodLevel, int newbornFoodLevel,
                              boolean activeAtNight, int breedingAge,
                              double breedingProbability, int maxLitterSize,
                              Map<Class<? extends LivingBeing>, Integer> foodValues)
        {
            this.maxAge = maxAge;
            this.initialFoodLevel = initialFoodLevel;
            this.newbornFoodLevel = newbornFoodLevel;
            this.activeAtNight = activeAtNight;
            this.breedingAge = breedingAge;
            this.breedingProbability = breedingProbability;
            this.maxLitterSize = maxLitterSize;
            this.foodValues = foodValues;
        }
    }

    /**
     * Create a new animal at location in field.
     * @param randomAge If true, the animal will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param profile The species-specific lifecycle configuration.
     */
    public Animal(boolean randomAge, Field field, Location location, AnimalProfile profile)
    {
        super(field, location);
        this.profile = profile;
        sex = randomSex();

        if(randomAge) {
            age = rand.nextInt(profile.maxAge);
            foodLevel = rand.nextInt(profile.initialFoodLevel);
        }
        else {
            age = 0;
            foodLevel = profile.newbornFoodLevel;
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

    private static Map<Class<? extends LivingBeing>, Integer> foodValues(FoodValue... values)
    {
        Map<Class<? extends LivingBeing>, Integer> foods = new LinkedHashMap<>();
        for(FoodValue value : values) {
            foods.put(value.type, value.value);
        }
        return Collections.unmodifiableMap(foods);
    }

    protected static AnimalProfile profile(int maxAge, int initialFoodLevel,
                                           int newbornFoodLevel, boolean activeAtNight,
                                           int breedingAge, double breedingProbability,
                                           int maxLitterSize, FoodValue... foods)
    {
        return new AnimalProfile(maxAge, initialFoodLevel, newbornFoodLevel,
                                 activeAtNight, breedingAge, breedingProbability,
                                 maxLitterSize, foodValues(foods));
    }

    /**
     * Run the standard animal lifecycle for one simulation step.
     * @param newAnimals A list to receive newly born animals.
     */
    private void live(List<LivingBeing> newAnimals)
    {
        incrementAge();
        incrementHunger();

        if(isNight() != profile.activeAtNight || !isAlive()) {
            return;
        }

        giveBirth(newAnimals);

        Location newLocation = findFood();
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
        if(age > profile.maxAge) {
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

    private Location findFood()
    {
        Field field = getField();
        if(field == null || getLocation() == null) {
            return null;
        }

        for(Location where : field.adjacentLocations(getLocation())) {
            LivingBeing occupant = field.getLivingBeingAt(where);
            for(Map.Entry<Class<? extends LivingBeing>, Integer> food :
                    profile.foodValues.entrySet()) {
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

    private void giveBirth(List<LivingBeing> newAnimals)
    {
        Field field = getField();
        if(field == null || getLocation() == null || !canBreed()) {
            return;
        }

        for(Location where : field.adjacentLocations(getLocation())) {
            LivingBeing occupant = field.getLivingBeingAt(where);
            if(getClass().isInstance(occupant) && hasOppositeSex((Animal) occupant)) {
                List<Location> free = field.getFreeAdjacentLocations(getLocation());
                int births = breed();
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

    private int breed()
    {
        if(rand.nextDouble() <= profile.breedingProbability) {
            return rand.nextInt(profile.maxLitterSize) + 1;
        }
        return 0;
    }

    private boolean canBreed()
    {
        return age >= profile.breedingAge;
    }
    
    /**
     * Create a newborn of this animal's species.
     * @param field The field the newborn will occupy.
     * @param location The newborn's location.
     * @return The newborn animal.
     */
    protected abstract Animal createOffspring(Field field, Location location);

    /**
     * Act for one simulation step.
     * @param newAnimals A list to receive newly born animals.
     */
    @Override
    public final void act(List<LivingBeing> newAnimals)
    {
        live(newAnimals);
    }
    
}
