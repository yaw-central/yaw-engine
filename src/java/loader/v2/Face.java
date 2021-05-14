package loader.v2;


import java.util.ArrayList;
import java.util.List;

public class Face {
    public List<FaceVertex> vertices = new ArrayList<>();
    public Material material = null;
    public Material map = null;

    public NormalVertex faceNormal = new NormalVertex(0, 0, 0);

    public Face() {
    }

    public void addVertex(FaceVertex vertex) {
        vertices.add(vertex);
    }

    public void processTriangleNormal() {
        float[] side1 = new float[3];
        float[] side2 = new float[3];
        float[] normal = new float[3];

        GeometricVertex v1 = vertices.get(0).geometric;
        GeometricVertex v2 = vertices.get(0).geometric;
        GeometricVertex v3 = vertices.get(0).geometric;

        float[] p1 = {v1.x, v1.y, v1.z};
        float[] p2 = {v2.x, v2.y, v2.z};
        float[] p3 = {v3.x, v3.y, v3.z};

        // processing face triangle
        side1[0] = p2[0] - p1[0];
        side1[1] = p2[1] - p1[1];
        side1[2] = p2[2] - p1[2];

        side2[0] = p3[0] - p2[0];
        side2[1] = p3[1] - p2[1];
        side2[2] = p3[2] - p2[2];

        // calculation of the triangle's normal vertex
        normal[0] = side1[1] * side2[2] - side1[2] * side2[1];
        normal[1] = side1[2] * side2[0] - side1[0] * side2[2];
        normal[2] = side1[0] * side2[1] - side1[1] * side2[0];

        faceNormal.x = normal[0];
        faceNormal.y = normal[1];
        faceNormal.z = normal[2];
    }

    @Override
    public String toString() {
        return "Face{" +
                "vertices=" + vertices +
                '}';
    }
}
