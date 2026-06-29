package com.termux.terminal;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

/**
 * Characterization (golden-master) tests for {@link TerminalBuffer}, exercising it directly rather than through the
 * Android-dependent {@link TerminalEmulator}. These pin down the currently observed behaviour so that structural
 * refactorings of {@link TerminalBuffer} can be verified to be behaviour-preserving.
 *
 * <p>The {@link #fingerprint(TerminalBuffer)} helper hashes the complete internal cell state (every line's chars,
 * per-cell styles and the line-wrap flag) of the whole active area, so any change to how characters or styles end up
 * laid out in the buffer changes the hash. Expected hashes were captured from the unmodified implementation.</p>
 */
public class TerminalBufferCharacterizationTest extends TestCase {

    /** Expected fingerprints captured from the original implementation, keyed by checkpoint name. */
    private static final Map<String, Long> EXPECTED = new HashMap<>();
    static {
        EXPECTED.put("basic/ctor", 7384354285228960233L);
        EXPECTED.put("basic/written", -2311945985331944720L);
        EXPECTED.put("basic/scrolled", 5289191286142039574L);
        EXPECTED.put("basic/multiScroll", -1817306948014957299L);
        EXPECTED.put("wide/written", -8654449338326021448L);
        EXPECTED.put("block/set", 8143831699835481705L);
        EXPECTED.put("block/copy", -1797499800559048057L);
        EXPECTED.put("block/effect", -5466783441547723373L);
        EXPECTED.put("resizeFast/shrink", -1839959978485033550L);
        EXPECTED.put("resizeFast/expand", 1213315745879384306L);
        EXPECTED.put("resizeReflow/done", 4224870245243769065L);
        EXPECTED.put("resizeReflow/blankAndScroll", 8335931869241220076L);
        EXPECTED.put("resizeReflow/expandCols", 5730689743749656787L);
        // Cursor-placement checkpoints (fingerprint above ignores cursor[]).
        EXPECTED.put("resizeFast/shrink.cur", 12884901890L);
        EXPECTED.put("resizeFast/expand.cur", 12884901893L);
        EXPECTED.put("resizeReflow/done.cur", 17179869187L);
        EXPECTED.put("resizeReflow/blankAndScroll.cur", 8589934592L);
        EXPECTED.put("resizeReflow/expandCols.cur", 21474836480L);
        EXPECTED.put("resizeReflow/scrollFlush", 1337946018642690756L);
        EXPECTED.put("resizeReflow/scrollFlush.cur", 0L);
    }

    /** When true the tests only print observed fingerprints (used to capture the golden baseline) and assert nothing. */
    private static final boolean CAPTURE = Boolean.getBoolean("characterization.capture");

    private void check(String name, long value) {
        if (CAPTURE) {
            System.out.println("FP " + name + " " + value);
            return;
        }
        Long expected = EXPECTED.get(name);
        assertNotNull("No expected fingerprint registered for " + name, expected);
        assertEquals("Fingerprint changed for checkpoint " + name, (long) expected, value);
    }

    private void check(String name, TerminalBuffer buffer) {
        check(name, fingerprint(buffer));
    }

    /**
     * Pin the (column,row) cursor pair produced by a resize. The fingerprint hashes cell state only, so cursor
     * placement (which the reflow's cursor-adjustment logic affects) needs its own checkpoint.
     */
    private void checkCursor(String name, int[] cursor) {
        check(name, ((long) cursor[0] << 32) ^ (cursor[1] & 0xffffffffL));
    }

    /** Hash the full internal cell state of every active line (transcript + screen). */
    private static long fingerprint(TerminalBuffer b) {
        long h = 1125899906842597L;
        h = 31 * h + b.mColumns;
        h = 31 * h + b.mScreenRows;
        h = 31 * h + b.mTotalRows;
        h = 31 * h + b.getActiveTranscriptRows();
        for (int row = -b.getActiveTranscriptRows(); row < b.mScreenRows; row++) {
            TerminalRow line = b.mLines[b.externalToInternalRow(row)];
            if (line == null) {
                h = 31 * h + 7;
                continue;
            }
            h = 31 * h + (line.mLineWrap ? 2 : 3);
            int used = line.getSpaceUsed();
            h = 31 * h + used;
            for (int i = 0; i < used; i++) h = 31 * h + line.mText[i];
            for (int i = 0; i < b.mColumns; i++) h = 31 * h + line.getStyle(i);
        }
        return h;
    }

    /** Write an ASCII string starting at (col, row), one cell per char. */
    private static void writeString(TerminalBuffer b, int row, int col, String s) {
        for (int i = 0; i < s.length(); i++) b.setChar(col + i, row, s.charAt(i), TextStyle.NORMAL);
    }

    // ---- Fingerprint (golden-master) scenarios -------------------------------------------------

    public void testFingerprintBasicAndScrolling() {
        TerminalBuffer b = new TerminalBuffer(8, 12, 4);
        check("basic/ctor", b);

        writeString(b, 0, 0, "abcdEFGH");
        writeString(b, 1, 0, "1234");
        writeString(b, 2, 2, "xy");
        check("basic/written", b);

        b.scrollDownOneLine(0, 4, TextStyle.NORMAL);
        check("basic/scrolled", b);

        for (int i = 0; i < 6; i++) {
            writeString(b, 3, 0, "row" + i);
            b.scrollDownOneLine(0, 4, TextStyle.NORMAL);
        }
        check("basic/multiScroll", b);
    }

    public void testFingerprintWideAndCombining() {
        TerminalBuffer b = new TerminalBuffer(8, 8, 3);
        // Wide CJK char occupying columns 0-1.
        b.setChar(0, 0, 0x4E16, TextStyle.NORMAL);
        // Base char + combining acute accent in the same column.
        b.setChar(2, 0, 'e', TextStyle.NORMAL);
        b.setChar(2, 0, 0x0301, TextStyle.NORMAL);
        // Supplementary-plane (surrogate pair) char.
        b.setChar(4, 0, 0x1F600, TextStyle.NORMAL);
        check("wide/written", b);
    }

    public void testFingerprintBlockOps() {
        TerminalBuffer b = new TerminalBuffer(10, 10, 6);
        writeString(b, 0, 0, "ABCDEFGHIJ");
        writeString(b, 1, 0, "klmnopqrst");
        writeString(b, 2, 0, "0123456789");

        b.blockSet(2, 1, 3, 2, '#', TextStyle.encode(1, 2, TextStyle.CHARACTER_ATTRIBUTE_BOLD));
        check("block/set", b);

        b.blockCopy(0, 0, 4, 3, 5, 2);
        check("block/copy", b);

        b.setOrClearEffect(TextStyle.CHARACTER_ATTRIBUTE_UNDERLINE, true, false, true, 0, 10, 0, 1, 3, 6);
        check("block/effect", b);
    }

    public void testFingerprintResizeFast() {
        TerminalBuffer b = new TerminalBuffer(10, 50, 6);
        writeString(b, 0, 0, "line0");
        writeString(b, 1, 0, "line1");
        writeString(b, 2, 0, "line2");
        // Build some transcript.
        for (int i = 0; i < 4; i++) b.scrollDownOneLine(0, 6, TextStyle.NORMAL);

        int[] cursor = {3, 2};
        // Same columns, fewer rows -> fast path (shrink).
        b.resize(10, 4, 50, cursor, TextStyle.NORMAL, false);
        check("resizeFast/shrink", b);
        checkCursor("resizeFast/shrink.cur", cursor);

        // Same columns, more rows -> fast path (expand, pulls from transcript).
        b.resize(10, 7, 50, cursor, TextStyle.NORMAL, false);
        check("resizeFast/expand", b);
        checkCursor("resizeFast/expand.cur", cursor);
    }

    public void testFingerprintResizeReflow() {
        TerminalBuffer b = new TerminalBuffer(10, 50, 5);
        writeString(b, 0, 0, "abcdefghij");
        b.setLineWrap(0);
        writeString(b, 1, 0, "klmno");
        writeString(b, 2, 0, "PQRSTUVWXY");

        int[] cursor = {4, 2};
        // Column change -> full reflow path.
        b.resize(6, 5, 50, cursor, TextStyle.NORMAL, false);
        check("resizeReflow/done", b);
        checkCursor("resizeReflow/done.cur", cursor);
    }

    /**
     * Reflow with a blank line in the middle (exercising the skipped-blank-line flush) and enough wrapped content to
     * overflow the small screen, forcing scrolling while the cursor has already been placed on an early row.
     */
    public void testFingerprintResizeReflowBlankAndScroll() {
        TerminalBuffer b = new TerminalBuffer(8, 50, 4);
        writeString(b, 0, 0, "12345678");
        b.setLineWrap(0);
        // Row 1 left blank on purpose (blank line between non-blank rows).
        writeString(b, 2, 0, "ABCDEFGH");
        b.setLineWrap(2);
        writeString(b, 3, 0, "xy");

        int[] cursor = {2, 0}; // cursor on the first (early) row
        b.resize(4, 4, 50, cursor, TextStyle.NORMAL, false);
        check("resizeReflow/blankAndScroll", b);
        checkCursor("resizeReflow/blankAndScroll.cur", cursor);
    }

    /**
     * Reflow where the cursor is placed on an early row, output is then driven down to the last row by a long
     * wrapped line, and a blank line sits just before more content -- so the skipped-blank-line flush scrolls the
     * screen <em>while the cursor is already placed</em>. This is the exact case the {@code adjustPlacedCursor=false}
     * argument in {@code advanceReflowRow} governs, so the cursor checkpoint pins it.
     */
    public void testFingerprintResizeReflowScrollDuringFlush() {
        TerminalBuffer b = new TerminalBuffer(8, 50, 2);
        writeString(b, 0, 0, "12345678"); // wraps to fill both new rows on reflow to 4 columns
        b.setLineWrap(0);
        // Row 1 left blank; the blank flush happens once the next content row is reached after scrolling occurs.
        // Build a transcript row of content below so there is a non-blank row after the blank.
        b.scrollDownOneLine(0, 2, TextStyle.NORMAL);
        writeString(b, 1, 0, "ABCD");

        int[] cursor = {0, -1}; // cursor on the (now transcript) wrapped row, column 0
        b.resize(4, 2, 50, cursor, TextStyle.NORMAL, false);
        check("resizeReflow/scrollFlush", b);
        checkCursor("resizeReflow/scrollFlush.cur", cursor);
    }

    /** Reflow that widens the columns, re-joining previously wrapped lines. */
    public void testFingerprintResizeReflowExpandCols() {
        TerminalBuffer b = new TerminalBuffer(4, 50, 6);
        writeString(b, 0, 0, "abcd");
        b.setLineWrap(0);
        writeString(b, 1, 0, "efgh");
        b.setLineWrap(1);
        writeString(b, 2, 0, "ij");

        int[] cursor = {1, 1};
        b.resize(8, 6, 50, cursor, TextStyle.NORMAL, false);
        check("resizeReflow/expandCols", b);
        checkCursor("resizeReflow/expandCols.cur", cursor);
    }

    // ---- Readable behaviour assertions ---------------------------------------------------------

    public void testTranscriptText() {
        TerminalBuffer b = new TerminalBuffer(10, 20, 4);
        writeString(b, 0, 0, "Hello");
        writeString(b, 1, 0, "World");
        assertEquals("Hello\nWorld", b.getTranscriptText());
    }

    public void testLineWrapJoining() {
        TerminalBuffer b = new TerminalBuffer(5, 20, 4);
        writeString(b, 0, 0, "ABCDE");
        b.setLineWrap(0);
        writeString(b, 1, 0, "FG");
        // With back-lines joined (default) the wrapped row continues without a newline.
        assertEquals("ABCDEFG", b.getTranscriptText());
        // Without joining, the wrap becomes a newline.
        assertEquals("ABCDE\nFG", b.getTranscriptTextWithoutJoinedLines());
    }

    public void testGetWordAtLocation() {
        TerminalBuffer b = new TerminalBuffer(20, 20, 3);
        writeString(b, 0, 0, "the quick brown");
        assertEquals("quick", b.getWordAtLocation(5, 0));
        assertEquals("the", b.getWordAtLocation(1, 0));
        assertEquals("", b.getWordAtLocation(3, 0)); // on the space
    }

    public void testExternalToInternalRow() {
        TerminalBuffer b = new TerminalBuffer(10, 20, 5);
        assertEquals(0, b.externalToInternalRow(0));
        assertEquals(4, b.externalToInternalRow(4));
        b.scrollDownOneLine(0, 5, TextStyle.NORMAL);
        // After one scroll the screen start moved down by one in the ring buffer.
        assertEquals(1, b.externalToInternalRow(0));
        assertEquals(0, b.externalToInternalRow(-1)); // transcript row maps back
        assertEquals(1, b.getActiveTranscriptRows());
        assertEquals(6, b.getActiveRows());
    }

    public void testClearTranscript() {
        TerminalBuffer b = new TerminalBuffer(10, 20, 4);
        for (int i = 0; i < 5; i++) b.scrollDownOneLine(0, 4, TextStyle.NORMAL);
        assertEquals(5, b.getActiveTranscriptRows());
        b.clearTranscript();
        assertEquals(0, b.getActiveTranscriptRows());
        assertEquals(4, b.getActiveRows());
    }
}
