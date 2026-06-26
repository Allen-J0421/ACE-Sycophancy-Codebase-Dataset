package savannah.model;

import java.awt.Color;
import java.util.Random;

import savannah.config.SimulationConfig;
import savannah.engine.SimulationContext;

/**
 * Defines the species-specific behavior, metadata, and creation logic for an organism.
 */
public interface OrganismFactory
{
    SpeciesType getSpeciesType();

    String getDisplayName();

    Color getFillColor();

    Color getTextColor();

    boolean isPlant();

    boolean isAnimal();

    boolean isPredator();

    default boolean isPrey()
    {
        return isAnimal() && !isPredator();
    }

    SimulationConfig.SpeciesConfig animalConfig(SimulationConfig config);

    SimulationConfig.PlantConfig plantConfig(SimulationConfig config);

    default int initialAge(boolean randomAge, Random rand, SimulationConfig config)
    {
        if (!isAnimal()) {
            throw new IllegalStateException("Only animal species have ages");
        }
        return randomAge ? rand.nextInt(animalConfig(config).maxAge) : 0;
    }

    default int initialFoodLevel(boolean randomAge, Random rand, SimulationConfig config)
    {
        if (!isAnimal()) {
            throw new IllegalStateException("Only animal species have food levels");
        }

        SimulationConfig.SpeciesConfig speciesConfig = animalConfig(config);
        if (randomAge) {
            return rand.nextInt(speciesConfig.maxFoodLevel);
        }
        if (speciesConfig.useRandomNewbornFoodLevel) {
            double percentageOfMaxFoodLevel = rand.nextDouble() * speciesConfig.randomNewbornFoodLevelMaxFraction;
            return (int) (percentageOfMaxFoodLevel * speciesConfig.maxFoodLevel);
        }
        return (int) (speciesConfig.newbornFoodLevelFraction * speciesConfig.maxFoodLevel);
    }

    default double initialPlantHealth(boolean randomHealth, Random rand, SimulationConfig config)
    {
        if (!isPlant()) {
            throw new IllegalStateException("Only plant species have health");
        }

        SimulationConfig.PlantConfig plantConfig = plantConfig(config);
        if (!randomHealth) {
            return plantConfig.newbornHealthFraction;
        }

        double healthPercentage = rand.nextDouble();
        return Math.max(healthPercentage, plantConfig.minimumRandomHealthFraction);
    }

    default double getPlantSpreadProbability(Weather.WeatherType weather, SimulationConfig config)
    {
        if (!isPlant()) {
            throw new IllegalStateException("Only plant species have spread probabilities");
        }

        return switch (weather) {
            case Sunny -> config.sunnyPlantSpreadProbability;
            case Rainy -> config.rainyPlantSpreadProbability;
            case Foggy -> config.foggyPlantSpreadProbability;
            case Cloudy -> config.cloudyPlantSpreadProbability;
            case Clear -> config.clearPlantSpreadProbability;
        };
    }

    default Animal createAnimal(SimulationContext context, Location location, boolean randomAge, boolean infected, boolean immune)
    {
        throw new UnsupportedOperationException(getDisplayName() + " is not an animal factory");
    }

    default Plant createPlant(SimulationContext context, boolean randomHealthPercentage, Location location)
    {
        throw new UnsupportedOperationException(getDisplayName() + " is not a plant factory");
    }
}
