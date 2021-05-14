package loader.v2;

public class FaceVertex {
    int index = -1;

    public GeometricVertex geometric = null;
    public TextureVertex texture = null;
    public NormalVertex normal = null;

    @Override
    public String toString() {
        return "FaceVertex{" +
                "geometric=" + geometric +
                ", texture=" + texture +
                ", normal=" + normal +
                '}';
    }
}
