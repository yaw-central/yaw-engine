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

    public static int searchNextSpaceOrEOL(String line, int frompos) {
        for (int pos = frompos; ; pos++) {
            if (pos >= line.length()) {
                return pos;
            } else if (Character.isSpaceChar(line.charAt(pos))) {
                while (Character.isSpaceChar(line.charAt(pos))) {
                    pos++;
                }
                return pos;
            }
        }
    }

    public static class FloatParse {
        public final int endpos;
        public final float f;

        public FloatParse(float f, int endpos) {
            this.f = f;
            this.endpos = endpos;
        }
    }
    public static FloatParse parseFloat(String line, int frompos) {
        int topos = searchNextSpaceOrEOL(line, frompos);
        if(topos > frompos) {
            try {
                String sub = line.substring(frompos, topos);
                float f = Float.parseFloat(sub);
                return new FloatParse(f, topos);
            } catch(NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
