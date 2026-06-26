import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntPredicate;

/**
 * Central registry for species-specific tuning parameters.
 */
public final class SpeciesTuning
{
    private static final AnimalTuning DINGO = animal(
            50, 700, 0.04, 3, 100,
            time -> time >= 8 && time <= 24,
            foodMap(Arrays.asList(entry(Snake.class, 100))));

    private static final AnimalTuning ANT = animal(
            20, 400, 0.32, 14, 60,
            time -> time >= 4 && time <= 20,
            foodMap(Arrays.asList(entry(Acacia.class, 60), entry(Grass.class, 60))));

    private static final AnimalTuning RAT = animal(
            25, 600, 0.31, 15, 100,
            time -> time >= 0 && time <= 18,
            foodMap(Arrays.asList(entry(Ant.class, 100))));

    private static final AnimalTuning SNAKE = animal(
            30, 700, 0.33, 11, 100,
            time -> time >= 5 && time <= 23,
            foodMap(Arrays.asList(entry(Rat.class, 100))));

    private static final AnimalTuning EAGLE = animal(
            50, 700, 0.10, 11, 60,
            time -> time >= 6 && time <= 22,
            foodMap(Arrays.asList(entry(Snake.class, 60), entry(Rat.class, 40))));

    private static final AnimalTuning EMU = animal(
            30, 600, 0.17, 7, 60,
            time -> time <= 9 || time >= 21,
            foodMap(Arrays.asList(entry(Grass.class, 60))));

    private static final PlantTuning ACACIA = plant(0.71, 9);
    private static final PlantTuning GRASS = plant(0.77, 9);

    private SpeciesTuning() {}

    public static AnimalTuning dingo() {
        return DINGO;
    }

    public static AnimalTuning ant() {
        return ANT;
    }

    public static AnimalTuning rat() {
        return RAT;
    }

    public static AnimalTuning snake() {
        return SNAKE;
    }

    public static AnimalTuning eagle() {
        return EAGLE;
    }

    public static AnimalTuning emu() {
        return EMU;
    }

    public static PlantTuning acacia() {
        return ACACIA;
    }

    public static PlantTuning grass() {
        return GRASS;
    }

    private static AnimalTuning animal(int breedingAge, int maxAge, double breedingProbability,
            int maxLitterSize, int newbornFoodLevel, IntPredicate activeHours,
            Map<Class<?>, Integer> foodValues) {
        return new AnimalTuning(breedingAge, maxAge, breedingProbability, maxLitterSize,
                newbornFoodLevel, activeHours, foodValues);
    }

    private static PlantTuning plant(double reproducingProbability, int maxOffspringSize) {
        return new PlantTuning(reproducingProbability, maxOffspringSize);
    }

    private static Map<Class<?>, Integer> foodMap(List<Map.Entry<Class<?>, Integer>> entries) {
        Map<Class<?>, Integer> map = new HashMap<>();
        for (Map.Entry<Class<?>, Integer> entry : entries) {
            map.put(entry.getKey(), entry.getValue());
        }
        return Collections.unmodifiableMap(map);
    }

    private static Map.Entry<Class<?>, Integer> entry(Class<?> species, int value) {
        return new AbstractMap.SimpleImmutableEntry<>(species, Integer.valueOf(value));
    }

    /**
     * Tuning parameters for a species of animal.
     */
    public static final class AnimalTuning {
        private final int breedingAge;
        private final int maxAge;
        private final double breedingProbability;
        private final int maxLitterSize;
        private final int newbornFoodLevel;
        private final IntPredicate activeHours;
        private final Map<Class<?>, Integer> foodValues;

        private AnimalTuning(int breedingAge, int maxAge, double breedingProbability,
                int maxLitterSize, int newbornFoodLevel, IntPredicate activeHours,
                Map<Class<?>, Integer> foodValues) {
            this.breedingAge = breedingAge;
            this.maxAge = maxAge;
            this.breedingProbability = breedingProbability;
            this.maxLitterSize = maxLitterSize;
            this.newbornFoodLevel = newbornFoodLevel;
            this.activeHours = activeHours;
            this.foodValues = foodValues;
        }

        public int getBreedingAge() {
            return breedingAge;
        }

        public int getMaxAge() {
            return maxAge;
        }

        public double getBreedingProbability() {
            return breedingProbability;
        }

        public int getMaxLitterSize() {
            return maxLitterSize;
        }

        public int getNewbornFoodLevel() {
            return newbornFoodLevel;
        }

        public boolean isActive(int time) {
            return activeHours.test(time);
        }

        public int foodValueFor(Class<?> preyClass) {
            Integer value = foodValues.get(preyClass);
            return value == null ? 0 : value.intValue();
        }
    }

    /**
     * Tuning parameters for a species of plant.
     */
    public static final class PlantTuning {
        private final double reproducingProbability;
        private final int maxOffspringSize;

        private PlantTuning(double reproducingProbability, int maxOffspringSize) {
            this.reproducingProbability = reproducingProbability;
            this.maxOffspringSize = maxOffspringSize;
        }

        public double getReproducingProbability() {
            return reproducingProbability;
        }

        public int getMaxOffspringSize() {
            return maxOffspringSize;
        }
    }
}
