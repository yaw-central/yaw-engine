package yaw.engine.resources;

import yaw.engine.geom.GeometryBuilder;
import yaw.engine.mesh.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjScene {
    private Map<String, GeometryBuilder> geometries;
    private List<String> geometryIds;

    private Map<String, MtlMaterial> materials;
    private List<String> materialIds;
    private Map<String, String> materialMap;


    public ObjScene() {
        geometries = new HashMap<>();
        geometryIds = new ArrayList<>();
        materials = new HashMap<>();
        materialIds = new ArrayList<>();
        materialMap = new HashMap<>();
    }

    public String getFreshGeomName() {
        return "Geom-" + Integer.toString(geometries.size() + 1);
    }

    public void addGeom(String objName, GeometryBuilder geom) {
        if (geometries.containsKey(objName)) {
            throw new Error("Object '" + objName + "' already added.");
        }
        geometryIds.add(objName);
        geometries.put(objName, geom);
    }

    public void addMaterial(String matName, MtlMaterial material) {
        if (materials.containsKey(matName)) {
            throw new Error("Material '" + matName + "' already added.");
        }
        materialIds.add(matName);
        materials.put(matName, material);
    }

    public void assignMaterial(String objName, String matName) {
        materialMap.put(objName, matName);
    }

    public int nbGeometries() {
        return geometryIds.size();
    }

    public GeometryBuilder getGeometryByIndex(int index) {
        String geomName = geometryIds.get(index);
        return geometries.get(geomName);
    }

    public MtlMaterial getMaterialByIndex(int index) {
        String matName = materialIds.get(index);
        return materials.get(matName);
    }

}
