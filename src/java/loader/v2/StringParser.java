package loader.v2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The StringParser provides static methods to parse strings.
 * It is used in the OBJLoader
 */
public class StringParser {

    // ========== Methods ==========


    public static float[] parseStringToFloatArray(String s) {
        if (s == null || s.equals("")) return null;

        // Removing spaces
        String[] strFloats = s.split("\\s+");
        // Preparing the result float array
        float[] res = new float[strFloats.length - 1];

        for (int i = 0; i < res.length; i++) {
            // Ignore first String that is the keyword (f, v, vt...)
            res[i] = Float.parseFloat(strFloats[i + 1]);
        }

        return res;
    }

    public static int[] parseStringToIntArray(String s) {
        if (s == null || s.equals("")) return null;

        // Removing spaces
        String[] strInts = s.split("\\s+");
        // Preparing the result float array
        int[] res = new int[strInts.length - 1];

        for (int i = 0; i < res.length; i++) {
            // Ignore first String that is the keyword (f, v, vt...)
            res[i] = Integer.parseInt(strInts[i + 1]);
        }
        return res;
    }

    public static int[] parseMultipleVertices(String s, int elemspertuple) {
        if (s == null || s.equals("")) return null;

        String[] vertices = parseWhiteSpaces(s);
        ArrayList<Integer> resList = new ArrayList<>();

        for (int i = 0; i < vertices.length; i++) {
            parseVertice(s, resList, elemspertuple);
        }

        int resArray[] = new int[resList.size()];
        for (int i = 0; i < resList.size(); i++) {
            resArray[i] = resList.get(i);
        }

        return resArray;
    }

    public static void parseVertice(String s, ArrayList<Integer> returnList, int elemspertruple) {
        // Parse on the slashes
        String[] tmp = s.split(" ");
        List<String> tmplist = new ArrayList<>();
        for (String subs : tmp){
            String[] subselems = subs.trim().split("/");
            for (String ss : subselems){
                tmplist.add(ss);
            }
        }
        String[] numbers = new String[tmplist.size()];
        numbers = tmplist.toArray(numbers);

        int cpt = 0;
        int index = 0;
        while (index < numbers.length) {
            if (numbers[index].trim().equals("")) returnList.add(Integer.MIN_VALUE);
            else returnList.add(Integer.parseInt(numbers[index]));
            index++;
            cpt++;
        }
        while (cpt < elemspertruple){
            returnList.add(Integer.MIN_VALUE);
            cpt++;
        }
    }

    public static String[] parseWhiteSpaces(String s) {
        if (s == null || s.equals("")) return null;
        // Removing spaces
        return s.split("\\s+");
    }

}
