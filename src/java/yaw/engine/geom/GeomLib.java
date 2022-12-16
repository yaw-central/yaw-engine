package yaw.engine.geom;

import org.joml.Vector2f;
import org.joml.Math;

import java.util.ArrayList;
import java.util.List;

public class GeomLib {

    public static List<Vector2f> makeCircleApprox(float cx, float cy, float radius, int nbVertices) {
        List<Vector2f> coords = new ArrayList<>(nbVertices);

        float theta = 0;
        float delta = (float) (2 * Math.PI / nbVertices);
        for (int i=0; i<nbVertices; i++) {
            coords.add(new Vector2f(cx + radius * Math.cos(theta),
                                    cy + radius * Math.sin(theta)));
            theta += delta;
        }
        return coords;
    }

    public static List<Vector2f> makeCircleApprox(int nbVertices) {
        return makeCircleApprox(0, 0, 1.0f, nbVertices);
    }

    public static Geometry makeCuboid(float xLength, float yLength, float zLength) {
        Geometry cuboid = new Geometry();
        // vertices
        float x = xLength / 2f;
        float y = yLength / 2f;
        float z = zLength / 2f;
        cuboid.addVertices(
                //Front face
                x, y, z, -x, y, z, -x, -y, z, x, -y, z,
                //Top face
                x, y, z, -x, y, z, x, y, -z, -x, y, -z,
                //Back face
                x, y, -z, -x, y, -z, -x, -y, -z, x, -y, -z,
                //Bottom face
                x, -y, z, -x, -y, z, x, -y, -z, -x, -y, -z,
                //Left face
                -x, y, z, -x, y, -z, -x, -y, z, -x, -y, -z,
                //Right face
                x, y, z, x, y, -z, x, -y, z, x, -y, -z);

        // normals
        cuboid.addNormals(
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
                1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0);

        // triangles
        cuboid.addTriangles(
                //Front face
                0, 1, 2, 0, 2, 3,
                //Top face
                4, 6, 5, 6, 7, 5,
                //Back face
                8, 11, 10, 8, 10, 9,
                //Bottom Face
                14, 12, 13, 14, 13, 15,
                //Left face
                16, 19, 18, 16, 17, 19,
                //Right face
                20, 22, 21, 22, 23, 21);

        return cuboid;
    }

    public static Geometry makeCube(float length) {
        return makeCuboid(length, length, length);
    }

    public static Geometry makeRectangle(float xLength, float yLength) {
        Geometry rect = new Geometry();
        // vertices
        float x = xLength / 2f;
        float y = yLength / 2f;
        float z = 0;
        rect.addVertices(x, y, z, -x, y, z, -x, -y, z, x, -y, z);

        // normals
        rect.addNormals(0, 0, 1f, 0, 0, 1f, 0, 0, 1f, 0, 0);

        // triangles
        rect.addTriangles(0, 1, 2, 0, 2, 3);

        return rect;
    }

}
