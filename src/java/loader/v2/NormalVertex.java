package loader.v2;

public class NormalVertex {
    public float x, y, z;

    public NormalVertex(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void addNormal(float x, float y, float z){
        this.x += x;
        this.y += y;
        this.z += z;
    }
}
