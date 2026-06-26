public class HeadlessTest {
    public static void main(String[] a) {
        System.out.println("headless=" + java.awt.GraphicsEnvironment.isHeadless());
        try {
            javax.swing.JFrame f = new javax.swing.JFrame("t");
            f.pack();
            System.out.println("JFrame OK");
        } catch (Throwable t) {
            System.out.println("JFrame FAILED: " + t.getClass().getName() + ": " + t.getMessage());
        }
    }
}
