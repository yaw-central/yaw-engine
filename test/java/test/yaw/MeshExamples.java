package test.yaw;

import yaw.engine.geom.GeomLib;
import yaw.engine.geom.Geometry;
import yaw.engine.mesh.Mesh;
import yaw.engine.mesh.Texture;
import yaw.engine.mesh.strategy.DefaultDrawingStrategy;

public class MeshExamples {

    public static Mesh makeDice(float length) {
        return makeDice(length, length, length);
    }
    public static Mesh makeDice(float xLength, float yLength, float zLength) {
        float x = xLength / 2;
        float y = yLength / 2;
        float z = zLength / 2;
        float[] vertices = new float[] {
                //Front face
                -x, y, z,   -x, -y, z,   x, -y, z,   x, y, z,
                //Top face
                -x, y, -z,  -x, y, z,    x, y, z,    x, y, -z,
                //Back face
                x, y, -z,   x, -y, -z,   -x, -y, -z,   -x, y, -z,
                //Bottom face
                x, -y, -z,  x, -y, z,    -x, -y, z,    -x, -y, -z,
                //Left face
                -x, y, -z,  -x, -y, -z,    -x, -y, z,    -x, y, z,
                //Right face
                x, y, z,     x, -y, x,    x, -y, -z,    x, y, -z,
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

        Mesh dice = new Mesh(vertices, textCoord, normals, indices,24);
        dice.getMaterial().setTexture(new Texture("/resources/dice.png"));
        dice.setDrawingStrategy(new DefaultDrawingStrategy());
        return dice;
    }
}
