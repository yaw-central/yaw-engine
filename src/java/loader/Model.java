package loader;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * A Model represents an object from a OBJ file's contents.
 */
public class Model {

    // ========== Attributes ==========

    /** List of the object's vertices */
    private List<Vector3f> vertices = new ArrayList<>();
    /** List of the object's normals */
    private List<Vector3f> normals = new ArrayList<>();
    /** List of the object's faces */
    private List<Face> faces = new ArrayList<>();

    // TODO : comprendre ce que sont ces pIndices et rgb pour savoir où les intégrer
    private int[] pIndices;
    //private float[] rgb;

    public int[] getpIndices() {
        return pIndices;
    }

    public void setpIndices(int[] pIndices) {
        this.pIndices = pIndices;
    }

    // ========== Constructors ==========

    public Model() { }

    public Model(List<Vector3f> vertices, List<Vector3f> normals, List<Face> faces) {
        this.vertices = vertices;
        this.normals = normals;
        this.faces = faces;
    }

    // ========== Getters ==========

    public List<Vector3f> getVertices() { return this.vertices; }
    public List<Vector3f> getNormals() { return this.normals; }
    public List<Face> getFaces() { return this.faces; }

    // ========== Methods ==========

    public void addVertex(Vector3f v) { this.vertices.add(v); }
    public void addNormal(Vector3f v) { this.normals.add(v); }
    public void addFace(Face f) { this.faces.add(f); }

}
