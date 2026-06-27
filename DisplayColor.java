/**
 * UI-framework-neutral color value for simulation displays.
 *
 * @version 2022.03.02
 */
public class DisplayColor
{
    public static final DisplayColor WHITE = new DisplayColor(255, 255, 255);
    public static final DisplayColor GRAY = new DisplayColor(128, 128, 128);
    public static final DisplayColor DARK_GRAY = new DisplayColor(64, 64, 64);
    public static final DisplayColor MAGENTA = new DisplayColor(255, 0, 255);
    public static final DisplayColor RED = new DisplayColor(255, 0, 0);
    public static final DisplayColor ORANGE = new DisplayColor(255, 200, 0);
    public static final DisplayColor YELLOW = new DisplayColor(255, 255, 0);
    public static final DisplayColor GREEN = new DisplayColor(0, 255, 0);

    private final int red;
    private final int green;
    private final int blue;

    public DisplayColor(int red, int green, int blue)
    {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public int getRed()
    {
        return red;
    }

    public int getGreen()
    {
        return green;
    }

    public int getBlue()
    {
        return blue;
    }
}
