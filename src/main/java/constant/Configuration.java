package constant;

public class Configuration {
    public static boolean isLogEnabled = false;
    public static String startTime = "2020-01-01 08:00:00";
    public static int numberOfObjetives = 2;
//    true -> ascending
//    false -> descending
    public static boolean[] objTypes = {true, true};

//    ALGO TYPE
    public static final int BASED = 1;
    public static final int SKU = 2;
    public static final int PROPOSED = 3;

//    ROUTING ALGORITHM
    public static final int S_SHAPE = 1;
    public static final int LARGEST_GAP = 2;
    public static final int COMBINED_PLUS = 3;

//    WAREHOUSE CONFIGURATION
    public static int numberOfHorizontalAisle = 6;
    public static int numberOfVerticalAisle = 12;
    public static int numberOfItemPerAisleSide = 12;

    //    ALGO CONFIGURATION
    public static int[][] datasetConfiguration = {
        {200, 200, 3, 4, 4},
        {840, 200, 3, 12, 4},
    };

    public static boolean renewDataset = false;
    public static int algorithm = PROPOSED;
    public static int routeAlgorithm = LARGEST_GAP;
    public static int numberOfPopulation = 20;
    public static int numberOfGeneration = 100;
}
