/**
 * Marks an entity that can carry a Disease and exposes the minimal API
 * needed to read and set that disease state. Implemented by Organism so
 * that DiseaseManager can operate on any organism without depending on
 * a specific subclass.
 *
 * @version 2022.03.02
 */
public interface DiseaseAware
{
    /**
     * Returns true if this entity currently has a disease.
     */
    boolean isDiseased();

    /**
     * Returns the disease carried by this entity, or null if healthy.
     */
    Disease getDisease();

    /**
     * Infects this entity with the given disease.
     * @param disease The disease to apply.
     */
    void setDisease(Disease disease);
}
