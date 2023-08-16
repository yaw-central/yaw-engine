package yaw.engine.resources;

import org.joml.Vector3f;
import yaw.engine.mesh.Material;

import static org.joml.Math.clamp;

public class MtlMaterial {

    private String name;
    public float shineness = 8;

    public Vector3f ambient  = null;
    public Vector3f diffuse = null;
    public Vector3f specular = null;
    public Vector3f emissive = null;
    public float opacity = 0;

    public MtlMaterial(String name) {
        this.name = name;
    }

    public String toString() {
        String s = "MtlMaterial{" +
                "shininess=" + shineness +
                ", ambient=" + ambient +
                ", diffuse=" + diffuse +
                ", specular=" + specular +
                ", emissive=" + emissive +
                ", opacity=" + opacity +
                '}';
        return s;
    }

    public Material getMaterial(boolean withShadows) {
        // XXX : MTL has no notion of a "basic color"
        Vector3f baseColor = new Vector3f(1.0f, 1.0f, 1.0f);
        return new Material(baseColor,
                ambient == null ? baseColor : ambient,
                emissive == null ? new Vector3f(0, 0, 0) : emissive,
                emissive == null ? 0 : 1.0f,
                diffuse == null ? baseColor : diffuse,
                specular == null ? baseColor : specular,
                shineness,
                withShadows);
    }
}
