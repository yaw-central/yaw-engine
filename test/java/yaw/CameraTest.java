package yaw;

import yaw.engine.UpdateCallback;
import yaw.engine.World;
import yaw.engine.items.ItemObject;
import yaw.engine.light.SpotLight;
import yaw.engine.meshs.Mesh;
import yaw.engine.meshs.MeshBuilder;
import yaw.engine.meshs.Texture;

/**
 * The objective of this exemple is to show the lights behaviour, with mixed color, testing the positionning of different lights
 */
public class CameraTest implements UpdateCallback {

    private World world;

    private ItemObject cube;
    private float speed = 10;

    public CameraTest() {

        Mesh cubem = MeshBuilder.generateBlock(1, 1, 1);

        world = new World(0, 0, 800, 600);
        world.getCamera().setPosition(0,0,5);

        //world.getSceneLight().getSpotTable()[0] = new SpotLight(0, 255, 0, 0, 0, 0, 1, 0, 0.5f, 0, 0, 0, -5, 10f);
        world.getSceneLight().getSpotTable()[1] = new SpotLight(0, 255, 0, 0.2f, 0f,0f, 1, 0, 0.75f, 0, 0, 0, -5, 3);
        world.getSceneLight().getSpotTable()[2] = new SpotLight(255, 0, 0, -0.2f, 0.0f, 0, 1f, 0, 0.75f, 0, 0f, 0, -5, 3f);
        //world.getSceneLight().setSun(new DirectionalLight());

        cube = world.createItemObject("cube", 0f, 0, 0, 1.0f, cubem);

        // System.out.println("Working Directory = " + System.getProperty("user.dir"));
        cube.getMesh().getMaterial().setTexture(new Texture("/resources/diamond.png"));
        //cube.rotate(0,45,0);

        world.registerUpdateCallback(this);

    }

    public void run() {

        world.launch();
        world.waitFortermination();

    }

    public void update(double deltaTime) {

        world.getCamera().rotateXYZ(0.0f, 3.1415925f * speed * (float) deltaTime, 0.0f);

    }

    public static void main(String[] args) {

        CameraTest test = new CameraTest();

        test.run();

    }

}
