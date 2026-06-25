import java.util.Random;
/**
 * General Utility class that solve generic issues that appear frequently
 *
 * @version 1.0
 */
public final class Utils
{

    private static Random rand = Randomizer.getRandom();
    
    /**
     * Throws an exception as a utility class cannot be instantiated.
     */
    private Utils()
    {
        throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    
    /**
     * Returns a random value from the set of possible values in an enumerator.
     * 
     * @param enumerator The enumaretor of a given class from which the values are taken.
     * @return T a random value from the set of possible values in an enumerator.
     */
    public static <T extends Enum<?>> T getRandomEnumValue(Class<T> enumerator){
        int x = rand.nextInt(enumerator.getEnumConstants().length);
        return enumerator.getEnumConstants()[x];
    }
    
    /**
     * Formats a String by adding padding to the right.
     * 
     * @param data String to which the padding is added.
     * @param n number of padding spaces.
     * @return a formatted String.
     */
    public static String padRight(String data, int n)
    {
       return String.format("%-" + n +  "s", data).replace(" ", "0");
    }
    
    /**
     * Formats a String by adding padding to the left.
     * 
     * @param data String to which the padding is added.
     * @param n number of padding spaces.
     * @return a formatted String.
     */
    public static String padLeft(String data, int n)
    {
        return String.format("%" + n + "s", data).replace(" ", "0");
    }
}
