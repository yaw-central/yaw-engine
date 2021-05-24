package loader.v2;

/**
 * The Material of a face describes the properties of this surface like its reflectivity, transparency...
 */
public class Material {

    // ========== Attributes ==========


    /** The material's name */
    public String name;
    /** Reflectivity type */
    public int reflType = -1;
    /** Reflectivity filename */
    public String reflFilename = null;
    /** Reflectivity's alpha */
    public Reflectivity ka = new Reflectivity();
    public Reflectivity kd = new Reflectivity();
    public Reflectivity ks = new Reflectivity();
    public Reflectivity tf = new Reflectivity();
    public int illumModel = 0;
    public boolean dHalo = false;
    public double dFactor = 0.0;
    public double nsExponent = 0.0;
    public double sharpnessValue = 0.0;
    public double niOpticalDensity = 0.0;
    public String mapKaFilename = null;
    public String mapKdFilename = null;
    public String mapKsFilename = null;
    public String mapNsFilename = null;
    public String mapDFilename = null;
    public String decalFilename = null;
    public String dispFilename = null;
    public String bumpFilename = null;


    // ========== Constructors ==========


    /**
     * Basic constructor
     * @param name The material name
     */
    public Material(String name) { this.name = name; }

}
