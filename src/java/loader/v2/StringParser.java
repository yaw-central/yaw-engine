package loader.v2;

import java.util.ArrayList;

/**
 * The StringParser provides static methods to parse strings.
 * It is used in the OBJLoader
 */
public class StringParser {

    // ========== Methods ==========


    public static float[] parseStringToFloatArray(int numFloats, String list, int startIndex) {
        if (list == null || list.equals("")) return null;

        float[] returnArray = new float[numFloats];
        int returnArrayCount = 0;

        // Copy list into a char array.
        char[] listChars = new char[list.length()];
        list.getChars(0, list.length(), listChars, 0);
        int listLength = listChars.length;

        int count = startIndex;
        int itemStart;
        int itemEnd;
        int itemLength;

        while (count < listLength) {
            // Skip any leading whitespace
            itemEnd = skipWhiteSpace(count, listChars, null);
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
            returnArray[returnArrayCount++] = Float.parseFloat(new String(listChars, itemStart, itemLength));
            if (returnArrayCount >= numFloats) {
                break;
            }

            count = itemEnd;
        }
        return returnArray;
    }

    public static int[] parseStringToIntArray(String list, int startIndex) {
        if (list == null || list.equals("")) return null;

        ArrayList<Integer> returnList = new ArrayList<Integer>();
        // Copy list into a char array.
        char[] listChars;
        listChars = new char[list.length()];
        list.getChars(0, list.length(), listChars, 0);
        int listLength = listChars.length;

        int count = startIndex;
        int itemStart;
        int itemEnd = 0;
        int itemLength = 0;

        while (count < listLength) {
            // Skip any leading whitespace
            itemEnd = skipWhiteSpace(count, listChars, null);
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

    public static int[] parseMultipleVertices(String s, int elemspertuple) {
        if (s == null) {
            return null;
        }
        if (s.equals("")) {
            return null;
        }

        String[] vertexStrings = parseWhiteSpaces(s);

        ArrayList<Integer> returnList = new ArrayList<>();
        for (String vertexString : vertexStrings) {
            parseVertice(vertexString, returnList, elemspertuple);
        }

        int[] returnArray = new int[returnList.size()];
        for (int loopi = 0; loopi < returnList.size(); loopi++) {
            returnArray[loopi] = returnList.get(loopi);
        }
        return returnArray;
    }

    public static void parseVertice(String s, ArrayList<Integer> returnList, int elemspertruple) {
        // Parse on the slashes
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
        while (foundCount < elemspertruple) {
            returnList.add(Integer.MIN_VALUE);
            foundCount++;
        }
    }

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
            itemEnd = skipWhiteSpace(count, listChars, null);
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

    public static int skipWhiteSpace(int mCount, char[] messageChars, String errMsg) {
        //Skip whitespace
        while (mCount < messageChars.length) {
            if (messageChars[mCount] == ' ' || messageChars[mCount] == '\n' || messageChars[mCount] == '\t') {
                mCount++;
            } else {
                break;
            }
        }
        if (errMsg != null) {
            if (mCount >= messageChars.length) {
                return -1;
            }
        }
        return mCount;
    }

    public static String[] parseList(char delim, String list) {
        if (list == null) {
            return null;
        }
        if (list.equals("")) {
            return null;
        }

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
        // Convert from vector to array, and return it.
        returnArray = new String[1];
        returnArray = (String[]) returnVec.toArray((Object[]) returnArray);
        return returnArray;
    }

}
