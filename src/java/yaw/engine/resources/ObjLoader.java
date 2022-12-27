package yaw.engine.resources;

import yaw.engine.mesh.Mesh;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ObjLoader {

    public static class ParseError extends Error {
        public ParseError(String message) {
            super("Parse error ==> " + message);
        }

        public ParseError(String message, int linepos) {
            super("Parse error at line " + linepos + "\n ==> " + message);
        }

        public ParseError(String message, Throwable cause) {
            super("Parse error ==> " + message, cause);
        }

        public ParseError(String message, int linepos, Throwable cause) {
            super("Parse error at line " + linepos + "\n ==> " + message, cause);
        }
    }

    public static Mesh parseFromFile(String filename) throws IOException {
        return parseFromBufferedReader(new BufferedReader(new FileReader(filename)));
    }



    public static Mesh parseFromBufferedReader(BufferedReader reader) throws IOException {
        String[] lines = Utils.readLines(reader);
        return parseFromLines(lines);
    }

    public static Mesh parseFromLines(String[] lines) {
        throw new Error("Not implemented");
    }

    public static ObjEntry parseLine(int linepos, String line) {
        if (line.isEmpty()) {
            return new NoEntry(linepos);
        }
        int charpos = 1;
        char nextChar = fetchChar(line, charpos, linepos);
        if (nextChar == '#') {
            return new LineComment(line.substring(1).trim(), linepos);
        } else if (nextChar == 'v') {
            charpos += 1;
            nextChar = fetchChar(line, charpos, linepos);
            if (Character.isSpaceChar(nextChar)) {
                charpos += 1;
                nextChar = fetchChar(line, charpos, linepos);
                // parse a vertex
                Utils.FloatParse fpx = Utils.parseFloat(line, charpos-1);
                if (fpx == null) {
                    throw new ParseError("Expecting X coordinate for vertex", linepos);
                }
                charpos = fpx.endpos + 1;
                Utils.FloatParse fpy = Utils.parseFloat(line, charpos-1);
                if (fpy == null) {
                    throw new ParseError("Expecting Y coordinate for vertex", linepos);
                }
                charpos = fpy.endpos + 1;
                Utils.FloatParse fpz = Utils.parseFloat(line, charpos-1);
                if (fpz == null) {
                    throw new ParseError("Expecting Z coordinate for vertex", linepos);
                }
                return new VertexEntry(fpx.f, fpy.f, fpz.f, linepos);

            } else if (nextChar == 'n') {
                // parse a normal
            } else if (nextChar == 't') {
                // parse a texture coordinate

            } else {
                throw new ParseError("Unsupported entry type: v" + nextChar, linepos);
            }

        } else if (nextChar == 'f') {
            // parse a face (vertex indices)
        } else {
            throw new ParseError("Unsupported entry: " + line, linepos);
        }

        return null;
    }

    private static char fetchChar(String str, int charpos, int linepos) {
        if (charpos -1 < 0) {
            throw new Error("Wrong char position: " + (charpos - 1) + " (please report)");
        }
        if (charpos - 1 >= str.length()) {
            throw new ParseError("Unexpected end of line", linepos);
        }
        return str.charAt(charpos - 1);
    }

}

