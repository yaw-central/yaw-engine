package yaw.engine.resources;

import org.joml.Vector3f;
import yaw.engine.mesh.Material;

import static org.joml.Math.clamp;

public class MtlMaterial {

    private String name;
    private Material mat;
    public float shininess = 0;

    public Vector3f ambient  = null;
    public Vector3f diffuse = null;
    public Vector3f specular = null;
    public Vector3f emissive = null;
    public float opacity = 0;

    public MtlMaterial(String name) {
        this.name = name;
        mat = new Material();
    }

    public String toString() {
        String s = "Material{" +
                "shininess=" + shininess +
                ", ambient=" + ambient +
                ", diffuse=" + diffuse +
                ", specular=" + specular +
                ", emissive=" + emissive +
                ", opacity=" + opacity +
                '}';
        return s;
    }
    public Material getMaterial(){
        Vector3f final_color = new Vector3f();
        Vector3f vide = new Vector3f(0, 0, 0);

        //System.out.println(toString());

       if (ambient != null) {
            final_color.add(ambient.mul(opacity));
        }

        if (diffuse != null) {
            final_color.mul(diffuse.mul(opacity));
        }

        if (specular != null) {
            final_color.mul(specular.mul(opacity));
        }

        if (emissive != null && !vec3fEqual(emissive, vide)) {
            final_color.mul(emissive.mul(opacity));
        }

        final_color.set(clamp(final_color.x, 0, 1), clamp(final_color.y, 0, 1), clamp(final_color.z, 0, 1));
        mat.setColor(final_color);
        mat.setReflectance(shininess);
        return mat;
    }

    public boolean vec3fEqual(Vector3f v1, Vector3f v2)
    {
        return (v1.x == v2.x && v2.y == v1.y && v1.z == v2.z);
    }
}
