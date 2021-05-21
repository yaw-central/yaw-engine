package loader.v2;


import java.util.ArrayList;
import java.util.List;

/**
 * A Face is a geometric element composed of 3 vertices (triangle).
 */
public class Face {

    // ========== Attributes ==========

    /** The vertices that describe the face */
    public List<FaceVertex> vertices = new ArrayList<>();
    /** The material attached to the face */
    public Material material = null;
    public Material map = null;
    public NormalVertex faceNormal = new NormalVertex(0, 0, 0);


    // ========== Constructors ==========


    /** Empty constructor */
    public Face() { }


    // ========== Methods ==========


    /** Adds a vertex to the face's vertices */
    public void addVertex(FaceVertex vertex) {
        vertices.add(vertex);
    }

    /** Updates the faceNormal from the vertices' information */
    public void processTriangleNormal() {
        float[] side1 = new float[3];
        float[] side2 = new float[3];
        float[] normal = new float[3];

        // Get each vertex of the triangle
        GeometricVertex v1 = vertices.get(0).geometric;
        GeometricVertex v2 = vertices.get(1).geometric;
        GeometricVertex v3 = vertices.get(2).geometric;

        float[] p1 = {v1.x, v1.y, v1.z};
        float[] p2 = {v2.x, v2.y, v2.z};
        float[] p3 = {v3.x, v3.y, v3.z};

        // Processing face triangle
        side1[0] = p2[0] - p1[0];
        side1[1] = p2[1] - p1[1];
        side1[2] = p2[2] - p1[2];

        side2[0] = p3[0] - p2[0];
        side2[1] = p3[1] - p2[1];
        side2[2] = p3[2] - p2[2];

        // Calculation of the triangle's normal vertex
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
