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
        if (line.charAt(0) == '#') {
            return new LineComment(line.substring(1).trim(), linepos);
        }

        String[] parts = line.split("\\s+");
        if (parts.length == 0) {
            return new NoEntry(linepos);
        }

        if (parts[0].equals("v")) {
            // parse a vertex
            if (parts.length < 4) {
                throw new ParseError("Not enough coordinates for vertex", linepos);
            }
            float x,y ,z;
            try {
                x = Float.parseFloat(parts[1]);
                y = Float.parseFloat(parts[2]);
                z = Float.parseFloat(parts[3]);
            } catch (NumberFormatException e) {
                throw new ParseError("Cannot parse vertex coordinates", linepos, e);
            }

            return new VertexEntry(x, y, z, linepos);
        } else if (parts[0].equals("vn")) {
            // parse a normal
            if (parts.length < 4) {
                throw new ParseError("Not enough coordinates for normal", linepos);
            }
            float nx, ny ,nz;
            try {
                nx = Float.parseFloat(parts[1]);
                ny = Float.parseFloat(parts[2]);
                nz = Float.parseFloat(parts[3]);
            } catch (NumberFormatException e) {
                throw new ParseError("Cannot parse normal coordinates", linepos, e);
            }

            return new NormalEntry(nx, ny, nz, linepos);
        } else if (parts[0].equals("vt")) {
            // parse a texture coordinates
            if (parts.length < 3) {
                throw new ParseError("Not enough coordinates for texture", linepos);
            }
            float tx, ty;
            try {
                tx = Float.parseFloat(parts[1]);
                ty = Float.parseFloat(parts[2]);
            } catch (NumberFormatException e) {
                throw new ParseError("Cannot parse texture coordinates", linepos, e);
            }

            return new TextureEntry(tx, ty, linepos);
        } else if (parts[0].equals("f")) {
            // Parse a face
            List<FaceVertex> faces = new ArrayList<>();
            for(int i=1; i<parts.length; i++) {
                String[] sindices = parts[i].split("/");
                if (sindices.length == 0 || sindices.length > 3) {
                    throw new ParseError("Cannot parse face : wrong indices", linepos);
                }
                int[] indices = new int[sindices.length];
                for(int j=0; j<sindices.length; j++) {
                    try {
                        if (sindices[j].equals("")) {
                            indices[j] = 0;
                        } else {
                            indices[j] = Integer.parseInt(sindices[j]);
                        }
                    } catch (NumberFormatException e) {
                        throw new ParseError("Cannot parse face index", linepos, e);
                    }
                }
                switch (indices.length) {
                    case 1: faces.add(new FaceVertex(indices[0], 0, 0)); break;
                    case 2: faces.add(new FaceVertex(indices[0], indices[1], 0)); break;
                    case 3: faces.add(new FaceVertex(indices[0], indices[1], indices[2]));break;
                    default:
                        throw new Error("Should not be reachable (please report)");
                }
            }
            return new FaceEntry(faces.toArray(new FaceVertex[0]), linepos);
        } else {
            return new UnsupportedEntry(parts[0], line, linepos);
        }

    }

}

