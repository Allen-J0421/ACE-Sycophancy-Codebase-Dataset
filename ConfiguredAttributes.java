/**
 * Factory for building organism attributes from external species configuration.
 *
 * @version 2022.03.02
 */
public final class ConfiguredAttributes
{
    private ConfiguredAttributes()
    {
    }

    public static OrganismAttributes organism(Species species, OrganismFactory factory,
                                              BreedingStrategy breedingStrategy,
                                              RelocationStrategy relocationStrategy,
                                              OrganismActionCommand... actionCommands)
    {
        SpeciesConfig config = SpeciesConfigurationLoader.getConfig(species);
        return new OrganismAttributes(species, config.isDiurnal(),
                                      config.getBreedingAge(), config.getMaxAge(),
                                      config.getBreedingProbability(),
                                      config.getMaxLitterSize(), factory,
                                      breedingStrategy, relocationStrategy,
                                      actionCommands);
    }

    public static AnimalAttributes animal(Species species, OrganismFactory factory,
                                          BreedingStrategy breedingStrategy,
                                          RelocationStrategy relocationStrategy,
                                          OrganismActionCommand... actionCommands)
    {
        SpeciesConfig config = SpeciesConfigurationLoader.getConfig(species);
        Integer maxHealth = config.getMaxHealth();
        if(maxHealth == null) {
            throw new IllegalStateException("Missing maxHealth for animal species " + species);
        }

        return new AnimalAttributes(species, config.isDiurnal(),
                                    config.getBreedingAge(), config.getMaxAge(),
                                    config.getBreedingProbability(),
                                    config.getMaxLitterSize(), maxHealth.intValue(),
                                    factory, config.getFoodSources(),
                                    breedingStrategy, relocationStrategy,
                                    actionCommands);
    }
}
