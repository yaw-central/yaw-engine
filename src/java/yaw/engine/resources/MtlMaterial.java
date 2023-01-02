package yaw.engine.resources;

public class MtlMaterial {

    private String name;

    public float shininess = 0;
    public float ambient  = 0;
    public float diffuse = 0;
    public float specular = 0;
    public float emissive = 0;
    public float opacity = 0;

    public MtlMaterial(String name) {
        this.name = name;
    }
}
