package lobstre.chtrie;

public class TestHelper {

    public static void assertEquals (long expected, long found) {
        if (expected != found) {
            new Exception ("Error : expected: " + expected + " found: " + found).printStackTrace ();
        }
    }

    public static void assertEquals (int expected, int found) {
        if (expected != found) {
            new Exception ("Error : expected: " + expected + " found: " + found).printStackTrace ();
        }
    }

    public static void assertEquals (Object expected, Object found) {
        if ((expected == null || found == null) && expected != found) {
            new Exception ("Error : expected: " + expected + " found: " + found).printStackTrace ();
        } else if (!expected.equals (found)) {
            new Exception ("Error : expected: " + expected + " found: " + found).printStackTrace ();
        }
    }

}
