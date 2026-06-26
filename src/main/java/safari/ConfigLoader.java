package safari;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;

/**
 * Utility for loading external configuration files.
 */
final class ConfigLoader
{
    private ConfigLoader()
    {
    }

    static Properties loadProperties(String relativePath)
    {
        Properties properties = new Properties();
        Path path = Path.of(relativePath);
        try {
            if(Files.exists(path)) {
                try(InputStream input = Files.newInputStream(path)) {
                    properties.load(input);
                    return properties;
                }
            }
            String resourcePath = relativePath.startsWith("/") ? relativePath : "/" + relativePath;
            try(InputStream input = ConfigLoader.class.getResourceAsStream(resourcePath)) {
                if(input != null) {
                    properties.load(input);
                    return properties;
                }
            }
        }
        catch(IOException ioe) {
            throw new IllegalStateException("Failed to load configuration from " + relativePath, ioe);
        }
        throw new IllegalStateException("Missing configuration file: " + relativePath);
    }

    static String requiredString(Properties properties, String key)
    {
        String value = properties.getProperty(key);
        if(value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required configuration key: " + key);
        }
        return value.trim();
    }

    static int requiredInt(Properties properties, String key)
    {
        return Integer.parseInt(requiredString(properties, key));
    }

    static double requiredDouble(Properties properties, String key)
    {
        return Double.parseDouble(requiredString(properties, key));
    }

    static Color requiredColor(Properties properties, String key)
    {
        try {
            return Color.decode(requiredString(properties, key));
        }
        catch(NumberFormatException nfe) {
            throw new IllegalStateException("Invalid color value for key: " + key, nfe);
        }
    }

    static Map<Weather, Double> requiredWeatherMap(Properties properties, String prefix)
    {
        Map<Weather, Double> values = new EnumMap<>(Weather.class);
        for(Weather weather : Weather.values()) {
            values.put(weather, requiredDouble(properties, prefix + weather.name()));
        }
        return values;
    }

    static Map<ActorKind, Integer> requiredActorKindMap(Properties properties, String prefix)
    {
        Map<ActorKind, Integer> values = new EnumMap<>(ActorKind.class);
        for(String key : properties.stringPropertyNames()) {
            if(key.startsWith(prefix)) {
                String kindName = key.substring(prefix.length());
                values.put(ActorKind.valueOf(kindName), requiredInt(properties, key));
            }
        }
        if(values.isEmpty()) {
            throw new IllegalStateException("Missing required configuration entries for prefix: " + prefix);
        }
        return values;
    }
}
