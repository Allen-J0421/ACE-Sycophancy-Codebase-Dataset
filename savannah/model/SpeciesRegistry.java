package savannah.model;

import java.awt.Color;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

import savannah.config.SimulationConfig;
import savannah.engine.SimulationContext;

/**
 * Central registry for species metadata, factory methods, and species-specific
 * configuration access.
 */
public final class SpeciesRegistry
{
    public static final SpeciesRegistry INSTANCE = new SpeciesRegistry();

    private final Map<SpeciesType, Descriptor> descriptors;

    private SpeciesRegistry()
    {
        descriptors = new EnumMap<>(SpeciesType.class);

        register(SpeciesType.LION, new Descriptor(
            "Lion",
            Color.RED,
            Color.WHITE,
            true,
            true,
            (context, randomAge, location, infected, immune) -> new Lion(context, randomAge, location, infected, immune),
            null,
            config -> config.lion,
            null
        ));
        register(SpeciesType.CHEETAH, new Descriptor(
            "Cheetah",
            Color.ORANGE,
            Color.BLACK,
            true,
            true,
            (context, randomAge, location, infected, immune) -> new Cheetah(context, randomAge, location, infected, immune),
            null,
            config -> config.cheetah,
            null
        ));
        register(SpeciesType.ZEBRA, new Descriptor(
            "Zebra",
            Color.BLACK,
            Color.WHITE,
            true,
            false,
            (context, randomAge, location, infected, immune) -> new Zebra(context, randomAge, location, infected, immune),
            null,
            config -> config.zebra,
            null
        ));
        register(SpeciesType.GIRAFFE, new Descriptor(
            "Giraffe",
            Color.YELLOW,
            Color.BLACK,
            true,
            false,
            (context, randomAge, location, infected, immune) -> new Giraffe(context, randomAge, location, infected, immune),
            null,
            config -> config.giraffe,
            null
        ));
        register(SpeciesType.LEMUR, new Descriptor(
            "Lemur",
            Color.BLUE,
            Color.WHITE,
            true,
            false,
            (context, randomAge, location, infected, immune) -> new Lemur(context, randomAge, location, infected, immune),
            null,
            config -> config.lemur,
            null
        ));
        register(SpeciesType.PLANT, new Descriptor(
            "Plant",
            Color.GREEN,
            Color.BLACK,
            false,
            false,
            null,
            (context, randomHealth, location) -> new Plant(context, randomHealth, location),
            null,
            config -> config.plant
        ));
    }

    private void register(SpeciesType speciesType, Descriptor descriptor)
    {
        descriptors.put(speciesType, descriptor);
    }

    public String getDisplayName(SpeciesType speciesType)
    {
        return descriptor(speciesType).displayName;
    }

    public Color getFillColor(SpeciesType speciesType)
    {
        return descriptor(speciesType).fillColor;
    }

    public Color getTextColor(SpeciesType speciesType)
    {
        return descriptor(speciesType).textColor;
    }

    public boolean isPlant(SpeciesType speciesType)
    {
        return descriptor(speciesType).plant;
    }

    public boolean isAnimal(SpeciesType speciesType)
    {
        return !isPlant(speciesType);
    }

    public boolean isPredator(SpeciesType speciesType)
    {
        return descriptor(speciesType).predator;
    }

    public boolean isPrey(SpeciesType speciesType)
    {
        return isAnimal(speciesType) && !isPredator(speciesType);
    }

    public SimulationConfig.SpeciesConfig animalConfig(SpeciesType speciesType, SimulationConfig config)
    {
        SpeciesType validatedSpeciesType = validateAnimal(speciesType);
        return descriptor(validatedSpeciesType).animalConfigAccessor.get(config);
    }

    public SimulationConfig.PlantConfig plantConfig(SpeciesType speciesType, SimulationConfig config)
    {
        validatePlant(speciesType);
        return descriptor(speciesType).plantConfigAccessor.get(config);
    }

    public int initialAge(SpeciesType speciesType, boolean randomAge, Random rand, SimulationConfig config)
    {
        return randomAge ? rand.nextInt(animalConfig(speciesType, config).maxAge) : 0;
    }

    public int initialFoodLevel(SpeciesType speciesType, boolean randomAge, Random rand, SimulationConfig config)
    {
        SimulationConfig.SpeciesConfig speciesConfig = animalConfig(speciesType, config);
        if (randomAge) {
            return rand.nextInt(speciesConfig.maxFoodLevel);
        }
        if (speciesConfig.useRandomNewbornFoodLevel) {
            double percentageOfMaxFoodLevel = rand.nextDouble() * speciesConfig.randomNewbornFoodLevelMaxFraction;
            return (int) (percentageOfMaxFoodLevel * speciesConfig.maxFoodLevel);
        }
        return (int) (speciesConfig.newbornFoodLevelFraction * speciesConfig.maxFoodLevel);
    }

    public double initialPlantHealth(SpeciesType speciesType, boolean randomHealth, Random rand, SimulationConfig config)
    {
        SimulationConfig.PlantConfig plantConfig = plantConfig(speciesType, config);
        if (!randomHealth) {
            return plantConfig.newbornHealthFraction;
        }

        double healthPercentage = rand.nextDouble();
        return Math.max(healthPercentage, plantConfig.minimumRandomHealthFraction);
    }

    public double getPlantSpreadProbability(SpeciesType speciesType, Weather.WeatherType weather, SimulationConfig config)
    {
        validatePlant(speciesType);

        return switch (weather) {
            case Sunny -> config.sunnyPlantSpreadProbability;
            case Rainy -> config.rainyPlantSpreadProbability;
            case Foggy -> config.foggyPlantSpreadProbability;
            case Cloudy -> config.cloudyPlantSpreadProbability;
            case Clear -> config.clearPlantSpreadProbability;
        };
    }

    public Plant createPlant(SpeciesType speciesType, SimulationContext context, boolean randomHealthPercentage, Location location)
    {
        return descriptor(validatePlant(speciesType)).plantFactory.create(context, randomHealthPercentage, location);
    }

    public Animal createAnimal(SpeciesType speciesType, SimulationContext context, Location location, boolean randomAge, boolean infected, boolean immune)
    {
        return descriptor(validateAnimal(speciesType)).animalFactory.create(context, randomAge, location, infected, immune);
    }

    private Descriptor descriptor(SpeciesType speciesType)
    {
        Descriptor descriptor = descriptors.get(speciesType);
        if (descriptor == null) {
            throw new IllegalArgumentException("Unknown species type: " + speciesType);
        }
        return descriptor;
    }

    private SpeciesType validateAnimal(SpeciesType speciesType)
    {
        if (isPlant(speciesType)) {
            throw new IllegalStateException("Plant species do not create animals");
        }
        return speciesType;
    }

    private SpeciesType validatePlant(SpeciesType speciesType)
    {
        if (!isPlant(speciesType)) {
            throw new IllegalStateException("Only plant species use plant behavior");
        }
        return speciesType;
    }

    private record Descriptor(
        String displayName,
        Color fillColor,
        Color textColor,
        boolean plant,
        boolean predator,
        AnimalFactory animalFactory,
        PlantFactory plantFactory,
        AnimalConfigAccessor animalConfigAccessor,
        PlantConfigAccessor plantConfigAccessor
    ) {}

    @FunctionalInterface
    private interface AnimalFactory
    {
        Animal create(SimulationContext context, boolean randomAge, Location location, boolean infected, boolean immune);
    }

    @FunctionalInterface
    private interface PlantFactory
    {
        Plant create(SimulationContext context, boolean randomHealthPercentage, Location location);
    }

    @FunctionalInterface
    private interface AnimalConfigAccessor
    {
        SimulationConfig.SpeciesConfig get(SimulationConfig config);
    }

    @FunctionalInterface
    private interface PlantConfigAccessor
    {
        SimulationConfig.PlantConfig get(SimulationConfig config);
    }
}
