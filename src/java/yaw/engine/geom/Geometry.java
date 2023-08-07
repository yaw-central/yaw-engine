package yaw.engine.geom;

import org.joml.Vector3f;
import yaw.engine.mesh.Material;

import java.util.ArrayList;
import java.util.HashMap;

public class Geometry {
    private float[] vertices;//vertices
    private float[] normals;
    private int[] indices;
    private float[] textCoords;

    /**
     * Construct a Geometry
     *
     * @param vertices   The vertices of the geometry
     * @param normals    The normals at vertices (can be computed)
     * @param indices   vertex indices  (for indexed rendering)
     */
    public Geometry(float[] vertices, float[] normals, int[] indices) {
        this(vertices, null, normals, indices);
    }

    /**
     * Construct a Geometry
     *
     * @param vertices   The vertices of the geometry
     * @param textCoords Texture coordinates (optional, for non-textured rendering)
     * @param normals    The normals at vertices (can be computed)
     * @param indices   vertex indices  (for indexed rendering)
     */
    public Geometry(float[] vertices, float[] textCoords, float[] normals, int[] indices) {
        this.vertices = vertices;
        this.indices = indices;
        this.normals = normals == null ? generateNormals() : normals;
        this.textCoords = textCoords == null ? new float[1] : textCoords;
    }

    public static Vector3f getVec(float[] arr, int i) {
        return new Vector3f(arr[i], arr[i+1], arr[i+2]);
    }

    public static void setVec(float[] arr, int i, Vector3f vec) {
        arr[i] = vec.x;
        arr[i+1] = vec.y;
        arr[i+2] = vec.z;
    }

    /** Generate normals from vertices (assumes CCW ordering)
     * @return the normals as a linearized float array.
     */
    public float[] generateNormals() {
        float[] normals = new float[vertices.length];

        for(int i = 0; i<indices.length; i+=3) {
            int i1 = indices[i]*3;
            int i2 = indices[i+1]*3;
            int i3 = indices[i+2]*3;

            Vector3f v1 = getVec(vertices, i1);
            Vector3f v2 = getVec(vertices, i2);
            Vector3f v3 = getVec(vertices, i3);

            Vector3f n1 = getVec(normals, i1);
            Vector3f n2 = getVec(normals, i2);
            Vector3f n3 = getVec(normals, i3);

            Vector3f trinorm = v2.sub(v1).cross(v3.sub(v1)).normalize();

            setVec(normals, i1, n1.add(trinorm));
            setVec(normals, i2, n2.add(trinorm));
            setVec(normals, i3, n3.add(trinorm));
        }

        for(int i = 0; i<normals.length; i+=3) {
            Vector3f n = getVec(normals, i);
            setVec(normals, i, n.normalize());
        }

        return normals;
    }

    public float[] getVertices() {
        return vertices;
    }

    public float[] getTextCoords() {
        return textCoords;
    }

    public float[] getNormals() {
        return normals;
    }

    public int[] getIndices() {
        return indices;
    }
}
