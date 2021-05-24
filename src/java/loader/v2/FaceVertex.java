package loader.v2;

/**
 * A FaceVertex represents face information :
 * its index, its normal vertex, and optionally its geometric & texture vertices.
 */
public class FaceVertex {

    // ========== Attributes ==========


    /** The face vertex's index */
    int index = -1;
    /** The attached geometric vertex (optional) */
    public GeometricVertex geometric = null;
    /** The attached texture vertex (optional) */
    public TextureVertex texture = null;
    /** The attached normal vertex */
    public NormalVertex normal = null;


    // ========== Constructors ==========


    /** Empty constructor */
    public FaceVertex() { }


    // ========== Methods ==========


    @Override
    public String toString() {
        return "FaceVertex{" +
                "geometric=" + geometric +
                ", texture=" + texture +
                ", normal=" + normal +
                '}';
    }
}