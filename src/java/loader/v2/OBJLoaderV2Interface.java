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

    void addPoints(int values[]);

    void addLine(int values[]);

    void addMapLib(String[] names);

    void doneParsingMaterial();

    void doneParsingObj(String filename);

    void setCurrentSmoothingGroup(int groupNumber);

    void setXYZ(int type, float x, float y, float z);

    void setRGB(int type, float r, float g, float b);

    void setIllum(int illumModel);

    void setD(boolean halo, float factor);

    void setNs(float exponent);

    void setSharpness(float value);

    void setNi(float opticalDensity);

    void setMapDecalDispBump(int type, String filename);

    void setRefl(int type, String filename);

    void setCurrentMap(String name);

    void setCurrentMaterial(String name);

    void setObjFileName(String filename);

}
