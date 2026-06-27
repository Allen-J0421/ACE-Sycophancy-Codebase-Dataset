import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

/**
 * Central factory that loads per-species configuration from entities.properties
 * at runtime. Simulator and EntityFactory remain free of concrete entity class
 * references; all type knowledge lives in the external config file.
 */
public class EntityFactory {

    private static class EntityConfig {
        final double probability;
        final Class<?> entityClass;
        final Color color;
        // Animals expose (boolean randomAge, Field, Location); plants expose (Field, Location).
        private final Constructor<?> ctor;
        private final boolean isAnimal;

        EntityConfig(double probability, Class<?> entityClass, Color color)
                throws ReflectiveOperationException {
            this.probability = probability;
            this.entityClass = entityClass;
            this.color = color;
            Constructor<?> animalCtor = null;
            try {
                animalCtor = entityClass.getDeclaredConstructor(
                        boolean.class, Field.class, Location.class);
            } catch (NoSuchMethodException ignored) {}
            if (animalCtor != null) {
                this.ctor = animalCtor;
                this.isAnimal = true;
            } else {
                this.ctor = entityClass.getDeclaredConstructor(Field.class, Location.class);
                this.isAnimal = false;
            }
        }

        LivingEntity create(Field field, Location location)
                throws ReflectiveOperationException {
            if (isAnimal) {
                return (LivingEntity) ctor.newInstance(true, field, location);
            }
            return (LivingEntity) ctor.newInstance(field, location);
        }
    }

    private final List<EntityConfig> configs;

    public EntityFactory() {
        configs = loadConfigs();
    }

    private static List<EntityConfig> loadConfigs() {
        Properties props = new Properties();
        try (InputStream in = EntityFactory.class.getResourceAsStream("entities.properties")) {
            if (in == null) {
                throw new RuntimeException("entities.properties not found on classpath");
            }
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load entities.properties", e);
        }

        int count = Integer.parseInt(props.getProperty("entity.count").trim());
        List<EntityConfig> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String name = props.getProperty("entity." + i).trim();
            double prob = Double.parseDouble(props.getProperty(name + ".probability").trim());
            String colorName = props.getProperty(name + ".color").trim();
            try {
                Class<?> cls = Class.forName(name);
                Color color = (Color) Color.class.getField(colorName).get(null);
                result.add(new EntityConfig(prob, cls, color));
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("Failed to configure entity: " + name, e);
            }
        }
        return result;
    }

    public void registerColors(SimulationObserver observer) {
        for (EntityConfig cfg : configs) {
            observer.onColorRegistered(cfg.entityClass, cfg.color);
        }
    }

    public void populate(Field field, List<LivingEntity> entities) {
        Random rand = Randomizer.getRandom();
        field.clear();
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                for (EntityConfig cfg : configs) {
                    if (rand.nextDouble() <= cfg.probability) {
                        try {
                            entities.add(cfg.create(field, location));
                        } catch (ReflectiveOperationException e) {
                            throw new RuntimeException(
                                    "Failed to create entity: " + cfg.entityClass.getSimpleName(), e);
                        }
                        break;
                    }
                }
            }
        }
    }
}
