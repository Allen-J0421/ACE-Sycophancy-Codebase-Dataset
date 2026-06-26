import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Registry of interactions between animal species and food sources.
 */
public final class AnimalInteractionRegistry
{
    private enum FoodSource
    {
        SEAWEED,
        COD,
        SALMON
    }

    private static final Map<AnimalSpecies, Map<FoodSource, Integer>> FOOD_VALUES =
        new EnumMap<>(AnimalSpecies.class);
    private static final Map<AnimalSpecies, Set<AnimalSpecies>> COMPATIBLE_MATES =
        new EnumMap<>(AnimalSpecies.class);

    static {
        registerFood(AnimalSpecies.COD, FoodSource.SEAWEED, 13);
        registerFood(AnimalSpecies.SALMON, FoodSource.SEAWEED, 13);
        registerFood(AnimalSpecies.SHARK, FoodSource.COD, 8);
        registerFood(AnimalSpecies.SHARK, FoodSource.SALMON, 8);
        registerFood(AnimalSpecies.WHALE, FoodSource.COD, 8);
        registerFood(AnimalSpecies.WHALE, FoodSource.SALMON, 8);

        registerMate(AnimalSpecies.COD, AnimalSpecies.COD);
        registerMate(AnimalSpecies.SALMON, AnimalSpecies.SALMON);
        registerMate(AnimalSpecies.SHARK, AnimalSpecies.COD);
        registerMate(AnimalSpecies.WHALE, AnimalSpecies.SALMON);
    }

    private AnimalInteractionRegistry()
    {
    }

    public static int getFoodValueFromSeaweed(AnimalSpecies predator)
    {
        return getFoodValue(predator, FoodSource.SEAWEED);
    }

    public static int getFoodValue(AnimalSpecies predator, AnimalSpecies prey)
    {
        FoodSource preySource = foodSourceFor(prey);
        if(preySource == null) {
            return 0;
        }
        return getFoodValue(predator, preySource);
    }

    public static boolean canMateWith(AnimalSpecies animal, AnimalSpecies other)
    {
        Set<AnimalSpecies> mates = COMPATIBLE_MATES.get(animal);
        return mates != null && mates.contains(other);
    }

    private static int getFoodValue(AnimalSpecies predator, FoodSource foodSource)
    {
        Map<FoodSource, Integer> values = FOOD_VALUES.get(predator);
        if(values == null) {
            return 0;
        }
        Integer value = values.get(foodSource);
        return value == null ? 0 : value;
    }

    private static void registerFood(AnimalSpecies predator, FoodSource foodSource, int value)
    {
        Map<FoodSource, Integer> values = FOOD_VALUES.get(predator);
        if(values == null) {
            values = new EnumMap<>(FoodSource.class);
            FOOD_VALUES.put(predator, values);
        }
        values.put(foodSource, value);
    }

    private static void registerMate(AnimalSpecies animal, AnimalSpecies mate)
    {
        Set<AnimalSpecies> mates = COMPATIBLE_MATES.get(animal);
        if(mates == null) {
            mates = EnumSet.noneOf(AnimalSpecies.class);
            COMPATIBLE_MATES.put(animal, mates);
        }
        mates.add(mate);
    }

    private static FoodSource foodSourceFor(AnimalSpecies species)
    {
        switch(species) {
            case COD:
                return FoodSource.COD;
            case SALMON:
                return FoodSource.SALMON;
            default:
                return null;
        }
    }
}
