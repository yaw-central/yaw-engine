package loader.v2;


import java.util.ArrayList;
import java.util.List;

/**
 * A Face is a geometric element composed of 3 vertices (triangle).
 * Faces with 4+ vertices exists but are sub-
 */
public class Face {

    // ========== Attributes ==========

    /** The vertices that describe the face */
    private List<FaceVertex> vertices = new ArrayList<>();
    /** The material attached to the face */
    private Material material = null;
    private Material map = null;
    /** The face's normal */
    private NormalVertex faceNormal = new NormalVertex(0, 0, 0);


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

        // Processing face triangle
        side1[0] = v2.getX() - v1.getX();
        side1[1] = v2.getY() - v1.getY();
        side1[2] = v2.getZ() - v1.getZ();

        side2[0] = v3.getX() - v2.getX();
        side2[1] = v3.getY() - v2.getY();
        side2[2] = v3.getZ() - v2.getZ();

        // Calculation of the triangle's normal vertex. See https://en.wikipedia.org/wiki/Normal_(geometry)
        normal[0] = side1[1] * side2[2] - side1[2] * side2[1];
        normal[1] = side1[2] * side2[0] - side1[0] * side2[2];
        normal[2] = side1[0] * side2[1] - side1[1] * side2[0];

        faceNormal.setX(normal[0]);
        faceNormal.setY(normal[1]);
        faceNormal.setZ(normal[2]);
    }

    @Override
    public String toString() {
        return "Face{" +
                "vertices=" + vertices +
                '}';
    }


    // ========== Getters ==========


    public List<FaceVertex> getVertices() { return this.vertices; }

    public Material getMaterial() { return this.material; }

    public Material getMap() { return this.map; }

    public NormalVertex getFaceNormal() { return this.faceNormal; }


    // ========== Setters ==========


    public void setMaterial(Material material) { this.material = material; }

    public void setMap(Material map) { this.map = map; }

}
