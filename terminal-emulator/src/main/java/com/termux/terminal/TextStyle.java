package com.termux.terminal;

/**
 * <p>
 * Encodes effects, foreground and background colors into a 64 bit long, which are stored for each cell in a terminal
 * row in {@link TerminalRow#mStyle}.
 * </p>
 * <p>
 * The bit layout is:
 * </p>
 * - 16 flags (11 currently used).
 * - 24 for foreground color (only 9 first bits if a color index).
 * - 24 for background color (only 9 first bits if a color index).
 */
public final class TextStyle {

    public final static int CHARACTER_ATTRIBUTE_BOLD = 1;
    public final static int CHARACTER_ATTRIBUTE_ITALIC = 1 << 1;
    public final static int CHARACTER_ATTRIBUTE_UNDERLINE = 1 << 2;
    public final static int CHARACTER_ATTRIBUTE_BLINK = 1 << 3;
    public final static int CHARACTER_ATTRIBUTE_INVERSE = 1 << 4;
    public final static int CHARACTER_ATTRIBUTE_INVISIBLE = 1 << 5;
    public final static int CHARACTER_ATTRIBUTE_STRIKETHROUGH = 1 << 6;
    /**
     * The selective erase control functions (DECSED and DECSEL) can only erase characters defined as erasable.
     * <p>
     * This bit is set if DECSCA (Select Character Protection Attribute) has been used to define the characters that
     * come after it as erasable from the screen.
     * </p>
     */
    public final static int CHARACTER_ATTRIBUTE_PROTECTED = 1 << 7;
    /** Dim colors. Also known as faint or half intensity. */
    public final static int CHARACTER_ATTRIBUTE_DIM = 1 << 8;
    /** If true (24-bit) color is used for the cell for foreground. */
    private final static int CHARACTER_ATTRIBUTE_TRUECOLOR_FOREGROUND = 1 << 9;
    /** If true (24-bit) color is used for the cell for foreground. */
    private final static int CHARACTER_ATTRIBUTE_TRUECOLOR_BACKGROUND= 1 << 10;

    public final static int COLOR_INDEX_FOREGROUND = 256;
    public final static int COLOR_INDEX_BACKGROUND = 257;
    public final static int COLOR_INDEX_CURSOR = 258;

    /** The 256 standard color entries and the three special (foreground, background and cursor) ones. */
    public final static int NUM_INDEXED_COLORS = 259;

    /** Bit offset of the 24-bit foreground color field within an encoded style. */
    private final static int FOREGROUND_COLOR_SHIFT = 40;
    /** Bit offset of the 24-bit background color field within an encoded style. */
    private final static int BACKGROUND_COLOR_SHIFT = 16;
    /** Mask for a 9-bit indexed color, enough to hold any of the {@link #NUM_INDEXED_COLORS} indices. */
    private final static long COLOR_INDEX_MASK = 0b111111111L;
    /** Mask for a 24-bit true (RGB) color. */
    private final static long TRUECOLOR_MASK = 0x00ffffffL;
    /** The high alpha byte marking a value as a 24-bit color rather than a color index. */
    private final static int TRUECOLOR_ALPHA = 0xff000000;

    /** Normal foreground and background colors and no effects. */
    final static long NORMAL = encode(COLOR_INDEX_FOREGROUND, COLOR_INDEX_BACKGROUND, 0);

    static long encode(int foreColor, int backColor, int effect) {
        long result = effect & 0b111111111;
        if ((TRUECOLOR_ALPHA & foreColor) == TRUECOLOR_ALPHA) {
            // 24-bit color.
            result |= CHARACTER_ATTRIBUTE_TRUECOLOR_FOREGROUND | ((foreColor & TRUECOLOR_MASK) << FOREGROUND_COLOR_SHIFT);
        } else {
            // Indexed color.
            result |= (foreColor & COLOR_INDEX_MASK) << FOREGROUND_COLOR_SHIFT;
        }
        if ((TRUECOLOR_ALPHA & backColor) == TRUECOLOR_ALPHA) {
            // 24-bit color.
            result |= CHARACTER_ATTRIBUTE_TRUECOLOR_BACKGROUND | ((backColor & TRUECOLOR_MASK) << BACKGROUND_COLOR_SHIFT);
        } else {
            // Indexed color.
            result |= (backColor & COLOR_INDEX_MASK) << BACKGROUND_COLOR_SHIFT;
        }

        return result;
    }

    public static int decodeForeColor(long style) {
        if ((style & CHARACTER_ATTRIBUTE_TRUECOLOR_FOREGROUND) == 0) {
            return (int) ((style >>> FOREGROUND_COLOR_SHIFT) & COLOR_INDEX_MASK);
        } else {
            return TRUECOLOR_ALPHA | (int) ((style >>> FOREGROUND_COLOR_SHIFT) & TRUECOLOR_MASK);
        }

    }

    public static int decodeBackColor(long style) {
        if ((style & CHARACTER_ATTRIBUTE_TRUECOLOR_BACKGROUND) == 0) {
            return (int) ((style >>> BACKGROUND_COLOR_SHIFT) & COLOR_INDEX_MASK);
        } else {
            return TRUECOLOR_ALPHA | (int) ((style >>> BACKGROUND_COLOR_SHIFT) & TRUECOLOR_MASK);
        }
    }

    public static int decodeEffect(long style) {
        return (int) (style & 0b11111111111);
    }

}
