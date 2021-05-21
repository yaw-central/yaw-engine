package loader.v2;

/**
 * A NormalVertex represents a face information and is composed of a simple 3D vertex.
 */
public class NormalVertex {

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
    public NormalVertex(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }


    // ========== Methods ==========


    /**
     * Changes the coordinates to move the vertex
     * @param x The number to add to the vertex's x coordinate
     * @param y The number to add to the vertex's y coordinate
     * @param z The number to add to the vertex's z coordinate
     */
    public void addNormal(float x, float y, float z){
        this.x += x;
        this.y += y;
        this.z += z;
    }

    @Override
    public String toString() {
        return "NormalVertex{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
