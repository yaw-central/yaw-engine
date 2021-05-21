package loader.v2;

/**
 * A GeometricVertex represents a face information and is composed of a simple 3D vertex.
 */
public class GeometricVertex {

    // ========== Attributes ==========


    /** Vertex's coordinates */
    public float x, y, z;


    // ========== Constructors ==========


    /**
     * Basic constructor
     * @param x The x coordinate
     * @param y The y coordinate
     * @param z The z coordinate
     */
    public GeometricVertex(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }


    // ========== Methods ==========


    @Override
    public String toString() {
        return "GeometricVertex{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
