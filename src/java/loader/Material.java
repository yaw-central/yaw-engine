package loader;

/**
 * The Material of a face describes the properties of this surface like its reflectivity, transparency...
 */
public class Material {

    // ========== Attributes ==========


    /** Material name */
    private String name;
    /** Reflectivity type */
    private int reflType = -1;
    /** Reflectivity filename */
    private String reflFilename = null;
    /** Reflectivity's alpha */
    private Reflectivity ka = new Reflectivity();
    private Reflectivity kd = new Reflectivity();
    private Reflectivity ks = new Reflectivity();
    private Reflectivity tf = new Reflectivity();
    private int illumModel = 0;
    private boolean dHalo = false;
    private double dFactor = 0.0;
    private double nsExponent = 0.0;
    private double sharpnessValue = 0.0;
    private double niOpticalDensity = 0.0;
    private String mapKaFilename = null;
    private String mapKdFilename = null;
    private String mapKsFilename = null;
    private String mapNsFilename = null;
    private String mapDFilename = null;
    private String decalFilename = null;
    private String dispFilename = null;
    private String bumpFilename = null;


    // ========== Constructors ==========


    /**
     * Basic constructor
     * @param name The material name
     */
    public Material(String name) { this.name = name; }


    // ========== Getters ==========


    public Reflectivity getKa() {
        return ka;
    }

    public Reflectivity getKd() {
        return kd;
    }

    public Reflectivity getKs() {
        return ks;
    }

    public Reflectivity getTf() {
        return tf;
    }


    // ========== Setters ==========


    public void setReflType(int reflType) {
        this.reflType = reflType;
    }

    public void setReflFilename(String reflFilename) {
        this.reflFilename = reflFilename;
    }

    public void setIllumModel(int illumModel) {
        this.illumModel = illumModel;
    }

    public void setdHalo(boolean dHalo) {
        this.dHalo = dHalo;
    }

    public void setdFactor(double dFactor) {
        this.dFactor = dFactor;
    }

    public void setNsExponent(double nsExponent) {
        this.nsExponent = nsExponent;
    }

    public void setSharpnessValue(double sharpnessValue) {
        this.sharpnessValue = sharpnessValue;
    }

    public void setNiOpticalDensity(double niOpticalDensity) {
        this.niOpticalDensity = niOpticalDensity;
    }

    public void setMapKaFilename(String mapKaFilename) {
        this.mapKaFilename = mapKaFilename;
    }

    public void setMapKdFilename(String mapKdFilename) {
        this.mapKdFilename = mapKdFilename;
    }

    public void setMapKsFilename(String mapKsFilename) {
        this.mapKsFilename = mapKsFilename;
    }

    public void setMapNsFilename(String mapNsFilename) {
        this.mapNsFilename = mapNsFilename;
    }

    public void setMapDFilename(String mapDFilename) {
        this.mapDFilename = mapDFilename;
    }

    public void setDecalFilename(String decalFilename) {
        this.decalFilename = decalFilename;
    }

    public void setDispFilename(String dispFilename) {
        this.dispFilename = dispFilename;
    }

    public void setBumpFilename(String bumpFilename) {
        this.bumpFilename = bumpFilename;
    }


}
