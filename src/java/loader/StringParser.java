package loader;

import java.util.ArrayList;

/**
 * The StringParser provides static methods to parse strings.
 * It is used in the OBJLoader
 */
public class StringParser {

    // ========== Methods ==========

    /**
     * Parses a string containing float values into a float array.
     * @param numFloats The number of floats to read
     * @param list The string to parse
     * @param startIndex The number of characters to ignore first
     * @return The float array resulting from the parsing
     */
    public static float[] parseStringToFloatArray(int numFloats, String list, int startIndex) {
        if (list == null || list.equals("")) return null;

        // Copy list into a char array
        char[] listChars = new char[list.length()];
        int listLength = listChars.length;
        list.getChars(0, list.length(), listChars, 0);

        float[] returnArray = new float[numFloats];
        int returnArrayCount = 0;

        int count = startIndex;
        int itemStart;
        int itemEnd;
        int itemLength;

        // For every character of the string
        while (count < listLength) {
            // Skip any whitespace
            itemEnd = skipWhiteSpace(count, listChars);
            count = itemEnd;
            if (count >= listLength) break;

            itemStart = count;
            itemEnd = itemStart;
            // Gather every non-space character by stocking its index start and end
            while (itemEnd < listLength) {
                if ((listChars[itemEnd] != ' ') && (listChars[itemEnd] != '\n') && (listChars[itemEnd] != '\t')) {
                    itemEnd++;
                } else break;
            }

            itemLength = itemEnd - itemStart;
            // Parse the item into a float number
            returnArray[returnArrayCount++] = Float.parseFloat(new String(listChars, itemStart, itemLength));

            // Break if all the desired floats are assembled
            if (returnArrayCount >= numFloats) break;
            count = itemEnd;
        }

        return returnArray;
    }

    /**
     * Parses a string containing int values into an int array.
     * Follows the same pattern as parseStringToFloatArray.
     * @param list The string to parse
     * @param startIndex The number of characters to ignore first
     * @return
     */
    public static int[] parseStringToIntArray(String list, int startIndex) {
        if (list == null || list.equals("")) return null;

        // Copy list into a char array
        char[] listChars = new char[list.length()];
        int listLength = listChars.length;
        list.getChars(0, list.length(), listChars, 0);

        ArrayList<Integer> returnList = new ArrayList<Integer>();
        int returnArrayCount = 0;

        int count = startIndex;
        int itemStart;
        int itemEnd;
        int itemLength;

        while (count < listLength) {
            // Skip any leading whitespace
            itemEnd = skipWhiteSpace(count, listChars);
            count = itemEnd;
            if (count >= listLength) {
                break;
            }
            itemStart = count;
            itemEnd = itemStart;
            while (itemEnd < listLength) {
                if ((listChars[itemEnd] != ' ') && (listChars[itemEnd] != '\n') && (listChars[itemEnd] != '\t')) {
                    itemEnd++;
                } else {
                    break;
                }
            }
            itemLength = itemEnd - itemStart;
            returnList.add(Integer.parseInt(new String(listChars, itemStart, itemLength)));

            count = itemEnd;
        }

        int[] returnArray;
        returnArray = new int[returnList.size()];
        for (int loopi = 0; loopi < returnList.size(); loopi++) {
            returnArray[loopi] = returnList.get(loopi);
        }

        return returnArray;
    }

    /**
     * Parses the given string representing some vertices into an int array representing them.
     * @param s The string to parse
     * @param elemsPerTuple The size of the tuples
     * @return The int array representing the vertices
     */
    public static int[] parseMultipleVertices(String s, int elemsPerTuple) {
        if (s == null || s.equals("")) {
            return null;
        }

        String[] vertexStrings = parseWhiteSpaces(s);

        ArrayList<Integer> returnList = new ArrayList<>();
        for (String vertexString : vertexStrings) {
            parseVertices(vertexString, returnList, elemsPerTuple);
        }

        int[] returnArray = new int[returnList.size()];
        for (int loopi = 0; loopi < returnList.size(); loopi++) {
            returnArray[loopi] = returnList.get(loopi);
        }
        return returnArray;
    }

    /**
     * Parses the given string representing a vertices tuple into ints and adds this to the given int array.
     * @param s The string to parse
     * @param returnList The array to modify by adding the int vertices information
     * @param elemsPerTuple The size of the tuples
     */
    public static void parseVertices(String s, ArrayList<Integer> returnList, int elemsPerTuple) {
        // Parse on the slashes (a 3-sized tuple example : 4/5/7)
        String[] numbers = parseList('/', s);
        int foundCount = 0;

        int index = 0;
        while (index < numbers.length) {
            if (numbers[index].trim().equals("")) {
                returnList.add(Integer.MIN_VALUE);
            } else {
                returnList.add(Integer.parseInt(numbers[index]));
            }
            foundCount++;
            index++;
        }
        while (foundCount < elemsPerTuple) {
            returnList.add(Integer.MIN_VALUE);
            foundCount++;
        }
    }

    /**
     * Parses the given string into sub-strings, divided by white spaces.
     * @param s The string to parse
     * @return The result string array
     */
    public static String[] parseWhiteSpaces(String s) {
        if (s == null || s.equals("")) return null;
        // Removing spaces

        ArrayList<String> returnVec = new ArrayList<>();
        String[] returnArray;

        // Copy list into a char array.
        char[] listChars;
        listChars = new char[s.length()];
        s.getChars(0, s.length(), listChars, 0);

        int count = 0;
        int itemStart = 0;
        int itemEnd = 0;
        String newItem;

        while (count < listChars.length) {
            // Skip any leading whitespace
            itemEnd = skipWhiteSpace(count, listChars);
            count = itemEnd;
            if (count >= listChars.length) {
                break;
            }
            itemStart = count;
            itemEnd = itemStart;
            while (itemEnd < listChars.length) {
                if ((listChars[itemEnd] != ' ') && (listChars[itemEnd] != '\n') && (listChars[itemEnd] != '\t')) {
                    itemEnd++;
                } else {
                    break;
                }
            }
            newItem = new String(listChars, itemStart, itemEnd - itemStart);
            itemEnd++;
            count = itemEnd;
            returnVec.add(newItem);
        }
        // Convert from vector to array, and return it.
        returnArray = new String[1];
        returnArray = (String[]) returnVec.toArray((Object[]) returnArray);
        return returnArray;
    }

    /**
     * Detect the next non-whitespaces from a char array
     * @param mCount count non whitespaces chars
     * @param messageChars array of chars to process
     * @return the index of the next non-whitespaces char of the array
     */
    public static int skipWhiteSpace(int mCount, char[] messageChars) {
        // Skip whitespace
        while (mCount < messageChars.length) {
            if (messageChars[mCount] == ' ' || messageChars[mCount] == '\n' || messageChars[mCount] == '\t') {
                mCount++;
            } else break;
        }
        return mCount;
    }

    /**
     * Parse a string into an array in function of a certain delimiter
     * @param delim parsing delimiter
     * @param list list to parse
     * @return a vector converted into an array
     */
    public static String[] parseList(char delim, String list) {
        if (list == null || list.equals("")) return null;

        ArrayList<String> returnVec = new ArrayList<String>();
        String[] returnArray = null;

        // Copy list into a char array.
        char[] listChars;
        listChars = new char[list.length()];
        list.getChars(0, list.length(), listChars, 0);

        int count = 0;
        int itemStart = 0;
        int itemEnd = 0;
        String newItem = null;

        while (count < listChars.length) {
            count = itemEnd;
            if (count >= listChars.length) {
                break;
            }
            itemStart = count;
            itemEnd = itemStart;
            while (itemEnd < listChars.length) {
                if (delim != listChars[itemEnd]) {
                    itemEnd++;
                } else {
                    break;
                }
            }
            newItem = new String(listChars, itemStart, itemEnd - itemStart);
            itemEnd++;
            count = itemEnd;
            returnVec.add(newItem);
        }
        // Convert from vector to array
        returnArray = new String[1];
        returnArray = (String[]) returnVec.toArray((Object[]) returnArray);
        return returnArray;
    }

}
