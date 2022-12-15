package test.yaw;

import yaw.engine.UpdateCallback;
import yaw.engine.World;
import yaw.engine.items.Item;
import yaw.engine.items.ItemObject;
import yaw.engine.mesh.Mesh;
import yaw.engine.mesh.Texture;
import yaw.engine.mesh.builder.Cuboid;

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
        float[] vertices = new float[] {
                //Front face
                -1, 1, 1,   -1, -1, 1,   1, -1, 1,   1, 1, 1,
                //Top face
                -1, 1, -1,  -1, 1, 1,    1, 1, 1,    1, 1, -1,
                //Back face
                1, 1, -1,   1, -1, -1,   -1, -1, -1,   -1, 1, -1,
                //Bottom face
                1, -1, -1,  1, -1, 1,    -1, -1, 1,    -1, -1, -1,
                //Left face
                -1, 1, -1,  -1, -1, -1,    -1, -1, 1,    -1, 1, 1,
                //Right face
                1, 1, 1,     1, -1, 1,    1, -1, -1,    1, 1, -1,
        };


        float[] colours = new float[]{ 0.0f,1.0f,0.0f};


        int[] indices = new int[]{
                //Front face
                0, 1, 3, 1, 2, 3,
                //Top face
                4, 5, 6, 4, 6, 7,
                //Back face
                8, 9, 10, 8, 10, 11,
                //Bottom Face
                12, 13, 14, 12, 14, 15,
                //Left face
                16, 17, 18, 16, 18, 19,
                //Right face
                20, 21, 22, 20, 22, 23
        };

        float[] normals = new float[]{
                //Front face
                0, 0, 1f, 0, 0, 1f, 0, 0, 1f, 0, 0, 1f,
                //Top face
                0, 1f, 0, 0, 1f, 0, 0, 1f, 0, 0, 1f, 0,
                //Back face
                0, 0, -1f, 0, 0, -1f, 0, 0, -1f, 0, 0, -1f,
                //Bottom face
                0, -1f, 0, 0, -1f, 0, 0, -1f, 0, 0, -1f, 0,
                //Left face
                -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0,
                //Right face
                1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0
        };

        float[] textCoord = new float[]{
              0.5f,0,
                0.5f,0.25f,
                0.75f,0.25f,
                0.75f,0,

                0.25f,0.25f,
                0.25f,0.5f,
                0.5f,0.5f,
                0.5f,0.25f,

                0.5f,0.25f,
                0.5f,0.5f,
                0.75f,0.5f,
                0.75f,0.25f,

                0.75f,0.25f,
                0.75f,0.5f,
                1,0.5f,
                1,0.25f,

                0.5f,0.5f,
                0.5f,0.75f,
                0.75f,0.75f,
                0.75f,0.5f,

                0.5f,0.75f,
                0.5f,1,
                0.75f,1,
                0.75f,0.75f
        };

        Mesh m1 = world.createMesh(
                vertices,
                textCoord,
                normals,
                indices,
                24,
                colours,
                "/resources/dice.png"
        );

        Mesh m2 = new Cuboid(1).generate();
        m2.getMaterial().setTexture(new Texture("/resources/dice.png"));

        ItemObject item = world.createItemObject("item",0,0,-5,1.25f, m1);

        item.rotateXYZ(30,40, 0);

        TextureTest rGroup = new TextureTest(item);

        world.registerUpdateCallback(rGroup);

        world.launchSync();
    }
}