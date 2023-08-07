package test.yaw;

import org.joml.Vector3f;
import yaw.engine.Input;
import yaw.engine.InputCallback;
import yaw.engine.UpdateCallback;
import yaw.engine.World;
import yaw.engine.geom.Geometry;
import yaw.engine.items.ItemObject;
import yaw.engine.light.DirectionalLight;
import yaw.engine.mesh.Mesh;
import yaw.engine.mesh.strategy.DefaultDrawingStrategy;
import yaw.engine.resources.ObjLoader;

import java.io.IOException;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Basic example of a cube rotating on y axis
 */
public class RotatingObjHelper implements UpdateCallback {
    private int nbUpdates = 0;
    private double totalDeltaTime = 0.0;
    private static long deltaRefreshMillis = 1000;
    private long prevDeltaRefreshMillis = 0;
    private ItemObject cube ;
    private float speed = 0.1f;

    public boolean actifSummit = false;
    public boolean actifNormal = false;
    public boolean actifAxesMesh = false;

    public RotatingObjHelper(ItemObject cube) {
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
            //System.out.println("Average deltaTime = " + Double.toString(avgDeltaTime) +" s ("+nbUpdates+")");
            nbUpdates = 0;
            totalDeltaTime = 0.0;
            prevDeltaRefreshMillis = currentMillis;
        }

        //cube.rotateXYZ(0f, 3.1415925f * speed * (float) deltaTime, 0f);
        //cube.rotateZAround(1f, new Vector3f(0f, 0f, -3f));

        float angle = 2.0f * 3.1415925f * (float) deltaTime * speed;
        //System.out.println(deltaTime);
        cube.rotateY(angle);
        //cube.rotateXYZAround(0f, 3.1415925f * speed * (float) deltaTime, 0f, new Vector3f(0f, 0f, -10f));
        //cube.rotateX(0.0f);

        if (Input.isKeyDown(GLFW_KEY_H)) {
            System.out.println("Hello World");
        }

    }


    public static void main(String[] args) {

        World world = new World(0, 0, 800, 600);
        world.getSceneLight().setSun(new DirectionalLight());
        //world.getSceneLight().getSun().setDirection(-1f, 3f, 5f);
        world.getSceneLight().setSun(new DirectionalLight(new Vector3f(1,1,1), 0.7f, new Vector3f(-1,-1,-1)));

        ObjLoader objLoader = new ObjLoader();
        try {
            objLoader.parseFromResource("/resources/models/icosphere.obj");
        } catch (IOException e) {
            System.out.println("Errror : " + e.getMessage());
            System.exit(1);
        }
        Geometry geom = objLoader.getScene().getGeometryByIndex(0).build();

        Mesh objm = new Mesh(geom, objLoader.getScene().getMaterialByIndex(0));
        objm.setDrawingStrategy(new DefaultDrawingStrategy());
        ItemObject obji = world.createItemObject("obj", 0f, 0f, 0f, 1.0f, objm);
        //obji.translate(2f,0f, -5f);

        world.getCamera().translate(0, 0,4);


        RotatingObjHelper rObj = new RotatingObjHelper(obji);

        // press S to show summit N to show normal H to show both
        InputCallback key = new InputCallback() {
            @Override
            public void sendKey(int key, int scancode, int action, int mods) {
                if (action == GLFW_PRESS)
                    switch (key){
                        case GLFW_KEY_S:
                            rObj.actifSummit = !rObj.actifSummit;
                            obji.getMesh().toggleHelperVertices(rObj.actifSummit);
                            break;
                        case GLFW_KEY_N:
                            rObj.actifNormal = !rObj.actifNormal;
                            obji.getMesh().toggleHelperNormals(rObj.actifNormal);
                            break;
                        case GLFW_KEY_Q :
                            rObj.actifAxesMesh = !rObj.actifAxesMesh;
                            obji.getMesh().toggleHelperAxes(rObj.actifAxesMesh);
                            break;
                        case GLFW_KEY_H:
                            if (rObj.actifNormal == rObj.actifSummit){
                                rObj.actifSummit = !rObj.actifSummit;
                                rObj.actifNormal = !rObj.actifNormal;
                                obji.getMesh().toggleHelperVertices(rObj.actifSummit);
                                obji.getMesh().toggleHelperNormals(rObj.actifNormal);

                            } else {
                                rObj.actifSummit = true;
                                rObj.actifNormal = true;
                                obji.getMesh().toggleHelperVertices(rObj.actifSummit);
                                obji.getMesh().toggleHelperNormals(rObj.actifNormal);
                            }
                    }
            }
        };

        world.registerInputCallback(key);
        world.registerUpdateCallback(rObj);

        world.launchSync();
    }

}
