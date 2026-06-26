import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Central configuration for simulation tuning values.
 */
public class SimulationConfig
{
    private static final SimulationConfig DEFAULT_CONFIG = createDefaultConfig();

    private final Map<Class<?>, AnimalSettings> animalSettings;
    private final Map<Class<?>, PlantSettings> plantSettings;
    private final Map<Class<?>, PopulationSettings> populationSettings;

    public SimulationConfig() {
        animalSettings = new LinkedHashMap<>();
        plantSettings = new LinkedHashMap<>();
        populationSettings = new LinkedHashMap<>();
    }

    public static SimulationConfig defaultConfig() {
        return DEFAULT_CONFIG;
    }

    public void addAnimalSettings(Class<?> speciesClass, AnimalSettings settings) {
        animalSettings.put(speciesClass, settings);
    }

    public void addPlantSettings(Class<?> speciesClass, PlantSettings settings) {
        plantSettings.put(speciesClass, settings);
    }

    public void addPopulationSettings(PopulationSettings settings) {
        populationSettings.put(settings.getSpeciesClass(), settings);
    }

    public AnimalSettings getAnimalSettings(Class<?> speciesClass) {
        AnimalSettings settings = animalSettings.get(speciesClass);
        if(settings == null) {
            throw new IllegalArgumentException("No animal settings for " + speciesClass.getName());
        }
        return settings;
    }

    public PlantSettings getPlantSettings(Class<?> speciesClass) {
        PlantSettings settings = plantSettings.get(speciesClass);
        if(settings == null) {
            throw new IllegalArgumentException("No plant settings for " + speciesClass.getName());
        }
        return settings;
    }

    public PopulationSettings getPopulationSettings(Class<?> speciesClass) {
        PopulationSettings settings = populationSettings.get(speciesClass);
        if(settings == null) {
            throw new IllegalArgumentException("No population settings for " + speciesClass.getName());
        }
        return settings;
    }

    public List<PopulationSettings> getPopulationSettings() {
        return new ArrayList<>(populationSettings.values());
    }

    private static SimulationConfig createDefaultConfig() {
        SimulationConfig config = new SimulationConfig();

        config.addAnimalSettings(Ant.class,
                new AnimalSettings(20, 400, 0.32, 14, 4, 20, 60,
                        new Animal.FoodSource[] {
                            new Animal.FoodSource(Acacia.class, 60),
                            new Animal.FoodSource(Grass.class, 60)
                        }));
        config.addAnimalSettings(Dingo.class,
                new AnimalSettings(50, 700, 0.04, 3, 8, 24, 100,
                        new Animal.FoodSource[] {
                            new Animal.FoodSource(Snake.class, 100)
                        }, 0.5));
        config.addAnimalSettings(Eagle.class,
                new AnimalSettings(50, 700, 0.1, 11, 6, 22, 60,
                        new Animal.FoodSource[] {
                            new Animal.FoodSource(Snake.class, 60),
                            new Animal.FoodSource(Rat.class, 40)
                        }));
        config.addAnimalSettings(Emu.class,
                new AnimalSettings(30, 600, 0.17, 7, 21, 9, 60,
                        new Animal.FoodSource[] {
                            new Animal.FoodSource(Grass.class, 60)
                        }));
        config.addAnimalSettings(Rat.class,
                new AnimalSettings(25, 600, 0.31, 15, 0, 18, 100,
                        new Animal.FoodSource[] {
                            new Animal.FoodSource(Ant.class, 100)
                        }));
        config.addAnimalSettings(Snake.class,
                new AnimalSettings(30, 700, 0.33, 11, 5, 23, 100,
                        new Animal.FoodSource[] {
                            new Animal.FoodSource(Rat.class, 100)
                        }));

        config.addPlantSettings(Acacia.class, new PlantSettings(0.71, 9));
        config.addPlantSettings(Grass.class, new PlantSettings(0.77, 9));

        config.addPopulationSettings(new PopulationSettings(Dingo.class, Color.ORANGE, 0.09));
        config.addPopulationSettings(new PopulationSettings(Ant.class, Color.GRAY, 0.13));
        config.addPopulationSettings(new PopulationSettings(Snake.class, Color.BLACK, 0.12));
        config.addPopulationSettings(new PopulationSettings(Rat.class, Color.PINK, 0.11));
        config.addPopulationSettings(new PopulationSettings(Eagle.class, Color.RED, 0.12));
        config.addPopulationSettings(new PopulationSettings(Emu.class, Color.YELLOW, 0.08));
        config.addPopulationSettings(new PopulationSettings(Acacia.class, Color.GREEN, 0.34));
        config.addPopulationSettings(new PopulationSettings(Grass.class, Color.CYAN, 0.36));

        return config;
    }

    public static class AnimalSettings
    {
        private final int breedingAge;
        private final int maxAge;
        private final double breedingProbability;
        private final int maxLitterSize;
        private final int activeStart;
        private final int activeEnd;
        private final int initialFoodValue;
        private final Animal.FoodSource[] foodSources;
        private final double fogFoodSearchProbability;

        public AnimalSettings(int breedingAge, int maxAge, double breedingProbability,
                              int maxLitterSize, int activeStart, int activeEnd,
                              int initialFoodValue, Animal.FoodSource[] foodSources) {
            this(breedingAge, maxAge, breedingProbability, maxLitterSize, activeStart,
                    activeEnd, initialFoodValue, foodSources, 1.0);
        }

        public AnimalSettings(int breedingAge, int maxAge, double breedingProbability,
                              int maxLitterSize, int activeStart, int activeEnd,
                              int initialFoodValue, Animal.FoodSource[] foodSources,
                              double fogFoodSearchProbability) {
            this.breedingAge = breedingAge;
            this.maxAge = maxAge;
            this.breedingProbability = breedingProbability;
            this.maxLitterSize = maxLitterSize;
            this.activeStart = activeStart;
            this.activeEnd = activeEnd;
            this.initialFoodValue = initialFoodValue;
            this.foodSources = foodSources;
            this.fogFoodSearchProbability = fogFoodSearchProbability;
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

        public int getActiveStart() {
            return activeStart;
        }

        public int getActiveEnd() {
            return activeEnd;
        }

        public int getInitialFoodValue() {
            return initialFoodValue;
        }

        public Animal.FoodSource[] getFoodSources() {
            return foodSources;
        }

        public double getFogFoodSearchProbability() {
            return fogFoodSearchProbability;
        }
    }

    public static class PlantSettings
    {
        private final double reproducingProbability;
        private final int maxOffspringSize;

        public PlantSettings(double reproducingProbability, int maxOffspringSize) {
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

    public static class PopulationSettings
    {
        private final Class<?> speciesClass;
        private final Color color;
        private final double creationProbability;

        public PopulationSettings(Class<?> speciesClass, Color color, double creationProbability) {
            this.speciesClass = speciesClass;
            this.color = color;
            this.creationProbability = creationProbability;
        }

        public Class<?> getSpeciesClass() {
            return speciesClass;
        }

        public Color getColor() {
            return color;
        }

        public double getCreationProbability() {
            return creationProbability;
        }
    }
}
