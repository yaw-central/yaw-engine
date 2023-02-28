package test.yaw;




import yaw.engine.World;
import yaw.engine.items.ItemObject;
import yaw.engine.mesh.Mesh;

import java.io.IOException;

public class TestAxesReferentiels {

    public static void main(String[] args) {

        World world = new World(0, 0, 800, 600);

        Mesh axes = MeshReferentiel.makeReferentiel();
        ItemObject ref = world.createItemObject("axeX", 0f, 0f, 0f, 2.0f, axes);

        Mesh cube = MeshExamples.makeDice(1.0f);
        ItemObject cubes = world.createItemObject("cube", 0f, 0f, 0f, 1.0f, cube);


        world.getCamera().translate(1, 1,4);


        world.launchSync();
    }
}
