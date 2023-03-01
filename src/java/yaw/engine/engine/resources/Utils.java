package yaw.engine.resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static String[] readLines(BufferedReader reader) throws IOException {
        List<String> lines = new ArrayList<>();
        while (true) {
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            lines.add(line.trim());
        }
        return lines.toArray(new String[0]);
    }

    public static String fetchDirname(String path) {
        String dirname = path.substring(0, path.lastIndexOf('/'));
        if (dirname == null) {
            return "";
        }
        return dirname;
    }

    public static String fetchBasename(String path) {
        String basename = path.substring(path.lastIndexOf('/')+1);
        if (basename == null) {
            return "";
        }
        return basename;
    }

    public static String fetchRelativeName(String path, String name) {
        String dirName = fetchDirname(path);
        return dirName + "/" + name;
    }

}
