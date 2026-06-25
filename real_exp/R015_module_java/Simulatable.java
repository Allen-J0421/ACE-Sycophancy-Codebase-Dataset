
import java.util.List;


/** 
 * This is an interface which enforces all functionality requried to add a player to the sim 
 * 
 * @version 2.9.2
 */
interface Simulatable {


	/**
	 * Should be called to define a players actions and how they respond to the environment 
	 */ 
	public void act(List<Simulatable> newAnimals, SimulatorState currentState);

	/**
	 * Should return if a player is alive
	 */ 
	public boolean isAlive();
    
    /**
     * Will kill the player
     */
    public void setDead();


}