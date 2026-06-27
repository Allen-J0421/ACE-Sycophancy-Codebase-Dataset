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
public final class SpeciesConfigurationLoader
{
    private static final String CONFIG_FILE = "organism-config.properties";
    private static final EnumMap<Species, SpeciesConfig> CONFIGS = loadConfigs();

    private SpeciesConfigurationLoader()
    {
    }

    public static SpeciesConfig getConfig(Species species)
    {
        SpeciesConfig config = CONFIGS.get(species);
        if(config == null) {
            throw new IllegalStateException("Missing config for species " + species);
        }
        return config;
    }

    private static EnumMap<Species, SpeciesConfig> loadConfigs()
    {
        Properties properties = new Properties();
        try (InputStream stream = openConfig()) {
            properties.load(stream);
        }
        catch (IOException e) {
            throw new IllegalStateException("Unable to load " + CONFIG_FILE, e);
        }

        EnumMap<Species, SpeciesConfig> configs = new EnumMap<>(Species.class);
        for(Species species : Species.values()) {
            configs.put(species, loadSpeciesConfig(species, properties));
        }
        return configs;
    }

    private static InputStream openConfig() throws IOException
    {
        InputStream classpathStream =
            SpeciesConfigurationLoader.class.getResourceAsStream("/" + CONFIG_FILE);
        if(classpathStream != null) {
            return classpathStream;
        }

        Path path = Paths.get(CONFIG_FILE);
        if(Files.exists(path)) {
            return Files.newInputStream(path);
        }

        throw new IOException("Configuration file not found: " + CONFIG_FILE);
    }

    private static SpeciesConfig loadSpeciesConfig(Species species, Properties properties)
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
                                 creationProbability,
                                 maxHealth, foodSources);
    }

    private static boolean readBoolean(Properties properties, String key)
    {
        return Boolean.parseBoolean(readRequired(properties, key));
    }

    private static int readInt(Properties properties, String key)
    {
        return Integer.parseInt(readRequired(properties, key));
    }

    private static Integer readOptionalInt(Properties properties, String key)
    {
        String value = properties.getProperty(key);
        if(value == null || value.trim().length() == 0) {
            return null;
        }
        return Integer.valueOf(Integer.parseInt(value.trim()));
    }

    private static double readDouble(Properties properties, String key)
    {
        return Double.parseDouble(readRequired(properties, key));
    }

    private static Set<Species> readSpeciesSet(Properties properties, String key)
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

    private static String readRequired(Properties properties, String key)
    {
        String value = properties.getProperty(key);
        if(value == null) {
            throw new IllegalStateException("Missing required config key: " + key);
        }
        return value.trim();
    }
}
