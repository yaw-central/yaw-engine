package loader.v2;

public class TextureVertex {
    // The letters "U" and "V" denote the axes of the 2D texture because "X", "Y", and "Z" are already used to denote the axes of the 3D object in model space -- Wikipedia
    public float u, v;

    public TextureVertex(float u, float v) {
        this.u = u;
        this.v = v;
    }
}
