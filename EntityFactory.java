import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Central factory that maintains an ordered registry of EntityProviders.
 * Concrete entity types are registered at startup via register(); the
 * order of registration determines the RNG call sequence in populate().
 */
public class EntityFactory {

    private static final List<EntityProvider> registry = new ArrayList<>();

    /** Register a provider. Call order determines populate() priority. */
    public static void register(EntityProvider provider) {
        registry.add(provider);
    }

    public void registerColors(EventBus bus) {
        for (EntityProvider p : registry) {
            bus.publish(new ColorRegistrationEvent(p.getEntityClass(), p.getColor()));
        }
    }

    public void populate(Field field, List<LivingEntity> entities) {
        Random rand = Randomizer.getRandom();
        field.clear();
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                for (EntityProvider p : registry) {
                    if (rand.nextDouble() <= p.getCreationProbability()) {
                        entities.add(p.create(field, location));
                        break;
                    }
                }
            }
        }
    }
}
