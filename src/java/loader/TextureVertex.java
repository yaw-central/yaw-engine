package loader;

/**
 * A TextureVertex represents a face information and is composed of two coordinates.
 */
public class TextureVertex {

    // ========== Attributes ==========


    /**
     * The vertex's coordinates (two-dimensional because it is in the face's referential).
     * The letters "U" and "V" denote the axes of the 2D texture because "X", "Y", and "Z" are already used to denote the axes of the 3D object in model space -- Wikipedia
     */
    public float u, v;


    // ========== Constructors ==========


    /**
     * Basic constructor
     * @param u The u coordinate (abscissa)
     * @param v The v coordinate (ordinate)
     */
    public TextureVertex(float u, float v) {
        this.u = u;
        this.v = v;
    }


    // ========== Methods ==========


    @Override
    public String toString() {
        return "TextureVertex{" +
                "u=" + u +
                ", v=" + v +
                '}';
    }
}
