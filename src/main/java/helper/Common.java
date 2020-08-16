package helper;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Common {
    public static URL getResource(String name){
        return Common.class.getClassLoader().getResource(name);
    }

    public static int randInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    public static int sortDoubleAsc(double el1, double el2){
        if (el1 > el2)
            return 1;
        else if (el1 < el2)
            return -1;
        else {
            return 0;
        }
    }

    public static int sortDoubleDesc(double el1, double el2){
        if (el1 < el2)
            return 1;
        else if (el1 > el2)
            return -1;
        else {
            return 0;
        }
    }

    public static Date convertStringToDate(String str) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(str);
    }

    public static String convertDateToString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }
}
