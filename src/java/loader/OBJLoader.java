package loader;

import org.joml.Vector3f;
import yaw.DifferentLights;
import yaw.engine.World;
import yaw.engine.items.ItemObject;
import yaw.engine.light.SpotLight;
import yaw.engine.meshs.Mesh;
import yaw.engine.meshs.MeshBuilder;
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
        //TODO need fix on size
        int[] tmp = new int[999999];
        int cpt = 0;
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
                tmp[cpt] = (int) vertexIndices.x;
                tmp[cpt+1] = (int) vertexIndices.y;
                tmp[cpt+2] = (int) vertexIndices.z;
                cpt+=3;

                Vector3f normalIndices =
                        new Vector3f(
                                Float.parseFloat(elems[1].split("/")[2]),
                                Float.parseFloat(elems[2].split("/")[2]),
                                Float.parseFloat(elems[3].split("/")[2])
                        );

                model.addFace(new Face(vertexIndices, normalIndices));

            }

        }

        model.setpIndices(tmp);
        return model;
    }

    public static void main(String[] args) throws IOException {
        Model m = loadModel(new File("src/java/ressources/tree.obj"));

        World world = new World(0, 0, 800, 600);

        world.getCamera().setPosition(0, 1, 0);
        world.getCamera().rotate(0, 10, 0);

        Mesh tmpcube = MeshBuilder.generateBlock(1, 1, 1);
        ItemObject cube = world.createItemObject("cube", 0f, 0f, -5f, 1.0f, tmpcube);

        cube.getMesh().getMaterial().setTexture(new Texture("/ressources/diamond.png"));

        ItemObject tree = world.addModel("tree", 0f, 0f, -15f, 0.5f, m);
//        tree.getMesh().getMaterial().setTexture(new Texture("/ressources/grassblock.png"));
        world.launch();
    }

}
