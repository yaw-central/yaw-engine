package yaw;

import org.joml.Vector3f;
import yaw.engine.UpdateCallback;
import yaw.engine.World;
import yaw.engine.items.ItemObject;
import yaw.engine.light.DirectionalLight;
import yaw.engine.light.ShadowMap;
import yaw.engine.light.SpotLight;
import yaw.engine.meshs.Mesh;
import yaw.engine.meshs.MeshBuilder;
import yaw.engine.meshs.Texture;

/**
 * The objective of this exemple is to show the lights behaviour, with mixed color, testing the positionning of different lights
 */
public class ShadowTest implements UpdateCallback {

    private World world;

    private ItemObject cube;
    private ItemObject floor;
    private float speed = 10;

    public ShadowTest() {

        world = new World(0, 0, 800, 600);
        world.getCamera().lookAt(new Vector3f(-3,6,5), new Vector3f(0,0,0), new Vector3f(0,1,0));

        world.getSceneLight().setSun(new DirectionalLight(new Vector3f(1,1,1), 0.7f, new Vector3f(-1,-1,-1)));

        ShadowMap shadow = new ShadowMap();
        shadow.setCenter(new Vector3f());
        shadow.setLeft(-10);
        shadow.setRight(10);
        shadow.setBottom(-10);
        shadow.setTop(10);
        world.getSceneLight().getSun().setShadowMap(shadow);

        world.getSceneLight().getAmbientLight().setIntensity(0.3f);

        Mesh cubem = MeshBuilder.generateBlock(1, 1, 1);
        Mesh floorm = MeshBuilder.generateBlock(10, 0.1f, 10);

        cube = world.createItemObject("cube", 0, 2, 0, 1.0f, cubem);
        floor = world.createItemObject("floor", 0, 0, 0, 1.0f, floorm);

        world.registerUpdateCallback(this);

    }

    public void run() {

        world.launch();
        world.waitFortermination();

    }

    public void update(double deltaTime) {

        cube.rotateXYZ(3.1415925f / 4f * speed * (float) deltaTime, 3.1415925f * speed * (float) deltaTime, 3.1415925f / 2f * speed * (float) deltaTime);

    }

    public static void main(String[] args) {

        ShadowTest test = new ShadowTest();

        test.run();

    }

}
