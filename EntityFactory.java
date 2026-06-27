import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;

/**
 * Central factory that owns all per-species configuration:
 * creation probability, display colour, and constructor reference.
 * Eliminates Simulator's explicit knowledge of concrete entity types.
 */
public class EntityFactory {

    private static class EntityConfig {
        final double probability;
        final Class<?> entityClass;
        final Color color;
        final BiFunction<Field, Location, LivingEntity> creator;

        EntityConfig(double probability, Class<?> entityClass, Color color,
                     BiFunction<Field, Location, LivingEntity> creator) {
            this.probability = probability;
            this.entityClass = entityClass;
            this.color = color;
            this.creator = creator;
        }
    }

    // Order must match the original populate() else-if chain to preserve RNG call sequence.
    private static final List<EntityConfig> CONFIGS = Arrays.asList(
        new EntityConfig(0.09, Dingo.class,  Color.ORANGE, (f, l) -> new Dingo(true, f, l)),
        new EntityConfig(0.13, Ant.class,    Color.GRAY,   (f, l) -> new Ant(true, f, l)),
        new EntityConfig(0.12, Snake.class,  Color.BLACK,  (f, l) -> new Snake(true, f, l)),
        new EntityConfig(0.11, Rat.class,    Color.PINK,   (f, l) -> new Rat(true, f, l)),
        new EntityConfig(0.12, Eagle.class,  Color.RED,    (f, l) -> new Eagle(true, f, l)),
        new EntityConfig(0.08, Emu.class,    Color.YELLOW, (f, l) -> new Emu(true, f, l)),
        new EntityConfig(0.34, Acacia.class, Color.GREEN,  (f, l) -> new Acacia(f, l)),
        new EntityConfig(0.36, Grass.class,  Color.CYAN,   (f, l) -> new Grass(f, l))
    );

    public void registerColors(SimulationObserver observer) {
        for (EntityConfig cfg : CONFIGS) {
            observer.onColorRegistered(cfg.entityClass, cfg.color);
        }
    }

    public void populate(Field field, List<LivingEntity> entities) {
        Random rand = Randomizer.getRandom();
        field.clear();
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                for (EntityConfig cfg : CONFIGS) {
                    if (rand.nextDouble() <= cfg.probability) {
                        entities.add(cfg.creator.apply(field, location));
                        break;
                    }
                }
            }
        }
    }
}
