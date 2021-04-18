package loader;

import org.joml.Vector3f;
import yaw.DifferentLights;
import yaw.engine.World;
import yaw.engine.items.ItemObject;
import yaw.engine.light.SpotLight;
import yaw.engine.meshs.Mesh;
import yaw.engine.meshs.Texture;

import java.io.*;

import static org.lwjgl.opengl.GL11.*;

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
        //TODO faire les indices comme il faut -- pas sur dfu procéder
        int[] tmp = new int[model.getVertices().size()];
        for (int i = 0; i < model.getVertices().size(); i++) {
            tmp[i] = i+1;
        }
        model.setpIndices(tmp);
        return model;
    }

    public static void main(String[] args) throws IOException {
        Model m = loadModel(new File("src/java/ressources/tree.obj"));
        System.out.println(m.getFaces());

        World world = new World(0, 0, 800, 600);
        //TODO fix camera position to see object
        world.getCamera().setPosition(-10, 10, 0);

        world.addModel(m);
        world.launch();


    }

}
