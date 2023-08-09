package test.yaw;

import yaw.engine.SceneRenderer;
import yaw.engine.UpdateCallback;
import yaw.engine.World;
import yaw.engine.items.Item;
import yaw.engine.items.ItemObject;
import yaw.engine.light.LightModel;
import yaw.engine.mesh.Mesh;

public class TextureTest implements UpdateCallback {
    private int nbUpdates = 0;
    private double totalDeltaTime = 0.0;
    private static long deltaRefreshMillis = 1000;
    private long prevDeltaRefreshMillis = 0;
    private Item cube ;

    public TextureTest(Item cube) {
        this.cube = cube;
    }

    public Item getItem() {
        return cube;
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
        cube.rotateXYZ(0.2f / 256, 0.8f / 256, 0.01f);
    }

    public static void main(String[] args) {
        World world = new World(0, 0, 700, 700);/* Create the world with its dimensions. */
        world.installScene(new SceneRenderer(new LightModel()));
        Mesh mesh = MeshExamples.makeDice(1);

        ItemObject item = world.createItemObject("item",0,0,-5,1.25f, mesh);

        item.rotateXYZ(30,40, 0);

        TextureTest rGroup = new TextureTest(item);

        world.registerUpdateCallback(rGroup);

        world.launchSync();
    }
}