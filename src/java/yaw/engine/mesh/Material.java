package yaw.engine.mesh;

import org.joml.Vector3f;

/**
 * More complex material classes:
 * ColorMapping, TextMapping, ProceduralMapping (material generator with reuse of images)
 */

public class Material {
    private Vector3f color;

    private Texture texture;
    private Vector3f ambient;
    private Vector3f emissive;
    private float emissiveAmount;
    private Vector3f diffuse;
    private Vector3f specular;
    private float shineness;

    //RGB vector
    private Vector3f mColor;

    public Material(Vector3f color, Vector3f ambient, Vector3f emissive, float emissiveAmount, Vector3f diffuse, Vector3f specular, float shineness) {
        this.color = color;
        this.texture = null;
        this.ambient = ambient;
        this.emissive = emissive;
        this.emissiveAmount = emissiveAmount;
        this.diffuse = diffuse;
        this.specular = specular;
        this.shineness = shineness;
    }

    public Material(Texture texture, Vector3f ambient, Vector3f emissive, float emissiveAmount, Vector3f diffuse, Vector3f specular, float shineness) {
        this.color = null;
        this.texture = texture;
        this.ambient = ambient;
        this.emissive = emissive;
        this.emissiveAmount = emissiveAmount;
        this.diffuse = diffuse;
        this.specular = specular;
        this.shineness = shineness;
    }

    public Material(Vector3f color, Vector3f emissive, float emissiveAmount, Vector3f diffuse, Vector3f specular, float shineness) {
        this(color, new Vector3f(1.0f, 1.0f, 1.0f), emissive, emissiveAmount, diffuse, specular, shineness);
    }

    public Material(Texture texture, Vector3f emissive, float emissiveAmount, Vector3f diffuse, Vector3f specular, float shineness) {
        this(texture, new Vector3f(1.0f, 1.0f, 1.0f), emissive, emissiveAmount, diffuse, specular, shineness);
    }

    public Material(Vector3f color, Vector3f diffuse, Vector3f specular, float shineness) {
        this(color, new Vector3f(0, 0, 0), 0.0f, diffuse, specular, shineness);
    }

    public Material(Texture texture, Vector3f diffuse, Vector3f specular, float shineness) {
        this(texture, new Vector3f(0, 0, 0), 0.0f, diffuse, specular, shineness);
    }

    public Material(Vector3f color, Vector3f specular, float shineness) {
        this(color, new Vector3f(1.0f, 1.0f, 1.0f), specular, shineness);
    }

    public Material(Texture texture, Vector3f specular, float shineness) {
        this(texture, new Vector3f(1.0f, 1.0f, 1.0f), specular, shineness);
    }

    public Material(Vector3f color, float shineness) {
        this(color, new Vector3f(1.0f, 1.0f, 1.0f), shineness);
    }

    public Material(Texture texture, float shineness) {
        this(texture, new Vector3f(1.0f, 1.0f, 1.0f), shineness);
    }

    public Material(Vector3f color) {
        this(color, 8f);
    }

    public Material(Texture texture) {
        this(texture, 8f);
    }

    public Texture getTexture() {
        return mTexture;
    }

    public void setTexture(Texture pTexture) {
        mTexture = pTexture;
    }

    public boolean isTextured() {
        return this.mTexture != null;
    }

    public Vector3f getColor() {
        return mColor;
    }

    public void setColor(Vector3f pColor) {mColor = pColor;}

    public float getReflectance() {
        return mReflectance;
    }

    public void setReflectance(float pReflectance) {
        mReflectance = pReflectance;
    }
}
