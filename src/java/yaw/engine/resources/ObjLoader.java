package yaw.engine.resources;

import org.w3c.dom.Text;
import yaw.engine.geom.Geometry;
import yaw.engine.mesh.Mesh;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjLoader {
    public enum TriangulationMode {
        NO_TRIANGULATION
        , TRIANGLE_FANS
    };

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

    public static Mesh parseFromResource(String name) throws IOException {
        InputStream istream = ObjLoader.class.getResourceAsStream(name);
        if (istream == null) {
            throw new ParseError("Cannot find resource: "+name);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(istream));
        return parseFromBufferedReader(reader);
    }

    public static Mesh parseFromBufferedReader(BufferedReader reader) throws IOException {
        String[] lines = Utils.readLines(reader);
        return parseFromLines(lines);
    }

    public static Mesh parseFromLines(String[] lines) {
        List<VertexEntry> vertexEntries = new ArrayList<>();
        List<TextureEntry> textureEntries = new ArrayList<>();
        List<NormalEntry> normalEntries = new ArrayList<>();
        List<FaceEntry> faceEntries = new ArrayList<>();
        for(int i=0; i<lines.length; i++) {
            ObjEntry entry = parseLine(i+1, lines[i]);
            switch (entry.getType()) {
                case VERTEX: vertexEntries.add(entry.asVertex());break;
                case TEXT_COORD: textureEntries.add(entry.asTexture());break;
                case NORMAL: normalEntries.add(entry.asNormal());break;
                case FACE: faceEntries.add(entry.asFace());break;
                default:
                    break; // skip
            }
        }

        // Building the mesh components
        Map<GLVertex, Integer> vertexMap = new HashMap<>();
        List<GLVertex> glVertices = new ArrayList<>();
        List<GLTriangle> glTriangles = new ArrayList<>();
        boolean hasTextCoords = false;
        boolean hasNormals = false;

        for(FaceEntry faceEntry : faceEntries) {
            int[] triIndices = new int[faceEntry.face.length];
            for(int i = 0; i<faceEntry.face.length; i++) {
                FaceVertex faceVert = faceEntry.face[i];
                VertexEntry vertexEntry = vertexEntries.get(faceVert.vertexId-1);
                TextureEntry textureEntry = null;
                if (faceVert.textId != 0) {
                    textureEntry = textureEntries.get(faceVert.textId-1);
                }
                NormalEntry normalEntry = null;
                if (faceVert.normId != 0) {
                    normalEntry = normalEntries.get(faceVert.normId-1);
                }
                float[] positions = new float[3];
                positions[0] = vertexEntry.x;
                positions[1] = vertexEntry.y;
                positions[2] = vertexEntry.z;

                float[] textCoords = null;
                if (textureEntry != null) {
                    textCoords = new float[2];
                    textCoords[0] = textureEntry.tx;
                    textCoords[1] = textureEntry.ty;
                    hasTextCoords = true;
                }

                float[] normals = null;
                if(normalEntry != null) {
                    normals = new float[3];
                    normals[0] = normalEntry.nx;
                    normals[1] = normalEntry.ny;
                    normals[2] = normalEntry.nz;
                    hasNormals = true;
                }
                GLVertex vertex = new GLVertex(positions, textCoords, normals);

                int vertIndex = -1;
                Integer vertId = vertexMap.get(vertex);
                if(vertId != null) {
                    vertIndex = vertId.intValue();
                } else {
                    // new vertex
                    glVertices.add(vertex);
                    vertIndex = glVertices.size()-1;
                    vertexMap.put(vertex, vertIndex);
                }
                triIndices[i] = vertIndex;
            }
            // Here we have the vertex indices, we now apply a dumb triangulation
            // algorithm for faces with more than 3 points
            for(int i=1; i<triIndices.length - 1; i++) {
                GLTriangle tri = new GLTriangle(triIndices[0], triIndices[i], triIndices[i+1]);
                glTriangles.add(tri);
            }
        }

        Geometry geom = new Geometry();

        // we may now build the Mesh geometry
        for(int i=0; i<glVertices.size(); i++) {
            geom.addVertices(glVertices.get(i).positions[0], glVertices.get(i).positions[1], glVertices.get(i).positions[2]);
            if (hasTextCoords) {
                geom.addTextCoord(glVertices.get(i).textCoords[0], glVertices.get(i).textCoords[1]);
            }
            if (hasNormals) {
                geom.addNormal(glVertices.get(i).normals[0], glVertices.get(i).normals[1], glVertices.get(i).normals[2]);
            }
        }

        for(int i=0; i<glTriangles.size();i++) {
            geom.addTriangle(glTriangles.get(i).indice1, glTriangles.get(i).indice2, glTriangles.get(i).indice3);
        }

        Mesh mesh = geom.buildMesh();
        return mesh;
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

class GLVertex {
    public float[] positions;
    public float[] textCoords;
    public float[] normals;

    public GLVertex(float[] positions, float[] textCoords, float[] normals) {
        this.positions = positions;
        this.textCoords = textCoords;
        this.normals = normals;
    }

    @Override
    public int hashCode() {
        return positions.hashCode() + (textCoords == null ? 4242 : textCoords.hashCode())
                + (normals == null ? 66666 : normals.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if(obj == null || obj.getClass()!= this.getClass()) {
            return false;
        }
        GLVertex other = (GLVertex) obj;
        if (!other.positions.equals(positions)) {
            return false;
        }
        if(other.textCoords == null) {
            return textCoords == null;
        }
        if(!other.textCoords.equals(textCoords)) {
            return false;
        }
        if(other.normals == null) {
            return normals == null;
        }
        if(!other.normals.equals(normals)) {
            return false;
        }
        return true;
    }
}

class GLTriangle {
    public int indice1;
    public int indice2;
    public int indice3;

    public GLTriangle(int indice1, int indice2, int indice3) {
        this.indice1 = indice1;
        this.indice2 = indice2;
        this.indice3 = indice3;
    }
}
