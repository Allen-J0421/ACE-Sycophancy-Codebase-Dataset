import java.util.List;

/**
 * Common lifecycle interface for all entities in the simulation.
 * Allows animals and plants to be managed in a single polymorphic collection.
 */
public interface LivingEntity {
    void act(List<LivingEntity> newEntities, int time);
    boolean isAlive();
    void setDead();
}
