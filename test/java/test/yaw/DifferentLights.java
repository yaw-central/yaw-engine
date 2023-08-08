package test.yaw;

import yaw.engine.Scene;
import yaw.engine.UpdateCallback;
import yaw.engine.World;
import yaw.engine.items.ItemObject;
import yaw.engine.light.DirectionalLight;
import yaw.engine.light.LightModel;
import yaw.engine.light.SpotLight;
import yaw.engine.mesh.*;
import yaw.engine.mesh.builder.Cuboid;

/**
 * The objective of this exemple is to show the lights behaviour, with mixed color, testing the positionning of different lights
 */
public class DifferentLights implements UpdateCallback {
    private int nbUpdates = 0;
    private double totalDeltaTime = 0.0;
    private static long deltaRefreshMillis = 1000;
    private long prevDeltaRefreshMillis = 0;
    private ItemObject cube ;
    private float speed = 10;

    public DifferentLights(ItemObject cube) {
        this.cube = cube;
    }

    public ItemObject getItem() {
        return cube;
    }

    public void update(double deltaTime) {
        nbUpdates++;
        totalDeltaTime += deltaTime;

        long currentMillis = System.currentTimeMillis();
        if (currentMillis - prevDeltaRefreshMillis > deltaRefreshMillis) {
            double avgDeltaTime = totalDeltaTime / (double) nbUpdates;
            System.out.println("Average deltaTime = " + Double.toString(avgDeltaTime) +" s ("+nbUpdates+")");
            nbUpdates = 0;
            totalDeltaTime = 0.0;
            prevDeltaRefreshMillis = currentMillis;
        }

        cube.rotateXYZ(0.0f, (3.1415925f / 36f) * speed * (float) deltaTime, 0.0f);


    }

    public static void main(String[] args) {
        World world = new World(0, 0, 800, 600);
        world.installScene(new Scene(new LightModel()));
        world.getCamera().setPosition(0,0,0);
        //world.getCamera().rotateXYZ(0,10,0);

        world.getSceneLight().getSpotTable()[0] = new SpotLight(0, 255, 0, 0, 0, 0, 1, 0, 0.5f, 0, 0, 0, -5, 10f);
        world.getSceneLight().getSpotTable()[1] = new SpotLight(0, 0, 255, 0.2f, 0f,0f, 1, 0, 0.75f, 0, 0, 0, -2, 3);
        world.getSceneLight().getSpotTable()[2] = new SpotLight(255, 0, 0, -0.2f, 0.0f, 0, 1f, 0, 0.75f, 0, 0f, 0, -2, 3f);
        world.getSceneLight().setSun(new DirectionalLight());

        //Mesh cubem = new Cuboid(1).generate();
        Mesh cubem = MeshExamples.makeDice(1);
        ItemObject cube = world.createItemObject("cube", 0f, 0f, -2.0f, 1.0f, cubem);

        DifferentLights rCube = new DifferentLights(cube);

        world.registerUpdateCallback(rCube);

        world.launchSync();
    }

}
