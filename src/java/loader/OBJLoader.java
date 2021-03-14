package loader;

import java.io.*;

/**
 * OBJLoader is a tool for reading and parsing an OBJ file into a Model.
 */
public class OBJLoader {

    // ===== Methods =====

    public static Model loadModel(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        Model model = new Model();
        String line;

        // Reading file line by line, stops at the end of the file
        while ((line = reader.readLine()) != null) {

            if (line.startsWith("v ")) {    // The line represents a vertex

                String[] elems = line.split("\\s+");
                float x = Float.parseFloat(elems[1]);
                float y = Float.parseFloat(elems[2]);
                float z = Float.parseFloat(elems[3]);
                model.addVertex(new Vector3f(x, y, z));

            } else if (line.startsWith("vn ")) {    // The line represents a normal

                String[] elems = line.split("\\s+");
                float x = Float.parseFloat(elems[1]);
                float y = Float.parseFloat(elems[2]);
                float z = Float.parseFloat(elems[3]);
                model.addNormal(new Vector3f(x, y, z));

            } else if (line.startsWith("f ")) {     // The line represents a face

                String[] elems = line.split("\\s+");

                Vector3f vertexIndices =
                        new Vector3f(
                                Float.parseFloat(elems[1].split("/")[0]),
                                Float.parseFloat(elems[2].split("/")[0]),
                                Float.parseFloat(elems[3].split("/")[0])
                        );

                Vector3f normalIndices =
                        new Vector3f(
                                Float.parseFloat(elems[1].split("/")[2]),
                                Float.parseFloat(elems[2].split("/")[2]),
                                Float.parseFloat(elems[3].split("/")[2])
                        );

                model.addFace(new Face(vertexIndices, normalIndices));

            }

        }
        return model;
    }

}
