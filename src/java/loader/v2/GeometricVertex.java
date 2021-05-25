package loader.v2;

/**
 * A GeometricVertex represents a face information and is composed of a simple 3D vertex.
 */
public class GeometricVertex {

    // ========== Attributes ==========


    /** Vertex x coordinate */
    private float x;
    /** Vertex y coordinate */
    private float y;
    /** Vertex z coordinate */
    private float z;


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


    // ========== Getters ==========


    public float getX() { return x; }

    public float getY() { return y; }

    public float getZ() { return z; }


    // ========== Setters ==========


    public void setX(float x) { this.x = x; }

    public void setY(float y) { this.y = y; }

    public void setZ(float z) { this.z = z; }
}
