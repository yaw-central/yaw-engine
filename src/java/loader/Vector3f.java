package loader;

/**
 * A Vector3f is a vector (kind of list) composed of 3 floating coordinates.
 * It can represent 3D points/vertices and be used for stocking OBJ files' vertices.
 */
public class Vector3f {

    // ===== Attributes =====

    private float x;
    private float y;
    private float z;

    // ===== Constructors =====

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // ===== Getters =====

    public float getX() { return this.x; }
    public float getY() { return this.y; }
    public float getZ() { return this.z; }


    // TODO :
    // Se passer de explode dans util.clj (qui prenait jusqu'alors la responsabilité de casser la liste de coordonnées en trios
    // afin de pouvoir avoir leurs points individuellement)

}
