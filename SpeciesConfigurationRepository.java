/**
 * Provides species configuration data to simulation components.
 *
 * @version 2022.03.02
 */
public interface SpeciesConfigurationRepository
{
    SpeciesConfig getConfig(Species species);
}
