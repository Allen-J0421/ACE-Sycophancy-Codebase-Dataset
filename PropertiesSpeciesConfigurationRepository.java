import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Loads species configuration from an external properties file.
 *
 * @version 2022.03.02
 */
public class PropertiesSpeciesConfigurationRepository
    implements SpeciesConfigurationRepository
{
    private final String configFile;
    private final EnumMap<Species, SpeciesConfig> configs;

    public PropertiesSpeciesConfigurationRepository(String configFile)
    {
        this.configFile = configFile;
        configs = loadConfigs();
    }

    public SpeciesConfig getConfig(Species species)
    {
        SpeciesConfig config = configs.get(species);
        if(config == null) {
            throw new IllegalStateException("Missing config for species " + species);
        }
        return config;
    }

    private EnumMap<Species, SpeciesConfig> loadConfigs()
    {
        Properties properties = new Properties();
        try (InputStream stream = openConfig()) {
            properties.load(stream);
        }
        catch (IOException e) {
            throw new IllegalStateException("Unable to load " + configFile, e);
        }

        EnumMap<Species, SpeciesConfig> loadedConfigs = new EnumMap<>(Species.class);
        for(Species species : Species.values()) {
            loadedConfigs.put(species, loadSpeciesConfig(species, properties));
        }
        return loadedConfigs;
    }

    private InputStream openConfig() throws IOException
    {
        InputStream classpathStream =
            getClass().getResourceAsStream("/" + configFile);
        if(classpathStream != null) {
            return classpathStream;
        }

        Path path = Paths.get(configFile);
        if(Files.exists(path)) {
            return Files.newInputStream(path);
        }

        throw new IOException("Configuration file not found: " + configFile);
    }

    private SpeciesConfig loadSpeciesConfig(Species species, Properties properties)
    {
        String prefix = species.getConfigKey() + ".";
        boolean diurnal = readBoolean(properties, prefix + "diurnal");
        int breedingAge = readInt(properties, prefix + "breedingAge");
        int maxAge = readInt(properties, prefix + "maxAge");
        double breedingProbability = readDouble(properties, prefix + "breedingProbability");
        int maxLitterSize = readInt(properties, prefix + "maxLitterSize");
        double creationProbability = readDouble(properties, prefix + "creationProbability");
        Integer maxHealth = readOptionalInt(properties, prefix + "maxHealth");
        Set<Species> foodSources = readSpeciesSet(properties, prefix + "foodSources");

        return new SpeciesConfig(diurnal, breedingAge, maxAge,
                                 breedingProbability, maxLitterSize,
                                 creationProbability, maxHealth, foodSources);
    }

    private boolean readBoolean(Properties properties, String key)
    {
        return Boolean.parseBoolean(readRequired(properties, key));
    }

    private int readInt(Properties properties, String key)
    {
        return Integer.parseInt(readRequired(properties, key));
    }

    private Integer readOptionalInt(Properties properties, String key)
    {
        String value = properties.getProperty(key);
        if(value == null || value.trim().length() == 0) {
            return null;
        }
        return Integer.valueOf(Integer.parseInt(value.trim()));
    }

    private double readDouble(Properties properties, String key)
    {
        return Double.parseDouble(readRequired(properties, key));
    }

    private Set<Species> readSpeciesSet(Properties properties, String key)
    {
        String value = properties.getProperty(key);
        if(value == null || value.trim().length() == 0) {
            return Collections.emptySet();
        }

        Set<Species> speciesSet = new LinkedHashSet<>();
        String[] values = value.split(",");
        for(String item : values) {
            speciesSet.add(Species.valueOf(item.trim().toUpperCase()));
        }
        return speciesSet;
    }

    private String readRequired(Properties properties, String key)
    {
        String value = properties.getProperty(key);
        if(value == null) {
            throw new IllegalStateException("Missing required config key: " + key);
        }
        return value.trim();
    }
}
