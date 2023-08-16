package yaw.engine.mesh;

import org.joml.Vector3f;

/**
 * More complex material classes:
 * ColorMapping, TextMapping, ProceduralMapping (material generator with reuse of images)
 */

public class Material {
    private Vector3f baseColor;
    private Texture texture;
    private Vector3f ambient;
    private Vector3f emissive;
    private float emissiveAmount;
    private Vector3f diffuse;
    private Vector3f specular;
    private float shineness;

    //RGB vector
    private Vector3f mColor;

    public Material(Vector3f baseColor, Vector3f ambient, Vector3f emissive, float emissiveAmount, Vector3f diffuse, Vector3f specular, float shineness) {
        this.baseColor = baseColor;
        texture = null;
        this.ambient = ambient;
        this.emissive = emissive;
        this.emissiveAmount = emissiveAmount;
        this.diffuse = diffuse;
        this.specular = specular;
        this.shineness = shineness;
    }

    public Material(Texture texture, Vector3f ambient, Vector3f emissive, float emissiveAmount, Vector3f diffuse, Vector3f specular, float shineness) {
        this.texture = texture;
        this.ambient = ambient;
        this.emissive = emissive;
        this.emissiveAmount = emissiveAmount;
        this.diffuse = diffuse;
        this.specular = specular;
        this.shineness = shineness;
    }

    public Vector3f getBaseColor() {
        return baseColor;
    }

    public boolean isTextured() {
        return texture != null;
    }

    public Vector3f getAmbientColor() {
        return ambient;
    }

    public Vector3f getEmissiveColor() {
        return emissive;
    }

    public float getEmissiveAmount() {
        return emissiveAmount;
    }

    public Vector3f getDiffuseColor() {
        return diffuse;
    }

    public Vector3f getSpecularColor() {
        return specular;
    }

    public float getShineness() {
        return shineness;
    }


}