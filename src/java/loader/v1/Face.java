package loader.v1;

import org.joml.Vector3f;

/**
 * A Face represents a face of a 3D object read from an OBJ file.
 */
public class Face {

    // ========== Attributes ==========

    /** 3 indices. Not vertices */
    private Vector3f vertex;
    /** 3 indices. Not normals */
    private Vector3f normal;

    // ========== Constructors ==========

    public Vector3f getVertex() {
        return vertex;
    }

    public Vector3f getNormal() {
        return normal;
    }

    public Face(Vector3f vertex, Vector3f normal) {
        this.vertex = vertex;
        this.normal = normal;
    }

}
