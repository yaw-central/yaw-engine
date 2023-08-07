package yaw.engine.resources;

import yaw.engine.geom.GeometryBuilder;
import yaw.engine.mesh.Material;

import java.util.HashMap;
import java.util.Map;

public class ObjScene {
    private Map<String, GeometryBuilder> geometries;
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

    public void addGeom(String objName, GeometryBuilder geom) {
        geometries.put(objName, geom);
    }

    public void assignMaterial(String objName, String matName) {
        materialMap.put(objName, matName);
    }

    public void addMaterial(String matName, MtlMaterial material) {
        materials.put(matName, material);
    }

    public GeometryBuilder getGeometryByIndex(int index) {
        int i = 0;
        for (GeometryBuilder geom : geometries.values()) {
            if (i == index) {
                return geom;
            }
        }
        return null;
    }

    public MtlMaterial getMtlMaterialByIndex(int index) {
        int i = 0;
        for (MtlMaterial mat : materials.values()) {
            if (i == index) {
                return mat;
            }
        }
        return null;
    }

    public Material getMaterialByIndex(int index) {
        int i = 0;
        for (MtlMaterial mat : materials.values()) {
            if (i == index) {
                return mat.getMaterial();
            }
        }
        return null;
    }
}
