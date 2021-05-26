package loader;

/**
 * Describes the reflectivity of a material
 */
public class Reflectivity {

    // ========== Attributes ==========


    /** Tells if the format is RGB */
    private boolean isRGB = true;
    /** Tells if the format is XYZ */
    private boolean isXYZ = false;
    /** Values x,y,z or r,g,b */
    private double rx, gy, bz;


    // ========== Constructors ==========


    /** Empty constructor */
    public Reflectivity() { }


    // ========== Setters ==========


    public void setRGB(boolean RGB) { isRGB = RGB; }

    public void setXYZ(boolean XYZ) {
        isXYZ = XYZ;
    }

    public void setRx(double rx) {
        this.rx = rx;
    }

    public void setGy(double gy) {
        this.gy = gy;
    }

    public void setBz(double bz) {
        this.bz = bz;
    }

}
