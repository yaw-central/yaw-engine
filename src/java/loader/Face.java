package loader;

/**
 * A Face represents a face of a 3D object read from an OBJ file.
 */
public class Face {

    // ===== Attributes =====

    /** 3 indices. Not vertices */
    private Vector3f vertex;
    /** 3 indices. Not normals */
    private Vector3f normal;

    // ===== Constructors =====

    public Face(Vector3f vertex, Vector3f normal) {
        this.vertex = vertex;
        this.normal = normal;
    }

}
