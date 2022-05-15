package yaw;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import yaw.engine.UpdateCallback;
import yaw.engine.World;
import yaw.engine.items.ItemObject;
import yaw.engine.light.DirectionalLight;
import yaw.engine.light.ShadowMap;
import yaw.engine.meshs.Material;
import yaw.engine.meshs.Mesh;
import yaw.engine.meshs.MeshBuilder;
import yaw.engine.meshs.strategy.DefaultDrawingStrategy;
import yaw.engine.util.LoggerYAW;

/**
 * The objective of this exemple is to show the lights behaviour, with mixed color, testing the positionning of different lights
 */
public class TreeTest implements UpdateCallback {

    private World world;

    private ItemObject tree;
    private ItemObject floor;
    private float speed = 10;

    private Vector3f center = new Vector3f(0,0,0);

    public TreeTest() {

        LoggerYAW.getInstance().activateConsoleMode();

        world = new World(0, 0, 800, 600);
        world.getCamera().lookAt(new Vector3f(-1,2,3).add(center), new Vector3f(0,1f,0).add(center), new Vector3f(0,1,0));

        world.getSceneLight().setSun(new DirectionalLight(new Vector3f(1,1,1), 0.7f, new Vector3f(-1,-1,-1)));

        ShadowMap shadow = new ShadowMap();
        shadow.setBias(0.0005f);
        world.getSceneLight().getSun().setShadowMap(shadow);

        world.getSceneLight().getAmbientLight().setIntensity(0.3f);

        Mesh treem = generateTreeMesh();

        Mesh floorm = MeshBuilder.generateBlock(10, 0.1f, 10);

        tree = world.createItemObject("tree", center.x, center.y, center.z, 1.0f, treem);
        floor = world.createItemObject("floor", center.x, center.y, center.z, 1.0f, floorm);
        floor.setCastShadows(false);

        world.registerUpdateCallback(this);

    }

    public void run() {

        world.launch();
        world.waitFortermination();

    }

    public void update(double deltaTime) {

        world.getCamera().getCameraMat().rotateLocalY((float) (deltaTime));

    }

    public static void set_3d(float[] b, int index, float x, float y, float z) {
        b[index*3] = x;
        b[index*3+1] = y;
        b[index*3+2] = z;
    }

    private interface ConeOperator {
        public void cone(float bh, float r, int n);
    }

    public static Mesh generateTreeMesh() {

        var vs = new float[(1+5+8+9+10)*3];
        var is = new int[(5+8+9+10)*3];

        var ii = new Object(){ int v = 0; };
        var vi = new Object(){ int v = 0; };

        final var s = 0.5f;
        final var h = 2.0f;

        set_3d(vs, vi.v++, 0, h, 0);

        ConeOperator cone = (float bh, float r, int n) -> {
            for(int i = 0; i<n; i++) {
                double angle = Math.PI*2 * i / n;
                set_3d(vs, vi.v+i, (float) (r*s*Math.cos(angle)), bh, (float) (r*s*Math.sin(angle)));
                is[ii.v+i*3+1] = vi.v+i;
                is[ii.v+i*3+0] = vi.v+((i+1) % n);
                is[ii.v+i*3+2] = 0;
            }
            ii.v += n*3;
            vi.v += n;
        };

        cone.cone(0f, 0.3f, 5);
        cone.cone(h/4, 1.0f, 10);
        cone.cone(h/2.2f, 0.9f, 9);
        cone.cone(h/1.6f, 0.8f, 8);

        Mesh treem = new Mesh(vs, is);
        treem.setDrawingStrategy(new DefaultDrawingStrategy());
        var mat = new Material(new Vector3f(0,1,0), 0.1f);
        treem.setMaterial(mat);

        return treem;
    }

    public static void main(String[] args) {

        TreeTest test = new TreeTest();

        test.run();

    }

}
