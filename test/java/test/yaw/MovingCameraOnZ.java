package test.yaw;

import yaw.engine.SceneRenderer;
import yaw.engine.UpdateCallback;
import yaw.engine.World;
import yaw.engine.camera.Camera;
import yaw.engine.items.ItemObject;
import yaw.engine.light.LightModel;
import yaw.engine.mesh.*;
import yaw.engine.mesh.builder.Cuboid;

/**
 * A camera moving on Z
 */
public class MovingCameraOnZ implements UpdateCallback {
    private int nbUpdates = 0;
    private double totalDeltaTime = 0.0;
    private static long deltaRefreshMillis = 1000;
    private long prevDeltaRefreshMillis = 0;
    private Camera camera;
    private float speed = 10;
    private float z = -10;
    private boolean inversingmove = false;

    public MovingCameraOnZ(Camera c) {
        this.camera = c;
    }

    public Camera getItem() {
        return camera;
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
        if(inversingmove){
            z+=0.1f;
            camera.translate(0,0,0.1f);
            if(z>=10){
                inversingmove = false;
            }
        }else{
            camera.translate(0,0,-0.1f);
            z-=0.1f;
            if(z<=-10){
                inversingmove = true;
            }
        }


    }

    public static void main(String[] args) {
        World world = new World(0, 0, 700, 700);/* Create the world with its dimensions. */
        world.installScene(new SceneRenderer(new LightModel()));

        float[] f = new float[]{0.f, 0.f, 0.f};

        for (int i = 0; i < 5; i++) {

            ItemObject item = world.createItemObject(i + "", 0.0f, 0.0f, 0.0f, 1, new Cuboid(1).generate());
            item.translate(i,i,i);

            if (i % 3 == 0)
                item.getMesh().getMaterial().setTexture(new Texture("/resources/grassblock.png"));
            else if (i % 3 == 1)
                item.getMesh().getMaterial().setTexture(new Texture("/resources/sand.png"));
            else
                item.getMesh().getMaterial().setTexture(new Texture("/resources/diamond.png"));
        }

        world.getCamera().translate(-15, 15, -10); // placing camera to have a side vue of the world
        //world.getCamera().rotateXYZ(Math.toRadians(-45),Math.toRadians(-90),Math.toRadians(0)); //rotate the camera to see the center of the world
        MovingCameraOnZ movingCamera = new MovingCameraOnZ(world.getCamera());


        world.registerUpdateCallback(movingCamera);

        world.launchSync();
    }

}

