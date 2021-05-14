package loader.v2;

public interface OBJLoaderV2Interface {

    void setObjFileName(String filename);

    void addGeometricVertex(float x, float y, float z);

    void addTextureVertex(float u, float v);

    void addNormalVertex(float x, float y, float z);

    void addFace(int[] vertexIndices);

    void setCurrentGroupNames(String[] names);

    void addObjectName(String name);

    void setCurrentMap(String name);

    void setCurrentMaterial(String name);

    void newMaterial(String name);

    void doneParsingMaterial();

}
