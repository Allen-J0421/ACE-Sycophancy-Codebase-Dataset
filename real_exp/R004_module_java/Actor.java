import java.util.List;

/**
 * A class representing all acting entities in the simulation
 * 
 * Referenced from: Objects First with Java
 *
 * @version 2022.03.02
 */
public interface Actor
{
    void act(List<Actor> newActors, Environment environment);
    
    boolean isAlive();
    
}
