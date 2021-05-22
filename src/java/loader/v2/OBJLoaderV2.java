package loader.v2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The OBJLoader loads OBJ files into its attributes by calling a parse on them.
 * It contains all OBJ file's elements.
 * It allows users to manipulate every attribute from an OBJ as they want.
 */
public class OBJLoaderV2 implements OBJLoaderV2Interface {

    // ========== Attributes ==========


    public String objFileName = null;

    public List<GeometricVertex> verticesG = new ArrayList<>();
    public ArrayList<TextureVertex> verticesT = new ArrayList<>();
    public ArrayList<NormalVertex> verticesN = new ArrayList<>();

    public Map<String, FaceVertex> faceVertexMap = new HashMap<>();

    public List<FaceVertex> faceVertexList = new ArrayList<>();
    public List<Face> faces = new ArrayList<>();

    public Map<String, ArrayList<Face>> groups = new HashMap<>();
    private List<String> currentGroups = new ArrayList<>();
    private List<ArrayList<Face>> currentGroupFaceLists = new ArrayList<>();
    public String objectName = null;
    private Material currentMaterial = null;
    private Material currentMap = null;
    public Map<String, Material> materialLib = new HashMap<>();
    private Material currentMaterialBeingParsed = null;
    public Map<String, Material> mapLib = new HashMap<>();
    private Material currentMapBeingParsed = null;

    public HashMap<Integer, ArrayList<Face>> smoothingGroups = new HashMap<Integer, ArrayList<Face>>();
    private int currentSmoothingGroupNumber = 0;
    private ArrayList<Face> currentSmoothingGroup = null;

    public List<Integer> pIndices = new ArrayList<>();
    public int faceTriCount = 0;
    public int faceQuadCount = 0;
    public int facePolyCount = 0;
    public int faceErrorCount = 0;


    // ========== Constructors ==========


    /**
     * Empty constructor
     */
    public OBJLoaderV2() {
    }


    // ========== Methods ==========


    public void addGeometricVertex(float x, float y, float z) {
        verticesG.add(new GeometricVertex(x, y, z));
    }

    public void addTextureVertex(float u, float v) {
        verticesT.add(new TextureVertex(u, v));
    }

    public void addNormalVertex(float x, float y, float z) {
        verticesN.add(new NormalVertex(x, y, z));
    }

    public void addFace(int[] vertexIndices) {
        Face f = new Face();
        f.material = currentMaterial;
        f.map = currentMap;
        int cpt = 0;

        while (cpt < vertexIndices.length) {

            FaceVertex fv = new FaceVertex();
            int currentVertexIndex;

            currentVertexIndex = vertexIndices[cpt++];
            if (currentVertexIndex < 0)
                currentVertexIndex += verticesG.size();

            if (((currentVertexIndex - 1) >= 0) && ((currentVertexIndex - 1) < verticesG.size()))
                fv.geometric = verticesG.get(currentVertexIndex - 1);
            else
                System.out.println("ERROR -- Vertex index out of range");

            // --------------------------
            currentVertexIndex = vertexIndices[cpt++];
            if (currentVertexIndex != Integer.MIN_VALUE) {
                if (currentVertexIndex < 0)
                    currentVertexIndex += verticesT.size();

                if ((currentVertexIndex - 1 >= 0) && (currentVertexIndex - 1 < verticesT.size()))
                    fv.texture = verticesT.get(currentVertexIndex - 1);
                else
                    System.out.println("ERROR -- Vertex index out of range");
            }
            //----------------------------------
            currentVertexIndex = vertexIndices[cpt++];
            if (currentVertexIndex != Integer.MIN_VALUE) {
                if (currentVertexIndex < 0)
                    currentVertexIndex += verticesN.size();

                if ((currentVertexIndex - 1 >= 0) && ((currentVertexIndex - 1) < verticesN.size()))
                    fv.normal = verticesN.get(currentVertexIndex - 1);
                else
                    System.out.println("ERROR -- Vertex index out of range");
            }
            //-----------------------------------
            if (fv.geometric == null) {
                System.out.println("ERROR -- cannot add vertex to face without vertex -- ignoring face");
                faceErrorCount++;
                return;
            }

            // Avoid redundant faces
            String key = fv.toString();
            FaceVertex tmp = faceVertexMap.get(key);
            if (tmp == null) {
                faceVertexMap.put(key, fv);
                fv.index = faceVertexList.size();
                faceVertexList.add(fv);
            } else {
                fv = tmp;
            }
            f.addVertex(fv);
        }

        if (currentGroupFaceLists.size() > 0) {
            for (ArrayList<Face> currentGroupFaceList : currentGroupFaceLists) {
                currentGroupFaceList.add(f);
            }
        }

        faces.add(f);

//        System.out.println(pIndices);

        // Stats for debug
        if (f.vertices.size() == 3) faceTriCount++;
        else if (f.vertices.size() == 4) faceQuadCount++;
        else facePolyCount++;
    }

    public void addObjectName(String name) {
        objectName = name;
    }

    public void newMaterial(String name) {
        currentMaterialBeingParsed = new Material(name);
        materialLib.put(name, currentMaterialBeingParsed);
    }

    public void addPoints(int[] values) {
        //TODO
    }

    public void addLine(int[] values) {
        //TODO
    }

    public void addMapLib(String[] names) {
        //TODO
    }

    public void doneParsingMaterial() {
        currentMapBeingParsed = null;
    }

    public void doneParsingObj(String filename) {
        System.out.println("obj parsing done");
    }


    // ========== Setters ==========


    public void setObjFileName(String filename) {
        objFileName = filename;
    }

    public void setRefl(int type, String filename) {
        currentMaterialBeingParsed.reflType = type;
        currentMaterialBeingParsed.reflFilename = filename;
    }

    public void setD(boolean halo, float factor) {
        currentMaterialBeingParsed.dHalo = halo;
        currentMaterialBeingParsed.dFactor = factor;
    }

    public void setNi(float opticalDensity) {
        currentMaterialBeingParsed.niOpticalDensity = opticalDensity;
    }

    public void setNs(float exponent) {
        currentMaterialBeingParsed.nsExponent = exponent;
    }

    public void setCurrentMap(String name) {
        currentMap = mapLib.get(name);
    }

    public void setCurrentMaterial(String name) {
        currentMaterial = materialLib.get(name);
    }

    public void setSharpness(float value) {
        currentMaterialBeingParsed.sharpnessValue = value;
    }

    public void setCurrentGroupNames(String[] names) {
        currentGroups.clear();
        currentGroupFaceLists.clear();
        if (names == null) return;
        for (String name : names) {
            String groupName = name.trim();
            currentGroups.add(groupName);
            if (groups.get(groupName) == null)
                groups.put(groupName, new ArrayList<>());
            currentGroupFaceLists.add(groups.get(groupName));
        }
    }

    public void setCurrentSmoothingGroup(int groupNumber) {
        currentSmoothingGroupNumber = groupNumber;
        if (currentSmoothingGroupNumber == 0) return;
        if (null == smoothingGroups.get(currentSmoothingGroupNumber)) {
            currentSmoothingGroup = new ArrayList<>();
            smoothingGroups.put(currentSmoothingGroupNumber, currentSmoothingGroup);
        }
    }

    public void setXYZ(int type, float x, float y, float z) {
        Reflectivity refl = currentMaterialBeingParsed.ka;
        if (type == 1) { // kd
            refl = currentMaterialBeingParsed.kd;
        } else if (type == 2) { // ks
            refl = currentMaterialBeingParsed.ks;
        } else if (type == 3) { // tf
            refl = currentMaterialBeingParsed.tf;
        }
        refl.rx = x;
        refl.gy = y;
        refl.bz = z;
        refl.isXYZ = true;
        refl.isRGB = false;
    }

    public void setRGB(int type, float r, float g, float b) {
        Reflectivity refl = currentMaterialBeingParsed.ka;
        if (type == 1) { // kd
            refl = currentMaterialBeingParsed.kd;
        } else if (type == 2) { // ks
            refl = currentMaterialBeingParsed.ks;
        } else if (type == 3) { // tf
            refl = currentMaterialBeingParsed.tf;
        }
        refl.rx = r;
        refl.gy = g;
        refl.bz = b;
        refl.isRGB = true;
        refl.isXYZ = false;
    }

    public void setIllum(int illumModel) {
        currentMaterialBeingParsed.illumModel = illumModel;
    }

    public void setMapDecalDispBump(int type, String filename) {
        //TODO
    }
}
