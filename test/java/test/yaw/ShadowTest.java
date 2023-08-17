package test.yaw;

import org.joml.Vector3f;
import yaw.engine.SceneRenderer;
import yaw.engine.UpdateCallback;
import yaw.engine.World;
import yaw.engine.items.ItemObject;
import yaw.engine.light.DirectionalLight;
import yaw.engine.light.LightModel;
import yaw.engine.light.ShadowMap;
import yaw.engine.mesh.Mesh;
import yaw.engine.mesh.builder.Rectangle;

/**
 * The objective of this example is to show the lights behaviour, with mixed color, testing the positioning of different lights
 */
public class ShadowTest implements UpdateCallback {

    private World world;

    private ItemObject cube;
    private ItemObject floor;
    private float speed = 0.25f;

    public ShadowTest() {

        world = new World(0, 0, 800, 600);
        world.installScene(new SceneRenderer(new LightModel()));
        world.getCamera().lookAt(new Vector3f(-3,6,5), new Vector3f(0,0,0), new Vector3f(0,1,0));

        world.getSceneLight().setDirectionalLight(new DirectionalLight(new Vector3f(1,1,1), 0.7f, new Vector3f(-1,-1,-1)));

        ShadowMap shadow = new ShadowMap();
        shadow.setCenter(new Vector3f());
        shadow.setLeft(-10);
        shadow.setRight(10);
        shadow.setBottom(-10);
        shadow.setTop(10);
        world.getSceneLight().getDirectionalLight().setShadowMap(shadow);

        world.getSceneLight().getAmbientLight().setIntensity(0.3f);

//        Mesh cubem = new Cuboid(1).generate();
        Mesh cubem = MeshExamples.makeDice(1);
        Mesh floorm = new Rectangle(10).generate();
                // DeprecatedMeshBuilder.generateBlock(10, 0.1f, 10);

        cube = world.createItemObject("cube", 0, 1.3f, 0, 1.0f, cubem);
        floor = world.createItemObject("floor", 0, 0, 0, 1.0f, floorm);
        floor.rotateX(-90);

        world.registerUpdateCallback(this);

    }

    public void run() {
        world.launchSync();
    }

    public void update(double deltaTime) {
        cube.rotateXYZ(3.1415925f / 4 * speed * (float) deltaTime, 3.1415925f * speed * (float) deltaTime, 3.1415925f / 2f * speed * (float) deltaTime);
    }

    public static void main(String[] args) {

        ShadowTest test = new ShadowTest();

        test.run();

    }

}
