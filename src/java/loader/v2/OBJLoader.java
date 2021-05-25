package loader.v2;

import java.util.*;

/**
 * The OBJLoader loads OBJ files into its attributes by calling a parse on them.
 * It contains all OBJ file's elements.
 * It allows users to manipulate every attribute from an OBJ as they want.
 */
public class OBJLoader {

    // ========== Attributes ==========


    /**
     * OBJ file name
     */
    private String objFileName = null;
    /**
     * Object name
     */
    private String objectName = null;
    /**
     * List of all the OBJ's geometric vertices
     */
    private List<GeometricVertex> verticesG = new ArrayList<>();
    /**
     * List of all the OBJ's texture vertices
     */
    private ArrayList<TextureVertex> verticesT = new ArrayList<>();
    /**
     * List of all the OBJ's normal vertices
     */
    private ArrayList<NormalVertex> verticesN = new ArrayList<>();
    /**
     * List of all faces
     */
    private List<Face> faces = new ArrayList<>();
    /**
     * List of all the face vertices
     */
    private List<FaceVertex> faceVertexList = new ArrayList<>();
    /**
     * Map of all the face vertices keyed with their string representation
     */
    private Map<String, FaceVertex> faceVertexMap = new HashMap<>();
    /**
     * Map of the name of the group associated with its faces list
     */
    private Map<String, ArrayList<Face>> groups = new HashMap<>();
    /**
     * Current working group
     */
    private List<String> currentGroups = new ArrayList<>();
    /**
     * Current working face list groups
     */
    private List<ArrayList<Face>> currentGroupFaceLists = new ArrayList<>();
    /**
     * List of indices
     */
    private List<Integer> pIndices = new ArrayList<>();

    /**
     * Current material
     */
    private Material currentMaterial = null;
    /**
     * Current map
     */
    private Material currentMap = null;
    /**
     * Map of a name associated with its material
     */
    private Map<String, Material> materialLib = new HashMap<>();
    /**
     * Current material under parsing
     */
    private Material currentMaterialBeingParsed = null;
    /**
     * Map of a name associated with its map
     */
    private Map<String, Material> mapLib = new HashMap<>();
    /**
     * Current map under parsing
     */
    private Material currentMapBeingParsed = null;
    /**
     * Map describing the smoothing groups, with a group number associated with a list of faces
     */
    private Map<Integer, ArrayList<Face>> smoothingGroups = new HashMap<Integer, ArrayList<Face>>();
    /**
     * Current smoothing group number
     */
    private int currentSmoothingGroupNumber = 0;
    /**
     * Current smoothing group (list of faces)
     */
    private ArrayList<Face> currentSmoothingGroup = null;

    // Debug statistics
    /**
     * Number a triangle faces
     */
    private int faceTriCount = 0;
    /**
     * Number of quadrilateral faces
     */
    private int faceQuadCount = 0;
    /**
     * Number of polygonal faces of 5+ vertices
     */
    private int facePolyCount = 0;
    /**
     * Number of errors encountered during face parsing (faces without at least a geometric vertex on one of its vertices)
     */
    private int faceErrorCount = 0;


    // ========== Constructors ==========


    /**
     * Empty constructor
     */
    public OBJLoader() {
    }


    // ========== Methods ==========


    /**
     * Adds a geometric vertex created from the given coordinates to the list.
     *
     * @param x The x vertex coordinate
     * @param y The y vertex coordinate
     * @param z The z vertex coordinate
     */
    public void addGeometricVertex(float x, float y, float z) {
        verticesG.add(new GeometricVertex(x, y, z));
    }

    /**
     * Adds a texture vertex created from the given parameters to the list.
     *
     * @param u The u vertex coordinate
     * @param v The v vertex coordinate
     */
    public void addTextureVertex(float u, float v) {
        verticesT.add(new TextureVertex(u, v));
    }

    /**
     * Adds a normal vertex created from the given coordinates to the list.
     *
     * @param x The x vertex coordinate
     * @param y The y vertex coordinate
     * @param z The z vertex coordinate
     */
    public void addNormalVertex(float x, float y, float z) {
        verticesN.add(new NormalVertex(x, y, z));
    }

    /**
     * Adds a face to the list. The face is created from the vertex indices list.
     *
     * @param vertexIndices The list of the vertices indices
     */
    public void addFace(int[] vertexIndices) {
//        create Face object + set material + map
        Face f = new Face();
        f.setMaterial(currentMaterial);
        f.setMap(currentMap);
//        initiate a counter
        int cpt = 0;

//        while loop to check every elements of vertexIndices
        while (cpt < vertexIndices.length) {
//            create a FaceVertex to add to a Face
            FaceVertex fv = new FaceVertex();

//            initiate the currentVertexIndex
            int currentVertexIndex;
            currentVertexIndex = vertexIndices[cpt++];

//            if currentVertexIndex < 0 then currentVertexIndex += verticesG.size() to have a valid value
            if (currentVertexIndex < 0)
                currentVertexIndex += verticesG.size();
//            set the facevertex's geometric vertex
            if (((currentVertexIndex - 1) >= 0) && ((currentVertexIndex - 1) < verticesG.size()))
                fv.geometric = verticesG.get(currentVertexIndex - 1);
            else
                System.out.println("ERROR -- Vertex index out of range");
            // --------------------------
//            update currentVertexIndex
            currentVertexIndex = vertexIndices[cpt++];
            //            check currentVertexIndex value
            if (currentVertexIndex != Integer.MIN_VALUE) {
                //            if currentVertexIndex < 0 then currentVertexIndex += verticesT.size() to have a valid value
                if (currentVertexIndex < 0)
                    currentVertexIndex += verticesT.size();
//            set the facevertex's texture vertex
                if ((currentVertexIndex - 1 >= 0) && (currentVertexIndex - 1 < verticesT.size()))
                    fv.texture = verticesT.get(currentVertexIndex - 1);
                else
                    System.out.println("ERROR -- Vertex index out of range");
            }
            // --------------------------
            //            update currentVertexIndex
            currentVertexIndex = vertexIndices[cpt++];
            //            check currentVertexIndex value
            if (currentVertexIndex != Integer.MIN_VALUE) {
                //            if currentVertexIndex < 0 then currentVertexIndex += verticesN.size() to have a valid value
                if (currentVertexIndex < 0)
                    currentVertexIndex += verticesN.size();
//            set the facevertex's normal vertex
                if ((currentVertexIndex - 1 >= 0) && ((currentVertexIndex - 1) < verticesN.size()))
                    fv.normal = verticesN.get(currentVertexIndex - 1);
                else
                    System.out.println("ERROR -- Vertex index out of range");
            }
            // --------------------------
//            if the faceVertex doesn't have a geometric vertex the face is no valid
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

        // Add the face to the working face lists
        if (currentGroupFaceLists.size() > 0) {
            for (ArrayList<Face> currentGroupFaceList : currentGroupFaceLists) {
                currentGroupFaceList.add(f);
            }
        }
        faces.add(f);

//        System.out.println(pIndices);
        // Statistics for debug
        if (f.getVertices().size() == 3) faceTriCount++;
        else if (f.getVertices().size() == 4) faceQuadCount++;
        else facePolyCount++;
    }

    /**
     * Puts a new material as the current parsed material.
     *
     * @param name
     */
    public void newMaterial(String name) {
        currentMaterialBeingParsed = new Material(name);
        materialLib.put(name, currentMaterialBeingParsed);
    }

//    sort les faces by their material
    public ArrayList<ArrayList<Face>> createFaceListsByMaterial() {
        ArrayList<ArrayList<Face>> facesByTextureList = new ArrayList<>();
        Material currentMaterial = null;
        ArrayList<Face> currentFaceList = new ArrayList<>();

        for (Face face : this.faces) {
            if (face.getMaterial() != currentMaterial) {
                if (!currentFaceList.isEmpty()) {
                    facesByTextureList.add(currentFaceList);
                }
                currentMaterial = face.getMaterial();
                currentFaceList = new ArrayList<>();
            }
            currentFaceList.add(face);
        }
        if (!currentFaceList.isEmpty()) {
            facesByTextureList.add(currentFaceList);
        }
        return facesByTextureList;
    }

    /**
     * Converts every quadrilateral face into triangle faces.
     *
     * @param faceList The list of all the faces
     * @return The given list were all quadrilateral faces are split into 2 triangle faces
     */
    public ArrayList<Face> splitQuads(ArrayList<Face> faceList) {
        ArrayList<Face> triangleList = new ArrayList<>();
        for (Face face : faceList) {
            List<FaceVertex> vertices = face.getVertices();
            if (vertices.size() == 3) {         // No need to split a triangle
                triangleList.add(face);
            } else if (vertices.size() == 4) {  // Quadrilateral faces need to be split
//                split a quadrilateral face into two triangle faces
                FaceVertex v1 = vertices.get(0);
                FaceVertex v2 = vertices.get(1);
                FaceVertex v3 = vertices.get(2);
                FaceVertex v4 = vertices.get(3);
                Face f1 = new Face();
                f1.setMap(face.getMap());
                f1.setMaterial(face.getMaterial());
                f1.addVertex(v1);
                f1.addVertex(v2);
                f1.addVertex(v3);
                triangleList.add(f1);
                Face f2 = new Face();
                f2.setMap(face.getMap());
                f2.setMaterial(face.getMaterial());
                f2.addVertex(v1);
                f2.addVertex(v3);
                f2.addVertex(v4);
                triangleList.add(f2);
            }
        }
//        System.out.println("liste depuis splitQuads" + triangleList);
        return triangleList;
    }

    /**
     * Calculates a default value for the faces that have no vertex normal
     *
     * @param triangleList The list of triangle faces
     */
    public void calcMissingVertexNormals(ArrayList<Face> triangleList) {
        for (Face face : triangleList) {
            face.processTriangleNormal();
            List<FaceVertex> vertices = face.getVertices();
            // For each vertex of the face
            for (int i = 0; i < vertices.size(); i++) {
                FaceVertex fv = vertices.get(i);
                if (vertices.get(0).normal == null) {   // There is no normal vertex
                    FaceVertex newFv = new FaceVertex();
                    newFv.geometric = fv.geometric;
                    newFv.texture = fv.texture;
                    newFv.normal = face.getFaceNormal();
                    vertices.set(i, newFv);
                }
            }
        }
//        System.out.println("calcmissing" + triangleList);
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

    /**
     * Puts the current parsed map at null.
     */
    public void doneParsingMaterial() {
        currentMapBeingParsed = null;
    }

    /**
     * Informs the user that the parsing is done.
     *
     * @param filename
     */
    public void doneParsingObj(String filename) {
        System.out.println("OBJ parsing done on " + filename);
    }


    // ========== Setters ==========


    public void setObjFileName(String filename) {
        objFileName = filename;
    }

    public void setObjectName(String name) {
        objectName = name;
    }

    public void setRefl(int type, String filename) {
        currentMaterialBeingParsed.setReflType(type);
        currentMaterialBeingParsed.setReflFilename(filename);
    }

    public void setD(boolean halo, float factor) {
        currentMaterialBeingParsed.setdHalo(halo);
        currentMaterialBeingParsed.setdFactor(factor);
    }

    public void setNi(float opticalDensity) {
        currentMaterialBeingParsed.setNiOpticalDensity(opticalDensity);
    }

    public void setNs(float exponent) {
        currentMaterialBeingParsed.setNsExponent(exponent);
    }

    public void setCurrentMap(String name) {
        currentMap = mapLib.get(name);
    }

    public void setCurrentMaterial(String name) {
        currentMaterial = materialLib.get(name);
    }

    public void setSharpness(float value) {
        currentMaterialBeingParsed.setSharpnessValue(value);
    }

    public void setIllum(int illumModel) {
        currentMaterialBeingParsed.setIllumModel(illumModel);
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
        Reflectivity refl = currentMaterialBeingParsed.getKa();
        if (type == 1) { // kd
            refl = currentMaterialBeingParsed.getKd();
        } else if (type == 2) { // ks
            refl = currentMaterialBeingParsed.getKs();
        } else if (type == 3) { // tf
            refl = currentMaterialBeingParsed.getTf();
        }
        refl.setRx(x);
        refl.setGy(y);
        refl.setBz(z);
        refl.setXYZ(true);
        refl.setRGB(false);
    }

    public void setRGB(int type, float r, float g, float b) {
        Reflectivity refl = currentMaterialBeingParsed.getKa();
        if (type == 1) { // kd
            refl = currentMaterialBeingParsed.getKd();
        } else if (type == 2) { // ks
            refl = currentMaterialBeingParsed.getKs();
        } else if (type == 3) { // tf
            refl = currentMaterialBeingParsed.getTf();
        }
        refl.setRx(r);
        refl.setGy(g);
        refl.setBz(b);
        refl.setRGB(true);
        refl.setXYZ(false);
    }

    public void setMapDecalDispBump(int type, String filename) {
        if (type == 0) {
            currentMaterialBeingParsed.setMapKaFilename(filename);
        } else if (type == 1) {
            currentMaterialBeingParsed.setMapKdFilename(filename);
        } else if (type == 2) {
            currentMaterialBeingParsed.setMapKsFilename(filename);
        } else if (type == 3) {
            currentMaterialBeingParsed.setMapNsFilename(filename);
        } else if (type == 4) {
            currentMaterialBeingParsed.setMapDFilename(filename);
        } else if (type == 5) {
            currentMaterialBeingParsed.setDecalFilename(filename);
        } else if (type == 6) {
            currentMaterialBeingParsed.setDispFilename(filename);
        } else if (type == 7) {
            currentMaterialBeingParsed.setBumpFilename(filename);
        }
    }


}
