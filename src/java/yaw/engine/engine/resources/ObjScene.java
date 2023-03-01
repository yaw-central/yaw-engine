package yaw.engine.resources;

import yaw.engine.geom.Geometry;
import yaw.engine.mesh.Mesh;

import java.util.HashMap;
import java.util.Map;

public class ObjScene {
    private Map<String, Geometry> geometries;
    private Map<String, MtlMaterial> materials;

    private Map<String, String> materialMap;

    public ObjScene() {
        geometries = new HashMap<>();
        materials = new HashMap<>();
        materialMap = new HashMap<>();
    }

    public String getFreshGeomName() {
        return "Geom-" + Integer.toString(geometries.size() + 1);
    }

    public void addGeom(String objName, Geometry geom) {
        geometries.put(objName, geom);
    }

    public void assignMaterial(String objName, String matName) {
        materialMap.put(objName, matName);
    }

    public void addMaterial(String matName, MtlMaterial material) {
        materials.put(matName, material);
    }

    public Geometry getGeometryByIndex(int index) {
        int i = 0;
        for (Geometry geom : geometries.values()) {
            if (i == index) {
                return geom;
            }
        }
        return null;
    }
}
