/**
 * A cell in the simulation grid. Each GridSpace holds at most one Animal
 * and one Plant simultaneously.
 *
 * @version 26/02/2022
 */
public class GridSpace
{
    private Animal animal;
    private Plant plant;

    /**
     * @return The animal in this grid space, or null if none.
     */
    public Animal getAnimal()
    {
        return animal;
    }

    /**
     * @return The plant in this grid space, or null if none.
     */
    public Plant getPlant()
    {
        return plant;
    }

    /**
     * @param objectType The type of object to retrieve (Animal.class or Plant.class).
     * @return The occupant of the requested type, or null if absent.
     */
    public Object getObject(Class<?> objectType)
    {
        if(objectType.equals(Animal.class)) {
            return animal;
        }
        if(objectType.equals(Plant.class)) {
            return plant;
        }
        return null;
    }

    /**
     * @param animal The animal to place in this grid space.
     */
    public void setAnimal(Animal animal)
    {
        this.animal = animal;
    }

    /**
     * @param plant The plant to place in this grid space.
     */
    public void setPlant(Plant plant)
    {
        this.plant = plant;
    }

    /**
     * Places the object in the appropriate slot based on its runtime type.
     *
     * @param object The Animal or Plant to store.
     */
    public void setObject(Object object)
    {
        if(object instanceof Animal) {
            setAnimal((Animal) object);
        }
        else if(object instanceof Plant) {
            setPlant((Plant) object);
        }
    }

    /**
     * Removes all occupants from this grid space.
     */
    public void clear()
    {
        animal = null;
        plant = null;
    }

    /**
     * Removes the occupant of the specified type.
     *
     * @param objectType The type to clear (Animal.class or Plant.class).
     */
    public void clear(Class<?> objectType)
    {
        if(objectType.equals(Animal.class)) {
            animal = null;
        }
        else if(objectType.equals(Plant.class)) {
            plant = null;
        }
    }
}
