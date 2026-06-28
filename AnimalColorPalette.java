import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Supplies the sequence of colors used to render animal species in the simulation.
 *
 * The palette hands out colors one at a time in a fixed order, keeping track of which
 * color comes next. It can be reset so a fresh simulation reuses the same colors in the
 * same order. The default palette holds 25 colors, so up to 25 distinct animal species
 * can be coloured; more colors can be added to support more species.
 *
 * @version 2022.03.01
 */
public class AnimalColorPalette
{
    // The colors available for animal species, handed out in order.
    private final List<Color> colors;
    // The index of the next color to hand out.
    private int nextColorIndex;

    /**
     * Build a palette pre-loaded with the default set of animal colors.
     */
    public AnimalColorPalette()
    {
        colors = buildDefaultColors();
        nextColorIndex = 0;
    }

    /**
     * Reset the palette so the next requested color is the first one again.
     */
    public void reset()
    {
        nextColorIndex = 0;
    }

    /**
     * Return the next color in the palette and advance the cursor.
     *
     * @return (Color) the next available color.
     */
    public Color nextColor()
    {
        Color color = colors.get(nextColorIndex);
        nextColorIndex++;
        return color;
    }

    /**
     * Build the default list of 25 colors used for animal species.
     *
     * @return (List<Color>) the default animal colors.
     */
    private static List<Color> buildDefaultColors()
    {
        List<Color> defaultColors = new ArrayList<>();
        defaultColors.add(Color.decode("0xFF1493"));
        defaultColors.add(Color.decode("0xFFA500"));
        defaultColors.add(Color.decode("0x007CFF"));
        defaultColors.add(Color.decode("0x44FF99"));
        defaultColors.add(Color.decode("0x7F0000"));
        defaultColors.add(Color.decode("0x00FFFF"));
        defaultColors.add(Color.decode("0xBECF33"));
        defaultColors.add(Color.decode("0x483D8B"));
        defaultColors.add(Color.decode("0x7F007F"));
        defaultColors.add(Color.decode("0xA020F0"));
        defaultColors.add(Color.decode("0x7E70CA"));
        defaultColors.add(Color.decode("0xFF9988"));
        defaultColors.add(Color.decode("0xFFFF00"));
        defaultColors.add(Color.decode("0x772D26"));
        defaultColors.add(Color.decode("0xBD7791"));
        defaultColors.add(Color.decode("0x808080"));
        defaultColors.add(Color.decode("0xD5A9F5"));
        defaultColors.add(Color.decode("0xFFB6C1"));
        defaultColors.add(Color.decode("0xFFE378"));
        defaultColors.add(Color.decode("0x00008B"));
        defaultColors.add(Color.decode("0x808000"));
        defaultColors.add(Color.decode("0x8FBC8F"));
        defaultColors.add(Color.decode("0xFF0000"));
        defaultColors.add(Color.decode("0x008B8B"));
        defaultColors.add(Color.decode("0xADD8E6"));
        return defaultColors;
    }
}
