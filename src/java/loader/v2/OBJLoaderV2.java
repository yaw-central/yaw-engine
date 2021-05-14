package loader.v2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OBJLoaderV2 {

    public String objFileName = null;

    public List<GeometricVertex> verticesG = new ArrayList<>();
    public ArrayList<TextureVertex> verticesT = new ArrayList<>();
    public ArrayList<NormalVertex> verticesN = new ArrayList<>();

    Map<String, FaceVertex> faceVerticeMap = new HashMap<>();

    public List<FaceVertex> faceVerticeList = new ArrayList<>();
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
    public int faceTriCount = 0;
    public int faceQuadCount = 0;
    public int facePolyCount = 0;
    public int faceErrorCount = 0;

    public OBJLoaderV2() {}

    public void setObjFileName(String filename){objFileName = filename;}

    public void addGeometricVertex(float x, float y, float z){
        verticesG.add(new GeometricVertex(x, y, z));
    }

    public void addTextureVertex(float u, float v){
        verticesT.add(new TextureVertex(u, v));
    }

    public void addNormalVertex(float x, float y, float z){
        verticesN.add(new NormalVertex(x, y, z));
    }

    public void addFace(int[] vertexIndices){
        Face f = new Face();

        f.material = currentMaterial;
        f.map = currentMap;

        int cpt = 0;
        while(cpt < vertexIndices.length){

            FaceVertex fv = new FaceVertex();
            int currentVertexIndice;

            currentVertexIndice = vertexIndices[cpt++];

            if(currentVertexIndice < 0)
                currentVertexIndice += verticesG.size();

            if ((currentVertexIndice - 1 >= 0) && (currentVertexIndice -1 < verticesG.size()))
                fv.geometric = verticesG.get(currentVertexIndice - 1);
            else
                System.out.println("ERROR -- Vertinx index out of range");

            // --------------------------
            currentVertexIndice = vertexIndices[cpt++];
            if (currentVertexIndice < 0)
                currentVertexIndice += verticesT.size();

            if ((currentVertexIndice - 1 >= 0) && (currentVertexIndice - 1 < verticesT.size()))
                fv.texture = verticesT.get(currentVertexIndice -1);
            else
                System.out.println("ERROR -- Vertinx index out of range");

            //----------------------------------
            currentVertexIndice = vertexIndices[cpt++];
            if (currentVertexIndice <0)
                currentVertexIndice += verticesN.size();

            if ((currentVertexIndice - 1 >= 0) && ((currentVertexIndice - 1) < verticesN.size()))
                fv.normal = verticesN.get(currentVertexIndice -1);
            else
                System.out.println("ERROR -- Vertinx index out of range");

            //-----------------------------------
            if (fv.geometric == null){
                System.out.println("ERROR -- cannot add vertex to face without vertex -- ignoring face");
                faceErrorCount++;
                return;
            }

            //avoid redundant faces
            String key = fv.toString();
            FaceVertex tmp = faceVerticeMap.get(key);
            if (tmp == null){
                faceVerticeMap.put(key, fv);
                fv.index = faceVerticeList.size();
                faceVerticeList.add(fv);
            } else {
                fv = tmp;
            }
            f.addVertex(fv);
        }

        if (currentGroupFaceLists.size() > 0){
            for (ArrayList<Face> currentGroupFaceList : currentGroupFaceLists) {
                currentGroupFaceList.add(f);
            }
        }

        faces.add(f);

        //stats for debug
        if (f.vertices.size() == 3)
            faceTriCount++;
        else if (f.vertices.size() == 4)
            faceQuadCount++;
        else
            facePolyCount++;
    }


}
