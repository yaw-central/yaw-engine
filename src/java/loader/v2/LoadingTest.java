package loader.v2;

import loader.Model;
import yaw.engine.World;
import yaw.engine.items.ItemObject;
import yaw.engine.meshs.Mesh;
import yaw.engine.meshs.MeshBuilder;
import yaw.engine.meshs.Texture;

import java.io.File;
import java.io.IOException;

public class LoadingTest {
    public static void main(String[] args) {
        String filename = "src/java/ressources/mickey.obj";

        try {
            OBJLoaderV2 objLoaderV2 = new OBJLoaderV2();
            OBJParser obj = new OBJParser(objLoaderV2, filename);

            World world = new World(0, 0, 800, 600);

            world.getCamera().setPosition(0, 1, 0);
            world.getCamera().rotate(0, 10, 0);

            Mesh tmpcube = MeshBuilder.generateBlock(1, 1, 1);
            ItemObject cube = world.createItemObject("cube", -5f, 0f, -5f, 1.0f, tmpcube);

            cube.getMesh().getMaterial().setTexture(new Texture("/ressources/diamond.png"));


            //Mesh mesh = world.createMesh(objLoaderV2.verticesG, objLoaderV2.verticesN, objLoaderV2.);
            //ItemObject res = world.createItemObject("test", 0f, 0f, -15f, 0.05f, mesh);

            world.launch();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
