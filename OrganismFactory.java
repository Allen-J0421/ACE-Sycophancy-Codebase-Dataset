/**
 * Creates organism instances for reproduction and population seeding.
 *
 * @version 2022.03.02
 */
public interface OrganismFactory
{
    Organism create(boolean randomAge, Field field, Location location);
}
