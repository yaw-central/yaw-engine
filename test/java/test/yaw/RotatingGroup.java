package test.yaw;

import org.joml.Vector3f;
import yaw.engine.ColorLib;
import yaw.engine.SceneRenderer;
import yaw.engine.UpdateCallback;
import yaw.engine.World;
import yaw.engine.items.ItemGroup;
import yaw.engine.items.ItemObject;
import yaw.engine.light.LightModel;
import yaw.engine.light.PointLight;
import yaw.engine.mesh.*;
import yaw.engine.mesh.builder.Cuboid;

/**
 * A group of 2 items revolving around another cube, and rotating
 */
public class RotatingGroup implements UpdateCallback {
    private int nbUpdates = 0;
    private double totalDeltaTime = 0.0;
    private static long deltaRefreshMillis = 1000;
    private long prevDeltaRefreshMillis = 0;
    private ItemGroup cubes ;
    private float speed = 10f / 512f;

    public RotatingGroup(ItemGroup cubes) {
        this.cubes = cubes;
    }

    public ItemGroup getItem() {
        return cubes;
    }

    static Mesh createCube() {
        return new Cuboid(1).generate();
    }

    @Override
    public void update(double deltaTime) {
        nbUpdates++;
        totalDeltaTime += deltaTime;

        long currentMillis = System.currentTimeMillis();
        if (currentMillis - prevDeltaRefreshMillis > deltaRefreshMillis) {
            double avgDeltaTime = totalDeltaTime / (double) nbUpdates;
            //System.out.println("Average deltaTime = " + Double.toString(avgDeltaTime) +" s ("+nbUpdates+")");
            nbUpdates = 0;
            totalDeltaTime = 0.0;
            prevDeltaRefreshMillis = currentMillis;
        }

        cubes.rotateXYZ(0.0f, 3.1415925f * speed * (float) deltaTime, 0.0f);
        cubes.fetchItem("first").rotateXYZ(0f, 0.01f, 0f);
        //cubes.rotateXYZ(0f, 0f, 0.1f);

        /*for(int i=0; i<cubes.getItems().size();i++){
            cubes.getItems().get(i).rotate(0.0f, -6.283f * speed * (float) deltaTime, 0.0f);
        }*/


    }

    public static void main(String[] args) {
        World world = new World(0, 0, 800, 600);
        world.installScene(new SceneRenderer(new LightModel()));

        world.getCamera().setPosition(0,0,0);
        //world.getSceneLight().setSpotLight(new SpotLight(1, 200, 1, 0, 0, 10, 1f, 0, 0.1f, 0, 0, 0, -.1f, 10f), 1);

        Mesh cubem = MeshLib.makeSolidCube(1, ColorLib.BLUE);
        Mesh cubem2 = MeshLib.makeSolidCube(1, ColorLib.RED);
        Mesh cubem3 = MeshLib.makeSolidCube(1, ColorLib.YELLOW);

        world.getSceneLight().setPointLight(new PointLight(ColorLib.YELLOW, new Vector3f(0, 0, -8.0f), 1.0f, 1.0f, 0.09f, 0.032f), 0);

        ItemObject cube = world.createItemObject("cube", -2.5f, 0f, -8f, 1.0f, cubem);
        //cube.getMesh().getMaterial().setTexture(new Texture("/resources/diamond.png"));
        ItemObject cube2 = world.createItemObject("cube2", 2.5f, 0f, -8f, 1.0f, cubem2);
        //cube2.getMesh().getMaterial().setTexture(new Texture("/resources/diamond.png"));
        ItemObject cube3 = world.createItemObject("cube3", 0f, 0f, -8f, 1.0f, cubem3);
        //cube2.getMesh().getMaterial().setTexture(new Texture("/resources/diamond.png"));
        ItemGroup g = new ItemGroup("g");
        g.add("first",cube);

        g.add("second",cube2);
        //g.rotate(0, 45, 0);

        RotatingGroup rGroup = new RotatingGroup(g);
        world.registerUpdateCallback(rGroup);

        world.launchSync();
    }

}
