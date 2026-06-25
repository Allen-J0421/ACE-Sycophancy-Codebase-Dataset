import java.awt.Color;


/**
 * An interface enforcing all the required functionality of an animal that can be displayed
 */
interface Displayable {

	/**
	 * Should return the color of an animal
	 */
	public Color getColor (); 

	/**
	 * Should return the border color of an animal
	 */
	public Color getBorderColor();
}