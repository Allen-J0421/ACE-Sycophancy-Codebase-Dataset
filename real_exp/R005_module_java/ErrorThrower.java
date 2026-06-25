import static javax.swing.JOptionPane.*;

/**
 * A helper class for other classes to be able to meaningfully
 * communicate errors with the user through message dialogues.
 *
 * @version 2022.03.02
 */
public class ErrorThrower
{
    /**
     * Communicates a specific message to the user through a message dialogue.
     *
     * @param message (String) The message to be passed to the user.
     */
    public void throwMessage(String message) {
        showMessageDialog(null, message);
    }
}
