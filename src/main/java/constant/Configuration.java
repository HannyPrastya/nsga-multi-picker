package constant;

public class Configuration {
    public static int numberOfObjetives = 2;
//    true -> ascending
//    false -> descending
    public static boolean[] objTypes = {true, true};
    public static int activeObjetive = 0;

    public static final int S_SHAPE = 1;
    public static final int LARGEST_GAP = 2;
    public static final int COMBINED_PLUS = 3;

//    WAREHOUSE CONFIGURATION
    public static int numberOfHorizontalAisle = 4;
    public static int numberOfVerticalAisle = 6;
    public static int numberOfItemPerAisleSide = 8;

    //    ALGO CONFIGURATION
    public static int totalRun = 3;
    public static int routeAlgorithm = S_SHAPE;
    public static int numberOfPopulation = 20;
    public static int numberOfGeneration = 100;

}
