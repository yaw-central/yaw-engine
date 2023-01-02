package yaw.engine.resources;

import org.joml.Vector3f;

public class MtlMaterial {

    private String name;

    public float shininess = 0;

    public Vector3f ambient  = null;
    public Vector3f diffuse = null;
    public Vector3f specular = null;
    public Vector3f emissive = null;
    public float opacity = 0;

    public MtlMaterial(String name) {
        this.name = name;
    }
}
