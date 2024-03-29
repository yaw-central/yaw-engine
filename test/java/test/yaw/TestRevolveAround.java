package test.yaw;

import org.joml.Vector3f;
import yaw.engine.SceneRenderer;
import yaw.engine.UpdateCallback;
import yaw.engine.World;
import yaw.engine.items.ItemObject;
import yaw.engine.light.LightModel;
import yaw.engine.mesh.*;
import yaw.engine.mesh.builder.Cuboid;

/**
 * Temporary test to test revolveAround
 */
public class TestRevolveAround implements UpdateCallback {
    private int nbUpdates = 0;
    private double totalDeltaTime = 0.0;
    private static long deltaRefreshMillis = 1000;
    private long prevDeltaRefreshMillis = 0;
    private ItemObject cube ;
    private float speed = 10;

    public TestRevolveAround(ItemObject cube) {
        this.cube = cube;
    }

    public ItemObject getItem() {
        return cube;
    }


    @Override
    public void update(double deltaTime) {
        nbUpdates++;
        totalDeltaTime += deltaTime;

        long currentMillis = System.currentTimeMillis();
        if (currentMillis - prevDeltaRefreshMillis > deltaRefreshMillis) {
            double avgDeltaTime = totalDeltaTime / (double) nbUpdates;
            System.out.println("Average deltaTime = " + avgDeltaTime +" s ("+nbUpdates+")");
            nbUpdates = 0;
            totalDeltaTime = 0.0;
            prevDeltaRefreshMillis = currentMillis;
        }

        cube.rotateXYZAround(0f, 0.00125f, 0f,new Vector3f(0f, 0f, 0f));
        //cube.rotateXYZ(0,0.0025f,0);






    }

    public static void main(String[] args) {
        World world = new World(0, 0, 800, 600);
        world.installScene(new SceneRenderer(new LightModel()));
        world.getCamera().setPosition(0,0,6);
        ItemObject cube = world.createItemObject("cube", 0f, 0f, -2f, 1.0f, new Cuboid(1).generate());
        cube.getMesh().getMaterial().setTexture(new Texture("/resources/diamond.png"));
        //cube.setPosition(new Vector3f(0f,3f,0f));
        TestRevolveAround rCube = new TestRevolveAround(cube);

        world.registerUpdateCallback(rCube);

        world.launchSync();
    }

}
