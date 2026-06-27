import java.awt.Color;

/**
 * SPI interface that each entity class implements to self-describe
 * its display color, creation probability, and how to instantiate itself.
 * Implementations are registered with EntityFactory at startup via
 * EntityFactory.register(EntityProvider).
 */
public interface EntityProvider {
    Class<?> getEntityClass();
    Color getColor();
    double getCreationProbability();
    LivingEntity create(Field field, Location location);
}
